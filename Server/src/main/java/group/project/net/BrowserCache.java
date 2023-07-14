package group.project.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.SameSiteAttribute;
import group.project.data.Credentials;
import group.project.data.IJsonSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrowserCache implements IJsonSerializable<JsonObject> {

    private Credentials credentials;
    private List<Cookie> cookies;

    protected BrowserCache() {

    }

    protected BrowserCache(Credentials credentials, List<Cookie> cookies) {
        this.credentials = credentials;
        this.cookies = cookies;
    }

    public static BrowserCache of(JsonObject json) {
        BrowserCache cache = new BrowserCache();
        cache.read(json);
        return cache;
    }

    public static BrowserCache of(Credentials credentials, BrowserContext context) {
        return new BrowserCache(credentials, new ArrayList<>(context.cookies()));
    }

    public Credentials getCredentials() {
        return this.credentials;
    }

    public List<Cookie> getCookies() {
        return this.cookies;
    }

    @Override
    public Optional<JsonObject> write() {
        JsonObject json = new JsonObject();

        if(this.credentials != null) {
            this.credentials.write().ifPresent(tag -> json.add("credentials", tag));
        }

        JsonArray cookies = new JsonArray();
        this.cookies.forEach(cookie -> cookies.add(writeCookie(cookie)));
        json.add("cookies", cookies);
        return Optional.of(json);
    }

    @Override
    public void read(JsonObject json) {
        if(json.has("credentials")) {
            this.credentials = Credentials.fromJson(json.getAsJsonObject("credentials"));
        }

        if(json.has("cookies")) {
            JsonArray array = json.getAsJsonArray("cookies");
            this.cookies = new ArrayList<>();
            array.forEach(cookie -> this.cookies.add(readCookie(cookie.getAsJsonObject())));
        }
    }

    public static JsonObject writeCookie(Cookie cookie) {
        JsonObject json = new JsonObject();
        if(cookie.name != null) json.addProperty("name", cookie.name);
        if(cookie.value != null) json.addProperty("value", cookie.value);
        if(cookie.url != null) json.addProperty("url", cookie.url);
        if(cookie.domain != null) json.addProperty("domain", cookie.domain);
        if(cookie.path != null) json.addProperty("path", cookie.path);
        if(cookie.expires != null) json.addProperty("expires", cookie.expires);
        if(cookie.httpOnly != null) json.addProperty("httpOnly", cookie.httpOnly);
        if(cookie.secure != null) json.addProperty("secure", cookie.secure);
        if(cookie.sameSite != null) json.addProperty("sameSite", cookie.sameSite.name());
        return json;
    }

    public static Cookie readCookie(JsonObject json) {
        Cookie cookie = new Cookie(null, null);
        if(json.has("name")) cookie.name = json.get("name").getAsString();
        if(json.has("value")) cookie.value = json.get("value").getAsString();
        if(json.has("url")) cookie.url = json.get("url").getAsString();
        if(json.has("domain")) cookie.domain = json.get("domain").getAsString();
        if(json.has("path")) cookie.path = json.get("path").getAsString();
        if(json.has("expires")) cookie.expires = json.get("expires").getAsDouble();
        if(json.has("httpOnly")) cookie.httpOnly = json.get("httpOnly").getAsBoolean();
        if(json.has("secure")) cookie.secure = json.get("secure").getAsBoolean();
        if(json.has("sameSite")) cookie.sameSite = Enum.valueOf(SameSiteAttribute.class,
                json.get("sameSite").getAsString());
        return cookie;
    }

}
