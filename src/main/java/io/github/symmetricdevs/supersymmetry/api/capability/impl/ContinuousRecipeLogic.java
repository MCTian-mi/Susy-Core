package io.github.symmetricdevs.supersymmetry.api.capability.impl;

import static com.gregtechceu.gtceu.api.GTValues.ULV;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import com.gregtechceu.gtceu.utils.GTUtil;
import io.github.symmetricdevs.supersymmetry.api.recipes.builders.logic.SuSyOverclockingLogic;
import io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystInfo;

public class ContinuousRecipeLogic extends CatalystRecipeLogic {

    public ContinuousRecipeLogic(MetaTileEntity BlockEntity, GTRecipeType<?> GTRecipeType,
                                 Supplier<IEnergyContainer> energyContainer) {
        super(BlockEntity, GTRecipeType, energyContainer);
    }

    @Override
    public boolean prepareRecipe(Recipe recipe) {
        recipe = Recipe.trimRecipeOutputs(recipe, this.getRecipeMap(), this.MetaMachine.getItemOutputLimit(),
                this.MetaMachine.getFluidOutputLimit());

        calculateOverclockLimit(recipe);
        recipe = findParallelRecipe(
                recipe,
                getInputInventory(),
                getInputTank(),
                getOutputInventory(),
                getOutputTank(),
                getMaxParallelVoltage(),
                getParallelLimit());

        if (recipe != null && this.setupAndConsumeRecipeInputs(recipe, this.getInputInventory())) {
            this.setupRecipe(recipe);
            return true;
        }
        return false;
    }

    @Override
    protected int[] runOverclockingLogic(@NotNull IRecipePropertyStorage propertyStorage, int recipeEUt,
                                         long maxVoltage, int duration, int amountOC) {
        double[] overclock = runContinuousOverclockingLogic(recipeEUt, maxVoltage, duration, amountOC);
        return new int[] { (int) overclock[0], overclock[1] <= 1 ? 1 : (int) overclock[1] };
    }

    protected double[] runContinuousOverclockingLogic(int recipeEUt, long maxVoltage, int duration, int amountOC) {
        if (requiredCatalystTier != CatalystInfo.NO_TIER && catalystInfo != null) {
            return SuSyOverclockingLogic.continuousCatalystOverclockingLogic(
                    recipeEUt,
                    maxVoltage,
                    duration,
                    amountOC,
                    catalystInfo,
                    requiredCatalystTier,
                    getOverclockingDurationDivisor(),
                    getOverclockingVoltageMultiplier());
        } else {
            return SuSyOverclockingLogic.continuousOverclockingLogic(
                    recipeEUt,
                    maxVoltage,
                    duration,
                    amountOC,
                    this.getOverclockingDurationDivisor(),
                    this.getOverclockingVoltageMultiplier());
        }
    }

    protected void calculateOverclockLimit(Recipe recipe) {
        if (!isAllowOverclocking()) return;

        int recipeTier = GTUtility.getTierByVoltage(recipeEUt);
        int maximumTier = getOverclockForTier(getMaximumOverclockVoltage());
        if (maximumTier <= GTValues.LV) return;

        // The maximum number of overclocks is determined by the difference between the tier the recipe is running at,
        // and the maximum tier that the machine can overclock to.
        int numberOfOCs = maximumTier - recipeTier;
        if (recipeTier == ULV) numberOfOCs--; // no ULV overclocking

        double parallelLimitDouble = 1 / runContinuousOverclockingLogic(recipe.getEUt(), getMaximumOverclockVoltage(),
                recipe.getDuration(), numberOfOCs)[1];

        setParallelLimit(parallelLimitDouble <= 1 ? 1 : (int) parallelLimitDouble);
    }
}
