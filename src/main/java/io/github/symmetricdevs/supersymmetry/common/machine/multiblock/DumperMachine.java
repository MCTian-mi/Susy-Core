package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.fluids.FluidState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluids;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityDumper}: a ground-level voiding
 * multiblock that destroys liquids (including vanilla water and lava) at 16,000 L/10t.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DumperMachine extends VoidingMultiblockBase {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            DumperMachine.class, VoidingMultiblockBase.MANAGED_FIELD_HOLDER);

    public DumperMachine(IMachineBlockEntity holder) {
        super(holder);
        // Hard-code vanilla fluids the legacy code always voided.
        fluidCache.put(Fluids.WATER, true);
        fluidCache.put(Fluids.LAVA, true);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public boolean canVoidState(FluidState state) {
        return state == FluidState.LIQUID;
    }

    @Override
    public int getBaseVoidingRate() {
        return 16000;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        if (isFormed()) {
            textList.add(Component.translatable("susy.machine.dumper.rate",
                    Component.literal(getBaseVoidingRate() + " L/10t")));
        }
    }

    /**
     * Verbatim port of the 1.12.2 dumper structure pattern.
     */
    public static @NotNull BlockPattern buildPattern(@NotNull MultiblockMachineDefinition definition) {
        return FactoryBlockPattern.start()
                .aisle("A  A", "BBBB", "A  A")
                .aisle("BBBB", "C##A", "BBBB")
                .aisle("A  A", "BSBB", "A  A")
                .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                .where('A', Predicates.frames(GTMaterials.Steel))
                .where('B', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()))
                .where('C', Predicates.abilities(PartAbility.IMPORT_FLUIDS).setExactLimit(1))
                .where(' ', Predicates.any())
                .where('#', Predicates.air())
                .build();
    }
}
