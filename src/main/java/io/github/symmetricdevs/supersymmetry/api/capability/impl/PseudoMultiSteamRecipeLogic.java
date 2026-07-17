package io.github.symmetricdevs.supersymmetry.api.capability.impl;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.IFluidTank;

import org.jetbrains.annotations.NotNull;


import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.PseudoMultiSteamMachineMetaTileEntity;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.PseudoMultiProperty;

public class PseudoMultiSteamRecipeLogic extends RecipeLogicSteam {

    private final PseudoMultiSteamMachineMetaTileEntity pmsMTE;

    public PseudoMultiSteamRecipeLogic(PseudoMultiSteamMachineMetaTileEntity BlockEntity, GTRecipeType GTRecipeType,
                                       boolean isHighPressure, IFluidTank steamFluidTank, double conversionRate) {
        super(BlockEntity, GTRecipeType, isHighPressure, steamFluidTank, conversionRate);
        this.pmsMTE = BlockEntity;
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe) {
        if (pmsMTE.getTargetBlockState() == null) return false; // if world was remote or null
        // if no property was given don't check if state matches
        return !recipe.hasProperty(PseudoMultiProperty.getInstance()) ||
                recipe.getProperty(PseudoMultiProperty.getInstance(), null)
                        .getValidBlockStates().contains(pmsMTE.getTargetBlockState()) && super.checkRecipe(recipe);
    }

    @Override
    public boolean canProgressRecipe() {
        // recipe stalled due to valid block removal will complete on world reload
        return previousRecipe == null || !previousRecipe.hasProperty(PseudoMultiProperty.getInstance()) ||
                previousRecipe.getProperty(PseudoMultiProperty.getInstance(), null).getValidBlockStates()
                        .contains(pmsMTE.getTargetBlockState()) && super.canProgressRecipe();
    }

    @Override
    public void onFrontFacingSet(Direction newFrontFacing) {
        super.onFrontFacingSet(newFrontFacing);
        if (getVentingSide() == pmsMTE.getFrontFacing() || getVentingSide() == pmsMTE.getFrontFacing().getOpposite()) {
            setVentingSide(newFrontFacing.rotateY());
        }
    }
}
