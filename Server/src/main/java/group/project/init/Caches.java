package group.project.init;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import group.project.net.BrowserCache;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Caches {

    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    private static final Map<String, BrowserCache> REGISTRY = new ConcurrentHashMap<>();

    public static void initialize() throws IOException {
        new File("./cache").mkdirs();
        System.out.println(new File("./cache").getAbsolutePath());

        for(Path path : Files.walk(Path.of("./cache")).toList()) {
            if(!path.toFile().isFile()) continue;
            JsonElement json = JsonParser.parseReader(new FileReader(path.toFile()));
            put(BrowserCache.of(json.getAsJsonObject()));
        }
    }

    public static void save(String principal) throws IOException {
        BrowserCache cache = REGISTRY.get(principal);
        Path path = Path.of("./cache", principal + ".json");

        if(cache == null) {
            Files.deleteIfExists(path);
            return;
        }

        Optional<JsonObject> opt = cache.write();

        if(opt.isPresent()) {
            Files.writeString(path, GSON.toJson(opt.get()));
        } else {
            Files.deleteIfExists(path);
        }
    }

    public static void put(BrowserCache cookie) {
        REGISTRY.put(cookie.getCredentials().getPrincipal(), cookie);
    }

    public static BrowserCache getOrInvalidate(String principal, String password) throws IOException {
        BrowserCache cookie = REGISTRY.get(principal);

        if(cookie != null && !cookie.getCredentials().canAuthenticate(principal, password)) {
            REGISTRY.remove(principal);
            save(principal);
            return null;
        }

        return cookie;
    }

}
