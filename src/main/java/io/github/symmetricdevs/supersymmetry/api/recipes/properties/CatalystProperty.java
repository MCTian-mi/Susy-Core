package io.github.symmetricdevs.supersymmetry.api.recipes.properties;

import javax.annotation.Nullable;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystGroup;
import io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystInfo;
import net.minecraft.nbt.Tag;

/**
 * Bridge for the 1.12.2 {@code RecipeProperty<CatalystPropertyValue>} for catalyst system.
 * <p>
 * Catalyst group and tier are now stored as {@link SuSyRecipePropertyKeys#CATALYST_GROUP} and
 * {@link SuSyRecipePropertyKeys#CATALYST_TIER} recipe data, applied during datagen and consumed
 * by machine recipe logic at runtime.
 */
public final class CatalystProperty {

    public static final String KEY = "catalyst";

    private static final CatalystProperty INSTANCE = new CatalystProperty();

    private CatalystProperty() {}

    public static CatalystProperty getInstance() {
        return INSTANCE;
    }

    /** Bridge: read catalyst group name from recipe data. */
    @Nullable
    public static String getCatalystGroup(GTRecipe recipe) {
        return recipe.data.contains(SuSyRecipePropertyKeys.CATALYST_GROUP, Tag.TAG_STRING)
                ? recipe.data.getString(SuSyRecipePropertyKeys.CATALYST_GROUP) : null;
    }

    /** Bridge: read catalyst tier from recipe data. */
    public static int getCatalystTier(GTRecipe recipe, int defaultValue) {
        return recipe.data.contains(SuSyRecipePropertyKeys.CATALYST_TIER, Tag.TAG_INT)
                ? recipe.data.getInt(SuSyRecipePropertyKeys.CATALYST_TIER) : defaultValue;
    }
}
