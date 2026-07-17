package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;
import io.github.symmetricdevs.supersymmetry.api.rocketry.WeightedBlock;

public class BlockRocketNozzle extends VariantBlock<BlockRocketNozzle.NozzleShapeType> implements WeightedBlock {

    public BlockRocketNozzle() {
        super(Material.IRON);
        setTranslationKey("rocket_nozzle");
        setHardness(7f);
        setResistance(25f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(NozzleShapeType.BELL_NOZZLE));
        setHarvestLevel("wrench", 4);
    }

    public enum NozzleShapeType implements IStringSerializable, IStateHarvestLevel {

        BELL_NOZZLE("bell_basic", 4),
        PLUG_NOZZLE("plug", 4), // note: these must be used with plug blocks
        EXPANDING_NOZZLE("expanding", 4);

        private String name;
        private int harvestLevel;

        NozzleShapeType(String name, int harvestLevel) {
            this.name = name;
            this.harvestLevel = harvestLevel;
        }

        @Override
        public int getHarvestLevel(BlockState BlockState) {
            return this.harvestLevel;
        }

        @Override
        public String getHarvestTool(BlockState state) {
            return "wrench";
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    @Override
    public double getMass(BlockState state) {
        NozzleShapeType type = getState(state);
        double multiplier = switch (type) {
            case BELL_NOZZLE -> 60.0;
            case PLUG_NOZZLE -> 65.0;
            case EXPANDING_NOZZLE -> 80.0;
        };
        return 500 + 100 * multiplier;
    }
}
