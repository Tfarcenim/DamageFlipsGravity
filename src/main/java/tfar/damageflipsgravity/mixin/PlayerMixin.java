package tfar.damageflipsgravity.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.damageflipsgravity.DamageFlipsGravity;
import tfar.gravity.ducks.PlayerDuck;

@Mixin(Player.class)
abstract class PlayerMixin extends LivingEntityMixin implements PlayerDuck {

    @Shadow public abstract EntityDimensions getDimensions(Pose pose);

    @Shadow public abstract float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions);

    private static final EntityDataAccessor<Boolean> DATA_UP = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TIMER = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    public PlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectCap(Level level, BlockPos blockPos, float f, GameProfile gameProfile, CallbackInfo ci) {
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    private void flipGravity(DamageSource damageSource, float f, CallbackInfo ci) {
        reverseGravity();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickTimer(CallbackInfo ci) {
        if (!this.level.isClientSide) {
            if (getTimer() > 0) {
                setTimer(getTimer() - 1);
            }
        }
    }

    @Override
    public void reverseGravity() {
        setGravity(!getGravity());
        AttributeInstance gravity = this.getAttribute(DamageFlipsGravity.ENTITY_GRAVITY);
        if (!gravity.hasModifier(DamageFlipsGravity.GRAVITY_FLIP)) {
            gravity.addPermanentModifier(DamageFlipsGravity.GRAVITY_FLIP);
        } else {
            gravity.removeModifier(DamageFlipsGravity.GRAVITY_FLIP);
        }
        setTimer(15);
    }

    @Inject(method = "updatePlayerPose",at = @At("RETURN"))
    private void fixEyeHeight(CallbackInfo ci) {
        ((EntityAccess)this).setEyeHeight(getStandingEyeHeight(getPose(),this.getDimensions(getPose())));
    }

    @Inject(method = "getStandingEyeHeight",at = @At("RETURN"),cancellable = true)
    private void upsideDownEyes(Pose pose, EntityDimensions entityDimensions, CallbackInfoReturnable<Float> cir) {
        if (((PlayerDuck)this).getGravity()) {
            float height = cir.getReturnValue();
            cir.setReturnValue(entityDimensions.height - height);
        }
    }

    @Override
    public boolean getGravity() {
        return entityData.get(DATA_UP);
    }

    @Override
    public void setGravity(boolean b) {
        entityData.set(DATA_UP, b);
    }

    @Override
    public int getTimer() {
        return entityData.get(DATA_TIMER);
    }

    @Override
    public void setTimer(int timer) {
        entityData.set(DATA_TIMER, timer);
    }

    @Inject(method = "defineSynchedData", at = @At("HEAD"))
    private void data(CallbackInfo ci) {
        this.entityData.define(DATA_UP, false);
        this.entityData.define(DATA_TIMER, 0);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readEx(CompoundTag compoundTag, CallbackInfo ci) {
        setGravity(compoundTag.getBoolean("gravity"));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void writeEx(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putBoolean("gravity", getGravity());
    }
}
