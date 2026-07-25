package io.github.symmetricdevs.supersymmetry.api.MetaMachine;

/**
 * Interface for pseudo-multiblock machines.
 * A pseudo multiblock is a single-block machine that checks adjacent blocks
 * for structure validity, functioning like a mini multiblock.
 */
public interface PseudoMultiMachineMetaTileEntity {

    /**
     * Check whether the block at the given position is a valid
     * structure block for this pseudo multiblock.
     *
     * @param pos the block position to check
     * @return true if the block at pos is part of the valid structure
     */
    boolean isSameBlock(net.minecraft.core.BlockPos pos);
}
