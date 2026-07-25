package io.github.symmetricdevs.supersymmetry.common.block;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import io.github.symmetricdevs.supersymmetry.api.util.SuSyDamageSources;
import io.github.symmetricdevs.supersymmetry.common.blockentity.EccentricRollBlockEntity;

import org.jetbrains.annotations.Nullable;

/**
 * Eccentric crusher roll: a GTCEu {@link ActiveBlock} carrying its own {@link EccentricRollBlockEntity}
 * so GeckoLib can render the spinning roll. The parent {@code WorkableMultiblockMachine} auto-collects
 * this block's position into its {@code vaBlocks} set (any {@code ActiveBlock} in the pattern) and
 * toggles {@code ACTIVE} while working, driving the animation. Ported from the 1.12.2
 * {@code BlockEccentricRoll} + {@code IAnimatablePartBlock}.
 */
public class EccentricRollBlock extends ActiveBlock implements EntityBlock {

    /** Inset collision box (legacy {@code COLLISION_BOX}) so the roll geometry isn't a full cube. */
    private static final VoxelShape SHAPE = Shapes.box(0.05, 0.05, 0.05, 0.95, 0.95, 0.95);

    public EccentricRollBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(DirectionalBlock.FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DirectionalBlock.FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Match EnumFacing.getDirectionFromEntityLiving from the legacy block: it uses the
        // player's horizontal body direction, with the original eye-height thresholds for vertical
        // placement. Keep the clicked-face fallback for non-player placement.
        if (context.getPlayer() == null) {
            return defaultBlockState().setValue(DirectionalBlock.FACING, context.getClickedFace().getOpposite());
        }

        var player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction facing = player.getDirection().getOpposite();
        if (Math.abs(player.getX() - (pos.getX() + 0.5D)) < 2.0D &&
                Math.abs(player.getZ() - (pos.getZ() + 0.5D)) < 2.0D) {
            double eyeY = player.getY() + player.getEyeHeight();
            if (eyeY - pos.getY() > 2.0D) {
                facing = Direction.UP;
            } else if (pos.getY() - eyeY > 0.0D) {
                facing = Direction.DOWN;
            }
        }
        return defaultBlockState().setValue(DirectionalBlock.FACING, facing);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        // The legacy block uses the baked steel-roll model while stationary and switches to its
        // animated renderer only when a formed crusher marks the ActiveBlock active.
        return state.getValue(GTBlockStateProperties.ACTIVE)
                ? RenderShape.ENTITYBLOCK_ANIMATED
                : RenderShape.MODEL;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && state.getValue(GTBlockStateProperties.ACTIVE)) {
            entity.hurt(SuSyDamageSources.getCrusherDamage(level), 2.0F);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EccentricRollBlockEntity(pos, state);
    }
}
