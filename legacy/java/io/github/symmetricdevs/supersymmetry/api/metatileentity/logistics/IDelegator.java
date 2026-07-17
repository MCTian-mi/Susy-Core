package io.github.symmetricdevs.supersymmetry.api.MetaMachine.logistics;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.Nullable;

// Mostly a marker interface
public interface IDelegator {

    /**
     * @return the facing that the input facing in delegating
     */
    @Nullable
    Direction getDelegatingFacing(Direction facing);
}
