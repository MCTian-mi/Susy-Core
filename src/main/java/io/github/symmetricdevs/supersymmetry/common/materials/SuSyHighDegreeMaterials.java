package io.github.symmetricdevs.supersymmetry.common.materials;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_FOIL;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Carbon;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Gold;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Hydrogen;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Oxygen;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.MetallizedBoPET;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import io.github.symmetricdevs.supersymmetry.SuSyValues;

public class SuSyHighDegreeMaterials {

    public static void init() {
        // The gold content is much less than this
        // Placed at the end of ThirdDegreeMaterials.groovy
        MetallizedBoPET = new Material.Builder(SuSyValues.susyId("metallized_bopet"))
                .polymer()
                .flags(GENERATE_FOIL)
                .components(Carbon, 10, Hydrogen, 6, Oxygen, 4, Gold, 1)
                .color(0x7e9e8e)
                .buildAndRegister();
    }
}
