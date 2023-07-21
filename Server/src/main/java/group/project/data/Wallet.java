package group.project.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Optional;

public class Wallet implements IJsonSerializable<JsonObject> {

    private String balance;
    private Transaction[] transactions;

    public Wallet() {

    }

    public Wallet(String balance, Transaction[] transactions) {
        this.balance = balance;
        this.transactions = transactions;
    }

    public static Wallet fromJson(JsonObject json) {
        Wallet wallet = new Wallet();
        wallet.read(json);
        return wallet;
    }

    @Override
    public Optional<JsonObject> write() {
        JsonObject json = new JsonObject();
        json.addProperty("balance", this.balance);
        JsonArray array = new JsonArray();

        for(Transaction transaction : this.transactions) {
            transaction.write().ifPresent(array::add);
        }

        json.add("transactions", array);
        return Optional.of(json);
    }

    @Override
    public void read(JsonObject json) {
        this.balance = json.get("balance").getAsString();
        JsonArray array = json.get("transactions").getAsJsonArray();
        this.transactions = new Transaction[array.size()];

        for(int i = 0; i < array.size(); i++) {
            Transaction transaction = new Transaction();
            transaction.read(array.get(i).getAsJsonObject());
            this.transactions[i] = transaction;
        }
    }

}
