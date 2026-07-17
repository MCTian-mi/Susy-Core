package io.github.symmetricdevs.supersymmetry.common;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Shared lifecycle hooks retained while client-only initialization is migrated to
 * the Forge 1.20 event model. Material, tag-prefix, fluid, and machine
 * registration live in {@code Supersymmetry} and {@code SuSyAddon}; the legacy
 * proxy registration responsibilities must not be duplicated here.
 */
public class CommonProxy {

    public void preLoad() {
        // Deferred: particle and stone-type initialization.
    }

    public void load() {
        // Deferred: world loaders and mob-horde events.
    }

    public void postLoad() {
        // The legacy ability-list mutation has no GTCEu-Modern counterpart.
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void itemToolTip(ItemTooltipEvent event) {
        // Deferred: coil tooltips and steam-extractor warnings.
    }
}
