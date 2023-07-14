package group.project.data;

import com.google.gson.JsonElement;

import java.util.Optional;

public interface IJsonSerializable<J extends JsonElement> {

    Optional<J> write();

    void read(J json);

}
