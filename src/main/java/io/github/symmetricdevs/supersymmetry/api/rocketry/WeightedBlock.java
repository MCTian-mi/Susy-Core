package io.github.symmetricdevs.supersymmetry.api.rocketry;

import net.minecraft.world.level.block.state.BlockState;

public interface WeightedBlock {

    double getMass(BlockState state);
}
