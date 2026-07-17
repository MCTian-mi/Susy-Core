package io.github.symmetricdevs.supersymmetry.common.metatileentities.multiblockpart;

import java.util.List;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

import com.gregtechceu.gtceu.api.gui.ModularUI;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;

import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MetaTileEntityMultiblockPart;
import io.github.symmetricdevs.supersymmetry.api.capability.SuSyCapabilities;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyMultiblockAbilities;
import io.github.symmetricdevs.supersymmetry.api.particle.IParticleBeamProvider;
import io.github.symmetricdevs.supersymmetry.api.particle.ParticleBeam;

public class MetaTileEntityBeamLineHatch extends MetaTileEntityMultiblockPart implements IParticleBeamProvider,
                                         IMultiblockAbilityPart<IParticleBeamProvider> {

    private ParticleBeam particleBeam;
    private boolean isExport;

    public MetaTileEntityBeamLineHatch(ResourceLocation metaTileEntityId, boolean isExport) {
        super(metaTileEntityId, 4);
        this.isExport = isExport;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityBeamLineHatch(metaTileEntityId, isExport);
    }

    @Override
    protected ModularUI createUI(Player Player) {
        return null;
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }

    @Override
    public MultiblockAbility<IParticleBeamProvider> getAbility() {
        return isExport ? SuSyMultiblockAbilities.BEAM_EXPORT : SuSyMultiblockAbilities.BEAM_IMPORT;
    }

    @Override
    public void registerAbilities(List<IParticleBeamProvider> abilityList) {
        abilityList.add(this);
    }

    @Override
    public ParticleBeam getParticleBeam() {
        return this.particleBeam;
    }

    @Override
    public ParticleBeam insertBeam(ParticleBeam beam) {
        if (this.particleBeam != null) {
            return beam;
        }
        this.particleBeam = beam;
        return null;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, Direction side) {
        if (capability == SuSyCapabilities.PARTICLE_BEAM_PROVIDER) {
            return SuSyCapabilities.PARTICLE_BEAM_PROVIDER.cast(this);
        }
        return super.getCapability(capability, side);
    }
}
