package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import org.jetbrains.annotations.NotNull;

/**
 * Modern port of the 1.12.2
 * {@code supersymmetry.common.metatileentities.multi.electric.MetaTileEntityHotIsostaticPress}.
 *
 * <p>
 * The legacy controller carried no bespoke recipe logic. Its structure pattern used the
 * six-way {@code orientation} predicate so the two hydraulic cylinders (one
 * {@link com.gregtechceu.gtceu.api.pattern.util.RelativeDirection#UP UP} above the coil
 * chamber, one {@code DOWN} below) snapped into their working facing as the structure
 * formed; it rejected extended (vertical) controller facing via
 * {@code allowsExtendedFacing() == false}; and it overrode only textures. In the port:
 * <ul>
 * <li>the cylinder facing fixups are inherited from {@link SuSyOrientationFixupMachine} —
 * the pure {@code SuSyPredicates.orientation} predicate records
 * {@code (pos, RelativeDirection)} during matching, and {@code onStructureFormed()}
 * resolves the exact six-way facing against the controller and applies it to the
 * {@link io.github.symmetricdevs.supersymmetry.common.block.DirectionalOrientableBlock}
 * {@code FACING} property (identical to the legacy predicate's
 * {@code dir.getRelativeFacing(front, upwards, flipped)} resolution, no axial
 * collapse);</li>
 * <li>{@code allowsExtendedFacing} is expressed by the registration's
 * {@code .allowExtendedFacing(false)};</li>
 * <li>the base casing texture and front overlay are the registration's
 * {@code .workableCasingModel(...)}.</li>
 * </ul>
 *
 * <p>
 * All structure content (silicon-carbide casing, steel pipe casing, nichrome coils, Invar
 * frames pending the unported Incoloy 908 material) lives in the
 * {@code HOT_ISOSTATIC_PRESS} registration in {@code SusyMachines}.
 */
public class HotIsostaticPressMachine extends SuSyOrientationFixupMachine {

    // No new @Persisted/@DescSynced fields yet; the chained holder keeps this class safe
    // against the silent-data-loss trap if any are added later (sibling idiom).
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            HotIsostaticPressMachine.class, SuSyOrientationFixupMachine.MANAGED_FIELD_HOLDER);

    public HotIsostaticPressMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
