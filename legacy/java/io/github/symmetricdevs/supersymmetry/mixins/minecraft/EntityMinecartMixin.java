package io.github.symmetricdevs.supersymmetry.mixins.minecraft;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.symmetricdevs.supersymmetry.common.event.GravityHandler;

@Mixin(EntityMinecart.class)
public abstract class EntityMinecartMixin extends Entity {

    public EntityMinecartMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    public void applyGravity(CallbackInfo callback) {
        GravityHandler.applyGravity(this);
    }
}
