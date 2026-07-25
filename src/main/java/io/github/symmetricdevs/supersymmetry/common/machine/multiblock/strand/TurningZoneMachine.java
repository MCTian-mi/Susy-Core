package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.strand;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import io.github.symmetricdevs.supersymmetry.api.capability.Strand;
import io.github.symmetricdevs.supersymmetry.api.pattern.SuSyPredicates;

import net.minecraft.MethodsReturnNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityTurningZone}: reshapes the strand by
 * rotating it 90 degrees (thickness and width are swapped).
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TurningZoneMachine extends StrandShaperMachine {

    public TurningZoneMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    protected boolean consumeInputsAndSetupRecipe() {
        if (this.input == null || this.input.getStrand() == null) {
            return false;
        }
        this.input.take();
        this.maxProgress = 20;
        return true;
    }

    @Override
    protected @Nullable Strand resultingStrand() {
        if (this.input == null || this.input.getStrand() == null) return null;
        Strand str = new Strand(this.input.getStrand());
        double tmp = str.thickness;
        str.thickness = str.width;
        str.width = tmp;
        return str;
    }

    public static TraceabilityPredicate rollOrientation(RelativeDirection direction) {
        return SuSyPredicates.axialOrientation(
                io.github.symmetricdevs.supersymmetry.common.data.SusyBlocks.METALLURGY_ROLL.get(), direction);
    }

    public static TraceabilityPredicate sheetedFrame() {
        return com.gregtechceu.gtceu.api.pattern.Predicates.frames(GTMaterials.Steel);
    }
}
