package io.github.symmetricdevs.supersymmetry.common.cover;

import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.client.renderer.cover.SimpleCoverRenderer;

import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Cover registration for SuSy-specific covers.
 * <p>
 * Ported from the 1.12.2 {@code supersymmetry.common.covers.SuSyCoverBehaviors}.
 * In Modern, covers are registered as {@link CoverDefinition} entries in
 * {@link GTRegistries#COVERS}, keyed by {@link ResourceLocation}. The corresponding
 * items used to place each cover are associated through the item's behaviour
 * (e.g. {@link com.gregtechceu.gtceu.api.item.component.ElectricStats} or cover
 * interaction components).
 * <p>
 * Call {@link #init()} from {@code SuSyAddon.registerCovers()}.
 */
public final class SusyCovers {

    private SusyCovers() {}

    public static void init() {
        SuSyValues.LOGGER.info("Registering SuSy covers...");

        register("conveyor.steam",
                (def, coverable, side) -> new CoverSteamConveyor(def, coverable, side),
                simpleRenderer("cover/conveyor_steam"));

        register("pump.steam",
                (def, coverable, side) -> new CoverSteamPump(def, coverable, side),
                simpleRenderer("cover/pump_steam"));

        register("air_vent",
                (def, coverable, side) -> new CoverAirVent(def, coverable, side, 100),
                simpleRenderer("cover/air_vent"));

        register("restrictive_filter",
                CoverRestrictive::new,
                simpleRenderer("cover/restrictive_filter"));
    }

    // -----------------------------------------------------------------
    // Registration helpers (mirror GTCovers' private register calls)
    // -----------------------------------------------------------------

    private static CoverDefinition register(String id,
                                            CoverDefinition.CoverBehaviourProvider behaviorCreator,
                                            Supplier<Supplier<com.gregtechceu.gtceu.client.renderer.cover.ICoverRenderer>> coverRenderer) {
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, id);
        var definition = new CoverDefinition(rl, behaviorCreator, coverRenderer);
        GTRegistries.COVERS.register(definition.getId(), definition);
        return definition;
    }

    private static Supplier<Supplier<com.gregtechceu.gtceu.client.renderer.cover.ICoverRenderer>> simpleRenderer(String texturePath) {
        ResourceLocation tex = ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, texturePath);
        return () -> () -> new SimpleCoverRenderer(tex);
    }
}
