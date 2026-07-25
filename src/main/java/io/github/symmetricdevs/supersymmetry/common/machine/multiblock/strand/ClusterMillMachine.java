package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.strand;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import io.github.symmetricdevs.supersymmetry.api.capability.Strand;

import net.minecraft.MethodsReturnNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityClusterMill}: a faster rolling mill.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClusterMillMachine extends RollingMillMachine {

    public ClusterMillMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    protected boolean consumeInputsAndSetupRecipe() {
        Strand orig = this.input == null ? null : this.input.take();
        if (orig == null) return false;
        this.maxProgress = (int) Math.ceil(1 / (8.0 * orig.thickness));
        return true;
    }

    @Override
    protected @Nullable Strand resultingStrand() {
        if (this.input == null || this.input.getStrand() == null || this.input.getStrand().isCut) return null;
        Strand str = new Strand(this.input.getStrand());
        // t / (2 - e^(-8t)) is a pretty good function for balancing
        double scaling = 2 - Math.pow(Math.E, -8 * str.thickness);
        str.thickness /= scaling;
        str.width *= scaling;
        return str;
    }
}
