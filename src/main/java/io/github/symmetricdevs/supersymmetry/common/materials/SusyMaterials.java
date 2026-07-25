package io.github.symmetricdevs.supersymmetry.common.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.DustProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidPipeProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.IMaterialProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.IngotProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.OreProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import io.github.symmetricdevs.supersymmetry.api.unification.material.info.SuSyMaterialFlags;
import io.github.symmetricdevs.supersymmetry.api.unification.material.properties.MillBallProperty;
import io.github.symmetricdevs.supersymmetry.api.unification.material.properties.SuSyPropertyKey;

/**
 * Static handles for every SuSy material, plus the {@code PostMaterialEvent}
 * mutations of existing GTCEu materials. Ported from the 1.12.2
 * {@code SusyMaterials}.
 * <p>
 * The 1.12.2 version used reflection to reach the private property/flag maps;
 * GTCEu-Modern exposes {@code setProperty}/{@code removeProperty}/{@code addFlags}
 * publicly (valid only during the material-modification window, i.e. inside
 * {@code PostMaterialEvent} — which is exactly where {@link #changeProperties()}
 * runs). The one exception is flag <em>removal</em>, which has no public API; see
 * {@link #changeProperties()}.
 */
public class SusyMaterials {

    public static Material ManganeseIronArsenicPhosphide;
    public static Material PraseodymiumNickel;
    public static Material GadoliniumSiliconGermanium;
    public static Material Gabbro;
    public static Material Gneiss;
    public static Material Limestone;
    public static Material Phyllite;
    public static Material Shale;
    public static Material Slate;
    public static Material Kimberlite;
    public static Material Anorthosite;
    public static Material Latex;
    public static Material Mud;
    public static Material Seawater;
    public static Material MidgradeLubricant;
    public static Material PremiumLubricant;
    public static Material SupremeLubricant;
    public static Material Coolant;
    public static Material AdvancedCoolant;
    public static Material LubricatingOil;
    public static Material MetallizedBoPET;
    public static Material AluminiumAlloy6061;
    public static Material AluminiumAlloy7075;

    public static Material RefractoryGunningMixture;

    // Minerals
    public static Material Anorthite;
    public static Material Albite;
    public static Material Oligoclase;
    public static Material Andesine;
    public static Material Labradorite;
    public static Material Bytownite;
    public static Material Clinochlore;
    public static Material Augite;
    public static Material Dolomite;
    public static Material Muscovite;
    public static Material Forsterite;
    public static Material Lizardite;
    public static Material Fluorite;

    // Thermodynamic materials
    public static Material PreheatedAir;

    public static Material RP_1;

    // Fluorinated Ketones
    public static Material Perfluoro2Methyl3Pentanone;
    public static Material WarmPerfluoro2Methyl3Pentanone;

    // Fuels
    public static Material LOX;

    /**
     * Register all SuSy materials. Called on {@code MaterialEvent}. Ordering
     * matters: later classes reference materials built by earlier ones.
     */
    public static void init() {
        SuSyElementMaterials.init();
        SuSyFirstDegreeMaterials.init();
        SuSySecondDegreeMaterials.init();
        SuSyOrganicChemistryMaterials.init();
        SuSyHighDegreeMaterials.init();
        SuSyUnknownCompositionMaterials.init();
    }

