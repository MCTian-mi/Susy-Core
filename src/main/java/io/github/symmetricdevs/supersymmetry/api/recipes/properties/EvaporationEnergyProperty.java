package io.github.symmetricdevs.supersymmetry.api.recipes.properties;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import net.minecraft.nbt.Tag;

/**
 * Bridge for the 1.12.2 {@code RecipeProperty<Integer>} for evaporation energy.
 * <p>
 * Replaced by {@link SuSyRecipePropertyKeys#EVAPORATION_ENERGY} recipe data.
 */
public final class EvaporationEnergyProperty {

    public static final String KEY = SuSyRecipePropertyKeys.EVAPORATION_ENERGY;

    private static final EvaporationEnergyProperty INSTANCE = new EvaporationEnergyProperty();

    private EvaporationEnergyProperty() {}

    public static EvaporationEnergyProperty getInstance() {
        return INSTANCE;
    }

    /** Bridge: read evaporation energy from recipe data. */
    public static int getEvaporationEnergy(GTRecipe recipe, int defaultValue) {
        return recipe.data.contains(KEY, Tag.TAG_INT) ? recipe.data.getInt(KEY) : defaultValue;
    }

    /** Bridge: does the recipe carry evaporation energy? */
    public static boolean hasEvaporationEnergy(GTRecipe recipe) {
        return recipe.data.contains(KEY, Tag.TAG_INT);
    }
}
