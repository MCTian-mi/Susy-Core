package io.github.symmetricdevs.supersymmetry.api.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;

import java.util.List;

/**
 * Modern port of the 1.12.2 {@code IRedstoneControllable}: a multiblock controller that
 * exposes a numbered list of redstone-triggered operations. A
 * {@link io.github.symmetricdevs.supersymmetry.common.machine.multiblock.part.ComponentRedstoneControllerMachine}
 * part can select an operation and pulse it when the part receives a redstone signal.
 */
public interface IRedstoneControllable {

    /** Whether redstone control is currently enabled for this controller. */
    default boolean redstoneControlEnabled() {
        return this instanceof IMultiController multi && multi.isFormed();
    }

    /** Highest valid signal index ({@code getSignals().size() - 1}). */
    default int getSignalCeiling() {
        return this.getSignals().size() - 1;
    }

    /** Translation-key suffix for the signal at {@code sig}. */
    String getSignalName(int sig);

    /** All signal names, used both for indexing and for the controller part UI. */
    List<String> getSignals();

    /** Execute the operation associated with {@code sig}. */
    void pulse(int sig);
}
