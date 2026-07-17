package io.github.symmetricdevs.supersymmetry.common.blocks;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.PropertyBool;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class BlockStockDetector extends Block implements ITileEntityProvider {

    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockStockDetector() {
        super(Material.IRON);
        setHarvestLevel("pickaxe", 0);
        setHardness(2F);
        setResistance(15F);
        setDefaultState(getDefaultState().withProperty(POWERED, Boolean.valueOf(false)));
        setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Override
    public BlockEntity createNewTileEntity(World worldIn, int meta) {
        return null;
        // return new stock_detector_tile_entity(worldIn, meta);
    }

    @Override
    public Item getItemDropped(BlockState p_180660_1_, Random p_180660_2_, int p_180660_3_) {
        return null;
        // return Item.getItemFromBlock(BRIBlocks.stock_detector);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
        // BetterRailInterfaces.logger.warn("broke block and is remote: " + worldIn.isRemote);
        // BetterRailInterfaces.logger.warn("broke block that had powered: " + state.getValue(POWERED));

        super.breakBlock(worldIn, pos, state);

        /*
         * BlockEntity BlockEntity = worldIn.getTileEntity(pos);
         * 
         * //worldIn.removeTileEntity(pos);
         * 
         * if (BlockEntity instanceof stock_detector_tile_entity)
         * {
         * super.breakBlock(worldIn, pos, state);
         * }
         */
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { POWERED });
    }

    /*
     * @Override
     * public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand)
     * {
     * }
     */

    // old and unused
    public void updateBlockState(World worldIn, BlockPos pos, BlockState state, boolean on) {
        if (worldIn.isRemote) {
            state = state.withProperty(POWERED, on);
            worldIn.setBlockState(pos, state, 3);
            worldIn.notifyNeighborsOfStateChange(pos, this, false);
        }
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(POWERED, meta == 1);
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(POWERED) ? 1 : 0;
    }

    /*
     * @Override
     * public BlockState getActualState(BlockState state, IBlockAccess world, BlockPos pos)
     * {
     * //BetterRailInterfaces.logger.warn("called getActualState, state had value: " + state.getValue(POWERED)); //seems
     * to always match server
     * stock_detector_tile_entity te = (stock_detector_tile_entity)world.getTileEntity(pos);
     * return state.withProperty(POWERED, te.detected);
     * }
     * 
     * @Override
     * public boolean isOpaqueCube(BlockState state) {
     * return true;
     * }
     * 
     * @Override
     * public boolean isFullCube(BlockState state) {
     * return true;
     * }
     */

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(BlockState state, IBlockAccess world, BlockPos pos, Direction facing) {
        // BetterRailInterfaces.logger.warn("weak power from state: " + state.getValue(POWERED) + ", weak power from
        // entity: " + ((stock_detector_tile_entity)world.getTileEntity(pos)).detected
        // + ", is remote: " + world.getTileEntity(pos).getWorld().isRemote
        // );
        return state.getValue(POWERED) ? 15 : 0;
        // return ((stock_detector_tile_entity)world.getTileEntity(pos)).detected ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockState state, IBlockAccess world, BlockPos pos, Direction facing) {
        return getWeakPower(state, world, pos, facing);
        // return ((stock_detector_tile_entity)world.getTileEntity(pos)).detected ? 15 : 0;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }
}