    /**
     * Mutate existing GTCEu materials. Called on {@code PostMaterialEvent}.
     * <p>
     * The 1.12.2 {@code removeFlags()} (stripping {@code DECOMPOSITION_BY_ELECTROLYZING}
     * from every material) is not ported: GTCEu-Modern has no public flag-removal
     * API and it must not be done via reflection. If that behavior is needed it
     * should be re-expressed as a datagen/recipe-level exclusion.
     */
    public static void changeProperties() {
        // NOTE: the 1.12.2 removeProperty(ORE) on Soapstone/Quartzite/Mica is NOT
        // ported. GTCEu-Modern's stock ore veins reference these materials (e.g. the
        // Kyanite vein's .surfaceRock(Mica)), and their surface-rock blocks are only
        // generated when the material keeps its ORE property — removing it makes the
        // ore-vein datapack reload throw "No surface rock registered for material".
        // SuSy's intent (don't spawn these as ores) is achieved by not defining veins
        // for them in the worldgen phase instead of by stripping the property.

        // Deliberate override: 1.12.2 explicitly replaced Lead's fluid-pipe stats,
        // so this one intentionally removes-then-sets rather than fill-only.
        GTMaterials.Lead.removeProperty(PropertyKey.FLUID_PIPE);
        GTMaterials.Lead.setProperty(PropertyKey.FLUID_PIPE,
                new FluidPipeProperties(1200, 8, true, true, false, false, 1));

        // Add dusts and fluids for elements that do not have them
        putProperty(GTMaterials.Iodine, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Scandium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Germanium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Selenium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Bromine, PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));
        putProperty(GTMaterials.Rubidium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Strontium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Zirconium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Technetium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Tellurium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Praseodymium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Promethium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Gadolinium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Terbium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Dysprosium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Holmium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Erbium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Thulium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Ytterbium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Hafnium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Rhenium, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Thallium, PropertyKey.DUST, new DustProperty());

        putProperty(GTMaterials.CalciumChloride, PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));
        putProperty(GTMaterials.MagnesiumChloride, PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));
        putProperty(GTMaterials.RockSalt, PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));
        putProperty(GTMaterials.Salt, PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));
        putProperty(GTMaterials.SodiumHydroxide, PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));
        putProperty(GTMaterials.Sodium, PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));

        putProperty(GTMaterials.Phosphorus, PropertyKey.INGOT, new IngotProperty());
        putProperty(GTMaterials.Phosphorus, PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder().temperature(317)));
        GTMaterials.Phosphorus.setMaterialARGB(0xfffed6);

        GTMaterials.HydrochloricAcid.setFormula("(H2O)(HCl)", true);
        GTMaterials.HydrofluoricAcid.setFormula("(H2O)(HF)", true);

        putProperty(GTMaterials.Dimethyldichlorosilane, PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));

        putProperty(GTMaterials.Iron3Chloride, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Nitrochlorobenzene, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Dichlorobenzene, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Dichlorobenzidine, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.PhthalicAcid, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.DiphenylIsophtalate, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.Diaminobenzidine, PropertyKey.DUST, new DustProperty());
        putProperty(GTMaterials.PolyvinylAcetate, PropertyKey.DUST, new DustProperty());

        GTMaterials.Platinum.addFlags(SuSyMaterialFlags.GENERATE_CATALYST_BED);
        GTMaterials.Cobalt.addFlags(SuSyMaterialFlags.GENERATE_CATALYST_BED);
        GTMaterials.Palladium.addFlags(SuSyMaterialFlags.GENERATE_CATALYST_BED);
        GTMaterials.Rhodium.addFlags(SuSyMaterialFlags.GENERATE_CATALYST_BED);
        GTMaterials.Copper.addFlags(SuSyMaterialFlags.GENERATE_CATALYST_BED);

        putProperty(GTMaterials.Electrum, PropertyKey.ORE, new OreProperty());

        // The only Java-defined 1.12.2 mill-ball material was Steel with this
        // debug-era durability. Keep it as the compatibility bootstrap until the
        // deferred bulk material data supplies production mill-ball materials.
        // Fill-only: a material pack may provide a deliberate value first.
        putProperty(GTMaterials.Steel, SuSyPropertyKey.MILL_BALL, new MillBallProperty(23_123));

        GTMaterials.Hydrogen.addFlags(MaterialFlags.FLAMMABLE);
    }

    /**
     * Add a material property only if the material does not already have one.
     * GTCEu-Modern's {@code Material.setProperty} throws if the property is already
     * present (1.12.2 silently overwrote). Where the material already defines the
     * property we keep the original values — these additions only fill in a
     * property the material lacks, so an existing definition is never replaced.
     */
    private static <T extends IMaterialProperty> void putProperty(Material material, PropertyKey<T> key, T property) {
        if (!material.hasProperty(key)) {
            material.setProperty(key, property);
        }
    }
}
