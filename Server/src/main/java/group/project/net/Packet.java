package group.project.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import group.project.data.IJsonSerializable;
import group.project.init.Packets;

import java.io.IOException;
import java.util.Optional;

public abstract class Packet implements IJsonSerializable<JsonObject> {

    private static final Gson GSON = new Gson().newBuilder().create();

    @Override
    public Optional<JsonObject> write() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void read(JsonObject json) {
        throw new UnsupportedOperationException();
    }

    public void handle(Connection connection) throws IOException {
        throw new UnsupportedOperationException();
    }

    public final Optional<String> serialize() {
        return this.write().map(json -> {
            json.addProperty("id", Packets.getId(this.getClass()));
            return GSON.toJson(json);
        });
    }

    public final void deserialize(String json) {
        this.read(JsonParser.parseString(json).getAsJsonObject());
    }

}
