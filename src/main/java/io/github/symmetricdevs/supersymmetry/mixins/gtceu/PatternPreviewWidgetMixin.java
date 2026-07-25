package io.github.symmetricdevs.supersymmetry.mixins.gtceu;

import com.gregtechceu.gtceu.api.gui.widget.PatternPreviewWidget;

import it.unimi.dsi.fastutil.longs.LongSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * GTCEu's 7.5.3 XEI widget forms a dummy controller, then removes every position recorded under
 * {@code renderMask} from its {@code SceneWidget}. Those masks are only meaningful for a real
 * formed multiblock with a GeckoLib model over its structure; XEI must retain the static blocks
 * declared by the shape preview.
 */
@Mixin(value = PatternPreviewWidget.class, remap = false)
public abstract class PatternPreviewWidgetMixin {

    @Redirect(method = {
            "setupScene(Lcom/gregtechceu/gtceu/api/gui/widget/PatternPreviewWidget$MBPattern;)V",
            "loadControllerFormed(Ljava/util/Collection;" +
                    "Lcom/gregtechceu/gtceu/api/machine/feature/multiblock/IMultiController;)V" },
              remap = false,
              at = @At(value = "INVOKE",
                       target = "Lit/unimi/dsi/fastutil/longs/LongSet;isEmpty()Z"))
    private boolean susy$keepMaskedPreviewBlocks(LongSet renderMask) {
        // Pretend no render mask was collected so GTCEu keeps every shape-info position in XEI.
        return true;
    }
}
