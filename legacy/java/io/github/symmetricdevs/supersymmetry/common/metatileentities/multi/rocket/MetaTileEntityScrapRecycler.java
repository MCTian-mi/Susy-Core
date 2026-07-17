package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.rocket;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.blocks.BlockBoilerCasing;
import com.gregtechceu.gtceu.common.block.CasingBlock;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;

public class MetaTileEntityScrapRecycler extends WorkableElectricMultiblockMachine {

    public MetaTileEntityScrapRecycler(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.SCRAP_RECYCLER);
        this.recipeMapWorkable = new RecipeLogic(this, true);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityScrapRecycler(this.metaTileEntityId);
    }

    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle(" CCC ", "CCCCC", "COOOC", "CCCCC", " CCC ")
                .aisle(" CCC ", "PAAAP", "PAAAP", "PAAAP", " CDC ")
                .aisle(" CCC ", "PAAAP", "PAAAP", "PAAAP", " CDC ")
                .aisle(" CCC ", "PAAAP", "PAAAP", "PAAAP", " CDC ")
                .aisle(" CCC ", "PAAAP", "PAAAP", "PAAAP", " CDC ")
                .aisle(" CCC ", "PAAAP", "PAAAP", "PAAAP", " CDC ")
                .aisle(" CCC ", "CISIC", "CCCCC", "PAAAP", " CCC ")
                .where(' ', any())
                .where('A', air())
                .where('S', this.selfPredicate())
                .where('P', states(getPipeCasingState()))
                .where('C', states(getCasingState()))
                .where('I',
                        states(getCasingState()).or(this.autoAbilities(false, true, true, false, false, false, false)))
                .where('O',
                        states(getCasingState()).or(this.autoAbilities(false, true, false, true, false, false, false)))
                .where('D',
                        states(getCasingState()).or(this.autoAbilities(true, true, false, false, false, false, false)))
                .build();
    }

    protected static BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.TITANIUM_STABLE);
    }

    protected static BlockState getPipeCasingState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TITANIUM_PIPE);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.STABLE_TITANIUM_CASING;
    }
}
