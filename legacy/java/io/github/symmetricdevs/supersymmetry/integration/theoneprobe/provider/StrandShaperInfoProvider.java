package io.github.symmetricdevs.supersymmetry.integration.theoneprobe.provider;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.gregtechceu.gtceu.api.util.TextFormattingUtil;
import mcjty.theoneprobe.api.*;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.capability.IStrandProvider;
import io.github.symmetricdevs.supersymmetry.api.capability.StrandConversion;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric.strand.MetaTileEntityStrandShaper;

public class StrandShaperInfoProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return Supersymmetry.MODID + ":strand_shaper_info_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo probeInfo, Player Player, World world,
                             BlockState blockState, IProbeHitData data) {
        if (blockState.getBlock().hasTileEntity(blockState)) {
            BlockEntity BlockEntity = world.getTileEntity(data.getPos());
            if (!(BlockEntity instanceof IGregTechTileEntity)) return;

            MetaTileEntity metaTileEntity = ((IGregTechTileEntity) BlockEntity).getMetaTileEntity();
            if (metaTileEntity instanceof MetaTileEntityStrandShaper shaper) {
                if (shaper.getEnergyContainer() == null) {
                    return;
                }
                long EUt = shaper.getVoltage();
                String text = ChatFormatting.RED + TextFormattingUtil.formatNumbers(EUt) + TextStyleClass.INFO +
                        " EU/t" + ChatFormatting.GREEN +
                        " (" + GTValues.VN[GTUtility.getTierByVoltage(EUt)] + ChatFormatting.GREEN + ")";
                probeInfo.text(TextStyleClass.INFO + "{*gregtech.top.energy_consumption*} " + text);
            } else if (metaTileEntity instanceof IStrandProvider bus) {
                if (bus.getStrand() == null) {
                    probeInfo.text(TextStyleClass.INFO + "{*supersymmetry.top.no_strand*}");
                } else {
                    probeInfo
                            .text(TextStyleClass.INFO + "{*supersymmetry.top.thickness*} " + bus.getStrand().thickness);
                    probeInfo.text(TextStyleClass.INFO + "{*supersymmetry.top.width*} " + bus.getStrand().width);
                    probeInfo.text(TextStyleClass.INFO + "{*supersymmetry.top.material*} " +
                            bus.getStrand().material.getLocalizedName());
                    StrandConversion conversion = StrandConversion.getConversion(bus.getStrand());
                    if (conversion == null) {
                        probeInfo.text(TextStyleClass.WARNING + "{*supersymmetry.top.strand_not_usable*}");
                    } else {
                        probeInfo
                                .text(TextStyleClass.INFO + "{*supersymmetry.top.conversion*} {*supersymmetry.prefix." +
                                        conversion.prefix.name.toLowerCase() + "*}");

                    }
                }
            }
        }
    }
}
