package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.strand;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntitySlabMold}.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SlabMoldMachine extends StrandMoldMachine {

    public SlabMoldMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    protected int getRequiredMetal() {
        return 2592;
    }

    @Override
    protected double getOutputThickness() {
        return 0.5;
    }

    @Override
    protected double getOutputWidth() {
        return 2.0;
    }
}
