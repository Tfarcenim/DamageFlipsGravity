package tfar.damageflipsgravity.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.gravity.ducks.PlayerDuck;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow private double accumulatedDX;

    @Shadow private double accumulatedDY;

    @Inject(method = "turnPlayer",at = @At("HEAD"))
    private void flipControls(CallbackInfo ci) {
        if (minecraft.player != null && ((PlayerDuck)minecraft.player).getGravity()) {
            accumulatedDX = - accumulatedDX;
            accumulatedDY = - accumulatedDY;
        }
    }
}
