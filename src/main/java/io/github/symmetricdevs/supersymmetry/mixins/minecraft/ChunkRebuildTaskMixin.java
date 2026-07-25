package io.github.symmetricdevs.supersymmetry.mixins.minecraft;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraftforge.client.model.data.ModelData;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.symmetricdevs.supersymmetry.client.renderer.RenderMaskManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Vanilla-terrain chunk meshing path: skip rendering block models at positions masked by a formed
 * multiblock, so its unified GeckoLib model isn't overlaid on the real structure blocks.
 *
 * <p>This is the fallback for when Embeddium/Sodium is absent; the Embeddium meshing path is
 * intercepted separately. Targets {@code ChunkRenderDispatcher.RenderChunk.RebuildTask#compile},
 * the vanilla 1.20.1 Forge name (NeoForge/Mojang call it {@code SectionRenderDispatcher}). The
 * target is referenced by string because {@code RebuildTask} is not a public inner class.
 */
@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask")
public abstract class ChunkRebuildTaskMixin {

    // renderBatched's 9-arg overload (ModelData + RenderType) is Forge-added, so it keeps its literal
    // name at runtime and is absent from the SRG table — remap = false stops the AP from remapping it.
    @WrapWithCondition(method = "compile",
                       at = @At(value = "INVOKE",
                            remap = false,
                            target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(" +
                                    "Lnet/minecraft/world/level/block/state/BlockState;" +
                                    "Lnet/minecraft/core/BlockPos;" +
                                    "Lnet/minecraft/world/level/BlockAndTintGetter;" +
                                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                                    "Lcom/mojang/blaze3d/vertex/VertexConsumer;Z" +
                                    "Lnet/minecraft/util/RandomSource;" +
                                    "Lnet/minecraftforge/client/model/data/ModelData;" +
                                    "Lnet/minecraft/client/renderer/RenderType;)V"))
    private boolean susy$skipMaskedBlock(BlockRenderDispatcher dispatcher, BlockState state, BlockPos pos,
                                      BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer,
                                      boolean checkSides, RandomSource random, ModelData modelData,
                                      RenderType renderType) {
        return !RenderMaskManager.isMasked(level, pos);
    }
}
