package group.project.net.packet;

import com.google.gson.JsonObject;
import group.project.net.Packet;

import java.util.Optional;

public class FailedLoginS2CPacket extends Packet {

    private String reason;

    public FailedLoginS2CPacket() {

    }

    public FailedLoginS2CPacket(String reason) {
        this.reason = reason;
    }

    @Override
    public Optional<JsonObject> write() {
        JsonObject json = new JsonObject();
        json.addProperty("reason", reason);
        return Optional.of(json);
    }

}
