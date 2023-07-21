package group.project.net.packet;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import group.project.data.Transaction;
import group.project.data.Wallet;
import group.project.net.Connection;
import group.project.net.Packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RequestWalletC2SPacket extends Packet {

    @Override
    public void handle(Connection connection) throws IOException {
        Page page = connection.getPage();
        page.navigate("https://carteuottawacard.uottawa.ca/login.aspx");
        page.getByText("Students").all().get(1).click();

        String balance = page.locator("//*[@id=\"ctl00_lgnView_cpMain_ctlAccBalance_grdAccounts\"]/tbody[1]/tr[2]/td[2]").textContent();
        List<Transaction> transactions = new ArrayList<>();

        Locator transactionLi = page.locator("//div[@id=\"ctl00_lgnView_menuDesktop\"]/ul/li").all().get(1);
        transactionLi.locator("//a").click();
        page.waitForSelector("//div[@id=\"ctl00_lgnView_menuDesktop\"]/ul/li[position()=1]/a");

        int numberOfPages = page.locator("//tr[@class=\"pager grid-pager\"]/td/table/tbody/tr/td").all().size() / 2;
        for (int i = 1; i <= numberOfPages; i++) {

            if (i != 1) {
                Locator currentTd;
                String previousA;

                if (i == 2) {
                    currentTd = page.locator("//tr[@class=\"pager grid-pager\"]/td/table/tbody/tr/td").all().get(i);
                    previousA = "//tr[@class='pager grid-pager']/td/table/tbody/tr/td[position()=" + (i - 1) + "]/a";
                } else {
                    currentTd = page.locator("//tr[@class=\"pager grid-pager\"]/td/table/tbody/tr/td").all().get(i - 1);
                    previousA = "//tr[@class='pager grid-pager']/td/table/tbody/tr/td[position()=" + (i - 2) + "]/a";
                }

                currentTd.locator("//a").click();
                page.waitForSelector(previousA);
            }

            for (Locator row : page.locator("//table[@id=\"ctl00_lgnView_cpMain_ctlAccHistory_grdHistory\"]/tbody/tr").all()) {
                String[] entries = row.locator("//td").all().stream()
                        .map(Locator::textContent).toArray(String[]::new);

                if (entries.length != 8) continue;
                String[] dateTime = entries[0].split(Pattern.quote(" "));

                transactions.add(new Transaction(
                        dateTime[0], dateTime[1],
                        entries[2].charAt(0) == '-' ? "N/A" : entries[2],
                        entries[3].charAt(0) == '-' ? "N/A" : entries[3],
                        entries[4], entries[5]));
            }
        }

        Wallet wallet = new Wallet(balance, transactions.toArray(new Transaction[0]));
        connection.send(new EvaluateWalletS2CPacket(wallet));
    }

}
