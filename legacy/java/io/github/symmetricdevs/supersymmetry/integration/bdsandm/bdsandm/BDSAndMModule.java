package io.github.symmetricdevs.supersymmetry.integration.bdsandm;

import net.minecraft.world.level.block.BlockDirectional;
import net.minecraft.core.Direction;
import net.minecraftforge.event.FMLInitializationEvent;

import funwayguy.bdsandm.core.BDSM;
import com.gregtechceu.gtceu.api.cover.CoverRayTracer;
import com.gregtechceu.gtceu.api.modules.GregTechModule;
import com.gregtechceu.gtceu.common.items.tool.rotation.CustomBlockRotations;
import com.gregtechceu.gtceu.common.items.tool.rotation.ICustomRotationBehavior;
import com.gregtechceu.gtceu.integration.IntegrationSubmodule;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.SusyLog;
import io.github.symmetricdevs.supersymmetry.modules.SuSyModules;

@GregTechModule(
                moduleID = SuSyModules.MODULE_BDSAndM,
                containerID = Supersymmetry.MODID,
                modDependencies = "bdsandm",
                name = "SuSy BDSAndM Integration",
                description = "SuSy BDSAndM Integration Module")
public class BDSAndMModule extends IntegrationSubmodule {

    public static final ICustomRotationBehavior BDSAndM_BARREL_BEHAVIOR = (state, world, pos, hitResult) -> {
        Direction gridSide = CoverRayTracer.determineGridSideHit(hitResult);
        if (gridSide == null) return false;
        gridSide = gridSide.getOpposite(); // IDK what's happening here, blame the original author
        if (gridSide != state.getValue(BlockDirectional.FACING)) {
            world.setBlockState(pos, state.withProperty(BlockDirectional.FACING, gridSide));
            return true;
        }
        return false;
    };

    @Override
    public void init(FMLInitializationEvent event) {
        SusyLog.logger.info("BDSAndM found. Enabling integration...");
        CustomBlockRotations.registerCustomRotation(BDSM.blockMetalBarrel, BDSAndM_BARREL_BEHAVIOR);
        CustomBlockRotations.registerCustomRotation(BDSM.blockWoodBarrel, BDSAndM_BARREL_BEHAVIOR);
        CustomBlockRotations.registerCustomRotation(BDSM.blockMetalCrate, BDSAndM_BARREL_BEHAVIOR);
        CustomBlockRotations.registerCustomRotation(BDSM.blockWoodCrate, BDSAndM_BARREL_BEHAVIOR);
    }
}
