package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.UpdateListener;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import io.github.symmetricdevs.supersymmetry.api.pattern.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.client.renderer.RenderMaskManager;

import it.unimi.dsi.fastutil.longs.LongSet;
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

    private static final BlockPos[] NO_MASK = new BlockPos[0];

    private boolean applyingOrientationFixups;

    /**
     * Structure positions whose block models are hidden while formed (the machine draws a unified
     * GeckoLib model over them). Populated on the server from the {@code renderMask} match-context
     * key and synced to clients, where {@link #onRenderMaskUpdated} feeds {@link RenderMaskManager}.
     */
    @DescSynced
    @UpdateListener(methodName = "onRenderMaskUpdated")
    private BlockPos[] renderMaskPositions = NO_MASK;

    public SuSyOrientationFixupMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // The desc-sync listener covers live updates; re-apply here for a machine that is already
        // formed when its block entity loads on the client (the initial state arrives with no delta).
        var level = getLevel();
        if (level != null && level.isClientSide && renderMaskPositions.length > 0) {
            onRenderMaskUpdated(renderMaskPositions, NO_MASK);
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        var level = getLevel();
        if (level != null && level.isClientSide) {
            RenderMaskManager.update(getPos(), new long[0]);
        }
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

        captureRenderMask();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        renderMaskPositions = NO_MASK;
    }

    /**
     * Read the {@code renderMask} positions the pattern collected (from predicates that called
     * {@code disableRenderFormed()}) and publish them to the synced field so clients can hide them.
     */
    private void captureRenderMask() {
        LongSet mask = getMultiblockState().getMatchContext().get("renderMask");
        if (mask == null || mask.isEmpty()) {
            renderMaskPositions = NO_MASK;
            return;
        }
        BlockPos[] positions = new BlockPos[mask.size()];
        int i = 0;
        for (long packed : mask) {
            positions[i++] = BlockPos.of(packed);
        }
        renderMaskPositions = positions;
    }

    /** Client-side: mirror the synced mask into the render-only {@link RenderMaskManager}. */
    @SuppressWarnings("unused")
    private void onRenderMaskUpdated(BlockPos[] newValue, BlockPos[] oldValue) {
        var level = getLevel();
        if (level == null || !level.isClientSide) {
            return;
        }
        long[] packed = new long[newValue == null ? 0 : newValue.length];
        for (int i = 0; i < packed.length; i++) {
            packed[i] = newValue[i].asLong();
        }
        RenderMaskManager.update(getPos(), packed);
    }
}
