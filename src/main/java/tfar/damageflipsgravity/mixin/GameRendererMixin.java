package tfar.damageflipsgravity.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.gravity.Hooks;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final private Camera mainCamera;

    @Inject(method = "renderLevel",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/Camera;getXRot()F"))
    private void camSetup(float deltaTime, long l, PoseStack poseStack, CallbackInfo ci) {
        Hooks.onCameraSetup((GameRenderer)(Object)this,this.mainCamera,poseStack,deltaTime);
    }
}
