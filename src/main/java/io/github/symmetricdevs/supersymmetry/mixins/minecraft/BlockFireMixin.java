package io.github.symmetricdevs.supersymmetry.mixins.minecraft;

import net.minecraft.world.level.block.BlockFire;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.symmetricdevs.supersymmetry.common.world.WorldProviderPlanet;

@Mixin(BlockFire.class)
public class BlockFireMixin {

    @Inject(method = "onBlockAdded", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void blockFire(World worldIn, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (worldIn.provider instanceof WorldProviderPlanet provider && !provider.getPlanet().supportsFire) {
            worldIn.setBlockToAir(pos);
        }
    }
}
