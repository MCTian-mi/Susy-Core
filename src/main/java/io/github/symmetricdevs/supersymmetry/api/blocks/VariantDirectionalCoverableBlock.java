package io.github.symmetricdevs.supersymmetry.api.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import org.jetbrains.annotations.Nullable;

/**
 * A directional block that supports cover attachment, extending {@link DirectionalBlock}.
 * <p>
 * Registers the inherited {@link DirectionalBlock#FACING} property and provides
 * standard placement logic: sneaking uses the clicked face; normal placement uses the
 * player's nearest looking direction.
 */
public class VariantDirectionalCoverableBlock extends DirectionalBlock {

    public VariantDirectionalCoverableBlock(Properties properties) {
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
        Direction facing;
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            facing = context.getClickedFace();
        } else {
            facing = context.getNearestLookingDirection();
        }
        return this.defaultBlockState().setValue(FACING, facing);
    }
}
