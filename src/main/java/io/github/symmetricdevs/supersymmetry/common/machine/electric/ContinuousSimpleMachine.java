package io.github.symmetricdevs.supersymmetry.common.machine.electric;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;

import org.jetbrains.annotations.Nullable;

import io.github.symmetricdevs.supersymmetry.api.recipes.logic.SuSyParallelLogic;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;

/**
 * A {@link CatalystSimpleMachine} that additionally batches its recipes — the
 * 1.12.2 {@code ContinuousMachineMetaTileEntity} + {@code ContinuousRecipeLogic}
 * (the CSTR / fixed-bed / trickle-bed / bubble-column reactor line).
 * <p>
 * On top of the catalyst behaviour it applies SuSy's <em>pure parallel</em>
 * ({@link SuSyParallelLogic#pureParallel}): the recipe runs up to
 * {@value #PARALLEL_LIMIT} copies per operation but is charged only the original
 * EUt and duration (the parallel modifier forces the EU multiplier to 0). The
 * catalyst layer is applied first, then the batch — so a continuous reactor gets
 * both its catalyst discount and its throughput.
 */
public class ContinuousSimpleMachine extends CatalystSimpleMachine {

    /** Hard upper bound on the pure-parallel batch (matches 1.12.2 getParallelLimit). */
    protected static final int PARALLEL_LIMIT = 256;

    public ContinuousSimpleMachine(IMachineBlockEntity holder, int tier, Int2IntFunction tankScalingFunction,
                                   Object... args) {
        super(holder, tier, tankScalingFunction, args);
    }

    @Override
    @Nullable
    public GTRecipe doModifyRecipe(GTRecipe recipe) {
        // Catalyst gating + discount + yield (rejects the recipe if no catalyst).
        GTRecipe modified = super.doModifyRecipe(recipe);
        if (modified == null) return null;

        // Then batch it: parallel copies at base EUt/duration. pureParallel returns
        // IDENTITY when no batching is possible and NULL only when inputs forbid the
        // recipe entirely (in which case the recipe shouldn't run).
        ModifierFunction parallel = SuSyParallelLogic.pureParallel(this, modified, PARALLEL_LIMIT);
        if (parallel == ModifierFunction.NULL) return null;
        return parallel.apply(modified);
    }
}
