package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;

import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeBuilder;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.blocks.BlockBoilerCasing;
import com.gregtechceu.gtceu.common.block.CasingBlock;
import com.gregtechceu.gtceu.common.blocks.BlockTurbineCasing;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.api.recipes.logic.SuSyParallelLogic;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockSuSyMultiblockCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class MetaTileEntityMetallurgicalConverter extends WorkableElectricMultiblockMachine {

    public MetaTileEntityMetallurgicalConverter(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.METALLURGICAL_CONVERTER);
        this.recipeMapWorkable = new MetallurgicalConverterLogic(this);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.BACK, RelativeDirection.UP, RelativeDirection.RIGHT)
                .aisle(" F   F ", " F   F ", " F   F ", " F   F ", " FF FF ", "  FAF  ", "  AGA  ", "   A   ",
                        "       ", "       ")
                .aisle("HHHHHH ", "HFFFFF ", "H ###  ", "  ###  ", "  BBB  ", "  VVV  ", "  BBB  ", "  VVV  ",
                        "  BBB  ", "       ")
                .aisle(" HHHHHH", " H###FH", " ##### ", " #BBB# ", " BRRRB ", " VRRRV ", " BRRRB ", " VRRRV ",
                        " BRRRB ", "  BBB  ")
                .aisle("  HHHHH", "  #P#FS", " ##P## ", " #BBB# ", " BRRRB ", " VR#RV ", " BR#RB ", " VR#RV ",
                        " BR#RB ", "  BMB  ")
                .aisle(" HHHHHH", " H###FH", " ##### ", " #BBB# ", " BRRRB ", " VRRRV ", " BRRRB ", " VRRRV ",
                        " BRRRB ", "  BBB  ")
                .aisle("HHHHHH ", "HFFFFF ", "H ###  ", "  ###  ", "  BBB  ", "  VVV  ", "  BBB  ", "  VVV  ",
                        "  BBB  ", "       ")
                .aisle(" F   F ", " F   F ", " F   F ", " F   F ", " FF FF ", "  FAF  ", "  AGA  ", "   A   ",
                        "       ", "       ")
                .where('#', air())
                .where('S', selfPredicate())
                .where('R', states(getRefractoryState()))
                .where('H',
                        states(getCasingState()).setMinGlobalLimited(31)
                                .or(autoAbilities(true, true, true, true, true, true, false)))
                .where('A', states(getCasingState()))
                .where('B',
                        states(GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                                .getState(BlockLargeMultiblockCasing.CasingType.STRESS_PROOF_CASING)))
                .where('F', frames(Materials.Steel))
                .where('V', states(GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.HEAT_VENT)))
                .where('G',
                        states(MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX)))
                .where('P', states(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE)))
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where(' ', any())
                .build();
    }

    protected Direction getRelativeFacing(RelativeDirection dir) {
        return dir.getRelativeFacing(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    private BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    private BlockState getRefractoryState() {
        return SuSyBlocks.MULTIBLOCK_CASING.getState(BlockSuSyMultiblockCasing.CasingType.TABULAR_ALUMINA_REFRACTORY);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityMetallurgicalConverter(metaTileEntityId);
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return SusyTextures.METALLURGICAL_CONVERTER_OVERLAY;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("susy.machine.parallel_pure", 64));
    }

    private class MetallurgicalConverterLogic extends RecipeLogic {

        public MetallurgicalConverterLogic(WorkableElectricMultiblockMachine BlockEntity) {
            super(BlockEntity);
        }

        @Override
        public int getParallelLimit() {
            return 64;
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
