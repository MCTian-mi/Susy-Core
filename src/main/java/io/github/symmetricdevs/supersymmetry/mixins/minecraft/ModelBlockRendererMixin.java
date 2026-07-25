package io.github.symmetricdevs.supersymmetry.mixins.minecraft;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.symmetricdevs.supersymmetry.client.renderer.RenderMaskManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * When an interior structure block is masked (hidden by a formed multiblock's GeckoLib model), the
 * still-visible neighbouring block previously had its touching face culled against that now-hidden
 * opaque block. That leaves a hole. Force the neighbour's face toward a masked block to render.
 *
 * <p>Ported from the legacy {@code BlockModelRendererMixin}: the {@code neighborPos} (5th argument)
 * of {@link Block#shouldRenderFace} is the block being culled against. This is the vanilla-terrain
 * path; the Embeddium meshing path is handled separately.
 */
@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {

    // Target ONLY the long (Forge-added) overloads: each of tesselateWith(out)AO has a short vanilla
    // overload that merely delegates (no shouldRenderFace call) plus this long one that does the work.
    // A bare name matches the delegating overloads too, so the injection lands nowhere and mixin aborts.
    // The ModelData/RenderType tail marks the Forge overload.
    @WrapOperation(method = {
            "tesselateWithAO(Lnet/minecraft/world/level/BlockAndTintGetter;" +
                    "Lnet/minecraft/client/resources/model/BakedModel;" +
                    "Lnet/minecraft/world/level/block/state/BlockState;" +
                    "Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JI" +
                    "Lnet/minecraftforge/client/model/data/ModelData;" +
                    "Lnet/minecraft/client/renderer/RenderType;)V",
            "tesselateWithoutAO(Lnet/minecraft/world/level/BlockAndTintGetter;" +
                    "Lnet/minecraft/client/resources/model/BakedModel;" +
                    "Lnet/minecraft/world/level/block/state/BlockState;" +
                    "Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JI" +
                    "Lnet/minecraftforge/client/model/data/ModelData;" +
                    "Lnet/minecraft/client/renderer/RenderType;)V" },
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/world/level/block/Block;shouldRenderFace(" +
                                    "Lnet/minecraft/world/level/block/state/BlockState;" +
                                    "Lnet/minecraft/world/level/BlockGetter;" +
                                    "Lnet/minecraft/core/BlockPos;" +
                                    "Lnet/minecraft/core/Direction;" +
                                    "Lnet/minecraft/core/BlockPos;)Z"))
    private boolean susy$renderFaceTowardMasked(BlockState state, BlockGetter level, BlockPos offset,
                                                Direction face, BlockPos pos,
                                                Operation<Boolean> method) {
        if (!RenderMaskManager.isMasked(level, pos)) {
            return method.call(state, level, offset, face, pos);
        }
        return true;
    }
}
