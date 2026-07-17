package io.github.symmetricdevs.supersymmetry.api.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import org.jetbrains.annotations.Nullable;

/**
 * A horizontally-rotatable block extending {@link HorizontalDirectionalBlock}.
 * <p>
 * Registers the inherited {@link HorizontalDirectionalBlock#FACING} property and
 * places the block facing away from the player (so the "front" faces the placer).
 */
public class VariantHorizontalRotatableBlock extends HorizontalDirectionalBlock {

    public VariantHorizontalRotatableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
}
