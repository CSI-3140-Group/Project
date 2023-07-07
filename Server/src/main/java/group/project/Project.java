package group.project;

import com.microsoft.playwright.*;
import group.project.init.Scripts;

import java.io.IOException;
import java.net.URISyntaxException;

public class Project {

    public static void main(String[] args) throws IOException, URISyntaxException {
        try(Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
            options.setHeadless(false);

            Browser browser = playwright.chromium().launch(options);
            BrowserContext context = browser.newContext();

            //Scripts.initialize(context);
            String[] credentials = Scripts.read("/credentials.creds").split("\n");
            Page page = context.newPage();
            navigateToCard(page, credentials);
            page.waitForClose(() -> {});
        }
    }

    public static void navigateToGrades(Page page, String[] credentials) {
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
    }

}
