package io.github.symmetricdevs.supersymmetry.api.unification.material.info;

import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;

/**
 * Per-tier icon sets used for tiered hull/component materials. Each is a
 * {@link MaterialIconSet#DULL}-parented set named after a voltage tier.
 */
public class SuSyMaterialIconSets {

    public static final MaterialIconSet ULV = new MaterialIconSet("ulv");
    public static final MaterialIconSet LV = new MaterialIconSet("lv");
    public static final MaterialIconSet MV = new MaterialIconSet("mv");
    public static final MaterialIconSet HV = new MaterialIconSet("hv");
    public static final MaterialIconSet EV = new MaterialIconSet("ev");
    public static final MaterialIconSet IV = new MaterialIconSet("iv");
    public static final MaterialIconSet LuV = new MaterialIconSet("luv");
    public static final MaterialIconSet ZPM = new MaterialIconSet("zpm");
    public static final MaterialIconSet UV = new MaterialIconSet("uv");
    public static final MaterialIconSet UHV = new MaterialIconSet("uhv");
    public static final MaterialIconSet UEV = new MaterialIconSet("uev");
    public static final MaterialIconSet UIV = new MaterialIconSet("uiv");
    public static final MaterialIconSet UXV = new MaterialIconSet("uxv");
    public static final MaterialIconSet OpV = new MaterialIconSet("opv");
    public static final MaterialIconSet[] TIERS = new MaterialIconSet[] { ULV, LV, MV, HV, EV, IV, LuV, ZPM, UV, UHV,
            UEV, UIV, UXV, OpV };

    private SuSyMaterialIconSets() {}
}
