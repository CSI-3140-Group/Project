package group.project;

public class OldServer {


/*    public static void main(String[] args) throws IOException, URISyntaxException {
        try(Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
            options.setHeadless(false);

            Browser browser = playwright.chromium().launch(options);
            BrowserContext context = browser.newContext();
            context.setDefaultTimeout(Integer.MAX_VALUE);

            //Scripts.initialize(context);
            String[] credentials = Scripts.read("/credentials.creds").split("\n");
            Page page = context.newPage();


            userAuth(page, 1, credentials);
            JSONArray testCard = scrapeCard(page);
            // JSONArray testGrades = scrapeGrades(page);

            try (FileWriter file = new FileWriter("server_data/transactions.json")) {
                file.write(testCard.toString());
            //try (FileWriter file = new FileWriter("server_data/grades.json")) {
                //file.write(testGrades.toString());
                System.out.println("JSON data has been written to the file");
            } catch (IOException e) {
                e.printStackTrace();
            }

            page.waitForClose(() -> {});
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

    private static JSONArray scrapeGrades(Page page) {

        JSONArray data = new JSONArray();
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

            JSONObject currentTerm = new JSONObject();
            currentTerm.put("level", semesterSplit[4]);
            currentTerm.put("term", semesterSplit[1]);
            currentTerm.put("year", semesterSplit[0]);
            currentTerm.put("tgpa", possibleTGPAs.get(possibleTGPAs.size()-1));
            currentTerm.put("cgpa", possibleCGPAs.get(possibleCGPAs.size()-1));

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

                    JSONObject currentGrade = new JSONObject();

                    currentGrade.put("program", courseNameSplit[0]);
                    currentGrade.put("code", courseNameSplit[1]);
                    currentGrade.put("description", courseDescription);
                    currentGrade.put("units", courseUnits);
                    currentGrade.put("grading", gradingBase);
                    currentGrade.put("letter", letterGrade);
                    currentGrade.put("points", gradePoints);

                    currentTerm.put(courseName, currentGrade);
                } catch (Exception e) {
                    break;
                }
            }

            // Push Data to JSON and Switch Term
            data.put(currentTerm);
            page.locator("//*[@id=\"DERIVED_SSS_SCT_SSS_TERM_LINK\"]").click();
        }

        return data;
    }

    private static JSONArray scrapeCard(Page page) {
        JSONArray data = new JSONArray();

        String flexBalance = page.locator("//*[@id=\"ctl00_lgnView_cpMain_ctlAccBalance_grdAccounts\"]/tbody[1]/tr[2]/td[2]").textContent();
        String diningBalance = page.locator("//*[@id=\"ctl00_lgnView_cpMain_ctlAccBalance_grdAccounts\"]/tbody[1]/tr[3]/td[2]").textContent();
        //System.out.println("Flex Balance: " + flexBalance);
        //System.out.println("Dining Balance: " + diningBalance);

        JSONObject balances = new JSONObject();
        balances.put("flex",flexBalance);
        balances.put("dining",flexBalance);
        data.put(balances);

        Locator transactionLi = page.locator("//div[@id=\"ctl00_lgnView_menuDesktop\"]/ul/li").all().get(1);
        transactionLi.locator("//a").click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        //# of pages is wrong - to change
    *//*    int numberOfPages = page.locator("//tr[@class=\"pager grid-pager\"]/td/table/tbody/tr/td").all().size() - 2;
        for (int i = 1; i <= numberOfPages; i++) {

            if (i != 1) {
                Locator currentTd;
                if (i == 2) {
                    currentTd = page.locator("//tr[@class=\"pager grid-pager\"]/td/table/tbody/tr/td").all().get(i);
                } else {
                    currentTd = page.locator("//tr[@class=\"pager grid-pager\"]/td/table/tbody/tr/td").all().get(i - 1);
                }

                currentTd.locator("//a").click();
                page.waitForLoadState(LoadState.NETWORKIDLE);
            } *//*


            // Table Page Scraping
            for (Locator row : page.locator("//table[@id=\"ctl00_lgnView_cpMain_ctlAccHistory_grdHistory\"]/tbody/tr").all()) {
                String[] entries = row.locator("//td").all().stream()
                        .map(Locator::textContent).toArray(String[]::new);

                if (entries.length != 8) continue;

                String[] dateTime = entries[0].split(" ");
                JSONObject currentTransaction = new JSONObject();

                currentTransaction.put("date", dateTime[0]);
                currentTransaction.put("time", dateTime[1]);
                currentTransaction.put("withdrawal", entries[2].charAt(0) == '-' ? "N/A" : entries[2]);
                currentTransaction.put("deposit", entries[3].charAt(0) == '-' ? "N/A" : entries[3]);
                currentTransaction.put("balance", entries[4]);
                currentTransaction.put("description", entries[5]);

                data.put(currentTransaction);
            }
       // }

        return data;
    }

    *//* public static void navigateToGrades(Page page, String[] credentials) {
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
