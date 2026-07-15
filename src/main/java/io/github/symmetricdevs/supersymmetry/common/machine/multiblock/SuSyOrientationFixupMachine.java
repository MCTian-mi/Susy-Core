package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import io.github.symmetricdevs.supersymmetry.api.pattern.SuSyPredicates;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Workable multiblock base for structure parts that must face an exact
 * controller-relative direction after the pure pattern match has completed.
 *
 * <p>This is distinct from {@link SuSyRotationGeneratorMachine}, whose turbine
 * parts intentionally collapse their facing to the positive X/Z axis. Conveyor
 * belts and hydraulic cylinders need the full resolved horizontal or six-way
 * direction instead.</p>
 */
public class SuSyOrientationFixupMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SuSyOrientationFixupMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private boolean applyingOrientationFixups;

    public SuSyOrientationFixupMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        if (applyingOrientationFixups)
            return;

        super.onStructureFormed();
        var level = getLevel();
        if (level == null || level.isClientSide)
            return;

        applyingOrientationFixups = true;
        try {
            for (var fixup : List.copyOf(SuSyPredicates.getOrientationFixups(this))) {
                var pos = fixup.getLeft();
                var facing = SuSyPredicates.resolveFacing(this, fixup.getRight());
                var state = level.getBlockState(pos);
                var oriented = state.hasProperty(RotatedPillarBlock.AXIS) ?
                        state.setValue(RotatedPillarBlock.AXIS, facing.getAxis()) :
                        state.hasProperty(BlockStateProperties.FACING) ?
                                SuSyPredicates.withFacing(state, facing) :
                                SuSyPredicates.withHorizontalFacing(state, facing);
                if (oriented != state) {
                    level.setBlockAndUpdate(pos, oriented);
                }
            }
        } finally {
            applyingOrientationFixups = false;
        }
    }
}
