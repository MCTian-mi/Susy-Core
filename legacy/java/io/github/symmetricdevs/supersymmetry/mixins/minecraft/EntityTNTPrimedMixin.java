package io.github.symmetricdevs.supersymmetry.mixins.minecraft;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.symmetricdevs.supersymmetry.common.event.GravityHandler;

@Mixin(EntityTNTPrimed.class)
public abstract class EntityTNTPrimedMixin extends Entity {

    public EntityTNTPrimedMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    public void applyGravity(CallbackInfo callback) {
        GravityHandler.applyGravity(this);
    }
}
