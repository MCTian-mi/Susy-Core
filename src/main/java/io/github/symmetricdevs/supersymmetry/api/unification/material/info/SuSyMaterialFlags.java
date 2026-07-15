package io.github.symmetricdevs.supersymmetry.api.unification.material.info;

import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;

import io.github.symmetricdevs.supersymmetry.api.unification.material.properties.SuSyPropertyKey;

/**
 * Custom {@link MaterialFlag}s. Ported from the 1.12.2 {@code SuSyMaterialFlags}.
 * <p>
 * GCYM was upstreamed into GTCEu-Modern, so the 1.12.2 GCYM alloy-blast hooks map
 * onto native API: {@code GCYMMaterialFlags.NO_ALLOY_BLAST_RECIPES} →
 * {@link MaterialFlags#DISABLE_ALLOY_BLAST}, and {@code GCYMPropertyKey.ALLOY_BLAST} →
 * {@link PropertyKey#ALLOY_BLAST}. The continuous <em>caster</em> itself is
 * GCYM-only and is reimplemented in a later phase.
 */
public final class SuSyMaterialFlags {

    public static final MaterialFlag GENERATE_CATALYST_PELLET = new MaterialFlag.Builder("generate_catalyst_pellet")
            .requireProps(PropertyKey.DUST)
            .build();

    public static final MaterialFlag GENERATE_CATALYST_BED = new MaterialFlag.Builder("generate_catalyst_bed")
            .requireProps(PropertyKey.DUST)
            .requireFlags(GENERATE_CATALYST_PELLET)
            .build();

    public static final MaterialFlag GENERATE_SIFTED = new MaterialFlag.Builder("generate_sifted")
            .requireProps(PropertyKey.ORE)
            .build();

    public static final MaterialFlag GENERATE_FLOTATED = new MaterialFlag.Builder("generate_flotated")
            .requireProps(PropertyKey.ORE)
            .build();

    public static final MaterialFlag GENERATE_CONCENTRATE = new MaterialFlag.Builder("generate_concentrate")
            .requireProps(PropertyKey.ORE)
            .build();

    public static final MaterialFlag GENERATE_FIBER = new MaterialFlag.Builder("generate_fiber")
            .requireProps(SuSyPropertyKey.FIBER)
            .build();

    public static final MaterialFlag GENERATE_WET_FIBER = new MaterialFlag.Builder("generate_wet_fiber")
            .requireProps(SuSyPropertyKey.FIBER)
            .build();

    public static final MaterialFlag GENERATE_THREAD = new MaterialFlag.Builder("generate_thread")
            .requireProps(SuSyPropertyKey.FIBER)
            .build();

    public static final MaterialFlag GENERATE_WET_DUST = new MaterialFlag.Builder("generate_wet_dust")
            .requireProps(PropertyKey.DUST)
            .build();

    public static final MaterialFlag HIP_PRESSED = new MaterialFlag.Builder("hip_pressed")
            .requireProps(PropertyKey.DUST)
            .requireFlags(MaterialFlags.NO_WORKING, MaterialFlags.NO_SMELTING)
            .build();

    public static final MaterialFlag SUPERALLOY = new MaterialFlag.Builder("superalloy")
            .requireProps(PropertyKey.DUST)
            .requireFlags(HIP_PRESSED)
            .build();

    /**
     * Marks an alloy as continuously cast. Such alloys still receive the alloy-blast
     * property (and a molten fluid) but generate no Alloy Blast Smelter recipe —
     * they are produced in the continuous caster (a SuSy machine from a later
     * phase) instead. Maps the 1.12.2 GCYM-backed flag onto native API.
     */
    public static final MaterialFlag CONTINUOUSLY_CAST = new MaterialFlag.Builder("continuously_cast")
            .requireProps(PropertyKey.DUST, PropertyKey.FLUID, PropertyKey.ALLOY_BLAST)
            .requireFlags(MaterialFlags.DISABLE_ALLOY_BLAST)
            .build();

    public static final MaterialFlag GENERATE_PINS = new MaterialFlag.Builder("generate_pins")
            .requireProps(PropertyKey.DUST)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .build();

    public static final MaterialFlag GENERATE_SPUTTERING_TARGET = new MaterialFlag.Builder(
            "generate_sputtering_target")
            .requireProps(PropertyKey.DUST)
            .build();

    private SuSyMaterialFlags() {}
}
