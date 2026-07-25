package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import io.github.symmetricdevs.supersymmetry.api.pattern.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.SuSyRecipePropertyKeys;
import io.github.symmetricdevs.supersymmetry.common.data.SusyBlocks;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Modern port of the sintering-oven brick variant and plasma recipe gate. */
public class SuSySinteringOvenMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SuSySinteringOvenMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @DescSynced
    private boolean canUsePlasma;

    public SuSySinteringOvenMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        canUsePlasma = SuSyPredicates.getChosenVariant(this, "SinteringBrick") ==
                SusyBlocks.MAGNETOPLATED_SINTERING_BRICK.get();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        canUsePlasma = false;
    }

    /** Gate-only modifier; the registration applies normal OC after this passes. */
    public static ModifierFunction plasmaGate(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof SuSySinteringOvenMachine oven)) {
            return RecipeModifier.nullWrongType(SuSySinteringOvenMachine.class, machine);
        }
        if (!recipe.data.contains(SuSyRecipePropertyKeys.PLASMA_ENABLED)) {
            return ModifierFunction.cancel(Component.translatable(
                    "susy.recipe_modifier.plasma_metadata_required"));
        }
        if (recipe.data.getBoolean(SuSyRecipePropertyKeys.PLASMA_ENABLED) && !oven.canUsePlasma) {
            return ModifierFunction.cancel(Component.translatable("susy.recipe_modifier.plasma_resistant_bricks_required"));
        }
        return ModifierFunction.IDENTITY;
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            textList.add(Component.translatable("susy.multiblock.sintering_oven.can_use_plasma",
                    Component.translatable(canUsePlasma ?
                            "susy.multiblocks.sintering_oven.use_plasma.affirmative" :
                            "susy.multiblocks.sintering_oven.use_plasma.negative")
                            .withStyle(ChatFormatting.LIGHT_PURPLE)));
        }
    }

    public boolean canUsePlasma() {
        return canUsePlasma;
    }
}
