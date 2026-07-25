package io.github.symmetricdevs.supersymmetry.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Full-collision casing with non-occluding inner faces and the reduced light blocking used by
 * the legacy coalescence plate and intermediate diaphragm.
 */
public class InnerCasingBlock extends Block {

    public InnerCasingBlock(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 3;
    }
}
