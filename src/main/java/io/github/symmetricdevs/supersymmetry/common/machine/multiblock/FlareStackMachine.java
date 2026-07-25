package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.fluids.FluidState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityFlareStack}: a tall vent that voids
 * gases and liquids, with a rate bonus that doubles for each block of height.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FlareStackMachine extends VoidingMultiblockBase {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            FlareStackMachine.class, VoidingMultiblockBase.MANAGED_FIELD_HOLDER);

    public static final int MIN_HEIGHT = 5;
    public static final int MAX_HEIGHT = 10;

    @Persisted
    @DescSynced
    private int height = MIN_HEIGHT;

    public FlareStackMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        updateHeight();
    }

    private void updateHeight() {
        this.height = scanHeightAboveController(MIN_HEIGHT, MAX_HEIGHT);
        this.rateBonus = (int) Math.pow(2, height - MIN_HEIGHT);
    }

    @Override
    public boolean canVoidState(FluidState state) {
        return state == FluidState.GAS || state == FluidState.LIQUID;
    }

    @Override
    public boolean incinerate() {
        return true;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        if (isFormed()) {
            textList.add(Component.translatable("susy.machine.flare_stack.height",
                    Component.literal(String.valueOf(height)).withStyle(ChatFormatting.BLUE)));
            textList.add(Component.translatable("susy.machine.flare_stack.rate",
                    Component.literal(rateBonus + "x").withStyle(ChatFormatting.DARK_PURPLE)));
        }
    }

    /**
     * Vertical stack: controller at the bottom, 3..7 firebox blocks above it, muffler at
     * the top. The import fluid hatch may replace any firebox block.
     */
    public static @NotNull BlockPattern buildPattern(@NotNull MultiblockMachineDefinition definition) {
        return FactoryBlockPattern.start(RelativeDirection.FRONT, RelativeDirection.RIGHT, RelativeDirection.UP)
                .aisle("S")
                .aisle("P").setRepeatable(3, 7)
                .aisle("F")
                .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                .where('P', Predicates.blocks(GTBlocks.FIREBOX_STEEL.get())
                        .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setExactLimit(1)))
                .where('F', Predicates.abilities(PartAbility.MUFFLER).setExactLimit(1))
                .build();
    }
}
