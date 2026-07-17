package io.github.symmetricdevs.supersymmetry.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlocksHardened1 extends VariantBlock<BlocksHardened1.HardenedBlockType> {

    public BlocksHardened1() {
        super(Material.ROCK);
        this.setHardness(200.0F);
        this.setResistance(150.0F);
        this.setSoundType(SoundType.STONE);
        this.setTranslationKey("hardened_blocks1");
    }

    @Override
    public EnumPushReaction getPushReaction(BlockState state) {
        return EnumPushReaction.BLOCK;
    }

    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state,
                         int fortune) {
        Random rand = world instanceof World ? ((World) world).rand : RANDOM;

        HardenedBlockType type = getState(state);
        int count = quantityDropped(state, fortune, rand);
        for (int i = 0; i < count; i++) {
            drops.add(type.droppedItem.get()); // Damage dropped is hardcoded here since `damageDropped` is used
                                               // elsewhere
        }
    }

    public enum HardenedBlockType implements IStringSerializable, IStateHarvestLevel {

        INDUSTRIAL_CONCRETE_HARDENED("industrial_concrete_hardened", 4,
                () -> new ItemStack(Item.getItemFromBlock(
                        SuSyBlocks.SUSY_STONE_BLOCKS.get(SusyStoneVariantBlock.StoneVariant.BRICKS)), 1, 9)),
        MILITARY_CONCRETE_COBBLESTONE_HARDENED("military_concrete_cobblestone_hardened", 4,
                () -> new ItemStack(Item.getItemFromBlock(
                        SuSyBlocks.SUSY_STONE_BLOCKS.get(SusyStoneVariantBlock.StoneVariant.COBBLE)), 1, 10)),
        MILITARY_CONCRETE_HARDENED("military_concrete_hardened", 4,
                () -> new ItemStack(Item.getItemFromBlock(
                        SuSyBlocks.SUSY_STONE_BLOCKS.get(SusyStoneVariantBlock.StoneVariant.SMOOTH)), 1, 10));

        private final String name;
        private final int harvestLevel;
        private final Supplier<ItemStack> droppedItem;

        HardenedBlockType(String name, int harvestLevel, Supplier<ItemStack> droppedItem) {
            this.name = name;
            this.harvestLevel = harvestLevel;
            this.droppedItem = droppedItem;
        }

        @Override
        public int getHarvestLevel(BlockState BlockState) {
            return this.harvestLevel;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
