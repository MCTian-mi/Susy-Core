package io.github.symmetricdevs.supersymmetry.common.materials;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.FLAMMABLE;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.AdvancedCoolant;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Coolant;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Latex;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.LubricatingOil;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.MidgradeLubricant;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Mud;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.PremiumLubricant;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.RefractoryGunningMixture;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.Seawater;
import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.SupremeLubricant;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;

import io.github.symmetricdevs.supersymmetry.SuSyValues;

public class SuSyUnknownCompositionMaterials {

    public static void init() {
        Latex = new Material.Builder(SuSyValues.susyId("latex"))
                .dust().fluid(FluidStorageKeys.LIQUID, new FluidBuilder().temperature(293))
                .color(0xFFFADA)
                .buildAndRegister();

        Mud = new Material.Builder(SuSyValues.susyId("mud"))
                .liquid()
                .color(0x211b14)
                .buildAndRegister();

        Seawater = new Material.Builder(SuSyValues.susyId("sea_water"))
                .liquid()
                .color(0x3c5bc2)
                .buildAndRegister();

        RefractoryGunningMixture = new Material.Builder(SuSyValues.susyId("refractory_gunning_mixture"))
                .liquid()
                .color(0x9c775c)
                .buildAndRegister();

        MidgradeLubricant = new Material.Builder(SuSyValues.susyId("midgrade_lubricant"))
                .liquid()
                .color(0xc7aa2a)
                .buildAndRegister();

        PremiumLubricant = new Material.Builder(SuSyValues.susyId("premium_lubricant"))
                .liquid()
                .color(0xba831c)
                .buildAndRegister();

        SupremeLubricant = new Material.Builder(SuSyValues.susyId("supreme_lubricant"))
                .liquid()
                .color(0xad5f10)
                .buildAndRegister();

        Coolant = new Material.Builder(SuSyValues.susyId("coolant"))
                .liquid()
                .color(0x46dde8)
                .buildAndRegister();

        AdvancedCoolant = new Material.Builder(SuSyValues.susyId("advanced_coolant"))
                .liquid()
                .color(0x33f5ee)
                .buildAndRegister();

        LubricatingOil = new Material.Builder(SuSyValues.susyId("lubricating_oil"))
                .liquid()
                .color(0x858146)
                .flags(FLAMMABLE)
                .buildAndRegister();
    }
}
