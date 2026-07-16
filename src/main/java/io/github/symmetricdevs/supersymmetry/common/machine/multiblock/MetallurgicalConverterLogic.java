package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;

import io.github.symmetricdevs.supersymmetry.api.recipes.logic.SuSyParallelLogic;

/**
 * Recipe-modifier holder for the Metallurgical Converter — the port of the 1.12.2
 * {@code MetaTileEntityMetallurgicalConverter.MetallurgicalConverterLogic}.
 *
 * <p>Behaviour inventory of the legacy inner class, and where each piece lives now:
 * <ul>
 * <li>{@code extends MultiblockRecipeLogic} with no overclocking override — the stock
 * non-perfect overclock — is the definition's {@code GTRecipeModifiers.OC_NON_PERFECT}
 * layer, applied alongside {@link #pureParallel} via
 * {@code .recipeModifiers(true, OC_NON_PERFECT, MetallurgicalConverterLogic::pureParallel)}.</li>
 * <li>{@code findParallelRecipe -> SuSyParallelLogic.pureParallelRecipe} is
 * {@link #pureParallel} below: batch the recipe up to {@value #PARALLEL_LIMIT} copies,
 * multiplying item/fluid inputs and outputs by the batch count while leaving EUt and
 * duration at the base recipe's values ("pure" parallel).</li>
 * <li>{@code getParallelLimit() == 64} is {@link #PARALLEL_LIMIT}.</li>
 * <li>{@code getMaxParallelVoltage() == ~Integer.MAX_VALUE} (voltage never gates the
 * batch) is reproduced by {@link ParallelLogic#getParallelAmountWithoutEU}, which skips
 * the EU capability entirely when sizing the batch.</li>
 * <li>{@code allowsExtendedFacing() == false} and the structure pattern live on the
 * definition ({@code .allowExtendedFacing(false)}), not here.</li>
 * </ul>
 *
 * <p>Everything else about the machine is stock {@code WorkableElectricMultiblockMachine},
 * so no controller subclass is needed. This class carries no state — it is only the home
 * for the static recipe modifier — hence no {@code ManagedFieldHolder}.
 */
public final class MetallurgicalConverterLogic {

    /** 1.12.2 {@code getParallelLimit()}. */
    public static final int PARALLEL_LIMIT = 64;

    private MetallurgicalConverterLogic() {}

    /**
     * SuSy pure-parallel batch for the converter: run up to {@value #PARALLEL_LIMIT}
     * copies of the recipe per operation, charging only the original EUt and duration.
     *
     * <p>The batch count is limited by input availability and by what can merge into the
     * output buses/hatches ({@link ParallelLogic#getParallelAmountWithoutEU} — EU is
     * deliberately not a limiter, matching the legacy voltage-ignoring
     * {@code getMaxParallelVoltage}), then by the legacy
     * {@link SuSyParallelLogic#limitByOutputSize} guard so big-output recipes batch
     * proportionally fewer copies even when outputs are voided.
     *
     * <p>EUt and duration multipliers are intentionally left {@code IDENTITY} (not
     * called): the parallel step of {@code pureParallelRecipe} reset EUt to the original
     * single-recipe value and never touched duration. Overclocking is a separate
     * definition layer ({@code OC_NON_PERFECT}); since the batch changes neither EUt nor
     * duration, the two layers commute and reproduce the 1.12.2
     * parallel-then-overclock end state.
     *
     * @return {@link ModifierFunction#NULL} when the recipe cannot run (insufficient
     *         inputs, unmergeable outputs, or the output-size guard rejects the batch —
     *         the 1.12.2 {@code invalidateInputs}/{@code invalidateOutputs} paths);
     *         {@link ModifierFunction#IDENTITY} for a single copy; otherwise the batch
     *         modifier.
     */
    public static @NotNull ModifierFunction pureParallel(@NotNull MetaMachine machine,
                                                         @NotNull GTRecipe recipe) {
        int parallel = ParallelLogic.getParallelAmountWithoutEU(machine, recipe, PARALLEL_LIMIT);
        if (parallel <= 0) {
            return ModifierFunction.NULL;
        }
        parallel = Math.min(parallel, SuSyParallelLogic.limitByOutputSize(recipe, PARALLEL_LIMIT));
        if (parallel <= 0) {
            return ModifierFunction.NULL;
        }
        if (parallel == 1) {
            return ModifierFunction.IDENTITY;
        }
        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallel))
                .parallels(parallel)
                .build();
    }
}
