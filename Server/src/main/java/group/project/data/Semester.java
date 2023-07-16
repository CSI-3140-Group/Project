package group.project.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Optional;

public class Semester implements IJsonSerializable<JsonObject> {

    public String level;
    public String term;
    public String year;
    public String tgpa;
    public String cgpa;
    public Course[] courses;

    public Semester() {

    }

    public Semester(String level, String term, String year, String tgpa, String cgpa, Course[] courses) {
        this.level = level;
        this.term = term;
        this.year = year;
        this.tgpa = tgpa;
        this.cgpa = cgpa;
        this.courses = courses;
    }

    @Override
    public Optional<JsonObject> write() {
        JsonObject json = new JsonObject();
        json.addProperty("level", this.level);
        json.addProperty("term", this.term);
        json.addProperty("year", this.year);
        json.addProperty("tgpa", this.tgpa);
        json.addProperty("cgpa", this.cgpa);

        JsonArray array = new JsonArray();

        for(Course course : this.courses) {
            course.write().ifPresent(array::add);
        }

        json.add("courses", array);
        return Optional.of(json);
    }

    @Override
    public void read(JsonObject json) {
        this.level = json.get("level").getAsString();
        this.term = json.get("term").getAsString();
        this.year = json.get("year").getAsString();
        this.tgpa = json.get("tgpa").getAsString();
        this.cgpa = json.get("cgpa").getAsString();

        JsonArray array = json.get("courses").getAsJsonArray();
        this.courses = new Course[array.size()];

        for(int i = 0; i < array.size(); i++) {
            Course course = new Course();
            course.read(array.get(i).getAsJsonObject());
            this.courses[i] = course;
        }
    }

}
