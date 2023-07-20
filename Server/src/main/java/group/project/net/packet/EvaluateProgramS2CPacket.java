package group.project.net.packet;

import com.google.gson.JsonObject;
import group.project.data.Program;
import group.project.net.Packet;

import java.util.Optional;

public class EvaluateProgramS2CPacket extends Packet {

    private Program program;

    public EvaluateProgramS2CPacket() {

    }

    public EvaluateProgramS2CPacket(Program program) {
        this.program = program;
    }

    @Override
    public Optional<JsonObject> write() {
        JsonObject json = new JsonObject();
        this.program.write().ifPresent(tag -> json.add("program", tag));
        System.out.println(json);
        return Optional.of(json);
    }

}
