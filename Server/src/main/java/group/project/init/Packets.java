package group.project.init;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import group.project.net.Packet;
import group.project.net.packet.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Packets {

    private static final Map<String, Supplier<? extends Packet>> ID_TO_PACKET = new HashMap<>();
    private static final Map<Class<? extends Packet>, String> PACKET_TO_ID = new HashMap<>();

    public static void initialize() {
        register("request_login", RequestLoginC2SPacket.class, RequestLoginC2SPacket::new);
        register("complete_login", CompleteLoginS2CPacket.class, CompleteLoginS2CPacket::new);
        register("failed_login", FailedLoginS2CPacket.class, FailedLoginS2CPacket::new);
        register("prompt_mfa", PromptMFAS2CPacket.class, PromptMFAS2CPacket::new);
        register("request_program", RequestProgramC2SPacket.class, RequestProgramC2SPacket::new);
        register("evaluate_program", EvaluateProgramS2CPacket.class, EvaluateProgramS2CPacket::new);
        register("request_wallet", RequestWalletC2SPacket.class, RequestWalletC2SPacket::new);
        register("evaluate_wallet", EvaluateWalletS2CPacket.class, EvaluateWalletS2CPacket::new);
    }

    public static <P extends Packet> void register(String id, Class<P> type, Supplier<P> packet) {
        ID_TO_PACKET.put(id, packet);
        PACKET_TO_ID.put(type, id);
    }

    public static <P extends Packet> P read(String message) {
        JsonElement json = JsonParser.parseString(message);
        Packet packet = ID_TO_PACKET.get(json.getAsJsonObject().get("id").getAsString()).get();
        packet.deserialize(message);
        return (P)packet;
    }

    public static String getId(Class<? extends Packet> type) {
        return PACKET_TO_ID.get(type);
    }

}
