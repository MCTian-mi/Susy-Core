package io.github.symmetricdevs.supersymmetry.api.recipes.properties;

/**
 * Keys for per-recipe data stored in {@code GTRecipe.data} (a
 * {@link net.minecraft.nbt.CompoundTag}). These replace the 1.12.2
 * {@code RecipeProperty} singletons, which have no equivalent in GTCEu-Modern —
 * a recipe now carries its extra parameters as NBT data, surfaced in the recipe
 * viewer via {@code GTRecipeType.addDataInfo(...)} and consumed by machine recipe
 * logic.
 * <p>
 * Only the keys whose values are actually read by ported logic are declared here.
 * The 1.12.2 properties that were pure XEI text (biome/dimension lists, etc.) are
 * re-expressed as {@code RecipeCondition}s or data lines at their point of use.
 */
public final class SuSyRecipePropertyKeys {

    /** String: registry name of the {@code CatalystGroup} a recipe accepts. */
    public static final String CATALYST_GROUP = "catalyst_group";
    /** Int: the minimum catalyst tier the recipe requires. */
    public static final String CATALYST_TIER = "catalyst_tier";

    /** Int: coiling-coil cooling temperature (was CoilingCoilTemperatureProperty). */
    public static final String COOLING_TEMPERATURE = "cooling_temperature";
    /** Boolean: cryogenic environment required (was CryogenicEnvironmentProperty). */
    public static final String CRYOGENIC_ENVIRONMENT = "cryogenic_environment";
    /** Int: evaporation-pool energy in Joules/tick (was EvaporationEnergyProperty). */
    public static final String EVAPORATION_ENERGY = "evaporation_energy";
    /** Int: required mixer-settler cell count, even (was MixerSettlerCellsProperty). */
    public static final String MIXER_SETTLER_CELLS = "mixer_settler_cells";
    /** Boolean: sintering recipe uses plasma (was SinterProperty). */
    public static final String PLASMA_ENABLED = "plasma_enabled";

    private SuSyRecipePropertyKeys() {}
}
