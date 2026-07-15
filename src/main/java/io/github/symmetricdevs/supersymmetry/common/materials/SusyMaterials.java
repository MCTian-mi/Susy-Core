package io.github.symmetricdevs.supersymmetry.common.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.DustProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidPipeProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.IngotProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.OreProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import io.github.symmetricdevs.supersymmetry.api.unification.material.info.SuSyMaterialFlags;

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
        GTMaterials.Soapstone.removeProperty(PropertyKey.ORE);
        GTMaterials.Quartzite.removeProperty(PropertyKey.ORE);
        GTMaterials.Mica.removeProperty(PropertyKey.ORE);

        GTMaterials.Lead.removeProperty(PropertyKey.FLUID_PIPE);
        GTMaterials.Lead.setProperty(PropertyKey.FLUID_PIPE,
                new FluidPipeProperties(1200, 8, true, true, false, false, 1));

        // Add dusts and fluids for elements that do not have them
        GTMaterials.Iodine.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Scandium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Germanium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Selenium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Bromine.setProperty(PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));
        GTMaterials.Rubidium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Strontium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Zirconium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Technetium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Tellurium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Praseodymium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Promethium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Gadolinium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Terbium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Dysprosium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Holmium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Erbium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Thulium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Ytterbium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Hafnium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Rhenium.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Thallium.setProperty(PropertyKey.DUST, new DustProperty());

        GTMaterials.CalciumChloride.setProperty(PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));
        GTMaterials.MagnesiumChloride.setProperty(PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));
        GTMaterials.RockSalt.setProperty(PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));
        GTMaterials.Salt.setProperty(PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));
        GTMaterials.SodiumHydroxide.setProperty(PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));
        GTMaterials.Sodium.setProperty(PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));

        GTMaterials.Phosphorus.setProperty(PropertyKey.INGOT, new IngotProperty());
        GTMaterials.Phosphorus.setProperty(PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder().temperature(317)));
        GTMaterials.Phosphorus.setMaterialARGB(0xfffed6);

        GTMaterials.HydrochloricAcid.setFormula("(H2O)(HCl)", true);
        GTMaterials.HydrofluoricAcid.setFormula("(H2O)(HF)", true);

        GTMaterials.Dimethyldichlorosilane.removeProperty(PropertyKey.FLUID);
        GTMaterials.Dimethyldichlorosilane.setProperty(PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));

        GTMaterials.Iron3Chloride.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Nitrochlorobenzene.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Dichlorobenzene.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Dichlorobenzidine.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.PhthalicAcid.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.DiphenylIsophtalate.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.Diaminobenzidine.setProperty(PropertyKey.DUST, new DustProperty());
        GTMaterials.PolyvinylAcetate.setProperty(PropertyKey.DUST, new DustProperty());

        GTMaterials.Platinum.addFlags(SuSyMaterialFlags.GENERATE_CATALYST_BED);
        GTMaterials.Cobalt.addFlags(SuSyMaterialFlags.GENERATE_CATALYST_BED);
        GTMaterials.Palladium.addFlags(SuSyMaterialFlags.GENERATE_CATALYST_BED);
        GTMaterials.Rhodium.addFlags(SuSyMaterialFlags.GENERATE_CATALYST_BED);
        GTMaterials.Copper.addFlags(SuSyMaterialFlags.GENERATE_CATALYST_BED);

        GTMaterials.Electrum.setProperty(PropertyKey.ORE, new OreProperty());

        GTMaterials.Hydrogen.addFlags(MaterialFlags.FLAMMABLE);
    }
}
