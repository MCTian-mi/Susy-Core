package io.github.symmetricdevs.supersymmetry.integration.pyrotech;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static io.github.symmetricdevs.supersymmetry.api.util.SuSyUtility.susyId;
import static io.github.symmetricdevs.supersymmetry.common.metatileentities.SuSyMetaTileEntities.PRIMITIVE_SMELTER;

import net.minecraftforge.event.FMLPreInitializationEvent;

import com.gregtechceu.gtceu.api.modules.GregTechModule;
import com.gregtechceu.gtceu.integration.IntegrationSubmodule;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.SusyLog;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.primitive.MetaTileEntityPrimitiveSmelter;
import io.github.symmetricdevs.supersymmetry.modules.SuSyModules;

@GregTechModule(
                moduleID = SuSyModules.MODULE_PYROTECH,
                containerID = Supersymmetry.MODID,
                modDependencies = "pyrotech",
                name = "SuSy Pyrotech Integration",
                description = "SuSy Pyrotech Integration Module")
public class PyrotechModule extends IntegrationSubmodule {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        SusyLog.logger.info("Pyrotech found. Enabling integration...");
        PRIMITIVE_SMELTER = registerMetaTileEntity(14800,
                new MetaTileEntityPrimitiveSmelter(susyId("primitive_smelter")));
    }
}
