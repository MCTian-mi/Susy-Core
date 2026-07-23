package io.github.symmetricdevs.supersymmetry.mixins.embeddium;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import io.github.symmetricdevs.supersymmetry.client.renderer.RenderMaskManager;

import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Embeddium/Sodium counterpart to {@code ModelBlockRendererMixin}: force a still-visible block's
 * face toward a masked neighbour to mesh, so hiding interior structure blocks doesn't leave holes.
 *
 * <p>Embeddium computes face culling in {@code BlockRenderer#isFaceVisible(BlockRenderContext,
 * Direction)}; the neighbour being culled against is {@code ctx.pos().relative(direction)}. Gated by
 * {@link io.github.symmetricdevs.supersymmetry.mixins.SusyMixinPlugin}; {@code remap = false}.
 */
@Mixin(value = BlockRenderer.class, remap = false)
public abstract class BlockRendererFaceMixin {

    @Inject(method = "isFaceVisible", at = @At("RETURN"), cancellable = true)
    private void susy$renderFaceTowardMasked(BlockRenderContext ctx, Direction face,
                                             CallbackInfoReturnable<Boolean> cir) {
        BlockPos pos = ctx.pos();
        if (pos != null && RenderMaskManager.isMasked(pos.relative(face))) {
            cir.setReturnValue(true);
        }
    }
}
