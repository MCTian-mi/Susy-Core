package io.github.symmetricdevs.supersymmetry.common.blocks;

import static net.minecraft.world.level.material.Material.IRON;

import net.minecraft.world.level.block.BlockHorizontal;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.cover.CoverRayTracer;
import com.gregtechceu.gtceu.common.items.tool.rotation.CustomBlockRotations;
import com.gregtechceu.gtceu.common.items.tool.rotation.ICustomRotationBehavior;
import io.github.symmetricdevs.supersymmetry.api.blocks.VariantHorizontalRotatableBlock;

public class BlockConveyor extends VariantHorizontalRotatableBlock<BlockConveyor.ConveyorType> {

    static final AABB AABB_BOTTOM_HALF = new AABB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);

    public static final ICustomRotationBehavior BLOCK_FLAT_HORIZONTAL_BEHAVIOR = new ICustomRotationBehavior() {

        @Override
        public boolean customRotate(BlockState state, World world, BlockPos pos, HitResult hitResult) {
            // Prohibit rotate other than up/down faces, as base GT does not support non-square faces
            if (hitResult.sideHit != Direction.UP && hitResult.sideHit != Direction.DOWN)
                return false;
            // The rest is the same with BLOCK_HORIZONTAL_BEHAVIOR
            Direction gridSide = CoverRayTracer.determineGridSideHit(hitResult);
            if (gridSide == null) return false;
            if (gridSide.getAxis() == Direction.Axis.Y) return false;

            if (gridSide != state.getValue(BlockHorizontal.FACING)) {
                state = state.withProperty(BlockHorizontal.FACING, gridSide);
                world.setBlockState(pos, state);
                return true;
            }
            return false;
        }

        @Override
        public boolean showGrid() {
            return false;
        }
    };

    public BlockConveyor() {
        super(IRON);
        CustomBlockRotations.registerCustomRotation(this, BLOCK_FLAT_HORIZONTAL_BEHAVIOR);
        setTranslationKey("conveyor_belt");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(ConveyorType.LV_CONVEYOR));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull BlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    @Override
    @NotNull
    @SuppressWarnings("deprecation")
    public AABB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
        return AABB_BOTTOM_HALF;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(@NotNull BlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(@NotNull BlockState state) {
        return false;
    }

    public enum ConveyorType implements IStringSerializable {

        LV_CONVEYOR("lv");

        public final String name;

        ConveyorType(String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public String getName() {
            return this.name;
        }

        @NotNull
        @Override
        public String toString() {
            return getName();
        }
    }
}
