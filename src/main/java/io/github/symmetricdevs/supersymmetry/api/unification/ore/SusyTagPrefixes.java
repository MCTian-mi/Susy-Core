package io.github.symmetricdevs.supersymmetry.api.unification.ore;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;

import io.github.symmetricdevs.supersymmetry.api.unification.material.info.SuSyMaterialFlags;
import io.github.symmetricdevs.supersymmetry.api.unification.material.info.SuSyMaterialIconTypes;
import io.github.symmetricdevs.supersymmetry.api.unification.material.properties.SuSyPropertyKey;

/**
 * Custom {@link TagPrefix}es (the Modern replacement for 1.12.2 {@code OrePrefix}).
 * Ported from the 1.12.2 {@code SusyOrePrefix}.
 * <p>
 * Modern uses {@code new TagPrefix(name)} + chainable setters rather than the
 * 1.12.2 positional constructor, and each prefix self-registers on construction —
 * so "registering" is just class-loading this holder, done from
 * {@code SuSyAddon#registerTagPrefixes()} (which runs after icon types
 * and materials are initialized).
 * <p>
 * The 1.12.2 ore-stone prefixes (oreGabbro, oreGneiss, ...) are NOT here: Modern
 * has no {@code StoneType}; those become {@code TagPrefix.oreTagPrefix(...).registerOre(...)}
 * bound to our stone blocks, and are deferred until the stone blocks exist
 * (Phase 5) and worldgen (Phase 8).
 */
public final class SusyTagPrefixes {

    // --- Catalysts ---

    public static final TagPrefix catalystBed = new TagPrefix("catalystBed")
            .materialAmount(GTValues.M * 4)
            .materialIconType(SuSyMaterialIconTypes.catalystBed)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(SuSyMaterialFlags.GENERATE_CATALYST_BED));

    public static final TagPrefix catalystPellet = new TagPrefix("catalystPellet")
            .materialAmount(GTValues.M / 4)
            .materialIconType(SuSyMaterialIconTypes.catalystPellet)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(SuSyMaterialFlags.GENERATE_CATALYST_PELLET));

    // Sheeted Frames (10 ingots in, 12 frames out -> 5/6 of an ingot each)
    public static final TagPrefix sheetedFrame = new TagPrefix("sheetedFrame")
            .materialAmount(GTValues.M * 5 / 6)
            .materialIconType(SuSyMaterialIconTypes.sheetedFrame)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(MaterialFlags.GENERATE_FRAME));

    // Tiered catalyst beds (fixed material sets, driven by datagen later)
    public static final TagPrefix catalystBedReduction = new TagPrefix("catalystBedReduction")
            .materialAmount(GTValues.M)
            .materialIconType(SuSyMaterialIconTypes.catalystBed)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> false);

    public static final TagPrefix catalystBedOxidation = new TagPrefix("catalystBedOxidation")
            .materialAmount(GTValues.M)
            .materialIconType(SuSyMaterialIconTypes.catalystBed)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> false);

    public static final TagPrefix catalystBedCracking = new TagPrefix("catalystBedCracking")
            .materialAmount(GTValues.M)
            .materialIconType(SuSyMaterialIconTypes.catalystBed)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> false);

    public static final TagPrefix catalystBedZieglerNatta = new TagPrefix("catalystBedZieglerNatta")
            .materialAmount(GTValues.M)
            .materialIconType(SuSyMaterialIconTypes.catalystBed)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> false);

    // Tiered catalyst pellets
    public static final TagPrefix catalystPelletReduction = new TagPrefix("catalystPelletReduction")
            .materialAmount(GTValues.M * 4)
            .materialIconType(SuSyMaterialIconTypes.catalystPellet)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> false);

    public static final TagPrefix catalystPelletOxidation = new TagPrefix("catalystPelletOxidation")
            .materialAmount(GTValues.M * 4)
            .materialIconType(SuSyMaterialIconTypes.catalystPellet)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> false);

    public static final TagPrefix catalystPelletCracking = new TagPrefix("catalystPelletCracking")
            .materialAmount(GTValues.M * 4)
            .materialIconType(SuSyMaterialIconTypes.catalystPellet)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> false);

    public static final TagPrefix catalystPelletZieglerNatta = new TagPrefix("catalystPelletZieglerNatta")
            .materialAmount(GTValues.M * 4)
            .materialIconType(SuSyMaterialIconTypes.catalystPellet)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> false);

    // --- Ore processing intermediates ---

    public static final TagPrefix sifted = new TagPrefix("dustSifted")
            .materialIconType(SuSyMaterialIconTypes.sifted)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(SuSyMaterialFlags.GENERATE_SIFTED));

    public static final TagPrefix flotated = new TagPrefix("dustFlotated")
            .materialIconType(SuSyMaterialIconTypes.flotated)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(SuSyMaterialFlags.GENERATE_FLOTATED));

    public static final TagPrefix concentrate = new TagPrefix("dustConcentrate")
            .materialIconType(SuSyMaterialIconTypes.concentrate)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(SuSyMaterialFlags.GENERATE_CONCENTRATE));

    // --- Fibers ---

    public static final TagPrefix fiber = new TagPrefix("fiber")
            .materialAmount(GTValues.M / 8)
            .materialIconType(SuSyMaterialIconTypes.fiber)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(SuSyMaterialFlags.GENERATE_FIBER));

    public static final TagPrefix wetFiber = new TagPrefix("fiberWet")
            .materialAmount(GTValues.M / 8)
            .materialIconType(SuSyMaterialIconTypes.wetFiber)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(SuSyMaterialFlags.GENERATE_WET_FIBER));

    public static final TagPrefix thread = new TagPrefix("thread")
            .materialAmount(GTValues.M / 8)
            .materialIconType(SuSyMaterialIconTypes.thread)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(SuSyMaterialFlags.GENERATE_THREAD));

    // --- Wet dust ---

    public static final TagPrefix dustWet = new TagPrefix("dustWet")
            .materialIconType(SuSyMaterialIconTypes.dustWet)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(SuSyMaterialFlags.GENERATE_WET_DUST));

    // --- Electrode (superalloy) ---

    public static final TagPrefix electrode = new TagPrefix("electrode")
            .materialAmount(GTValues.M)
            .materialIconType(SuSyMaterialIconTypes.electrode)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(SuSyMaterialFlags.SUPERALLOY));

    // --- Mill ball ---

    public static final TagPrefix millBall = new TagPrefix("millBall")
            .materialAmount(GTValues.M)
            .materialIconType(SuSyMaterialIconTypes.millBall)
            .unificationEnabled(true)
            .generateItem(true)
            .maxStackSize(1)
            .generationCondition(mat -> mat.hasProperty(SuSyPropertyKey.MILL_BALL));

    // --- Pins ---

    public static final TagPrefix pin = new TagPrefix("pin")
            .materialAmount(GTValues.M)
            .materialIconType(SuSyMaterialIconTypes.pin)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(SuSyMaterialFlags.GENERATE_PINS));

    // --- Sputtering target ---

    public static final TagPrefix target = new TagPrefix("target")
            .materialAmount(GTValues.M)
            .materialIconType(SuSyMaterialIconTypes.target)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(SuSyMaterialFlags.GENERATE_SPUTTERING_TARGET));

    private SusyTagPrefixes() {}
}
