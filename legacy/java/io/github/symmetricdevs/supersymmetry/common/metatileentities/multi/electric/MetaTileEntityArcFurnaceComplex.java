package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;

import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeBuilder;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import io.github.symmetricdevs.supersymmetry.api.recipes.logic.SuSyParallelLogic;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockElectrodeAssembly;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockSuSyMultiblockCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class MetaTileEntityArcFurnaceComplex extends MetaTileEntityAdvancedArcFurnace {

    public MetaTileEntityArcFurnaceComplex(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        this.recipeMapWorkable = new ArcFurnaceComplexLogic(this);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityArcFurnaceComplex(this.metaTileEntityId);
    }

    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("               ", "  AAA     AAA  ", "  AAA     AAA  ", "  EEE     EEE  ", "  AAA     AAA  ",
                        "  EEE     EEE  ", "  AAA     AAA  ", "               ", "               ", "               ",
                        "               ", "               ")
                .aisle("  AAA     AAA  ", " AAAAA   AAAAA ", " ABBBA   ABBBA ", " EBBBE   EBBBE ", " A###A   A###A ",
                        " E###E   E###E ", " A###A   A###A ", "  AAA     AAA  ", "               ", "               ",
                        "  AAA     AAA  ", "               ")
                .aisle(" AAAAA   AAAAA ", "AABBBAA AABBBAA", "AB###BA AB###BA", "EB###BE EB###BE", "A#C#C#A A#C#C#A",
                        "E#C#C#E E#C#C#E", "A#C#C#A A#C#C#A", " ACACA   ACACA ", "  C C     C C  ", "  C C     C C  ",
                        " ACACA   ACACA ", "  C C     C C  ")
                .aisle(" AAAAA   AAAAA ", "AABBBAA AABBBAA", "AB###BA AB###BA", "EB###BE EB###BE", "A#####A A#####A",
                        "E#####E E#####E", "A#####A A#####A", " AAAAA   AAAAA ", "               ", "               ",
                        " AAAAA   AAAAA ", "               ")
                .aisle(" AAAAA   AAAAA ", "AABBBAA AABBBAA", "AB###BA AB###BA", "EB###BE EB###BE", "A##C##A A##C##A",
                        "E##C##E E##C##E", "A##C##A A##C##A", " AACAA   AACAA ", "   C       C   ", "   C       C   ",
                        " AACAA   AACAA ", "   C       C   ")
                .aisle("  AAA     AAA  ", " AAAAA   AAAAA ", " ABBBA   ABBBA ", " EBBBE   EBBBE ", " A###A   A###A ",
                        " E###E   E###E ", " A###A   A###A ", "  AAA     AAA  ", "               ", "               ",
                        "  A A     A A  ", "               ")
                .aisle("      HHH      ", "  AAA FHF AAA  ", "  AAA  F  AAA  ", "  EEE     EEE  ", "  AAA     AAA  ",
                        "  EEE     EEE  ", "  AAA     AAA  ", "  FFF     FFF  ", "  FFF     FFF  ", "  FFF     FFF  ",
                        "  DDD     DDD  ", "               ")
                .aisle("      HHH      ", "      HHH      ", "      FFF      ", "       F       ", "       F       ",
                        "       F       ", "       F       ", "  FFFFFFFFFFF  ", "               ", "               ",
                        "               ", "               ")
                .aisle("      HHH      ", "  AAA FSF AAA  ", "  AAA  F  AAA  ", "  EEE     EEE  ", "  AAA     AAA  ",
                        "  EEE     EEE  ", "  AAA     AAA  ", "  FFF     FFF  ", "  FFF     FFF  ", "  FFF     FFF  ",
                        "  DDD     DDD  ", "               ")
                .aisle("  AAA     AAA  ", " AAAAA   AAAAA ", " ABBBA   ABBBA ", " EBBBE   EBBBE ", " A###A   A###A ",
                        " E###E   E###E ", " A###A   A###A ", "  AAA     AAA  ", "               ", "               ",
                        "  A A     A A  ", "               ")
                .aisle(" AAAAA   AAAAA ", "AABBBAA AABBBAA", "AB###BA AB###BA", "EB###BE EB###BE", "A##C##A A##C##A",
                        "E##C##E E##C##E", "A##C##A A##C##A", " AACAA   AACAA ", "   C       C   ", "   C       C   ",
                        " AACAA   AACAA ", "   C       C   ")
                .aisle(" AAAAA   AAAAA ", "AABBBAA AABBBAA", "AB###BA AB###BA", "EB###BE EB###BE", "A#####A A#####A",
                        "E#####E E#####E", "A#####A A#####A", " AAAAA   AAAAA ", "               ", "               ",
                        " AAAAA   AAAAA ", "               ")
                .aisle(" AAAAA   AAAAA ", "AABBBAA AABBBAA", "AB###BA AB###BA", "EB###BE EB###BE", "A#C#C#A A#C#C#A",
                        "E#C#C#E E#C#C#E", "A#C#C#A A#C#C#A", " ACACA   ACACA ", "  C C     C C  ", "  C C     C C  ",
                        " ACACA   ACACA ", "  C C     C C  ")
                .aisle("  AAA     AAA  ", " AAAAA   AAAAA ", " ABBBA   ABBBA ", " EBBBE   EBBBE ", " A###A   A###A ",
                        " E###E   E###E ", " A###A   A###A ", "  AAA     AAA  ", "               ", "               ",
                        "  AAA     AAA  ", "               ")
                .aisle("               ", "  AAA     AAA  ", "  AAA     AAA  ", "  EEE     EEE  ", "  AAA     AAA  ",
                        "  EEE     EEE  ", "  AAA     AAA  ", "               ", "               ", "               ",
                        "               ", "               ")
                .where('S', selfPredicate())
                .where('A',
                        states(GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                                .getState(BlockLargeMultiblockCasing.CasingType.STRESS_PROOF_CASING)))
                .where('B',
                        states(SuSyBlocks.MULTIBLOCK_CASING
                                .getState(BlockSuSyMultiblockCasing.CasingType.TABULAR_ALUMINA_REFRACTORY)))
                .where('C',
                        states(SuSyBlocks.ELECTRODE_ASSEMBLY
                                .getState(BlockElectrodeAssembly.ElectrodeAssemblyType.CARBON)))
                .where('D',
                        states(GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                                .getState(BlockLargeMultiblockCasing.CasingType.STRESS_PROOF_CASING))
                                        .setMinGlobalLimited(8)
                                        .or(autoAbilities(true, false, false, false, false, false, false)))
                .where('E', states(GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.HEAT_VENT)))
                .where('F', frames(Materials.Steel))
                .where('H',
                        states(GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                                .getState(BlockLargeMultiblockCasing.CasingType.STRESS_PROOF_CASING))
                                        .setMinGlobalLimited(6)
                                        .or(autoAbilities(false, true, true, true, true, true, false)))
                .where(' ', any())
                .where('#', air())
                .build();
    }

    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GCYMTextures.STRESS_PROOF_CASING;
    }

    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("susy.machine.parallel_pure", 256));
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return SusyTextures.ARC_FURNACE_OVERLAY;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    private class ArcFurnaceComplexLogic extends RecipeLogic {

        public ArcFurnaceComplexLogic(WorkableElectricMultiblockMachine BlockEntity) {
            super(BlockEntity);
        }

        @Override
        public int getParallelLimit() {
            return 256;
        }

        @Override
        protected long getMaxParallelVoltage() {
            return 2147432767L;
        }

        @Override
        public Recipe findParallelRecipe(@NotNull Recipe currentRecipe, @NotNull IItemHandlerModifiable inputs,
                                         @NotNull IMultipleTankHandler fluidInputs,
                                         @NotNull IItemHandlerModifiable outputs,
                                         @NotNull IMultipleTankHandler fluidOutputs, long maxVoltage,
                                         int parallelLimit) {
            if (parallelLimit > 1 && this.getRecipeMap() != null) {
                RecipeBuilder<?> parallelBuilder;
                parallelBuilder = SuSyParallelLogic.pureParallelRecipe(currentRecipe, this.getRecipeMap(), inputs,
                        fluidInputs, outputs, fluidOutputs, parallelLimit, maxVoltage, this.getMetaTileEntity());

                if (parallelBuilder == null) {
                    this.invalidateInputs();
                    return null;
                } else if (parallelBuilder.getParallel() == 0) {
                    this.invalidateOutputs();
                    return null;
                } else {
                    this.setParallelRecipesPerformed(parallelBuilder.getParallel());
                    this.applyParallelBonus(parallelBuilder);
                    return parallelBuilder.build().getResult();
                }
            } else {
                return currentRecipe;
            }
        }
    }
}
