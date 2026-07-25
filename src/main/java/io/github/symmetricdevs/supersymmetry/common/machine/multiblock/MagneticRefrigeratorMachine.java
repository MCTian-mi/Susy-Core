package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import io.github.symmetricdevs.supersymmetry.api.pattern.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.SuSyRecipePropertyKeys;
import io.github.symmetricdevs.supersymmetry.common.data.SusyBlocks;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Modern port of the 1.12.2 magnetic-refrigerator coil-temperature gate. */
public class MagneticRefrigeratorMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MagneticRefrigeratorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @DescSynced
    private int temperature = Integer.MAX_VALUE;

    public MagneticRefrigeratorMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Block chosen = SuSyPredicates.getChosenVariant(this, "CoolingCoil");
        if (chosen == SusyBlocks.GADOLINIUM_SILICON_GERMANIUM_COOLING_COIL.get()) {
            temperature = 1;
        } else if (chosen == SusyBlocks.PRASEODYMIUM_NICKEL_COOLING_COIL.get()) {
            temperature = 50;
        } else {
            temperature = 160;
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        temperature = Integer.MAX_VALUE;
    }

    /** Gate-only modifier; the registration applies normal OC after this passes. */
    public static ModifierFunction temperatureGate(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof MagneticRefrigeratorMachine refrigerator)) {
            return RecipeModifier.nullWrongType(MagneticRefrigeratorMachine.class, machine);
        }
        if (!recipe.data.contains(SuSyRecipePropertyKeys.COOLING_TEMPERATURE)) {
            return ModifierFunction.cancel(Component.translatable(
                    "susy.recipe_modifier.cooling_temperature_required"));
        }
        if (refrigerator.temperature > recipe.data.getInt(SuSyRecipePropertyKeys.COOLING_TEMPERATURE)) {
            return ModifierFunction.cancel(Component.translatable("susy.recipe_modifier.coil_temperature_too_high"));
        }
        return ModifierFunction.IDENTITY;
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            textList.add(Component.translatable("susy.multiblock.magnetic_refrigerator.min_temperature",
                    Component.literal(FormattingUtil.formatNumbers(temperature) + "K")
                            .withStyle(ChatFormatting.RED)));
        }
    }

    public int getTemperature() {
        return temperature;
    }
}
