package io.github.symmetricdevs.supersymmetry.api.MetaMachine;

/**
 * Interface for continuous-processing machines.
 * Implementations return true from isContinuous() to signal that they
 * should use continuous recipe logic.
 */
public interface ContinuousMachineMetaTileEntity {

    /**
     * @return true if this machine operates continuously
     */
    default boolean isContinuous() {
        return true;
    }
}
