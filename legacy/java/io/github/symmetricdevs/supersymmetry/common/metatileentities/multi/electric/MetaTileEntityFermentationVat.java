package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import javax.annotation.Nonnull;

import net.minecraft.resources.ResourceLocation;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.blocks.BlockMachineCasing.MachineCasingType;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;

public class MetaTileEntityFermentationVat extends WorkableElectricMultiblockMachine {

    public MetaTileEntityFermentationVat(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.FERMENTATION_VAT_RECIPES);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityFermentationVat(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("     ", "     ", " XXX ", " XXX ", " XXX ", "     ")
                .aisle(" F F ", " XXX ", "X###X", "X###X", "X###X", " XXX ")
                .aisle("     ", " XXX ", "X###X", "X###X", "X###X", " XMX ")
                .aisle(" F F ", " XXX ", "X###X", "X###X", "X###X", " XXX ")
                .aisle("     ", "     ", " XXX ", " XSX ", " XXX ", "     ")
                .where('S', selfPredicate())
                .where('X', states(MetaBlocks.MACHINE_CASING.getState(MachineCasingType.ULV))
                        .setMinGlobalLimited(40)
                        .or(autoAbilities(true, true, true, true, true, true, false)))
                .where('F', frames(Materials.Steel))
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where(' ', any())
                .where('#', air())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.VOLTAGE_CASINGS[0];
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.PYROLYSE_OVEN_OVERLAY;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return true;
    }
}
