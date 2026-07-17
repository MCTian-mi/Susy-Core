package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import io.github.symmetricdevs.supersymmetry.api.blocks.VariantHorizontalRotatableBlock;

public class BlockOuterHatch extends VariantHorizontalRotatableBlock<BlockOuterHatch.OuterHatchType> {

    public BlockOuterHatch() {
        super(net.minecraft.world.level.material.Material.IRON);
        setTranslationKey("rocket_outer_hatch");
        setHardness(5f);
        setResistance(15f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(OuterHatchType.OUTER_HATCH));
        setHarvestLevel("wrench", 4);
    }

    @Override
    public boolean canCreatureSpawn(@NotNull BlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    public enum OuterHatchType implements IStringSerializable, IStateHarvestLevel {

        OUTER_HATCH("al_2219", 4);

        private final String name;
        private final int harvestLevel;

        OuterHatchType(String name, int harvestLevel) {
            this.name = name;
            this.harvestLevel = harvestLevel;
        }

        @Override
        public int getHarvestLevel(BlockState BlockState) {
            return harvestLevel;
        }

        @Override
        public String getHarvestTool(BlockState state) {
            return "wrench";
        }

        public String getName() {
            return name;
        }
    }
}
