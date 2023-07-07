package group.project.init;

import com.microsoft.playwright.*;
import group.project.Project;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Scripts {

    public static void initialize(BrowserContext context) throws URISyntaxException, IOException {
        context.setExtraHTTPHeaders(Map.of(
                "webgl_vendor", "Intel Inc.",
                "webgl_renderer", "Intel Iris OpenGL Engine",
                "navigator_vendor", "Google Inc.",
                "navigator_platform", "null",
                "navigator_userAgent", "null",
                "languages", "en-US,en;q=0.9",
                "runOnInsecureOrigins", "null"
        ));

        context.addInitScript(read("/init.js"));
        context.addInitScript(read("/utils.js"));
        context.addInitScript(read("/generate.magic.arrays.js"));
        context.addInitScript(read("/navigator.hardwareConcurrency.js"));

        context.addInitScript(read("/chrome.app.js"));
        context.addInitScript(read("/chrome.csi.js"));
        context.addInitScript(read("/chrome.hairline.js"));
        context.addInitScript(read("/chrome.load.times.js"));
        context.addInitScript(read("/chrome.runtime.js"));
        context.addInitScript(read("/iframe.contentWindow.js"));
        context.addInitScript(read("/media.codecs.js"));
        context.addInitScript(read("/navigator.languages.js"));
        context.addInitScript(read("/navigator.permissions.js"));
        context.addInitScript(read("/navigator.platform.js"));
        context.addInitScript(read("/navigator.plugins.js"));
        context.addInitScript(read("/navigator.vendor.js"));
        context.addInitScript(read("/sourceurl.js"));
        context.addInitScript(read("/webgl.vendor.js"));
        context.addInitScript(read("/window.outerdimensions.js"));
        context.addInitScript(read("/webdriver.js"));
        context.addInitScript(read("/navigator.userAgent.js"));
        context.setDefaultTimeout(Integer.MAX_VALUE);
    }

    public static String read(String path) throws URISyntaxException, IOException {
        return Files.readString(Path.of(Project.class.getResource(path).toURI()));
    }

}
