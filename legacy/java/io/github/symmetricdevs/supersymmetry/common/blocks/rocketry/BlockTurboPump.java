package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import io.github.symmetricdevs.supersymmetry.api.blocks.VariantDirectionalRotatableBlock;
import io.github.symmetricdevs.supersymmetry.api.rocketry.WeightedBlock;

public class BlockTurboPump extends VariantDirectionalRotatableBlock<BlockTurboPump.HPPType>
                            implements WeightedBlock {

    public BlockTurboPump() {
        super(Material.IRON);
        setTranslationKey("rocket_turbopump");
        setHardness(5f);
        setResistance(15f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public enum HPPType implements IStringSerializable, IStateHarvestLevel {

        BASIC("basic", 3, 2000);

        private String name;
        private int harvestLevel;
        private double throughput; // kg/s

        HPPType(String name, int harvestLevel, double throughput) {
            this.name = name;
            this.harvestLevel = harvestLevel;
            this.throughput = throughput;
        }

        @Override
        public int getHarvestLevel(BlockState BlockState) {
            return harvestLevel;
        }

        @Override
        public String getHarvestTool(BlockState state) {
            return "wrench";
        }

        @Override
        public String getName() {
            return this.name;
        }

        public double getThroughput() {
            return this.throughput;
        }
    }

    @Override
    public double getMass(BlockState state) {
        HPPType type = getState(state);
        double multiplier = switch (type) {
            case BASIC -> 150.0;
        };
        return 1000 + 100 * multiplier;
    }
}
