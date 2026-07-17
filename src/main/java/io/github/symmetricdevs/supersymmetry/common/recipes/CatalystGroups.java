package io.github.symmetricdevs.supersymmetry.common.recipes;

import io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystGroup;

/**
 * Ported from the 1.12.2 {@code supersymmetry.common.recipes.CatalystGroups}.
 * Package-renamed to {@code io.github.symmetricdevs.supersymmetry.common.recipes}.
 */
public final class CatalystGroups {

    public static final CatalystGroup OXIDATION_CATALYST_BEDS = new CatalystGroup("oxidation_catalyst_beds");
    public static final CatalystGroup REDUCTION_CATALYST_BEDS = new CatalystGroup("reduction_catalyst_beds");
    public static final CatalystGroup CRACKING_CATALYST_BEDS = new CatalystGroup("cracking_catalyst_beds");
    // I don't think this has any use, we may as well yeet it
    // Keeping it in for now though
    public static final CatalystGroup STANDARD_CATALYSTS = new CatalystGroup("standard_catalysts");

    private CatalystGroups() {}
}
