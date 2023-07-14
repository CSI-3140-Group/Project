package group.project.data;

import com.google.gson.JsonObject;

import java.util.Optional;

public class Course implements IJsonSerializable<JsonObject> {

    public String faculty;
    public String code;
    public String description;
    public float units;
    public String grading;
    public String letter;
    public float points;

    public Course() {

    }

    public Course(String faculty, String code, String description, float units, String grading, String letter, float points) {
        this.faculty = faculty;
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
        object.addProperty("faculty", this.faculty);
        object.addProperty("code", this.code);
        object.addProperty("description", this.description);
        object.addProperty("units", this.units);
        object.addProperty("grading", this.grading);
        if(!this.letter.isEmpty()) object.addProperty("letter", this.letter);
        if(this.points >= 0.0F) object.addProperty("points", this.points);
        return Optional.of(object);
    }

    @Override
    public void read(JsonObject json) {
        this.faculty = json.get("faculty").getAsString();
        this.code = json.get("code").getAsString();
        this.description = json.get("description").getAsString();
        this.units = json.get("units").getAsFloat();
        this.grading = json.get("grading").getAsString();
        this.letter = json.has("letter") ? json.get("letter").getAsString() : "";
        this.points = json.has("points") ? json.get("points").getAsFloat() : -1.0F;
    }

}
