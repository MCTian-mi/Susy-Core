package io.github.symmetricdevs.supersymmetry.api.recipes.properties;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.nbt.Tag;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;

/**
 * Bridge for the 1.12.2 {@code RecipeProperty<IntList>} for dimension whitelist.
 * <p>
 * Replaced by recipe data or {@code DimensionCondition} during datagen. The
 * Dimension-based machines (Quarry, DronePad, HeatRadiator) are deferred to
 * Phase 8; this bridge preserves compilation for the old MTE files.
 */
public final class DimensionProperty {

    public static final String KEY = "dimension";

    private static final DimensionProperty INSTANCE = new DimensionProperty();

    private DimensionProperty() {}

    public static DimensionProperty getInstance() {
        return INSTANCE;
    }

    /** Bridge: read dimension list from recipe data. */
    public static IntList getDimensions(GTRecipe recipe, IntList defaultValue) {
        if (!recipe.data.contains(KEY, Tag.TAG_INT_ARRAY)) {
            return defaultValue;
        }
        int[] arr = recipe.data.getIntArray(KEY);
        if (arr.length == 0) return IntLists.EMPTY_LIST;
        IntList list = new IntArrayList(arr.length);
        for (int v : arr) list.add(v);
        return list;
    }
}
