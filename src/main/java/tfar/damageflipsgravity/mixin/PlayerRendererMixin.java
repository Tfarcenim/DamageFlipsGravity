package tfar.damageflipsgravity.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.gravity.Hooks;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Inject(method = "render",at = @At("HEAD"))
    private void onRenderPlayerPre(AbstractClientPlayer abstractClientPlayer, float yaw, float deltaTime, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        Hooks.onRenderPlayerPre(abstractClientPlayer, yaw, deltaTime, poseStack, multiBufferSource, i);
    }

    @Inject(method = "render",at = @At("RETURN"))
    private void onRenderPlayerPost(AbstractClientPlayer abstractClientPlayer, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        Hooks.onRenderPlayerPost(abstractClientPlayer, f, g, poseStack, multiBufferSource, i);
    }
}
