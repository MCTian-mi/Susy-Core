package io.github.symmetricdevs.supersymmetry.common.materials;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.DISABLE_DECOMPOSITION;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.NO_SMASHING;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.ROUGH;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Andradite;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Biotite;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Calcite;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Clay;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Olivine;
import static com.gregtechceu.gtceu.common.data.GTMaterials.SiliconDioxide;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Albite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Anorthite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Anorthosite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Augite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Bytownite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Clinochlore;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Dolomite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Fluorite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Forsterite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Gabbro;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Gneiss;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Kimberlite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Labradorite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Limestone;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Lizardite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Muscovite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Phyllite;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Shale;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Slate;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import io.github.symmetricdevs.supersymmetry.SuSyValues;

public class SuSySecondDegreeMaterials {

    public static void init() {
        Gabbro = new Material.Builder(SuSyValues.susyId("gabbro"))
                .dust()
                .color(0x5C5C5C).iconSet(ROUGH)
                .flags(NO_SMASHING, DISABLE_DECOMPOSITION)
                .components(Labradorite, 5, Bytownite, 3, Olivine, 2, Augite, 1, Biotite, 1)
                .buildAndRegister();

        Gneiss = new Material.Builder(SuSyValues.susyId("gneiss"))
                .dust()
                .color(0x643631).iconSet(ROUGH)
                .flags(NO_SMASHING, DISABLE_DECOMPOSITION)
                .components(Albite, 4, SiliconDioxide, 3, Biotite, 1, Muscovite, 1)
                .buildAndRegister();

        Limestone = new Material.Builder(SuSyValues.susyId("limestone"))
                .dust()
                .color(0xa9a9a9).iconSet(ROUGH)
                .flags(NO_SMASHING, DISABLE_DECOMPOSITION)
                .components(Calcite, 4, Dolomite, 1)
                .buildAndRegister();

        Phyllite = new Material.Builder(SuSyValues.susyId("phyllite"))
                .dust()
                .color(0x716f71).iconSet(ROUGH)
                .flags(NO_SMASHING, DISABLE_DECOMPOSITION)
                .components(Albite, 3, SiliconDioxide, 3, Muscovite, 4)
                .buildAndRegister();

        Shale = new Material.Builder(SuSyValues.susyId("shale"))
                .dust()
                .color(0x3f2e2f).iconSet(ROUGH)
                .flags(NO_SMASHING, DISABLE_DECOMPOSITION)
                .components(Calcite, 6, Clay, 2, SiliconDioxide, 1, Fluorite, 1)
                .buildAndRegister();

        Slate = new Material.Builder(SuSyValues.susyId("slate"))
                .dust()
                .color(0x756869).iconSet(ROUGH)
                .flags(NO_SMASHING, DISABLE_DECOMPOSITION)
                .components(SiliconDioxide, 5, Muscovite, 2, Clinochlore, 2, Albite, 1)
                .buildAndRegister();

        Kimberlite = new Material.Builder(SuSyValues.susyId("kimberlite"))
                .dust()
                .color(0x201313).iconSet(ROUGH)
                .flags(NO_SMASHING, DISABLE_DECOMPOSITION)
                .components(Forsterite, 3, Augite, 3, Andradite, 2, Lizardite, 1)
                .buildAndRegister();

        Anorthosite = new Material.Builder(SuSyValues.susyId("anorthosite"))
                .dust()
                .color(0xcecece).iconSet(ROUGH)
                .flags(NO_SMASHING, DISABLE_DECOMPOSITION)
                .components(Anorthite, 4, Albite, 2)
                .buildAndRegister();
    }
}
