package io.github.symmetricdevs.supersymmetry.api.recipes.builders;

import java.util.Map;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystGroup;
import io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystInfo;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.SuSyRecipePropertyKeys;

/**
 * Catalyst support for recipe building. Ported from the 1.12.2
 * {@code CatalystRecipeBuilder}.
 * <p>
 * The 1.12.2 version was a {@code RecipeBuilder} subclass that stamped a catalyst
 * property and added not-consumed catalyst inputs. GTCEu-Modern recipe types are
 * not parameterized by builder class and carry no {@code RecipeProperty}, so this
 * is a static helper applied to a {@link GTRecipeBuilder} during datagen: it
 * records the accepted {@link CatalystGroup} and required tier as recipe data
 * ({@link SuSyRecipePropertyKeys#CATALYST_GROUP} /
 * {@link SuSyRecipePropertyKeys#CATALYST_TIER}) and adds every catalyst item in the
 * group at or above the required tier as a <em>not-consumed</em> input, so the
 * recipe only matches when a valid catalyst is present. The matched catalyst's
 * modifiers are applied by the machine's recipe logic at runtime (Phase 4).
 */
public final class CatalystRecipeBuilder {

    private CatalystRecipeBuilder() {}

    public static GTRecipeBuilder catalyst(GTRecipeBuilder builder, CatalystGroup catalystGroup) {
        return catalyst(builder, catalystGroup, CatalystInfo.NO_TIER, 1);
    }

    public static GTRecipeBuilder catalyst(GTRecipeBuilder builder, CatalystGroup catalystGroup, int tier) {
        return catalyst(builder, catalystGroup, tier, 1);
    }

    public static GTRecipeBuilder catalyst(GTRecipeBuilder builder, CatalystGroup catalystGroup, int tier,
                                           int amount) {
        builder.addData(SuSyRecipePropertyKeys.CATALYST_GROUP, catalystGroup.getName());
        builder.addData(SuSyRecipePropertyKeys.CATALYST_TIER, tier);

        ItemStack[] inputStacks = catalystGroup.getCatalystInfos().streamEntries()
                .filter(entry -> entry.getValue().tier() >= tier)
                .map(Map.Entry::getKey)
                .map(stack -> {
                    ItemStack copy = stack.copy();
                    copy.setCount(amount);
                    return copy;
                }).toArray(ItemStack[]::new);

        return builder.notConsumable(Ingredient.of(inputStacks));
    }
}
