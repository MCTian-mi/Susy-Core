package io.github.symmetricdevs.supersymmetry.api.recipes.properties;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import net.minecraft.nbt.Tag;

/**
 * Bridge for the 1.12.2 {@code RecipeProperty<Boolean>} for cryogenic environment.
 * <p>
 * Replaced by {@link SuSyRecipePropertyKeys#CRYOGENIC_ENVIRONMENT} recipe data.
 */
public final class CryogenicEnvironmentProperty {

    public static final String KEY = SuSyRecipePropertyKeys.CRYOGENIC_ENVIRONMENT;

    private static final CryogenicEnvironmentProperty INSTANCE = new CryogenicEnvironmentProperty();

    private CryogenicEnvironmentProperty() {}

    public static CryogenicEnvironmentProperty getInstance() {
        return INSTANCE;
    }

    /** Bridge: read cryogenic environment flag from recipe data. */
    public static boolean getCryogenicEnvironment(GTRecipe recipe, Boolean defaultValue) {
        return recipe.data.contains(KEY, Tag.TAG_BYTE) ? recipe.data.getBoolean(KEY) :
                (defaultValue != null ? defaultValue : false);
    }
}
