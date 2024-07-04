package supersymmetry.api.blocks;

import gregtech.api.block.VariantBlock;
import gregtech.common.items.tool.rotation.CustomBlockRotations;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static gregtech.common.items.tool.rotation.CustomBlockRotations.BLOCK_DIRECTIONAL_BEHAVIOR;

public class VariantDirectionalRotatableBlock<T extends Enum<T> & IStringSerializable> extends VariantBlock<T> {
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public VariantDirectionalRotatableBlock(Material materialIn) {
        super(materialIn);
        this.setDefaultState(blockState.getBaseState().withProperty(VARIANT, VALUES[0]).withProperty(FACING, EnumFacing.SOUTH));
        CustomBlockRotations.registerCustomRotation(this, BLOCK_DIRECTIONAL_BEHAVIOR);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @NotNull EntityLivingBase placer) {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
    }

    @Override
    public ItemStack getItemVariant(T variant, int amount) {
        return new ItemStack(this, amount, variant.ordinal() * 6);
    }

    @Nonnull
    @Override
    public BlockStateContainer createBlockState() {
        Class<T> enumClass = getActualTypeParameter(getClass(), VariantDirectionalRotatableBlock.class);
        this.VARIANT = PropertyEnum.create("variant", enumClass);
        this.VALUES = enumClass.getEnumConstants();
        return new BlockStateContainer(this, VARIANT, FACING);
    }

    @Override
    public int damageDropped(@NotNull IBlockState state) {
        return state.getValue(VARIANT).ordinal() * 6;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        int i = meta / 6;
        // Makes meta = 0 -> EAST(ord = 5)
        int j = meta % 6;
        j = switch (j) {
            case 0 -> 2;
            case 1 -> 5;
            case 2, 3 -> j - 2;
            default -> j - 1;
        };

        EnumFacing enumfacing = EnumFacing.byIndex(j);
        return getDefaultState()
                .withProperty(FACING, enumfacing)
                .withProperty(VARIANT, VALUES[i % VALUES.length]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int otherIndex = state.getValue(FACING).getIndex();
        otherIndex = switch (otherIndex) {
            case 0, 1 -> otherIndex + 2;
            case 2 -> 0;
            case 3, 4 -> otherIndex + 1;
            default -> 1;
        };
        return state.getValue(VARIANT).ordinal() * 6 + otherIndex;
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(IBlockState state, @NotNull RayTraceResult target, @NotNull World world, @NotNull BlockPos pos, @NotNull EntityPlayer player) {
        return getItemVariant(state.getValue(VARIANT), 1);
    }
}
