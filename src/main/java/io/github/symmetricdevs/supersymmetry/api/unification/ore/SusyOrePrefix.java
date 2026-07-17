package io.github.symmetricdevs.supersymmetry.api.unification.ore;

import io.github.symmetricdevs.supersymmetry.api.unification.material.info.SuSyMaterialFlags;

/**
 * Legacy OrePrefix holder — delegates to {@link SusyTagPrefixes} for all modern prefixes.
 * <p>
 * The ore-stone prefixes (oreGabbro, oreGneiss, ...) are removed in Modern because
 * {@code OrePrefix} no longer exists; worldgen stone ores are deferred to Phase 8.
 * All processing-handler code that references this class should be migrated to
 * use {@code SusyTagPrefixes} with {@code ChemicalHelper} directly.
 */
@Deprecated
public class SusyOrePrefix {

    // --- Catalyst ---
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix catalystBed = SusyTagPrefixes.catalystBed;
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix catalystPellet = SusyTagPrefixes.catalystPellet;

    // --- Sheeted Frames ---
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix sheetedFrame = SusyTagPrefixes.sheetedFrame;

    // --- Tiered Catalyst Beds ---
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix catalystBedReduction = SusyTagPrefixes.catalystBedReduction;
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix catalystBedOxidation = SusyTagPrefixes.catalystBedOxidation;
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix catalystBedCracking = SusyTagPrefixes.catalystBedCracking;
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix catalystBedZieglerNatta = SusyTagPrefixes.catalystBedZieglerNatta;

    // --- Tiered Catalyst Pellets ---
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix catalystPelletReduction = SusyTagPrefixes.catalystPelletReduction;
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix catalystPelletOxidation = SusyTagPrefixes.catalystPelletOxidation;
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix catalystPelletCracking = SusyTagPrefixes.catalystPelletCracking;
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix catalystPelletZieglerNatta = SusyTagPrefixes.catalystPelletZieglerNatta;

    // --- Ore Processing Intermediates ---
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix sifted = SusyTagPrefixes.sifted;
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix flotated = SusyTagPrefixes.flotated;
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix concentrate = SusyTagPrefixes.concentrate;

    // --- Fiber ---
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix fiber = SusyTagPrefixes.fiber;
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix wetFiber = SusyTagPrefixes.wetFiber;
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix thread = SusyTagPrefixes.thread;

    // --- Wet dust ---
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix dustWet = SusyTagPrefixes.dustWet;

    // --- Electrode ---
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix electrode = SusyTagPrefixes.electrode;

    // --- Mill ball ---
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix millBall = SusyTagPrefixes.millBall;

    // --- Pin ---
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix pin = SusyTagPrefixes.pin;

    // --- Sputtering target ---
    public static final com.gregtechceu.gtceu.api.data.tag.TagPrefix target = SusyTagPrefixes.target;
}
