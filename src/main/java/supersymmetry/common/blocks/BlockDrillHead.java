package supersymmetry.common.blocks;

import gregtech.api.block.IStateHarvestLevel;
import gregtech.api.block.VariantBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import supersymmetry.common.tileentities.TileEntityDrillHead;

import javax.annotation.Nonnull;

import static net.minecraft.block.BlockDirectional.FACING;

public class BlockDrillHead extends VariantBlock<BlockDrillHead.DrillHeadType> implements ITileEntityProvider {

    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockDrillHead(){
        super(net.minecraft.block.material.Material.IRON);
        setTranslationKey("drill_head");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(DrillHeadType.STEEL));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    //TODO: MAKE THIS CREATE MINING PARTICLES WHEN MINING DRILL IS ACTIVE
    //TODO: MAKE THIS PLAY A LOUD MINING NOISE, PERHAPS HAVE STATUS EFFECTS FOR PLAYERS WHO COME NEAR THE MINING

    @NotNull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
        return hasTileEntity(state) ? createNewTileEntity(world, getMetaFromState(state)) : null;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
        return new TileEntityDrillHead();
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
        return state.getValue(ACTIVE) ? EnumBlockRenderType.ENTITYBLOCK_ANIMATED : EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(ACTIVE);
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @NotNull EntityLivingBase placer) {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
                .withProperty(ACTIVE, false);
    }

    @Nonnull
    @Override
    public BlockStateContainer createBlockState() {
        Class<DrillHeadType> enumClass = DrillHeadType.class;
        this.VARIANT = PropertyEnum.create("variant", enumClass);
        this.VALUES = enumClass.getEnumConstants();
        return new BlockStateContainer(this, VARIANT, ACTIVE);
    }

    @Override
    public int damageDropped(@NotNull IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean active = meta % 2 == 1;
        return getDefaultState()
                .withProperty(VARIANT, VALUES[meta / 2])
                .withProperty(ACTIVE, active);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).ordinal() * 2 + (state.getValue(ACTIVE) ? 1 : 0);
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(IBlockState state, @NotNull RayTraceResult target, @NotNull World world, @NotNull BlockPos pos, @NotNull EntityPlayer player) {
        return this.getItemVariant(state.getValue(VARIANT), 1);
    }

    public enum DrillHeadType implements IStringSerializable, IStateHarvestLevel {
        STEEL("steel", 1);

        private final String name;
        private final int harvestLevel;

        DrillHeadType(String name, int harvestLevel) {
            this.name = name;
            this.harvestLevel = harvestLevel;
        }

        @Nonnull
        public String getName() {
            return this.name;
        }

        public int getHarvestLevel(IBlockState state) {
            return this.harvestLevel;
        }

        public String getHarvestTool(IBlockState state) {
            return "wrench";
        }
    }
}
