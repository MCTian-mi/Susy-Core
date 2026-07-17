package io.github.symmetricdevs.supersymmetry.api.capability.impl;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.PseudoMultiMachineMetaTileEntity;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.PseudoMultiProperty;

public class PseudoMultiRecipeLogic extends RecipeLogicEnergy {

    private final PseudoMultiMachineMetaTileEntity pmMTE;

    public PseudoMultiRecipeLogic(PseudoMultiMachineMetaTileEntity BlockEntity, GTRecipeType GTRecipeType,
                                  Supplier<IEnergyContainer> energyContainer) {
        super(BlockEntity, GTRecipeType, energyContainer);
        this.pmMTE = BlockEntity;
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe) {
        if (pmMTE.getTargetBlockState() == null) return false; // if world was remote or null
        return !recipe.hasProperty(PseudoMultiProperty.getInstance()) ||
                recipe.getProperty(PseudoMultiProperty.getInstance(), null)
                        .getValidBlockStates().contains(pmMTE.getTargetBlockState()) && super.checkRecipe(recipe);
    }

    @Override
    public boolean canProgressRecipe() {
        return previousRecipe == null || !previousRecipe.hasProperty(PseudoMultiProperty.getInstance()) ||
                previousRecipe.getProperty(PseudoMultiProperty.getInstance(), null).getValidBlockStates()
                        .contains(pmMTE.getTargetBlockState()) && super.canProgressRecipe();
    }
}
