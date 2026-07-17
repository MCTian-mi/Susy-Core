package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import io.github.symmetricdevs.supersymmetry.api.blocks.VariantHorizontalRotatableBlock;

public class BlockRocketControl extends VariantHorizontalRotatableBlock<BlockRocketControl.RocketControlType> {

    public BlockRocketControl() {
        super(Material.IRON);
        setTranslationKey("rocket_control");
        setHardness(5f);
        setResistance(15f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(RocketControlType.ROCKET_CONTROL));
        setHarvestLevel("wrench", 4);
    }

    public enum RocketControlType implements IStringSerializable, IStateHarvestLevel {

        ROCKET_CONTROL("basic", 4);

        private String name;
        private int harvest;

        RocketControlType(String name, int harvest) {
            this.name = name;
            this.harvest = harvest;
        }

        @Override
        public int getHarvestLevel(BlockState BlockState) {
            return harvest;
        }

        @Override
        public String getHarvestTool(BlockState state) {
            return "wrench";
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
