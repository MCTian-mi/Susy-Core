package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;
import io.github.symmetricdevs.supersymmetry.api.rocketry.WeightedBlock;

public class BlockCombustionChamber extends VariantBlock<BlockCombustionChamber.CombustionType>
                                    implements WeightedBlock {

    public BlockCombustionChamber() {
        super(Material.IRON);
        setTranslationKey("rocket_combustion_chamber");
        setHardness(5f);
        setResistance(15f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(CombustionType.BIPROPELLANT));
        setHarvestLevel("wrench", 4);
    }

    public enum CombustionType implements IStringSerializable, IStateHarvestLevel {

        BIPROPELLANT("bipropellant", 4, 2),
        MONOPROPELLANT("monopropellant", 4, 1),
        OXIDISER("oxidiser", 4, 2);

        String name;
        int harvest;
        int pumps;

        CombustionType(String name, int harvest, int pumps) {
            this.name = name;
            this.harvest = harvest;
            this.pumps = pumps;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getHarvestLevel(BlockState BlockState) {
            return harvest;
        }

        @Override
        public String getHarvestTool(BlockState state) {
            return "wrench";
        }

        public int getMinPumps() {
            return pumps;
        }
    }

    @Override
    public double getMass(BlockState state) {
        CombustionType type = getState(state);
        return 800 + 100 * switch (type) {
            case BIPROPELLANT -> 200.0;
            case MONOPROPELLANT -> 150.0;
            case OXIDISER -> 200.0;
        };
    }
}
