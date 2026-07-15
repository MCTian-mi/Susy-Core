package io.github.symmetricdevs.supersymmetry.common.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * A concrete, cube-all {@link HorizontalDirectionalBlock} for SuSy casing blocks that
 * carry a horizontal {@code FACING} property but no other behaviour. Used by the
 * generator rotor / alternator-coil / crankshaft casings so a multiblock controller can
 * auto-orient the block on structure form (the modern replacement for the 1.12.2
 * side-effecting {@code horizontalOrientation} pattern predicate). The per-facing front
 * texture is a Phase 6 rendering concern; for now the block renders the same texture on
 * all faces, rotated by {@code FACING}.
 */
public class HorizontalOrientableBlock extends HorizontalDirectionalBlock {

    public HorizontalOrientableBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
