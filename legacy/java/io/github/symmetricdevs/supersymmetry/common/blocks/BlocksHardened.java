package io.github.symmetricdevs.supersymmetry.common.blocks;

import java.util.Random;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlocksHardened extends VariantBlock<BlocksHardened.HardenedBlockType> {

    public BlocksHardened() {
        super(Material.ROCK);
        this.setHardness(25.0F);
        this.setResistance(25.0F);
        this.setSoundType(SoundType.STONE);
        this.setTranslationKey("hardened_blocks");
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, BlockState state, Player player) {
        return false;
    }

    public static enum HardenedBlockType implements IStringSerializable, IStateHarvestLevel {

        LAIR10("lair10", 3),
        KRYP8("kryp8", 3),
        KRYP7("kryp7", 3),
        LAIR11("lair11", 3),
        LAIR7("lair7", 3);

        private final String name;
        private final int harvestLevel;

        private HardenedBlockType(String name, int harvestLevel) {
            this.name = name;
            this.harvestLevel = harvestLevel;
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
