package io.github.symmetricdevs.supersymmetry.mixins.minecraft;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;

import io.github.symmetricdevs.supersymmetry.api.util.ElytraFlyingUtils;

@Mixin(Player.class)
public abstract class EntityPlayerMixin extends LivingEntity {

    public EntityPlayerMixin(World worldIn) {
        super(worldIn);
    }

    @Override
    public boolean isElytraFlying() {
        boolean isElytraFlying = super.isElytraFlying() || ElytraFlyingUtils.isElytraFlying(this);
        this.setFlag(7, isElytraFlying);
        return isElytraFlying;
    }
}
