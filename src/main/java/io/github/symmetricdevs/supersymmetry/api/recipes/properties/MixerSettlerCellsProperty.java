package io.github.symmetricdevs.supersymmetry.api.recipes.properties;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import net.minecraft.nbt.Tag;

/**
 * Bridge for the 1.12.2 {@code RecipeProperty<Integer>} for mixer-settler cell count.
 * <p>
 * Replaced by {@link SuSyRecipePropertyKeys#MIXER_SETTLER_CELLS} recipe data.
 */
public final class MixerSettlerCellsProperty {

    public static final String KEY = SuSyRecipePropertyKeys.MIXER_SETTLER_CELLS;

    private static final MixerSettlerCellsProperty INSTANCE = new MixerSettlerCellsProperty();

    private MixerSettlerCellsProperty() {}

    public static MixerSettlerCellsProperty getInstance() {
        return INSTANCE;
    }

    /** Bridge: read required cells from recipe data. */
    public static int getRequiredCells(GTRecipe recipe, int defaultValue) {
        return recipe.data.contains(KEY, Tag.TAG_INT) ? recipe.data.getInt(KEY) : defaultValue;
    }
}
