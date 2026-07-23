package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IFluidRenderMulti;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Blender controller with GTCEu's synced fluid-render region.
 *
 * <p>The legacy Blender drew its active recipe output across a 3x3 surface one block above and
 * behind the controller. The native fluid-render interface owns its lifecycle and client sync; the
 * machine supplies only the orientation-aware relative offsets.
 */
public class BlenderMachine extends WorkableElectricMultiblockMachine implements IFluidRenderMulti {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            BlenderMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    @RequireRerender
    private Set<BlockPos> fluidBlockOffsets = new HashSet<>();

    public BlenderMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public @NotNull Set<BlockPos> getFluidBlockOffsets() {
        return fluidBlockOffsets;
    }

    @Override
    public void setFluidBlockOffsets(@NotNull Set<BlockPos> offsets) {
        fluidBlockOffsets = offsets;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        fluidBlockOffsets = saveOffsets();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        fluidBlockOffsets.clear();
    }

    @Override
    public @NotNull Set<BlockPos> saveOffsets() {
        Direction front = getFrontFacing();
        Direction up = RelativeDirection.UP.getRelative(front, getUpwardsFacing(), isFlipped());
        Direction left = RelativeDirection.LEFT.getRelative(front, getUpwardsFacing(), isFlipped());
        Direction right = left.getOpposite();
        Direction back = front.getOpposite();

        BlockPos controller = getPos();
        BlockPos center = controller.relative(up);
        Set<BlockPos> offsets = new HashSet<>();

        for (int depth = 0; depth < 3; depth++) {
            center = center.relative(back);
            offsets.add(center.subtract(controller));
            offsets.add(center.relative(left).subtract(controller));
            offsets.add(center.relative(right).subtract(controller));
        }
        return offsets;
    }
}
