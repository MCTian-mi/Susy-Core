package io.github.symmetricdevs.supersymmetry.api.capability.impl;

import static com.gregtechceu.gtceu.api.GTValues.ULV;

import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.GTValues;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.gregtechceu.gtceu.utils.GTUtil;
import io.github.symmetricdevs.supersymmetry.api.SusyLog;
import io.github.symmetricdevs.supersymmetry.api.recipes.builders.logic.SuSyOverclockingLogic;
import io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystInfo;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.CatalystProperty;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.CatalystPropertyValue;

public class ContinuousMultiblockRecipeLogic extends RecipeLogic {

    private CatalystInfo catalystInfo;
    private int requiredCatalystTier;

    public ContinuousMultiblockRecipeLogic(WorkableElectricMultiblockMachine BlockEntity) {
        super(BlockEntity);
    }

    public ContinuousMultiblockRecipeLogic(WorkableElectricMultiblockMachine BlockEntity, boolean hasPerfectOC) {
        super(BlockEntity, hasPerfectOC);
    }

    protected void tryFindCatalystInfo(@NotNull Recipe recipe) {
        this.catalystInfo = null;
        this.requiredCatalystTier = CatalystInfo.NO_TIER;

        if (recipe.hasProperty(CatalystProperty.getInstance())) {
            CatalystPropertyValue property = recipe.getProperty(CatalystProperty.getInstance(), null);
            if (property == null) return;

            // If it is a non-tiered catalyst, no bonuses need to be calculated
            // We can safely skip the inventory scanning
            if (property.getTier() == CatalystInfo.NO_TIER) {
                return;
            }

            // find the best catalyst in the inventory, and use that
            for (int i = 0; i < getInputInventory().getSlots(); i++) {
                ItemStack is = getInputInventory().getStackInSlot(i);
                if (!is.isEmpty()) {
                    CatalystInfo info = property.getCatalystGroup().getCatalystInfos().get(is);

                    if (info != null && (this.catalystInfo == null || this.catalystInfo.compareTo(info) > 0)) {
                        this.catalystInfo = info;
                    }

                }
            }

            // keep catalyst tier at NO_TIER unless info is found
            if (this.catalystInfo != null) {
                SusyLog.logger.info("3r390r9");
                this.requiredCatalystTier = property.getTier();
            }
        }
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
    protected void trySearchNewRecipeCombined() {
        long maxVoltage = getMaxVoltage();
        Recipe currentRecipe;
        IItemHandlerModifiable importInventory = getInputInventory();
        IMultipleTankHandler importFluids = getInputTank();

        // see if the last recipe we used still works
        if (checkPreviousRecipe()) {
            currentRecipe = this.previousRecipe;
            // If there is no active recipe, then we need to find one.
        } else {
            currentRecipe = findRecipe(maxVoltage, importInventory, importFluids);
        }
        // If a recipe was found, then inputs were valid. Cache found recipe.
        if (currentRecipe != null) {
            this.previousRecipe = currentRecipe;
            tryFindCatalystInfo(currentRecipe);
        }
        this.invalidInputsForRecipes = (currentRecipe == null);

        // proceed if we have a usable recipe.
        if (currentRecipe != null && checkRecipe(currentRecipe)) {
            prepareRecipe(currentRecipe);
        }
    }

    @Override
    protected void modifyOverclockPre(int @NotNull [] values, @NotNull IRecipePropertyStorage storage) {
        super.modifyOverclockPre(values, storage);

        // apply maintenance bonuses
        Tuple<Integer, Double> maintenanceValues = getMaintenanceValues();

        // duration bonus
        if (maintenanceValues.getSecond() != 1.0) {
            values[1] = (int) Math.round(values[1] * maintenanceValues.getSecond());
        }

        if (catalystInfo != null) {
            values[0] = Math.min(1, (int) (values[0] * catalystInfo.getEnergyEfficiency()));
            values[1] = Math.min(1, (int) (values[1] * catalystInfo.getSpeedEfficiency()));
        }
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe) {
        CatalystPropertyValue property = recipe.getProperty(CatalystProperty.getInstance(), null);
        if (property == null || property.getTier() == CatalystInfo.NO_TIER) {
            return super.checkRecipe(recipe);
        }

        if (catalystInfo == null) {
            return false;
        }

        return catalystInfo.getTier() >= property.getTier() && super.checkRecipe(recipe);
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
