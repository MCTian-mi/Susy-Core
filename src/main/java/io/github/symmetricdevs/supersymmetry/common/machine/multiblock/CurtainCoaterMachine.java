package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import io.github.symmetricdevs.supersymmetry.api.pattern.SuSyPredicates;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityCurtainCoater}.
 *
 * <p>
 * The coater's only bespoke structure behaviour is conveyor-belt orientation. That is
 * already split into the Modern two halves: the pure
 * {@link SuSyPredicates#conveyorBelt} predicate records each belt's position and
 * controller-relative travel direction ({@code RelativeDirection.LEFT}) in the match
 * context, and the {@link SuSyOrientationFixupMachine} base resolves and applies the
 * horizontal facing when the structure forms.
 *
 * <p>
 * What remains from the legacy controller is its {@code updateFormedValid()} loop:
 * while formed, every tick it re-resolved each recorded belt's facing against the
 * controller's current front/upwards/flip and force-corrected any belt blockstate
 * whose facing no longer matched. That matters because the belt predicate is
 * facing-agnostic (block type only), so a belt rotated after formation — e.g. by a
 * wrench — does not break the structure and would otherwise keep a wrong travel
 * direction. This class keeps that behaviour as a formed-gated server-tick runnable;
 * exactly like the legacy guard it only writes when the facing actually differs, so
 * the steady state performs no world mutations.
 *
 * <p>
 * The legacy {@code invalidateStructure()} list-clearing needs no port: the fixup
 * list lives in the multiblock match context, which the framework discards on
 * invalidation. Extended facing is disabled and flipping allowed via the definition
 * (legacy {@code allowsExtendedFacing() == false}, {@code allowsFlip() == true}).
 */
public class CurtainCoaterMachine extends SuSyOrientationFixupMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            CurtainCoaterMachine.class, SuSyOrientationFixupMachine.MANAGED_FIELD_HOLDER);

    public CurtainCoaterMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            subscribeServerTick(this::reassertBeltFacings);
        }
    }

    /**
     * Per-server-tick port of the legacy {@code updateFormedValid()} belt loop:
     * re-resolve each recorded conveyor belt's facing against the controller's current
     * orientation and repair any belt whose facing drifted after formation. Only
     * writes when the resolved state actually differs, so a correctly-oriented belt
     * line costs one blockstate read per belt per tick and no block updates.
     */
    protected void reassertBeltFacings() {
        if (!isFormed()) {
            return;
        }
        var level = getLevel();
        if (level == null || level.isClientSide) {
            return;
        }
        // Copy before iterating: a correcting setBlockAndUpdate fires the multiblock
        // block-state listener, which may invalidate the structure mid-loop.
        for (var fixup : List.copyOf(SuSyPredicates.getOrientationFixups(this))) {
            var pos = fixup.getLeft();
            var facing = SuSyPredicates.resolveFacing(this, fixup.getRight());
            var state = level.getBlockState(pos);
            var oriented = SuSyPredicates.withHorizontalFacing(state, facing);
            if (oriented != state) {
                level.setBlockAndUpdate(pos, oriented);
            }
        }
    }
}
