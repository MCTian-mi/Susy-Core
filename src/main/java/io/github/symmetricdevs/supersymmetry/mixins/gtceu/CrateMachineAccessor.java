package io.github.symmetricdevs.supersymmetry.mixins.gtceu;

import com.gregtechceu.gtceu.common.machine.storage.CrateMachine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor for {@link CrateMachine} internals that SuSy's locked crate needs to
 * manipulate. GTCEu-Modern does not expose a setter for the taped flag, and the
 * inventory has no public stack-limit hook, so a tiny accessor keeps the locked
 * crate sealed and single-stack.
 */
@Mixin(value = CrateMachine.class, remap = false)
public interface CrateMachineAccessor {

    @Accessor("isTaped")
    void setTaped(boolean taped);
}
