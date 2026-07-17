package io.github.symmetricdevs.supersymmetry.integration.theoneprobe.provider;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import mcjty.theoneprobe.api.*;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.logistics.IDelegator;

public class DelegatorInfoProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return Supersymmetry.MODID + ":delegator_info_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, World world, BlockState state,
                             IProbeHitData data) {
        if (state.getBlock().hasTileEntity(state)) {
            BlockEntity te = world.getTileEntity(data.getPos());
            if (te instanceof IGregTechTileEntity igtte) {
                MetaTileEntity mte = igtte.getMetaTileEntity();
                if (mte instanceof IDelegator delegator) {
                    probeInfo.text(TextStyleClass.INFO + "{*susy.top.delegator.delegating_face*}" +
                            delegator.getDelegatingFacing(data.getSideHit()));
                }
            }
        }
    }
}
