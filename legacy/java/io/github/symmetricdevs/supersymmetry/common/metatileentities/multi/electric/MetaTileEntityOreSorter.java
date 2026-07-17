package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.blocks.BlockBoilerCasing.BoilerCasingType;
import com.gregtechceu.gtceu.common.block.CasingBlock.MetalCasingType;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityOreSorter extends WorkableElectricMultiblockMachine {

    public MetaTileEntityOreSorter(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.ORE_SORTER_RECIPES);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityOreSorter(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle(" C C ", " C C ", " C C ", " D D ")
                .aisle("     ", "     ", "     ", " D D ")
                .aisle("ABBBA", "ABBBA", "ABBBA", " D D ")
                .aisle("ABBBA", "B###B", "ABBBA", " D D ")
                .aisle("ABSBA", "ABBBA", "ABBBA", " D D ")
                .where('S', selfPredicate())
                .where('A', frames(Materials.Steel))
                .where('B', states(MetaBlocks.METAL_CASING.getState(MetalCasingType.STEEL_SOLID))
                        .setMinGlobalLimited(16)
                        .or(autoAbilities(true, true, true, true, false, false, false)))
                .where('C', states(new BlockState[] { MetaBlocks.BOILER_CASING.getState(BoilerCasingType.STEEL_PIPE) })
                        .or(autoAbilities(false, false, false, false, true, true, false)))
                .where('D', frames(Materials.Aluminium))
                .where(' ', any())
                .where('#', air())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return SusyTextures.ORE_SORTER_OVERLAY;
    }
}
