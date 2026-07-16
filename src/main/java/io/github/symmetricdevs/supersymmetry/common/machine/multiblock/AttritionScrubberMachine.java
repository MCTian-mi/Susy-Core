package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityAttritionScrubber}
 * (supersymmetry.common.metatileentities.multi.electric).
 *
 * <p>Behaviour inventory of the legacy controller, and where each piece lives now:
 * <ul>
 * <li>{@code new MultiblockRecipeLogic(this, false)} — non-perfect overclocking —
 * is the definition's {@code GTRecipeModifiers.OC_NON_PERFECT} recipe modifier.</li>
 * <li>{@code getBaseTexture(...) == ABRASION_RESISTANT_CASING} is the definition's
 * appearance block and base casing model.</li>
 * <li>The legacy tooltip {@code susy.machine.parallel_pure} (32x) was pure
 * advertising — no parallel logic ever ran — and is intentionally not ported.</li>
 * <li>The legacy pattern declared {@code 'M' -> hiddenGearTooth(...)} but no aisle
 * contains {@code 'M'}, so the predicate could never match: no girth-gear-tooth
 * orientation fixup ran and no "Hidden" renderer-mask positions were ever recorded
 * for this machine. The dead clause is deliberately not ported.</li>
 * </ul>
 *
 * <p>What remains is a plain workable electric multiblock, so this class carries no
 * bespoke logic; it exists as the named controller type for the definition and as
 * the anchor for any future attrition-specific behaviour.
 */
// TODO)) Phase 6: if this structure ever gains the gear-tooth ring the dead legacy
//  'M' clause hinted at, wire SuSyPredicates.axialOrientation (via
//  SuSyOrientationFixupMachine) plus the hidden-position renderer mask (the 1.12.2
//  "Hidden" match-context key); client-side hiding of interior blocks while formed
//  is a Phase 6 renderer concern.
public class AttritionScrubberMachine extends WorkableElectricMultiblockMachine {

    public AttritionScrubberMachine(IMachineBlockEntity holder) {
        super(holder);
    }
}
