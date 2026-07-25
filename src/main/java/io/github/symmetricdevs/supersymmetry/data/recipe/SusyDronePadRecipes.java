package io.github.symmetricdevs.supersymmetry.data.recipe;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.common.data.SuSyRecipeTypes;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

/**
 * Test/dev recipes for the Drone Pad.
 *
 * <p>These recipes are intentionally cheap so the drone entity lifecycle and
 * recipe lookup can be exercised without full progression gating. Real
 * dimension-gated delivery recipes will be added once the space/rocket scope
 * is finalized.</p>
 */
public final class SusyDronePadRecipes {

    private SusyDronePadRecipes() {}

    public static void init(Consumer<FinishedRecipe> provider) {
        // Basic surface delivery test.
        GTRecipeBuilder surface = SuSyRecipeTypes.DRONE_PAD_RECIPES
                .recipeBuilder(SuSyValues.susyId("test_surface_delivery"))
                .inputItems(new ItemStack(Items.IRON_INGOT, 4))
                .inputItems(new ItemStack(Items.REDSTONE, 2))
                .outputItems(new ItemStack(Items.DIAMOND, 1))
                .duration(160)
                .EUt(GTValues.VA[GTValues.LV]);
        surface.save(provider);

        // Slightly longer test to exercise outbound + return flight timing.
        GTRecipeBuilder roundTrip = SuSyRecipeTypes.DRONE_PAD_RECIPES
                .recipeBuilder(SuSyValues.susyId("test_round_trip"))
                .inputItems(new ItemStack(Items.GOLD_INGOT, 2))
                .inputItems(new ItemStack(Items.LAPIS_LAZULI, 4))
                .outputItems(new ItemStack(Items.EMERALD, 1))
                .duration(300)
                .EUt(GTValues.VA[GTValues.LV]);
        roundTrip.save(provider);
    }
}
