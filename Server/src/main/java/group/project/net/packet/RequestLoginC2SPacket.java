package group.project.net.packet;

import com.google.gson.JsonObject;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import group.project.data.Credentials;
import group.project.init.Caches;
import group.project.net.BrowserCache;
import group.project.net.Connection;
import group.project.net.Packet;

import java.io.IOException;

public class RequestLoginC2SPacket extends Packet {

    private String principal;
    private String password;

    public RequestLoginC2SPacket() {

    }

    @Override
    public void read(JsonObject json) {
        this.principal = json.get("principal").getAsString();
        this.password = json.get("password").getAsString();
    }

    @Override
    public void handle(Connection connection) throws IOException {
        BrowserCache cache = Caches.getOrInvalidate(this.principal, this.password);
        Page page = connection.launch(cache);
        page.navigate("https://uozone2.uottawa.ca/?language=en");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        if(!connection.getPage().url().equals("https://uozone2.uottawa.ca/?language=en")) {
            page.getByPlaceholder("someone@example.com").fill(this.principal);
            page.getByText("Next").click();

            while(true) {
                if(page.isVisible("#usernameError")) {
                    connection.send(new FailedLoginS2CPacket("This username may be incorrect."));
                    return;
                } else if(page.isVisible("#credentialList")) {
                    connection.send(new FailedLoginS2CPacket("Invalid email domain."));
                    return;
                } else if(page.isVisible("//input[@name=\"passwd\" and not(@class=\"moveOffScreen\")]")) {
                    break;
                }

                if(!this.trySleep(100)) {
                    connection.send(new FailedLoginS2CPacket("An unexpected error occurred."));
                    return;
                }
            }

            page.getByPlaceholder("Password").fill(this.password);
            page.getByText("Sign in").first().click();

            while(true) {
                if(page.isVisible("#passwordError")) {
                    connection.send(new FailedLoginS2CPacket("Your account or password is incorrect."));
                    return;
                } else if(page.isVisible("#idRichContext_DisplaySign")) {
                    break;
                }

                if(!this.trySleep(100)) {
                    connection.send(new FailedLoginS2CPacket("An unexpected error occurred."));
                    return;
                }
            }

            String mfaCode = page.locator("#idRichContext_DisplaySign").textContent();
            connection.send(new PromptMFAS2CPacket(Integer.parseInt(mfaCode.trim())));

            while(true) {
                if(page.isVisible("#idA_SAASTO_TOTP")) {
                    connection.send(new FailedLoginS2CPacket("2FA failed to authenticate."));
                    return;
                } else if(page.isVisible("#KmsiCheckboxField")) {
                    break;
                }

                if(!this.trySleep(100)) {
                    connection.send(new FailedLoginS2CPacket("An unexpected error occurred."));
                    return;
                }
            }

            page.locator("#KmsiCheckboxField").click();
            page.getByText("Yes").click();

            page.waitForURL("https://uozone2.uottawa.ca/?language=en");
            Caches.put(BrowserCache.of(Credentials.create(this.principal, this.password), page.context()));
            Caches.save(this.principal);
        }

        connection.send(new CompleteLoginS2CPacket());

        // We cheat by not requiring the client to send the request packets
        connection.handle(new RequestWalletC2SPacket());
        connection.handle(new RequestProgramC2SPacket());
    }

    protected boolean trySleep(long time) {
        try {
            Thread.sleep(time);
            return true;
        } catch(InterruptedException e) {
            return false;
        }
    }

}
