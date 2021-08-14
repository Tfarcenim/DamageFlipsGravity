package tfar.damageflipsgravity.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.gravity.ducks.PlayerDuck;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow protected boolean onGround;

    @Shadow public boolean verticalCollision;

    @Shadow public abstract EntityDimensions getDimensions(Pose pose);

    @Shadow public abstract double getX();

    @Shadow public abstract double getY();

    @Shadow public abstract double getZ();

    @Shadow private float eyeHeight;

    @Shadow public abstract Pose getPose();

    @Inject(method = "move",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/entity/Entity;getOnPos()Lnet/minecraft/core/BlockPos;"))
    private void ticks(MoverType moverType, Vec3 vec3, CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof Player player) {
            if (((PlayerDuck)player).getGravity()) {
                this.onGround = this.verticalCollision && vec3.y > 0;
            }
        }
    }

    @Inject(method = "refreshDimensions",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/entity/Entity;reapplyPosition()V"))
    private void sa(CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof Player player) {
            if (((PlayerDuck) player).getGravity()) {
                this.eyeHeight = getDimensions(getPose()).height - this.eyeHeight;
            }
        }
    }
}
