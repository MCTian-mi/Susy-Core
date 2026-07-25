package io.github.symmetricdevs.supersymmetry.common.materials;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.DISABLE_DECOMPOSITION;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.DECOMPOSITION_BY_ELECTROLYZING;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.FLAMMABLE;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_FINE_WIRE;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.NO_SMASHING;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.CERTUS;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.EMERALD;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.LAPIS;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.ROUGH;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.RUBY;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Aluminium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Argon;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Arsenic;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Calcium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Carbon;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Chromium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Copper;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Fluorine;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Gadolinium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Germanium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Hydrogen;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Iron;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Magnesium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Manganese;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Nickel;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Nitrogen;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Oxygen;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Phosphorus;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Potassium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Praseodymium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Silicon;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Sodium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Sulfur;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Zinc;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.AdvancedCoolant;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Albite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.AluminiumAlloy6061;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.AluminiumAlloy7075;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Andesine;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Anorthite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Augite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Bytownite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Clinochlore;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Dolomite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Fluorite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Forsterite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.GadoliniumSiliconGermanium;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Labradorite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Lizardite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.ManganeseIronArsenicPhosphide;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Muscovite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Oligoclase;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Perfluoro2Methyl3Pentanone;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.PraseodymiumNickel;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.PreheatedAir;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.RP_1;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.WarmPerfluoro2Methyl3Pentanone;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;

import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.api.unification.material.info.SuSyMaterialFlags;

public class SuSyFirstDegreeMaterials {

