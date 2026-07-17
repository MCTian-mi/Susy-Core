package io.github.symmetricdevs.supersymmetry.mixins.minecraft;

import net.minecraft.commands.CommandSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.symmetricdevs.supersymmetry.common.event.GravityHandler;

@Mixin(Entity.class)
public abstract class EntityMixin implements ICommandSender, ICapabilitySerializable<CompoundTag> {

    @Inject(method = "onUpdate", at = @At("TAIL"))
    public void applyGravity(CallbackInfo callback) {
        GravityHandler.applyGravity((Entity) (Object) this);
    }
}
