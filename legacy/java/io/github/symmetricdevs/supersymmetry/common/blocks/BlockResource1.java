package io.github.symmetricdevs.supersymmetry.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlockResource1 extends VariantBlock<BlockResource1.ResourceBlockType> {

    public BlockResource1() {
        super(net.minecraft.world.level.material.Material.IRON);
        setTranslationKey("resource_block_1");
        setHardness(3.0f);
        setResistance(3.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(getState(ResourceBlockType.NATIVE_COPPER));
    }

    public enum ResourceBlockType implements IStringSerializable {

        NATIVE_COPPER("native_copper", 1),
        ANTHRACITE("anthracite", 0),
        LIGNITE("lignite", 0);

        private final String name;
        private final int harvestLevel;

        ResourceBlockType(String name, int harvestLevel) {
            this.name = name;
            this.harvestLevel = harvestLevel;
        }

        @Nonnull
        public String getName() {
            return this.name;
        }

        public int getHarvestLevel(BlockState state) {
            return this.harvestLevel;
        }

        public String getHarvestTool(BlockState state) {
            return "pickaxe";
        }
    }
}
