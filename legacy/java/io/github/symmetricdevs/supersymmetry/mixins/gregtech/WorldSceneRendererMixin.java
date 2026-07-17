package io.github.symmetricdevs.supersymmetry.mixins.gregtech;

import net.minecraft.core.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import com.gregtechceu.gtceu.client.renderer.scene.WorldSceneRenderer;
import io.github.symmetricdevs.supersymmetry.api.util.RenderMaskManager;

@Mixin(value = WorldSceneRenderer.class, remap = false)
public class WorldSceneRendererMixin {

    @ModifyExpressionValue(method = "lambda$drawWorld$0",
                           at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/block/Block;canRenderInLayer(Lnet/minecraft/block/state/BlockState;Lnet/minecraft/util/BlockRenderLayer;)Z"))
    public boolean ignoreBlocked(boolean original, @Local(name = "pos") BlockPos pos) {
        return original && !RenderMaskManager.isModelDisabledRaw(pos);
    }
}
