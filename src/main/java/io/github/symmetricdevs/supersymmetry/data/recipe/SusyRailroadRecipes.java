package io.github.symmetricdevs.supersymmetry.data.recipe;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.common.data.SuSyRecipeTypes;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

/**
 * Test/dev recipes for the Railroad Engineering Station.
 *
 * <p>These recipes are intentionally cheap so the Create train-spawning path
 * can be exercised without full progression gating. Real rolling-stock recipes
 * will be added once the stock definitions and gauges are finalized.</p>
 */
public final class SusyRailroadRecipes {

    private SusyRailroadRecipes() {}

    public static void init(Consumer<FinishedRecipe> provider) {
        // A trivial test recipe: some steel plates + a cogwheel → spawn a train.
        GTRecipeBuilder builder = SuSyRecipeTypes.RAILROAD_ENGINEERING_STATION_RECIPES
                .recipeBuilder(SuSyValues.susyId("test_spawn_train"))
                .inputItems(new ItemStack(Items.IRON_INGOT, 4))
                .inputItems(new ItemStack(Items.OAK_PLANKS, 2))
                .inputItems(new ItemStack(Items.CLOCK, 1))
                .outputItems(new ItemStack(Items.MINECART, 1))
                .duration(200)
                .EUt(GTValues.VA[GTValues.LV]);
        builder.save(provider);
    }
}
