package group.project.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Optional;

public class Program implements IJsonSerializable<JsonObject> {

    public Semester[] semesters;

    public Program() {

    }

    public Program(Semester[] semesters) {
        this.semesters = semesters;
    }

    public static Program fromJson(JsonObject json) {
        Program program = new Program();
        program.read(json);
        return program;
    }

    @Override
    public Optional<JsonObject> write() {
        JsonObject json = new JsonObject();
        JsonArray list = new JsonArray();

        for(Semester semester : this.semesters) {
            semester.write().ifPresent(list::add);
        }

        json.add("semesters", list);
        return Optional.of(json);
    }

    @Override
    public void read(JsonObject json) {
        JsonArray array = json.get("semesters").getAsJsonArray();
        this.semesters = new Semester[array.size()];

        for(int i = 0; i < array.size(); i++) {
            Semester semester = new Semester();
            semester.read(array.get(i).getAsJsonObject());
            this.semesters[i] = semester;
        }
    }

}
