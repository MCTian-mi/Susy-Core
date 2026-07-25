package io.github.symmetricdevs.supersymmetry.common.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * A concrete, cube-all {@link DirectionalBlock} for SuSy casing blocks that
 * carry a six-way {@code FACING} property but no other behaviour. This is used
 * by structure parts such as the hot-isostatic-press hydraulic cylinder, whose
 * working direction may be vertical. The directional front texture is a
 * {@code // TODO))} Phase 6 rendering concern.
 */
public class DirectionalOrientableBlock extends DirectionalBlock {

    public DirectionalOrientableBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
