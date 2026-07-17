package io.github.symmetricdevs.supersymmetry.integration.theoneprobe.provider;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import com.gregtechceu.gtceu.utils.GTUtil;
import mcjty.theoneprobe.api.*;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric.MetaTileEntityEvaporationPool;

public class EvaporationPoolInfoProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return Supersymmetry.MODID + ":evaporation_pool_provider";
    }

    @Override
    public void addProbeInfo(@Nonnull ProbeMode mode, @Nonnull IProbeInfo probeInfo, @Nonnull Player player,
                             @Nonnull World world, @Nonnull BlockState blockState, @Nonnull IProbeHitData data) {
        if (GTUtility.getMetaTileEntity(world, data.getPos()) instanceof MetaTileEntityEvaporationPool evapPool) {
            probeInfo.text(TextStyleClass.INFO + "{*susy.top.evaporation_pool_heated_preface*}" + " " +
                    (evapPool.isHeating() ?
                            (ChatFormatting.GREEN + "{*susy.multiblock.evaporation_pool.is_heating*}") :
                            (ChatFormatting.RED + "{*susy.multiblock.evaporation_pool.is_not_heating*}")));
            probeInfo.text(TextStyleClass.INFO + "{*susy.top.evaporation_pool.exposed_blocks*}" + " " +
                    (ChatFormatting.YELLOW + String.valueOf(evapPool.getExposedBlocks())));
            probeInfo.text(TextStyleClass.INFO + "{*susy.top.evaporation_pool.average_speed*}" + " " +
                    (ChatFormatting.AQUA + (String.format("%.2f", evapPool.getAverageSpeed()))) +
                    (ChatFormatting.WHITE + "x"));

        }
    }
}
