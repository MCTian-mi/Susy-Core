package io.github.symmetricdevs.supersymmetry.api.recipes.properties;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;

public class PseudoMultiPropertyValues {

    public ArrayList<BlockState> validBlockStates;

    public final String blockGroupName;

    public PseudoMultiPropertyValues(@Nonnull String blockGroupName, @Nonnull BlockState... validBlockStates) {
        this.validBlockStates = new ArrayList<>(Arrays.asList(validBlockStates));
        this.blockGroupName = blockGroupName;
    }

    public PseudoMultiPropertyValues(@Nonnull String blockGroupName, @Nonnull ArrayList<BlockState> validBlocks) {
        this.validBlockStates = validBlocks;
        this.blockGroupName = blockGroupName;
    }

    @Nonnull
    public ArrayList<BlockState> getValidBlockStates() {
        return validBlockStates;
    }

    @Nonnull
    public String getBlockGroupName() {
        return blockGroupName;
    }
}
