package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric.strand;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyMultiblockAbilities;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityBilletMold extends MetaTileEntityStrandMold {

    public MetaTileEntityBilletMold(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected int getRequiredMetal() {
        return 2592;
    }

    @Override
    protected double getOutputThickness() {
        return 1 / 3.;
    }

    @Override
    protected double getOutputWidth() {
        return 1 / 3.;
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("#CCC#", "#CCC#", "#CCC#", "#CCC#", "#CCC#")
                .aisle("COOOC", "CPPPC", "CPPPC", "CPPPC", "CIIIC")
                .aisle("COMOC", "CP PC", "CP PC", "CP PC", "CIIIC")
                .aisle("COOOC", "CPPPC", "CPPPC", "CPPPC", "CIIIC")
                .aisle("#CCC#", "#CCC#", "#CSC#", "#CCC#", "#CCC#")
                .where('C', states(getCasingState()).or(autoAbilities()))
                .where('P', states(getPipeCasingState()))
                .where('M', abilities(SuSyMultiblockAbilities.STRAND_EXPORT))
                .where('I',
                        abilities(MultiblockAbility.IMPORT_FLUIDS).setPreviewCount(1).or(states(getPipeCasingState())))
                .where('O',
                        abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(1).or(states(getPipeCasingState())))
                .where('S', selfPredicate())
                .where(' ', air())
                .where('#', any())
                .build();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityBilletMold(metaTileEntityId);
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return SusyTextures.BILLET_MOLD_OVERLAY;
    }
}
