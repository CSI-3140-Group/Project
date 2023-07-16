package group.project.data;

import com.google.gson.JsonObject;

import java.util.Optional;

public class Course implements IJsonSerializable<JsonObject> {

    public String program;
    public String code;
    public String description;
    public String units;
    public String grading;
    public String letter;
    public String points;

    public Course() {

    }

    public Course(String program, String code, String description, String units, String grading, String letter, String points) {
        this.program = program;
        this.code = code;
        this.description = description;
        this.units = units;
        this.grading = grading;
        this.letter = letter;
        this.points = points;
    }

    @Override
    public Optional<JsonObject> write() {
        JsonObject object = new JsonObject();
        object.addProperty("program", this.program);
        object.addProperty("code", this.code);
        object.addProperty("description", this.description);
        object.addProperty("units", this.units);
        object.addProperty("grading", this.grading);
        object.addProperty("letter", this.letter);
        object.addProperty("points", this.points);
        return Optional.of(object);
    }

    @Override
    public void read(JsonObject json) {
        this.program = json.get("program").getAsString();
        this.code = json.get("code").getAsString();
        this.description = json.get("description").getAsString();
        this.units = json.get("units").getAsString();
        this.grading = json.get("grading").getAsString();
        this.letter = json.get("letter").getAsString();
        this.points =  json.get("points").getAsString();
    }

}
