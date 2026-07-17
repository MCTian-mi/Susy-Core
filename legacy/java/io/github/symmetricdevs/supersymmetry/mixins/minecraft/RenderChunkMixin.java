package io.github.symmetricdevs.supersymmetry.mixins.minecraft;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.symmetricdevs.supersymmetry.api.util.RenderMaskManager;

@Mixin(RenderChunk.class)
public abstract class RenderChunkMixin {

    @WrapOperation(method = "rebuildChunk",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;renderBlock(Lnet/minecraft/block/state/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)Z"))
    public boolean lockBuilding(BlockRendererDispatcher dispatcher,
                                BlockState state,
                                BlockPos pos,
                                IBlockAccess blockAccess,
                                BufferBuilder bufferBuilder,
                                Operation<Boolean> method) {
        RenderMaskManager.isBuildingChunk.set(true);
        if (RenderMaskManager.isModelDisabled(pos)) {
            RenderMaskManager.isBuildingChunk.set(false);
            return false;
        }
        boolean rst = method.call(dispatcher, state, pos, blockAccess, bufferBuilder);
        RenderMaskManager.isBuildingChunk.set(false);
        return rst;
    }
}
