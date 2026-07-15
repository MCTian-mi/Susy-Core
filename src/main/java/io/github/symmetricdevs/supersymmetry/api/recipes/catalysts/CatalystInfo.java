package io.github.symmetricdevs.supersymmetry.api.recipes.catalysts;

import org.jetbrains.annotations.NotNull;

/**
 * Per-catalyst recipe modifiers. Ported from the 1.12.2 {@code CatalystInfo}
 * (an immutable POJO) as a Java record.
 * <p>
 * A catalyst is matched to a recipe via its {@link CatalystGroup}; the group's
 * map is keyed by catalyst item (ignoring stack count). The recipe declares a
 * <em>required</em> catalyst tier; the tier delta
 * {@code tier - requiredTier} drives the overclock bonus in the machine's recipe
 * logic (Phase 4):
 * <ul>
 * <li>energy: {@code EUt *= min(1, energyEfficiency ^ delta)}</li>
 * <li>duration: {@code duration /= speedEfficiency ^ delta}</li>
 * </ul>
 * {@code yieldEfficiency} is consumed by output/chanced-yield handling, also in
 * the machine logic.
 *
 * @param tier             the catalyst's tier; {@link #NO_TIER} means "no tier"
 * @param yieldEfficiency  multiplicative yield bonus per tier above required
 * @param energyEfficiency multiplicative energy discount per tier above required
 * @param speedEfficiency  multiplicative speed bonus per tier above required
 */
public record CatalystInfo(int tier, double yieldEfficiency, double energyEfficiency,
                           double speedEfficiency) implements Comparable<CatalystInfo> {

    /** Sentinel tier for a catalyst that confers no tier bonus. */
    public static final int NO_TIER = -1;

    /**
     * "Best catalyst" ordering: Tier, then Speed, then Yield, then Energy.
     */
    @Override
    public int compareTo(@NotNull CatalystInfo other) {
        int result = Integer.compare(this.tier, other.tier);
        if (result != 0) return result;
        result = Double.compare(this.speedEfficiency, other.speedEfficiency);
        if (result != 0) return result;
        result = Double.compare(this.yieldEfficiency, other.yieldEfficiency);
        if (result != 0) return result;
        return Double.compare(this.energyEfficiency, other.energyEfficiency);
    }
}
