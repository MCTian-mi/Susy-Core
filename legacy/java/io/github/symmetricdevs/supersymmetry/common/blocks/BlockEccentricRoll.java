package io.github.symmetricdevs.supersymmetry.common.blocks;

import static gregtech.common.items.tool.rotation.CustomBlockRotations.BLOCK_DIRECTIONAL_BEHAVIOR;
import static net.minecraft.world.level.block.BlockDirectional.FACING;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.PropertyEnum;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;
import com.gregtechceu.gtceu.common.items.tool.rotation.CustomBlockRotations;
import io.github.symmetricdevs.supersymmetry.api.blocks.IAnimatablePartBlock;
import io.github.symmetricdevs.supersymmetry.api.util.SuSyDamageSources;

public class BlockEccentricRoll extends VariantBlock<BlockEccentricRoll.RollType> implements IAnimatablePartBlock {

    /// So that [#onEntityCollision(World, BlockPos, BlockState, Entity)] works properly
    public static final AABB COLLISION_BOX = new AABB(0.05, 0.05, 0.05, 0.95, 0.95, 0.95);

    public BlockEccentricRoll() {
        super(net.minecraft.world.level.material.Material.IRON);
        setTranslationKey("eccentric_roll");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(blockState.getBaseState().withProperty(ACTIVE, false));
        CustomBlockRotations.registerCustomRotation(this, BLOCK_DIRECTIONAL_BEHAVIOR);
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(@NotNull World world, @NotNull BlockState state) {
        return hasTileEntity(state) ? createNewTileEntity(world, getMetaFromState(state)) : null;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(ACTIVE);
    }

    @NotNull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(@NotNull BlockState state) {
        return state.getValue(ACTIVE) ? EnumBlockRenderType.ENTITYBLOCK_ANIMATED : EnumBlockRenderType.MODEL;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForPlacement(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull Direction facing,
                                            float hitX, float hitY, float hitZ, int meta,
                                            @NotNull LivingEntity placer) {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
                .withProperty(FACING, Direction.getDirectionFromEntityLiving(pos, placer))
                .withProperty(ACTIVE, false);
    }

    @Nonnull
    @Override
    public BlockStateContainer createBlockState() {
        Class<RollType> enumClass = RollType.class;
        this.VARIANT = PropertyEnum.create("variant", enumClass);
        this.VALUES = enumClass.getEnumConstants();
        return new BlockStateContainer(this, VARIANT, FACING, ACTIVE);
    }

    @Override
    public int damageDropped(@NotNull BlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @Nonnull
    @Override
    public BlockState getStateFromMeta(int meta) {
        // (InActive) North... -> 0...=5
        // (Active) North... -> 6...=11
        int facing = meta % 6;
        boolean active = meta > 5;

        Direction Direction = Direction.byIndex(facing);
        return getDefaultState()
                .withProperty(VARIANT, VALUES[meta / 12])
                .withProperty(FACING, Direction)
                .withProperty(ACTIVE, active);
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(FACING).ordinal() + (state.getValue(ACTIVE) ? 6 : 0);
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(BlockState state, @NotNull HitResult target, @NotNull World world,
                                  @NotNull BlockPos pos, @NotNull Player player) {
        return this.getItemVariant(state.getValue(VARIANT), 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(@NotNull BlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(@NotNull BlockState state) {
        return false;
    }

    @Override
    public void onEntityCollision(@NotNull World worldIn, @NotNull BlockPos pos,
                                  @NotNull BlockState state, @NotNull Entity entityIn) {
        super.onEntityCollision(worldIn, pos, state, entityIn);

        if (state.getValue(ACTIVE)) {
            entityIn.attackEntityFrom(SuSyDamageSources.getCrusherDamage(), 2.0F);
        }
    }

    @SuppressWarnings("deprecation")
    @Nullable
    public AABB getCollisionBoundingBox(@NotNull BlockState blockState,
                                                 @NotNull IBlockAccess worldIn,
                                                 @NotNull BlockPos pos) {
        return COLLISION_BOX;
    }

    public enum RollType implements IStringSerializable, IStateHarvestLevel {

        STEEL("steel", 1);

        private final String name;
        private final int harvestLevel;

        RollType(String name, int harvestLevel) {
            this.name = name;
            this.harvestLevel = harvestLevel;
        }

        @Nonnull
        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public int getHarvestLevel(BlockState state) {
            return this.harvestLevel;
        }

        @Override
        public String getHarvestTool(BlockState state) {
            return "wrench";
        }
    }
}
