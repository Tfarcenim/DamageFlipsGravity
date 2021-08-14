package tfar.gravity.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import tfar.damageflipsgravity.DamageFlipsGravity;

public class PacketHandler {

    public static final ResourceLocation sync_eyes = new ResourceLocation(DamageFlipsGravity.MODID, "sync_data");

    public static void sendFixEyes(ServerPlayer player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ServerPlayNetworking.send(player, PacketHandler.sync_eyes, buf);
    }

}
