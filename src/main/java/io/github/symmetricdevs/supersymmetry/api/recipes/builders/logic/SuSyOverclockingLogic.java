package io.github.symmetricdevs.supersymmetry.api.recipes.builders.logic;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;

import io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystInfo;

/**
 * Catalyst-aware overclocking. Ported from the 1.12.2 {@code SuSyOverclockingLogic}.
 * <p>
 * A matched {@link CatalystInfo} layers a bonus <em>on top of</em> the standard
 * overclock, driven by the tier delta {@code catalyst.tier() - requiredTier}. When
 * the delta is positive the recipe's EUt and duration are pre-scaled before the
 * normal overclock runs:
 * <ul>
 * <li>energy: {@code EUt *= min(1, energyEfficiency ^ delta)} — can only ever
 * reduce (or hold) EUt, never increase it</li>
 * <li>duration: {@code duration /= speedEfficiency ^ delta} (clamped to ≥ 1 tick)</li>
 * </ul>
 * GTCEu-Modern expresses overclocking as an {@link OverclockingLogic} returning a
 * {@code ModifierFunction} rather than a raw {@code int[]}/{@code double[]}, so the
 * catalyst bonus is exposed as a {@code ModifierFunction} factory that machines
 * compose with their chosen base {@code OverclockingLogic} in their recipe logic
 * (Phase 4). The pure-math variants are retained for parity with the 1.12.2
 * behavior where a plain double result is needed.
 */
public final class SuSyOverclockingLogic {

    private SuSyOverclockingLogic() {}

    /**
     * Multiplier applied to a recipe's EUt for the given catalyst / required tier.
     * {@code 1.0} (no change) when the catalyst does not exceed the required tier.
     */
    public static double catalystEUtMultiplier(@NotNull CatalystInfo catalyst, int requiredTier) {
        int delta = catalyst.tier() - requiredTier;
        if (delta <= 0) return 1.0;
        // min(1, ...) clamps so a higher tier can only discount energy, never add a penalty
        return Math.min(1.0, Math.pow(catalyst.energyEfficiency(), delta));
    }

    /**
     * Multiplier applied to a recipe's duration for the given catalyst / required
     * tier. {@code 1.0} (no change) when the catalyst does not exceed the required
     * tier.
     */
    public static double catalystDurationMultiplier(@NotNull CatalystInfo catalyst, int requiredTier) {
        int delta = catalyst.tier() - requiredTier;
        if (delta <= 0) return 1.0;
        return 1.0 / Math.pow(catalyst.speedEfficiency(), delta);
    }

    /**
     * Build an {@link OverclockingLogic} that pre-scales EUt and duration by the
     * catalyst bonus, then delegates to {@code base}. Machines call this from their
     * recipe logic with the catalyst matched to the running recipe.
     *
     * @param catalyst     the catalyst matched to the recipe (from its
     *                     {@code CatalystGroup})
     * @param requiredTier the recipe's required catalyst tier (its
     *                     {@code catalyst_tier} data)
     * @param base         the machine's normal overclocking logic
     */
    public static OverclockingLogic catalystOverclock(@NotNull CatalystInfo catalyst, int requiredTier,
                                                      @NotNull OverclockingLogic base) {
        double eutMul = catalystEUtMultiplier(catalyst, requiredTier);
        double durMul = catalystDurationMultiplier(catalyst, requiredTier);
        if (eutMul == 1.0 && durMul == 1.0) {
            return base;
        }
        return (params, maxVoltage) -> base.runOverclockingLogic(
                new OverclockingLogic.OCParams(
                        Math.round(params.eut() * eutMul),
                        Math.max(1, (int) Math.round(params.duration() * durMul)),
                        params.ocAmount(),
                        params.maxParallels()),
                maxVoltage);
    }
}
