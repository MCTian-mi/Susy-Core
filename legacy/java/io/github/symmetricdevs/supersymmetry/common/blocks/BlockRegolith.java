package io.github.symmetricdevs.supersymmetry.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import io.github.symmetricdevs.supersymmetry.api.blocks.VariantBlockFalling;

public class BlockRegolith extends VariantBlockFalling<BlockRegolith.BlockRegolithType> {

    public BlockRegolith() {
        super(Material.SAND);
        setTranslationKey("regolith");
        setHardness(1.0f);
        setResistance(2.0f);
        setSoundType(SoundType.SAND);
    }

    public MapColor getMapColor(BlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.STONE;
    }

    @SideOnly(Side.CLIENT)
    public int getDustColor(BlockState state) {
        return -8356741;
    }

    public enum BlockRegolithType implements IStringSerializable, IStateHarvestLevel {

        HIGHLAND("highland", 1),
        LOWLAND("lowland", 1);

        private final String name;
        private final int harvestLevel;

        BlockRegolithType(String name, int harvestLevel) {
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
            return "shovel";
        }
    }
}
