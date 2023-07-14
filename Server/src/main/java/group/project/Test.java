package group.project;

import com.google.gson.*;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import group.project.init.Scripts;
import group.project.net.BrowserCache;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Test {

    public static void main(String[] args) throws IOException, URISyntaxException {
        try(Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
            options.setHeadless(false);

            Browser browser = playwright.chromium().launch(options);
            BrowserContext context = browser.newContext();
            context.setDefaultTimeout(Integer.MAX_VALUE);
            Page page = null;
            int target = 1;

            Path path = Path.of("./cookies.json");
            System.out.println(path.toFile().getAbsolutePath());

            if(path.toFile().exists()) {
                JsonElement json = JsonParser.parseReader(new FileReader(path.toFile()));
                BrowserCache cache = BrowserCache.of(json.getAsJsonObject());
                context.addCookies(cache.getCookies());
                page = context.newPage();

                if(target == 0) { // Grades
                    page.navigate("https://www.uocampus.uottawa.ca/psc/csprpr9www/EMPLOYEE/SA/c/NUI_FRAMEWORK.PT_LANDINGPAGE.GBL");
                } else if(target == 1) { // Card Transactions
                    page.navigate("https://carteuottawacard.uottawa.ca/login.aspx");
                }
            } else {
                page = context.newPage();
                String[] credentials = Scripts.read("/credentials.creds").split("\n");
                userAuth(page, target, credentials);
            }

            JsonArray testCard = scrapeCard(page);
            // JSONArray testGrades = scrapeGrades(page);

            try (FileWriter file = new FileWriter("server_data/transactions.json")) {
                file.write(testCard.toString());
            //try (FileWriter file = new FileWriter("server_data/grades.json")) {
                //file.write(testGrades.toString());
                System.out.println("JSON data has been written to the file");
            } catch (IOException e) {
                e.printStackTrace();
            }

            page.waitForClose(() -> {
                BrowserCache cache = BrowserCache.of(null, context);

                try {
                    Files.writeString(path, new Gson().newBuilder().setPrettyPrinting()
                            .create().toJson(cache.write().get()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void userAuth(Page page, int target, String[] credentials) {
        System.out.println("Trying to log in...");

        if (target == 0) { // Grades
            page.navigate("https://www.uocampus.uottawa.ca/psc/csprpr9www/EMPLOYEE/SA/c/NUI_FRAMEWORK.PT_LANDINGPAGE.GBL");
        } else if (target == 1) { // Card Transactions
            page.navigate("https://carteuottawacard.uottawa.ca/login.aspx");
            page.getByText("Students").all().get(1).click();
        } else {
            System.out.println("Invalid target provided");
            return;
        }

        page.getByPlaceholder("someone@example.com").fill(credentials[0]);
        page.getByText("Next").click();
        page.getByPlaceholder("Password").fill(credentials[1]);
        page.getByText("Sign in").click();

        String mfa = page.locator("#idRichContext_DisplaySign").textContent();
        System.out.println("Your 2FA code is: " + mfa);
        page.locator("#KmsiCheckboxField").click();
        page.getByText("Yes").click();
        System.out.println("Successfully logged in!");
    }

    private static JsonArray scrapeGrades(Page page) {

        JsonArray data = new JsonArray();
        page.navigate("https://www.uocampus.uottawa.ca/psc/csprpr9www_newwin/EMPLOYEE/SA/c/SA_LEARNER_SERVICES.SSR_SSENRL_GRADE.GBL?NavColl=true");

        // Sleep or it crashes
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Look through terms
        for(Locator locator : page.locator("//*[@name=\"SSR_DUMMY_RECV1$sels$0\"]").all()) {

            locator.setChecked(true);
            page.locator("#DERIVED_SSS_SCT_SSR_PB_GO").first().click();

            // Year - Term - Level
            String semester = page.locator("//*[@id=\"DERIVED_REGFRM1_SSR_STDNTKEY_DESCR$11$\"]").textContent();
            String[] semesterSplit = semester.split(" ");

            // TGPA and CGPA - Only id, and # of line varies from term to term
            ArrayList<String> possibleTGPAs = new ArrayList<>();
            ArrayList<String> possibleCGPAs = new ArrayList<>();
            for (int i = 0; i < 15; i++) {
                try {
                    String tgpa = page.querySelector("//*[@id=\"STATS_ENRL$" + i + "\"]").textContent();
                    possibleTGPAs.add(tgpa);

                    String cgpa = page.querySelector("//*[@id=\"STATS_CUMS$" + i + "\"]").textContent();
                    possibleCGPAs.add(cgpa);
                } catch (Exception e) {
                    break;
                }
            }

            JsonObject currentTerm = new JsonObject();
            currentTerm.addProperty("level", semesterSplit[4]);
            currentTerm.addProperty("term", semesterSplit[1]);
            currentTerm.addProperty("year", semesterSplit[0]);
            currentTerm.addProperty("tgpa", possibleTGPAs.get(possibleTGPAs.size()-1));
            currentTerm.addProperty("cgpa", possibleCGPAs.get(possibleCGPAs.size()-1));

            // Loop through courses
            for (int i = 0; i < 6; i++) {
                try {
                    String courseName = page.querySelector("//*[@id=\"win1divCLS_LINK$" + i + "\"]").textContent();
                    String[] courseNameSplit = courseName.split(" ");

                    String courseDescription = page.querySelector("//*[@id=\"win1divCLASS_TBL_VW_DESCR$" + i + "\"]").textContent();
                    String courseUnits = page.querySelector("//*[@id=\"win1divSTDNT_ENRL_SSV1_UNT_TAKEN$" + i + "\"]").textContent();
                    String gradingBase = page.querySelector("//*[@id=\"win1divGRADING_BASIS$" + i + "\"]").textContent();
                    String letterGrade = page.querySelector("//*[@id=\"win1divSTDNT_ENRL_SSV1_CRSE_GRADE_OFF$" + i + "\"]").textContent();
                    String gradePoints = page.querySelector("//*[@id=\"win1divSTDNT_ENRL_SSV1_GRADE_POINTS$" + i + "\"]").textContent();

                    JsonObject currentGrade = new JsonObject();

                    currentGrade.addProperty("program", courseNameSplit[0]);
                    currentGrade.addProperty("code", courseNameSplit[1]);
                    currentGrade.addProperty("description", courseDescription);
                    currentGrade.addProperty("units", courseUnits);
                    currentGrade.addProperty("grading", gradingBase);
                    currentGrade.addProperty("letter", letterGrade);
                    currentGrade.addProperty("points", gradePoints);

                    currentTerm.add(courseName, currentGrade);
                } catch (Exception e) {
                    break;
                }
            }

            // Push Data to JSON and Switch Term
            data.add(currentTerm);
            page.locator("//*[@id=\"DERIVED_SSS_SCT_SSS_TERM_LINK\"]").click();
        }

        return data;
    }

    private static JsonArray scrapeCard(Page page) {
        JsonArray data = new JsonArray();

        String flexBalance = page.locator("//*[@id=\"ctl00_lgnView_cpMain_ctlAccBalance_grdAccounts\"]/tbody[1]/tr[2]/td[2]").textContent();
        String diningBalance = page.locator("//*[@id=\"ctl00_lgnView_cpMain_ctlAccBalance_grdAccounts\"]/tbody[1]/tr[3]/td[2]").textContent();
        //System.out.println("Flex Balance: " + flexBalance);
        //System.out.println("Dining Balance: " + diningBalance);

        JsonObject balances = new JsonObject();
        balances.addProperty("flex",flexBalance);
        balances.addProperty("dining",flexBalance);
        data.add(balances);

        Locator transactionLi = page.locator("//div[@id=\"ctl00_lgnView_menuDesktop\"]/ul/li").all().get(1);
        transactionLi.locator("//a").click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        //# of pages is wrong - to change

        int numberOfPages = page.locator("//tr[@class=\"pager grid-pager\"]/td/table/tbody/tr/td").all().size() / 2;
        System.out.println(numberOfPages);
        for (int i = 1; i <= numberOfPages; i++) {

            if (i != 1) {
                Locator currentTd;
                if (i == 2) {
                    currentTd = page.locator("//tr[@class=\"pager grid-pager\"]/td/table/tbody/tr/td").all().get(i);
                } else {
                    currentTd = page.locator("//tr[@class=\"pager grid-pager\"]/td/table/tbody/tr/td").all().get(i - 1);
                }

                currentTd.locator("//a").click();
                //page.waitForSelector();
            }


            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Table Page Scraping
            for (Locator row : page.locator("//table[@id=\"ctl00_lgnView_cpMain_ctlAccHistory_grdHistory\"]/tbody/tr").all()) {
                String[] entries = row.locator("//td").all().stream()
                        .map(Locator::textContent).toArray(String[]::new);

                if (entries.length != 8) continue;

                String[] dateTime = entries[0].split(" ");
                JsonObject currentTransaction = new JsonObject();

                currentTransaction.addProperty("date", dateTime[0]);
                currentTransaction.addProperty("time", dateTime[1]);
                currentTransaction.addProperty("withdrawal", entries[2].charAt(0) == '-' ? "N/A" : entries[2]);
                currentTransaction.addProperty("deposit", entries[3].charAt(0) == '-' ? "N/A" : entries[3]);
                currentTransaction.addProperty("balance", entries[4]);
                currentTransaction.addProperty("description", entries[5]);

                data.add(currentTransaction);
            }
        }

        return data;
    }

    /* public static void navigateToGrades(Page page, String[] credentials) {
        System.out.println("Logging in...");
        page.navigate("https://www.uocampus.uottawa.ca/psc/csprpr9www/EMPLOYEE/SA/c/NUI_FRAMEWORK.PT_LANDINGPAGE.GBL");
        page.getByPlaceholder("someone@example.com").fill(credentials[0]);
        page.getByText("Next").click();
        page.getByPlaceholder("Password").fill(credentials[1]);
        page.getByText("Sign in").click();
        String mfa = page.locator("#idRichContext_DisplaySign").textContent();
        System.out.println("Your 2FA code is: " + mfa);
        page.locator("#KmsiCheckboxField").click();
        page.getByText("Yes").click();
        System.out.println("Successfully logged in!");
        page.navigate("https://www.uocampus.uottawa.ca/psc/csprpr9www_newwin/EMPLOYEE/SA/c/SA_LEARNER_SERVICES.SSR_SSENRL_GRADE.GBL?NavColl=true");
        page.locator("//*[@name=\"SSR_DUMMY_RECV1$sels$0\"]").first().check();
        page.locator("#DERIVED_SSS_SCT_SSR_PB_GO").first().click();
    }

    public static void navigateToCard(Page page, String[] credentials) {
        page.navigate("https://carteuottawacard.uottawa.ca/login.aspx");
        page.getByText("Students").all().get(1).click();
        System.out.println("Logging in...");
        page.getByPlaceholder("someone@example.com").fill(credentials[0]);
        page.getByText("Next").click();
        page.getByPlaceholder("Password").fill(credentials[1]);
        page.getByText("Sign in").click();
        String mfa = page.locator("#idRichContext_DisplaySign").textContent();
        System.out.println("Your 2FA code is: " + mfa);
        page.locator("#KmsiCheckboxField").click();
        page.getByText("Yes").click();
        System.out.println("Successfully logged in!");
        String flexBalance = page.locator("//*[@id=\"ctl00_lgnView_cpMain_ctlAccBalance_grdAccounts\"]/tbody[1]/tr[2]/td[2]").textContent();
        String diningBalance = page.locator("//*[@id=\"ctl00_lgnView_cpMain_ctlAccBalance_grdAccounts\"]/tbody[1]/tr[3]/td[2]").textContent();
        System.out.println("Flex Balance: " + flexBalance);
        System.out.println("Dining Balance: " + diningBalance);
    } */
}