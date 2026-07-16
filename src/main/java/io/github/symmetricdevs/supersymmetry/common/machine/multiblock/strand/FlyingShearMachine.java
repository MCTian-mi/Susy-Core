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
 * Modern port of the 1.12.2 {@code MetaTileEntityFlyingShear}: cuts a strand.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FlyingShearMachine extends StrandShaperMachine {

    public FlyingShearMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    protected boolean consumeInputsAndSetupRecipe() {
        Strand orig = this.input == null ? null : this.input.take();
        if (orig == null) return false;
        this.maxProgress = 10;
        return true;
    }

    @Override
    protected @Nullable Strand resultingStrand() {
        if (this.input == null || this.input.getStrand() == null) return null;
        Strand str = new Strand(this.input.getStrand());
        str.isCut = true;
        return str;
    }

    public static TraceabilityPredicate rollOrientation(RelativeDirection direction) {
        return SuSyPredicates.axialOrientation(SusyBlocks.METALLURGY_ROLL.get(), direction);
    }
}
