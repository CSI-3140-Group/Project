package group.project.data;

import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Random;

public class Credentials implements IJsonSerializable<JsonObject> {

    private static final String SALT_SPACE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    private String principal;
    private String passwordHash;
    private String passwordSalt;

    private Credentials() {

    }

    private Credentials(String principal, String passwordHash, String passwordSalt) {
        this.principal = principal;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
    }

    public static Credentials create(String principal, String password) {
        String salt = generateSalt(new Random(), 16);
        return new Credentials(principal, hash(password + salt), salt);
    }

    public static Credentials fromJson(JsonObject json) {
        if(json.has("password")) {
            return create(json.get("principal").getAsString(), json.get("password").getAsString());
        }

        Credentials credentials = new Credentials();
        credentials.read(json);
        return credentials;
    }

    public String getPrincipal() {
        return this.principal;
    }

    public boolean canAuthenticate(String principal, String password) {
        return this.principal.equals(principal)
                && this.passwordHash.equals(hash(password + this.passwordSalt));
    }

    @Override
    public Optional<JsonObject> write() {
        JsonObject json = new JsonObject();
        json.addProperty("principal", this.principal);
        json.addProperty("passwordHash", this.passwordHash);
        json.addProperty("passwordSalt", this.passwordSalt);
        return Optional.of(json);
    }

    @Override
    public void read(JsonObject json) {
        this.principal = json.get("principal").getAsString();
        this.passwordHash = json.get("passwordHash").getAsString();
        this.passwordSalt = json.get("passwordSalt").getAsString();
    }

    private static String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return new String(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return password;
    }

    private static String generateSalt(Random random, int size) {
        StringBuilder salt = new StringBuilder();

        for(int i = 0; i < size; i++) {
            salt.append(SALT_SPACE.charAt(random.nextInt(SALT_SPACE.length())));
        }

        return salt.toString();
    }

}
