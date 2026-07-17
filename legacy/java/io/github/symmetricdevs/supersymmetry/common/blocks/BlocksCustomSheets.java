package io.github.symmetricdevs.supersymmetry.common.blocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlocksCustomSheets extends VariantBlock<BlocksCustomSheets.MetalDecorationBlockType> {

    public BlocksCustomSheets() {
        super(Material.IRON);
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setSoundType(SoundType.METAL);
        this.setTranslationKey("custom_sheets");
    }

    public static enum MetalDecorationBlockType implements IStringSerializable, IStateHarvestLevel {

        DARKWHITEMETALSHEET("darkwhitemetalsheet", 2),
        LIGHTERGRAYMETALSHEET("lightergraymetalsheet", 2),
        DECORATIVECOPPER("decorativecopper", 2),
        DECORATIVECOPPERBRICKS("decorativecopperbricks", 2);

        private final String name;
        private final int harvestLevel;

        private MetalDecorationBlockType(String name, int harvestLevel) {
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
