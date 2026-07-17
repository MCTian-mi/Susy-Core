package io.github.symmetricdevs.supersymmetry.mixins.forge;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.symmetricdevs.supersymmetry.api.util.RenderMaskManager;

@Mixin(value = ForgeBlockModelRenderer.class, remap = false)
public abstract class ForgeBlockModelRendererMixin {

    @WrapOperation(method = "render",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/block/state/BlockState;shouldSideBeRendered(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)Z",
                            remap = true))
    private static boolean ignoreBlocked(
                                         BlockState state, IBlockAccess blockAccess, BlockPos pos, Direction facing,
                                         Operation<Boolean> method) {
        return RenderMaskManager.isModelDisabled(pos.offset(facing)) || method.call(state, blockAccess, pos, facing);
    }
}
