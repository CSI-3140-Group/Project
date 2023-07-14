package group.project.net.packet;

import com.google.gson.JsonObject;
import group.project.net.Packet;

import java.util.Optional;

public class CompleteLoginS2CPacket extends Packet {

    public CompleteLoginS2CPacket() {

    }

    @Override
    public Optional<JsonObject> write() {
        return Optional.of(new JsonObject());
    }

}
