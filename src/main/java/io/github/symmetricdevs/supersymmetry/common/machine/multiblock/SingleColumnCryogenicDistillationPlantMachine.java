package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.DistillationTowerMachine;

/**
 * Ordered distillation tower controller for the Single-Column Cryogenic
 * Distillation Plant. Ported from the 1.12.2
 * {@code MetaTileEntitySingleColumnCryogenicDistillationPlant}, which extended
 * {@code MetaTileEntityOrderedDT} (per-Y-layer fluid outputs, output index =
 * layer). Modern {@link DistillationTowerMachine} already implements exactly that
 * behaviour; this subclass exists to (a) pin the yOffset that the 1.12.2
 * {@code ExtendedDTLogicHandler(this, 2, i -> 1)} encoded and (b) carry the
 * cryo-coupling TODO below.
 * <p>
 * 1.12.2 offset mapping: {@code ExtendedDTLogicHandler} started its ordered fluid
 * outputs at {@code controllerY + offsetCounter.apply(layerCount)} with
 * {@code offsetCounter = i -> 1} (constant 1), i.e. the first fluid output layer is
 * one block above the controller. That is precisely the Modern default
 * {@code yOffset = 1} used by the {@code (holder)} constructor, so no explicit
 * yOffset override is needed here.
 * <p>
 * // TODO)) Bucket D (SuSyPredicates): port the ICryogenicReceiver /
 * ICryogenicProvider cross-machine link. In 1.12.2 this tower implemented
 * ICryogenicProvider and exposed a {@code cryogenicRecieverPredicate()} as the
 * ' ' (space) slot in the structure pattern — a SIDE-EFFECTING predicate that
 * captured the adjacent Bath Condenser (ICryogenicReceiver) at structure-match
 * time and called {@code receiver.setCryogenicProvider(this)} (cleared again in
 * {@code invalidateStructure()}). Modern pattern predicates are pure (no world
 * side-effects), so the link must instead be established in
 * {@code onStructureFormed()} (scan the blocks adjacent to the ' ' slot) and torn
 * down in {@code onStructureInvalid()}, once the ICryogenicProvider /
 * ICryogenicReceiver capability pair is ported. The ' ' slot is currently matched
 * with {@code Predicates.any()} so the shell forms and runs its recipes without
 * the link.
 */
public class SingleColumnCryogenicDistillationPlantMachine extends DistillationTowerMachine {

    public SingleColumnCryogenicDistillationPlantMachine(IMachineBlockEntity holder) {
        // yOffset defaults to 1 (controllerY + 1 == first fluid output layer),
        // matching the 1.12.2 ExtendedDTLogicHandler offsetCounter (i -> 1).
        super(holder);
    }
}
