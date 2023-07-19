package group.project.net;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import group.project.data.Credentials;
import group.project.init.Packets;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

import java.io.IOException;
import java.util.Optional;

public class Connection {

    private final Session session;
    private Credentials credentials;

    private Playwright playwright;
    private Page page;

    private Connection(Session session) {
        this.session = session;
    }

    public static Connection of(Session session) {
        Connection connection = (Connection)session.getUserProperties().get("connection");
        if(connection != null) return connection;
        connection = new Connection(session);
        session.getUserProperties().put("connection", connection);
        return connection;
    }

    public Session getSession() {
        return this.session;
    }

    public Credentials getCredentials() {
        return this.credentials;
    }

    public Page getPage() {
        return this.page;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public void send(Packet packet) throws IOException {
        Optional<String> opt = packet.serialize();

        if(opt.isPresent()) {
            this.session.getAsyncRemote().sendText(opt.get());
            System.out.println(opt.get());
        }
    }

    public void handle(String message) throws IOException {
        this.handle(Packets.read(message));
    }

    public void handle(Packet packet) throws IOException {
        packet.handle(this);
    }

    public Page launch(BrowserCache cache) {
        this.close(null);
        this.playwright = Playwright.create();
        Browser browser = this.playwright.chromium().launch(new LaunchOptions().setHeadless(true));
        BrowserContext context = browser.newContext();
        context.setDefaultTimeout(Integer.MAX_VALUE);
        if(cache != null) context.addCookies(cache.getCookies());
        return this.page = context.newPage();
    }

    public void close(CloseReason reason) {
        if(this.page != null) {
            this.page.close();
        }

        if(this.playwright != null) {
            this.playwright.close();
        }
    }

}
