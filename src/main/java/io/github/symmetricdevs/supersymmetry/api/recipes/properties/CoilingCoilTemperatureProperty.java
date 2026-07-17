package io.github.symmetricdevs.supersymmetry.api.recipes.properties;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import net.minecraft.nbt.Tag;

/**
 * Bridge for the 1.12.2 {@code RecipeProperty<Boolean>} for cooling temperature.
 * <p>
 * Replaced by {@link SuSyRecipePropertyKeys#COOLING_TEMPERATURE} recipe data.
 */
public final class CoilingCoilTemperatureProperty {

    public static final String KEY = SuSyRecipePropertyKeys.COOLING_TEMPERATURE;

    private static final CoilingCoilTemperatureProperty INSTANCE = new CoilingCoilTemperatureProperty();

    private CoilingCoilTemperatureProperty() {}

    public static CoilingCoilTemperatureProperty getInstance() {
        return INSTANCE;
    }

    /** Bridge: read cooling temperature from recipe data. */
    public static int getCoolingTemperature(GTRecipe recipe, int defaultValue) {
        return recipe.data.contains(KEY, Tag.TAG_INT) ? recipe.data.getInt(KEY) : defaultValue;
    }

    /** Bridge: does the recipe carry a cooling temperature? */
    public static boolean hasCoolingTemperature(GTRecipe recipe) {
        return recipe.data.contains(KEY, Tag.TAG_INT);
    }
}