    public static void init() {
        ManganeseIronArsenicPhosphide = new Material.Builder(SuSyValues.susyId("manganese_iron_arsenic_phosphide"))
                .ingot()
                .color(0x03FCF0).iconSet(MaterialIconSet.METALLIC)
                .cableProperties(GTValues.V[4], 2, 4)
                .components(Manganese, 2, Iron, 2, Arsenic, 1, Phosphorus, 1)
                .blastTemp(2100, BlastProperty.GasTier.LOW)
                .buildAndRegister();

        PraseodymiumNickel = new Material.Builder(SuSyValues.susyId("praseodymium_nickel"))
                .ingot()
                .color(0x03BAFC).iconSet(MaterialIconSet.METALLIC)
                .cableProperties(GTValues.V[4], 2, 4)
                .components(Praseodymium, 5, Nickel, 1)
                .blastTemp(2100, BlastProperty.GasTier.MID)
                .buildAndRegister();

        GadoliniumSiliconGermanium = new Material.Builder(SuSyValues.susyId("gadolinium_silicon_germanium"))
                .ingot()
                .color(0x0388FC).iconSet(MaterialIconSet.SHINY)
                .cableProperties(GTValues.V[4], 2, 4)
                .components(Gadolinium, 5, Silicon, 2, Germanium, 2)
                .blastTemp(2100, BlastProperty.GasTier.HIGH)
                .buildAndRegister();

        // Minerals

        Anorthite = new Material.Builder(SuSyValues.susyId("anorthite"))
                .dust()
                .gem()
                .color(0x595853).iconSet(CERTUS)
                .flags(NO_SMASHING, DECOMPOSITION_BY_ELECTROLYZING)
                .components(Calcium, 1, Aluminium, 2, Silicon, 2, Oxygen, 8)
                .formula("Ca(Al2Si2O8)", true)
                .buildAndRegister();

        Albite = new Material.Builder(SuSyValues.susyId("albite"))
                .dust()
                .gem()
                .color(0xc4a997).iconSet(CERTUS)
                .flags(NO_SMASHING, DECOMPOSITION_BY_ELECTROLYZING)
                .components(Sodium, 1, Aluminium, 1, Silicon, 3, Oxygen, 8)
                .formula("Na(AlSi3O8)", true)
                .buildAndRegister();

        Oligoclase = new Material.Builder(SuSyValues.susyId("oligoclase"))
                .dust()
                .gem()
                .color(0xd5c4b8).iconSet(CERTUS)
                .flags(NO_SMASHING, DECOMPOSITION_BY_ELECTROLYZING)
                .components(Albite, 4, Anorthite, 1)
                .formula("(Na,Ca)(Si,Al)4O8", true)
                .buildAndRegister();

        Andesine = new Material.Builder(SuSyValues.susyId("andesine"))
                .dust()
                .gem()
                .color(0xe18e6f).iconSet(EMERALD)
                .flags(NO_SMASHING, DECOMPOSITION_BY_ELECTROLYZING)
                .components(Albite, 3, Anorthite, 2)
                .formula("(Na,Ca)(Si,Al)4O8", true)
                .buildAndRegister();

        Labradorite = new Material.Builder(SuSyValues.susyId("labradorite"))
                .dust()
                .gem()
                .color(0x5c7181).iconSet(RUBY)
                .flags(NO_SMASHING, DECOMPOSITION_BY_ELECTROLYZING)
                .components(Albite, 2, Anorthite, 3)
                .formula("(Na,Ca)(Si,Al)4O8", true)
                .buildAndRegister();

        Bytownite = new Material.Builder(SuSyValues.susyId("bytownite"))
                .dust()
                .gem()
                .color(0xc99c67).iconSet(LAPIS)
                .flags(NO_SMASHING, DECOMPOSITION_BY_ELECTROLYZING)
                .components(Albite, 1, Anorthite, 4)
                .formula("(Na,Ca)(Si,Al)4O8", true)
                .buildAndRegister();

        Clinochlore = new Material.Builder(SuSyValues.susyId("clinochlore"))
                .dust()
                .gem()
                .color(0x303e38).iconSet(EMERALD)
                .flags(NO_SMASHING, DECOMPOSITION_BY_ELECTROLYZING)
                .components(Magnesium, 5, Aluminium, 2, Silicon, 3, Oxygen, 18, Hydrogen, 8)
                .formula("(Mg5Al)(AlSi3)O10(OH)8", true)
                .buildAndRegister();

        Augite = new Material.Builder(SuSyValues.susyId("augite"))
                .dust()
                .color(0x1b1717).iconSet(ROUGH)
                .flags(NO_SMASHING, DECOMPOSITION_BY_ELECTROLYZING)
                .components(Calcium, 2, Magnesium, 3, Iron, 3, Silicon, 8, Oxygen, 24)
                .formula("(Ca2MgFe)(MgFe)2(Si2O6)4", true)
                .buildAndRegister();

        Dolomite = new Material.Builder(SuSyValues.susyId("dolomite"))
                .dust()
                .color(0xbbb8b2)
                .flags(NO_SMASHING, DECOMPOSITION_BY_ELECTROLYZING)
                .components(Calcium, 1, Magnesium, 1, Carbon, 2, Oxygen, 6)
                .formula("CaMg(CO3)2", true)
                .buildAndRegister();

        Muscovite = new Material.Builder(SuSyValues.susyId("muscovite"))
                .dust()
                .color(0x8b876a)
                .flags(NO_SMASHING, DECOMPOSITION_BY_ELECTROLYZING)
                .components(Potassium, 1, Aluminium, 3, Silicon, 3, Oxygen, 12, Hydrogen, 10)
                .formula("KAl2(AlSi3O10)(OH)2)", true)
                .buildAndRegister();

        Fluorite = new Material.Builder(SuSyValues.susyId("fluorite"))
                .dust()
                .gem()
                .ore()
                .color(0x276a4c).iconSet(CERTUS)
                .flags(NO_SMASHING, DECOMPOSITION_BY_ELECTROLYZING)
                .components(Calcium, 1, Fluorine, 2)
                .buildAndRegister();

        Forsterite = new Material.Builder(SuSyValues.susyId("forsterite"))
                .dust()
                .gem()
                .color(0x1d640f).iconSet(LAPIS)
                .flags(NO_SMASHING, DECOMPOSITION_BY_ELECTROLYZING)
                .components(Magnesium, 2, Sulfur, 1, Oxygen, 4)
                .formula("Mg2(SiO4)", true)
                .buildAndRegister();

        Lizardite = new Material.Builder(SuSyValues.susyId("lizardite"))
                .dust()
                .color(0xa79e42)
                .flags(NO_SMASHING, DECOMPOSITION_BY_ELECTROLYZING)
                .components(Magnesium, 3, Silicon, 2, Oxygen, 9, Hydrogen, 4)
                .formula("Mg3Si2O5(OH)4", true)
                .buildAndRegister();

        // Flourinated Ketones

        Perfluoro2Methyl3Pentanone = new Material.Builder(SuSyValues.susyId("perfluoro_2_methyl_3_pentanone"))
                .liquid(new FluidBuilder().block())
                .color(0xA090D5FF)
                .flags(DISABLE_DECOMPOSITION)
                .components(Carbon, 6, Fluorine, 12, Oxygen, 1)
                .formula("C6F12O", true)
                .buildAndRegister();

        WarmPerfluoro2Methyl3Pentanone = new Material.Builder(SuSyValues.susyId("warm_perfluoro_2_methyl_3_pentanone"))
                .liquid()
                .color(0xCEE3F0)
                .flags(DISABLE_DECOMPOSITION)
                .components(Carbon, 6, Fluorine, 12, Oxygen, 1)
                .formula("C6F12O", true)
                .buildAndRegister();

        // Thermodynamic materials

        PreheatedAir = new Material.Builder(SuSyValues.susyId("preheated_air"))
                .gas(new FluidBuilder().temperature(1000))
                .color(0xA9D0F5)
                .flags(DISABLE_DECOMPOSITION)
                .components(Nitrogen, 78, Oxygen, 21, Argon, 9)
                .buildAndRegister();

        RP_1 = new Material.Builder(SuSyValues.susyId("rp_1"))
                .liquid()
                .color(0xb50707)
                .flags(FLAMMABLE)
                .buildAndRegister();

        // Aluminium Alloys

        AluminiumAlloy6061 = new Material.Builder(SuSyValues.susyId("aluminium_alloy_6061"))
                .ingot().liquid(new FluidBuilder().temperature(923))
                .color(0x8aa1e5)
                .flags(DISABLE_DECOMPOSITION, SuSyMaterialFlags.CONTINUOUSLY_CAST)
                .components(Aluminium, 634, Magnesium, 8, Silicon, 4, Copper, 1, Chromium, 1)
                .buildAndRegister();
        AluminiumAlloy6061.addFlags(GENERATE_FINE_WIRE);

        AluminiumAlloy7075 = new Material.Builder(SuSyValues.susyId("aluminium_alloy_7075"))
                .ingot().liquid(new FluidBuilder().temperature(913))
                .color(0x9fe9ef)
                .flags(DISABLE_DECOMPOSITION, SuSyMaterialFlags.CONTINUOUSLY_CAST)
                .components(Aluminium, 678, Zinc, 17, Magnesium, 20, Copper, 4, Chromium, 1)
                .buildAndRegister();
        AluminiumAlloy7075.addFlags(GENERATE_FINE_WIRE);
    }
}
