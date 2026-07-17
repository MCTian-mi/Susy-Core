package io.github.symmetricdevs.supersymmetry.integration.theoneprobe;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.event.FMLInitializationEvent;

import com.gregtechceu.gtceu.api.modules.GregTechModule;
import com.gregtechceu.gtceu.api.util.Mods;
import com.gregtechceu.gtceu.integration.IntegrationSubmodule;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;
import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.integration.theoneprobe.provider.DelegatorInfoProvider;
import io.github.symmetricdevs.supersymmetry.integration.theoneprobe.provider.EvaporationPoolInfoProvider;
import io.github.symmetricdevs.supersymmetry.integration.theoneprobe.provider.LittleTilesStorageInfoProvider;
import io.github.symmetricdevs.supersymmetry.integration.theoneprobe.provider.StrandShaperInfoProvider;
import io.github.symmetricdevs.supersymmetry.modules.SuSyModules;

@GregTechModule(
                moduleID = SuSyModules.MODULE_TOP,
                containerID = Supersymmetry.MODID,
                modDependencies = Mods.Names.THE_ONE_PROBE,
                name = "SuSy TheOneProbe Integration",
                description = "SuSy TheOneProbe Integration Module")
public class TheOneProbeModule extends IntegrationSubmodule {

    @Override
    public void init(FMLInitializationEvent event) {
        getLogger().info("TheOneProbe found. Enabling SuSy top integration...");
        ITheOneProbe oneProbe = TheOneProbe.theOneProbeImp;
        oneProbe.registerProvider(new EvaporationPoolInfoProvider());
        oneProbe.registerProvider(new DelegatorInfoProvider());
        oneProbe.registerProvider(new StrandShaperInfoProvider());
        if (Loader.isModLoaded(SuSyValues.MODID_LITTLE_TILES)) {
            oneProbe.registerProvider(new LittleTilesStorageInfoProvider());
        }
    }
}
