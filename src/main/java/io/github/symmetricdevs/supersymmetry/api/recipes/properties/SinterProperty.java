package io.github.symmetricdevs.supersymmetry.api.recipes.properties;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import net.minecraft.nbt.Tag;

/**
 * Bridge for the 1.12.2 {@code RecipeProperty<Boolean>} for plasma requirement.
 * <p>
 * Replaced by {@link SuSyRecipePropertyKeys#PLASMA_ENABLED} recipe data.
 */
public final class SinterProperty {

    public static final String KEY = SuSyRecipePropertyKeys.PLASMA_ENABLED;

    private static final SinterProperty INSTANCE = new SinterProperty();

    private SinterProperty() {}

    public static SinterProperty getInstance() {
        return INSTANCE;
    }

    /** Bridge: read plasma requirement from recipe data. */
    public static boolean getPlasmaEnabled(GTRecipe recipe, boolean defaultValue) {
        return recipe.data.contains(KEY, Tag.TAG_BYTE) ? recipe.data.getBoolean(KEY) : defaultValue;
    }
}
