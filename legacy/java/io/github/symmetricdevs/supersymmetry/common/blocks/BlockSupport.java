package io.github.symmetricdevs.supersymmetry.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlockSupport extends VariantBlock<BlockSupport.SupportType> {

    public BlockSupport() {
        super(Material.IRON);
        setTranslationKey("support");
        setBlockUnbreakable();
    }

    public void onEntityCollision(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state,
                                  Entity entityIn) {
        entityIn.motionX = MathHelper.clamp(entityIn.motionX, -0.15, 0.15);
        entityIn.motionZ = MathHelper.clamp(entityIn.motionZ, -0.15, 0.15);
        entityIn.fallDistance = 0.0F;
        if (entityIn.motionY < -0.15) {
            entityIn.motionY = -0.15;
        }

        if (entityIn.isSneaking() && entityIn.motionY < 0.0) {
            entityIn.motionY = 0.0;
        }

        if (entityIn.collidedHorizontally) {
            entityIn.motionY = 0.3;
        }
    }

    @Nonnull
    public EnumPushReaction getPushReaction(@Nonnull BlockState state) {
        return EnumPushReaction.NORMAL;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state,
                         int fortune) {
        // Nothing
    }
    /*
     * @Override
     * 
     * @NotNull
     * 
     * @SuppressWarnings("deprecation")
     * public AABB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos)
     * {
     * return ;
     * }
     */

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(@NotNull BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(@NotNull BlockState state) {
        return false;
    }

    public enum SupportType implements IStringSerializable {

        LAUNCH_PAD_TYPE("lv");

        public final String name;

        SupportType(String name) {
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
