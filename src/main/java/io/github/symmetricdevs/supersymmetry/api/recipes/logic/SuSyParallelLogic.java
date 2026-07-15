package io.github.symmetricdevs.supersymmetry.api.recipes.logic;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;

/**
 * SuSy parallel logic. Ported from the 1.12.2 {@code SuSyParallelLogic}.
 * <p>
 * {@code pureParallelRecipe} parallelizes a recipe across N copies but charges
 * <em>only the original EUt and original duration</em> — unlike the stock GTCEu
 * parallel logic it does not scale EUt with the batch size. GTCEu-Modern expresses
 * parallelization as a {@link ModifierFunction} applied to the recipe rather than a
 * rebuilt {@code RecipeBuilder}, so this is exposed as a factory returning such a
 * function; the machine's recipe logic supplies the machine + recipe and composes
 * the result with its overclocking logic.
 */
public final class SuSyParallelLogic {

    private SuSyParallelLogic() {}

    /**
     * Parallelize {@code recipe} up to {@code parallelLimit} copies without scaling
     * EUt or duration.
     * <p>
     * The batch count is limited by inputs and by how much output a single copy
     * produces ({@link #limitByOutputSize(GTRecipe, int)}, a SuSy-specific guard that
     * keeps big-output recipes from batching far beyond what an output bus can
     * hold). The returned modifier multiplies recipe contents by the batch count
     * and forces EUt to {@code 0} so the standard energy-charge path adds nothing —
     * matching the 1.12.2 behavior of running the batch at base EUt.
     *
     * @param machine       the machine (provides the capability holder for
     *                      input/output limiting)
     * @param recipe        the recipe to parallelize
     * @param parallelLimit hard upper bound on batch count
     * @return a {@link ModifierFunction} applying the pure-parallel batch, or
     *         {@link ModifierFunction#NULL} if no parallel is possible
     */
    public static ModifierFunction pureParallel(@NotNull MetaMachine machine, @NotNull GTRecipe recipe,
                                                int parallelLimit) {
        // EUt-neutral batch: inputs/outputs limit it, but EU is not part of the limit.
        int inputLimited = ParallelLogic.getParallelAmountWithoutEU(machine, recipe, parallelLimit);
        if (inputLimited <= 0) {
            return ModifierFunction.NULL;
        }
        int parallel = Math.min(inputLimited, limitByOutputSize(recipe, parallelLimit));
        parallel = Math.min(parallel, parallelLimit);
        if (parallel <= 1) {
            return ModifierFunction.IDENTITY;
        }
        final int batch = parallel;
        return ModifierFunction.builder()
                .parallels(batch)
                .eutMultiplier(0)
                .build();
    }

    /**
     * SuSy-specific output-size guard: caps the batch count by how much output a
     * single copy produces. Item outputs count by stack size; fluid outputs are
     * rounded up to whole-ingot-equivalent "slots" (144 mB). A recipe with large
     * outputs is allowed proportionally fewer parallels.
     */
    public static int limitByOutputSize(@NotNull GTRecipe recipe, int parallelLimit) {
        int itemCount = 0;
        for (Content content : recipe.getOutputContents(ItemRecipeCapability.CAP)) {
            for (ItemStack stack : ItemRecipeCapability.CAP.of(content.content).getItems()) {
                if (!stack.isEmpty()) {
                    itemCount += stack.getCount();
                }
            }
        }

        int fluidCount = 0;
        for (Content content : recipe.getOutputContents(FluidRecipeCapability.CAP)) {
            fluidCount += FluidRecipeCapability.CAP.of(content.content).getAmount();
        }
        fluidCount += 143; // round up to whole-ingot-equivalent slots
        fluidCount /= 144;

        int totalCount = Math.max(itemCount + fluidCount, 1);
        return parallelLimit / totalCount;
    }
}
