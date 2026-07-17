package io.github.symmetricdevs.supersymmetry.modules;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.modules.GregTechModule;
import com.gregtechceu.gtceu.api.modules.IGregTechModule;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.SusyLog;
import io.github.symmetricdevs.supersymmetry.common.network.CPacketRocketInteract;
import io.github.symmetricdevs.supersymmetry.common.network.SPacketFirstJoin;
import io.github.symmetricdevs.supersymmetry.common.network.SPacketRemoveFluidState;
import io.github.symmetricdevs.supersymmetry.common.network.SPacketUpdateRenderMask;

@GregTechModule(
                moduleID = SuSyModules.MODULE_CORE,
                containerID = Supersymmetry.MODID,
                name = "SuSy Core",
                description = "Core module of SuSy Core, so this should be called SuSy Core Core ngl.",
                coreModule = true)
public class SuSyCoreModule implements IGregTechModule {

    @Override
    public @NotNull Logger getLogger() {
        return SusyLog.logger;
    }

    @Override
    public void registerPackets() {
        GregTechAPI.networkHandler.registerPacket(SPacketRemoveFluidState.class);
        GregTechAPI.networkHandler.registerPacket(SPacketFirstJoin.class);
        GregTechAPI.networkHandler.registerPacket(CPacketRocketInteract.class);
        GregTechAPI.networkHandler.registerPacket(SPacketUpdateRenderMask.class);
    }
}
