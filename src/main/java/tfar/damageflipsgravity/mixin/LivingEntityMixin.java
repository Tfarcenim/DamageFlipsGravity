package tfar.damageflipsgravity.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.damageflipsgravity.DamageFlipsGravity;
import tfar.gravity.ducks.PlayerDuck;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    private static final UUID SLOW_FALLING_ID = UUID.fromString("A5B6CF2A-2F7C-31EF-9022-7C3E7D5E6ABA");

    private static final AttributeModifier SLOW_FALLING = new AttributeModifier(SLOW_FALLING_ID, "Slow falling acceleration reduction", -0.07, AttributeModifier.Operation.ADDITION); // Add -0.07 to 0.08 so we get the vanilla default of 0.01

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public @Nullable abstract AttributeInstance getAttribute(Attribute attribute);

    @Shadow public abstract boolean hasEffect(MobEffect mobEffect);

    @Shadow public abstract boolean isFallFlying();

    @Shadow public abstract boolean isSleeping();

    @Shadow public abstract boolean isAutoSpinAttack();

    @Inject(method = "createLivingAttributes",at = @At("RETURN"))
    private static void injectGravity(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.getReturnValue().add(DamageFlipsGravity.ENTITY_GRAVITY);
    }


    @ModifyVariable(method = "travel",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"))
    private double setGravity(double oldGravity) {
        AttributeInstance gravity = this.getAttribute(DamageFlipsGravity.ENTITY_GRAVITY);
        boolean flag = this.getDeltaMovement().y <= 0.0D;
        if (flag && this.hasEffect(MobEffects.SLOW_FALLING)) {
            if (!gravity.hasModifier(SLOW_FALLING)) gravity.addTransientModifier(SLOW_FALLING);
            this.fallDistance = 0.0F;
        } else if (gravity.hasModifier(SLOW_FALLING)) {
            gravity.removeModifier(SLOW_FALLING);
        }
        double d = gravity.getValue();
        return d;
    }

    @ModifyVariable(method = "jumpFromGround",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/entity/LivingEntity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;",ordinal = 0))
    private double redirectJump(double jump) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        if (livingEntity instanceof Player player) {
            if (((PlayerDuck)player).getGravity()) {
                return -jump;
            }
        }
        return jump;
    }
}
