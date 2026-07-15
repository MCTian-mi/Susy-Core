package io.github.symmetricdevs.supersymmetry.api.unification.material.info;

import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconType;

/**
 * Custom {@link MaterialIconType}s for SuSy item forms. These must be initialized
 * before any {@code TagPrefix} that references them (see
 * {@code SusyTagPrefixes}) — a plain holder class, forced by class-load order.
 */
public class SuSyMaterialIconTypes {

    public static final MaterialIconType catalystBed = new MaterialIconType("catalystBed");
    public static final MaterialIconType catalystPellet = new MaterialIconType("catalystPellet");
    public static final MaterialIconType sheetedFrame = new MaterialIconType("sheetedFrame");
    public static final MaterialIconType sifted = new MaterialIconType("sifted");
    public static final MaterialIconType flotated = new MaterialIconType("flotated");
    public static final MaterialIconType concentrate = new MaterialIconType("concentrate");
    public static final MaterialIconType fiber = new MaterialIconType("fiber");
    public static final MaterialIconType wetFiber = new MaterialIconType("wetFiber");
    public static final MaterialIconType thread = new MaterialIconType("thread");
    public static final MaterialIconType slurry = new MaterialIconType("slurry");
    public static final MaterialIconType supercritical = new MaterialIconType("supercritical");
    public static final MaterialIconType dustWet = new MaterialIconType("dustWet");
    public static final MaterialIconType electrode = new MaterialIconType("electrode");
    public static final MaterialIconType millBall = new MaterialIconType("millBall");
    public static final MaterialIconType pin = new MaterialIconType("pin");
    public static final MaterialIconType target = new MaterialIconType("target");

    private SuSyMaterialIconTypes() {}
}
