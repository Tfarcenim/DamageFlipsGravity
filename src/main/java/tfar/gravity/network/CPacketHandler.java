package tfar.gravity.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import tfar.gravity.network.client.S2CRecalculateEyeHeight;

public class CPacketHandler {

    public static void registerClientMessages() {
        ClientPlayNetworking.registerGlobalReceiver(PacketHandler.sync_eyes, new S2CRecalculateEyeHeight());
    }
}
