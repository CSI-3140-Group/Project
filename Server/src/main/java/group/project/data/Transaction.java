package group.project.data;

import com.google.gson.JsonObject;

import java.util.Optional;

public class Transaction implements IJsonSerializable<JsonObject> {

    public String date;
    public String time;
    public String withdrawal;
    public String deposit;
    public String balance;
    public String description;

    public Transaction() {

    }

    public Transaction(String date, String time, String withdrawal, String deposit, String balance, String description) {
        this.date = date;
        this.time = time;
        this.withdrawal = withdrawal;
        this.deposit = deposit;
        this.balance = balance;
        this.description = description;
    }

    @Override
    public Optional<JsonObject> write() {
        JsonObject json = new JsonObject();
        json.addProperty("date", this.date);
        json.addProperty("time", this.time);
        json.addProperty("withdrawal", this.withdrawal);
        json.addProperty("deposit", this.deposit);
        json.addProperty("balance", this.balance);
        json.addProperty("description", this.description);
        return Optional.of(json);
    }

    @Override
    public void read(JsonObject json) {
        this.date = json.get("date").getAsString();
        this.time = json.get("time").getAsString();
        this.withdrawal = json.get("withdrawal").getAsString();
        this.deposit = json.get("deposit").getAsString();
        this.balance = json.get("balance").getAsString();
        this.description = json.get("description").getAsString();
    }

}
