package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.strand;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;

import io.github.symmetricdevs.supersymmetry.api.capability.Strand;
import io.github.symmetricdevs.supersymmetry.api.pattern.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.common.data.SusyBlocks;

import net.minecraft.MethodsReturnNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityRollingMill}: thins and widens a strand.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RollingMillMachine extends StrandShaperMachine {

    public RollingMillMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    protected boolean consumeInputsAndSetupRecipe() {
        Strand orig = this.input == null ? null : this.input.take();
        if (orig == null) return false;
        this.maxProgress = (int) Math.ceil(1 / (4.0 * orig.thickness));
        return true;
    }

    @Override
    protected @Nullable Strand resultingStrand() {
        if (this.input == null || this.input.getStrand() == null || this.input.getStrand().isCut) return null;
        Strand str = new Strand(this.input.getStrand());
        // t / (2 - e^(-2t)) is a pretty good function for balancing
        double scaling = 2 - Math.pow(Math.E, -2 * str.thickness);
        str.thickness /= scaling;
        str.width *= scaling;
        return str;
    }

    public static TraceabilityPredicate rollOrientation(RelativeDirection direction) {
        return SuSyPredicates.axialOrientation(SusyBlocks.METALLURGY_ROLL.get(), direction);
    }

    public static TraceabilityPredicate hydraulicOrientation(RelativeDirection direction) {
        return SuSyPredicates.orientation(SusyBlocks.HYDRAULIC_CYLINDER.get(), direction);
    }
}
