package io.github.symmetricdevs.supersymmetry.mixins.embeddium;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;

import io.github.symmetricdevs.supersymmetry.client.renderer.RenderMaskManager;

import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Embeddium/Sodium chunk meshing path: skip meshing block models at positions masked by a formed
 * multiblock, so its unified GeckoLib model isn't overlaid on the real structure blocks.
 *
 * <p>Mirror of {@code ChunkRebuildTaskMixin} for the vanilla path. Applied only when Embeddium is
 * loaded (gated by {@link io.github.symmetricdevs.supersymmetry.mixins.SusyMixinPlugin}); target
 * classes are Embeddium's own, so {@code remap = false}.
 */
@Mixin(value = BlockRenderer.class, remap = false)
public abstract class BlockRendererMixin {

    @WrapMethod(method = "renderModel")
    private void susy$skipMaskedBlock(BlockRenderContext ctx, ChunkBuildBuffers buffers, Operation<Void> method) {
        BlockPos pos = ctx.pos();
        if (pos == null || !RenderMaskManager.isMasked(pos)) {
            method.call(ctx, buffers);
        }
    }
}
