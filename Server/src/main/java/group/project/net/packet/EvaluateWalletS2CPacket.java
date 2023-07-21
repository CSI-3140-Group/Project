package group.project.net.packet;

import com.google.gson.JsonObject;
import com.microsoft.playwright.options.ScreenshotType;
import group.project.data.Wallet;
import group.project.net.Packet;

import java.util.Optional;

public class EvaluateWalletS2CPacket extends Packet {

    private Wallet wallet;

    public EvaluateWalletS2CPacket() {

    }

    public EvaluateWalletS2CPacket(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public Optional<JsonObject> write() {
        JsonObject json = new JsonObject();
        this.wallet.write().ifPresent(tag -> json.add("wallet", tag));
        return Optional.of(json);
    }

}
