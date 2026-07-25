package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.DistillationTowerMachine;

/**
 * Low Pressure Cryogenic Distillation Plant.
 *
 * <p>
 * Port of the 1.12.2 {@code MetaTileEntityLowPressureCryogenicDistillationPlant}
 * (which extended {@code MetaTileEntityOrderedDT}). The ordered/per-layer fluid
 * output behaviour that {@code MetaTileEntityOrderedDT} + {@code DistillationTowerLogicHandler}
 * provided in 1.12.2 is natively implemented by modern {@link DistillationTowerMachine}:
 * output index == Y layer, outputs gathered from the controller Y + yOffset upward.
 *
 * <p>
 * The 1.12.2 source gathered fluid outputs starting one layer above the controller
 * (base "DDD" aisle, controller aisle, then the repeatable "XXX" output aisles), so the
 * default {@code yOffset == 1} (via {@link #LowPressureCryogenicDistillationPlant(IMachineBlockEntity)})
 * is correct and no explicit yOffset ctor is required for the pattern to function.
 *
 * <p>
 * TODO)) Bucket D (cryo coupling): the 1.12.2 tower is an {@code ICryogenicProvider}
 * (it implements {@code ICryogenicProvider} and holds a {@code @Nullable ICryogenicReceiver receiver}).
 * It linked to a Bath Condenser ({@code ICryogenicReceiver}) at the pattern's 'Z' position via a
 * SIDE-EFFECTING {@code cryogenicRecieverPredicate()} that called {@code setCryogenicProvider(...)}
 * at structure-match time, and cleared it again in {@code invalidateStructure()}.
 * Modern pattern predicates are pure (no world side-effects), so this cross-machine link is
 * intentionally NOT wired here. For this pass the tower forms and runs its recipes; the
 * 'Z' position is matched structurally (frost-proof casing) only. Re-implement the
 * ICryogenicProvider/ICryogenicReceiver handshake together with {@code SuSyPredicates} in Bucket D —
 * likely by resolving the adjacent Bath Condenser controller in {@code onStructureFormed()} and
 * clearing it in {@code onStructureInvalid()}, mirroring the old {@code setReceiver}/{@code invalidateStructure}
 * pair.
 */
public class LowPressureCryogenicDistillationPlant extends DistillationTowerMachine {

    public LowPressureCryogenicDistillationPlant(IMachineBlockEntity holder) {
        super(holder, 1);
    }
}
