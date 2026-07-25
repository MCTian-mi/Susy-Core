package io.github.symmetricdevs.supersymmetry.common.machine.electric;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.symmetricdevs.supersymmetry.api.recipes.builders.logic.SuSyOverclockingLogic;
import io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystGroup;
import io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystInfo;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.SuSyRecipePropertyKeys;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;

/**
 * A {@link SimpleTieredMachine} whose recipes may declare a catalyst — the
 * 1.12.2 {@code CatalystMachineMetaTileEntity} + {@code CatalystRecipeLogic}.
 * <p>
 * A recipe carrying {@code catalyst_group} / {@code catalyst_tier} data is
 * matched against the catalyst items in the machine's import inventory inside
 * {@link #doModifyRecipe(GTRecipe)}:
 * <ul>
 * <li><b>gating</b> — a tiered catalyst recipe with no sufficient catalyst in
 * inventory is rejected (returns {@code null}), matching 1.12.2
 * {@code checkRecipe};</li>
 * <li><b>overclock discount</b> — EUt and duration are scaled by the matched
 * {@link CatalystInfo} on top of the machine's normal overclock (the base
 * modifier);</li>
 * <li><b>yield boost</b> — every chanced output's per-tier chance boost
 * ({@code tierChanceBoost}) is scaled by the catalyst's
 * {@code yieldEfficiency}.</li>
 * </ul>
 * A recipe with no catalyst data (or the non-tiered sentinel) passes straight
 * through to the base modifier, matching 1.12.2
 * {@code standardOverclockingLogic}.
 */
public class CatalystSimpleMachine extends SimpleTieredMachine {

    public CatalystSimpleMachine(IMachineBlockEntity holder, int tier, Int2IntFunction tankScalingFunction,
                                 Object... args) {
        super(holder, tier, tankScalingFunction, args);
    }

    /**
     * The best catalyst in the import inventory for the recipe's group, or
     * {@code null} if none is present. Mirrors 1.12.2 {@code tryFindCatalystInfo}:
     * the best match is the one that sorts <em>lowest</em> by
     * {@link CatalystInfo#compareTo} (tier, then speed/yield/energy efficiency).
     */
    @Nullable
    protected CatalystInfo findCatalyst(GTRecipe recipe) {
        String groupName = recipe.data.getString(SuSyRecipePropertyKeys.CATALYST_GROUP);
        if (groupName.isEmpty()) return null;
        CatalystGroup group = CatalystGroup.byName(groupName);
        if (group == null) return null;

        CatalystInfo best = null;
        int slots = importItems.getSlots();
        for (int i = 0; i < slots; i++) {
            ItemStack stack = importItems.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            CatalystInfo info = group.getCatalystInfos().get(stack);
            // best == the smallest per CatalystInfo.compareTo (matches 1.12.2 `> 0` keep)
            if (info != null && (best == null || best.compareTo(info) > 0)) {
                best = info;
            }
        }
        return best;
    }

    @Override
    @Nullable
    public GTRecipe doModifyRecipe(GTRecipe recipe) {
        String groupName = recipe.data.getString(SuSyRecipePropertyKeys.CATALYST_GROUP);
        int requiredTier = recipe.data.contains(SuSyRecipePropertyKeys.CATALYST_TIER)
                ? recipe.data.getInt(SuSyRecipePropertyKeys.CATALYST_TIER)
                : CatalystInfo.NO_TIER;

        // No catalyst involved -> plain base behaviour (1.12.2 standardOverclockingLogic).
        if (groupName.isEmpty() || requiredTier == CatalystInfo.NO_TIER) {
            return super.doModifyRecipe(recipe);
        }

        CatalystInfo catalyst = findCatalyst(recipe);
        // Gating: a tiered catalyst recipe cannot run without a sufficient catalyst.
        if (catalyst == null || catalyst.tier() < requiredTier) {
            return null;
        }

        // Apply the machine's normal modifier (overclock / parallel), then layer the
        // catalyst discount and yield boost on top.
        GTRecipe modified = super.doModifyRecipe(recipe);
        if (modified == null) return null;

        double eutMul = SuSyOverclockingLogic.catalystEUtMultiplier(catalyst, requiredTier);
        double durMul = SuSyOverclockingLogic.catalystDurationMultiplier(catalyst, requiredTier);
        GTRecipe discounted = ModifierFunction.builder()
                .eutMultiplier(eutMul)
                .durationMultiplier(durMul)
                .build()
                .apply(modified);
        if (discounted == null) return null;

        return boostChancedOutputs(discounted, catalyst.yieldEfficiency());
    }

    /**
     * Returns a copy of {@code recipe} whose chanced outputs have their
     * {@code tierChanceBoost} scaled by the catalyst's yield efficiency (1.12.2's
     * yield bonus). Non-chanced outputs pass through unchanged.
     */
    private static GTRecipe boostChancedOutputs(GTRecipe recipe, double yieldMul) {
        if (yieldMul == 1.0) return recipe;
        Map<com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability<?>, List<Content>> outputs = new HashMap<>();
        recipe.outputs.forEach((cap, list) -> {
            List<Content> copy = new ArrayList<>(list.size());
            for (Content content : list) {
                if (content.chance < content.maxChance) {
                    copy.add(new Content(content.content, content.chance, content.maxChance,
                            (int) Math.round(content.tierChanceBoost * yieldMul)));
                } else {
                    copy.add(content);
                }
            }
            outputs.put(cap, copy);
        });
        return new GTRecipe(recipe.recipeType, recipe.id,
                new HashMap<>(recipe.inputs), outputs,
                new HashMap<>(recipe.tickInputs), new HashMap<>(recipe.tickOutputs),
                new HashMap<>(recipe.inputChanceLogics), new HashMap<>(recipe.outputChanceLogics),
                new HashMap<>(recipe.tickInputChanceLogics), new HashMap<>(recipe.tickOutputChanceLogics),
                new ArrayList<>(recipe.conditions), new ArrayList<>(recipe.ingredientActions),
                recipe.data, recipe.duration, recipe.recipeCategory, recipe.groupColor);
    }
}
