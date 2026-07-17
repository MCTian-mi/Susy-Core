package io.github.symmetricdevs.supersymmetry.mixins.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.symmetricdevs.supersymmetry.api.util.RenderMaskManager;

@Mixin(TileEntityRendererDispatcher.class)
public class TileEntityRendererDispatcherMixin {

    @Inject(method = "getRenderer(Lnet/minecraft/BlockEntity/BlockEntity;)Lnet/minecraft/client/renderer/BlockEntity/TileEntitySpecialRenderer;",
            at = @At(value = "HEAD"),
            cancellable = true)
    private <T extends BlockEntity> void ignoreBlocked(BlockEntity tileEntityIn,
                                                      CallbackInfoReturnable<TileEntitySpecialRenderer<T>> cir) {
        if (tileEntityIn != null) {
            if (tileEntityIn.getWorld() == Minecraft.getInstance().world &&
                    RenderMaskManager.isModelDisabledRaw(tileEntityIn.getPos())) {
                cir.setReturnValue(null);
            }
        }
    }
}
