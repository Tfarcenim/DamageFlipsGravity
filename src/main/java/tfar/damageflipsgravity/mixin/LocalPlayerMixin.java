package tfar.damageflipsgravity.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.gravity.ducks.PlayerDuck;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Shadow public Input input;

    @Shadow @Final protected Minecraft minecraft;

    @Inject(method = "aiStep",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/Minecraft;getTutorial()Lnet/minecraft/client/tutorial/Tutorial;"))
    private void adjustControls(CallbackInfo ci) {
        if (minecraft.player != null && ((PlayerDuck)minecraft.player).getGravity()) {
            input.leftImpulse = - input.leftImpulse;
        }
    }
}
