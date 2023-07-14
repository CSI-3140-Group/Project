package group.project.net.packet;

import com.google.gson.JsonObject;
import group.project.net.Packet;

import java.util.Optional;

public class PromptMFAS2CPacket extends Packet {

    public int code;

    public PromptMFAS2CPacket() {

    }

    public PromptMFAS2CPacket(int code) {
        this.code = code;
    }

    @Override
    public Optional<JsonObject> write() {
        JsonObject json = new JsonObject();
        json.addProperty("code", this.code);
        return Optional.of(json);
    }

}
