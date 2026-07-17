package io.github.symmetricdevs.supersymmetry.mixins.reccomplex;

import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.reccomplex.temp.RCPosTransformer;

@Mixin(value = RCPosTransformer.class, remap = false)
public class RCPosTransformerMixin {

    // Adds NBT to spawned TileEntities that prevents them from being cheesed with RefinedTools storage scanners
    @Inject(method = "transformAdditionalData", at = @At("HEAD"))
    private static void transformAdditionalData(BlockEntity BlockEntity, AxisAlignedTransform2D transform, int[] size,
                                                CallbackInfo ci) {
        BlockEntity.getTileData().setBoolean("StorageScannerBlacklisted", true);
    }
}
