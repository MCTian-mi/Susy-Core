package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlockGuidanceSystem extends VariantBlock<BlockGuidanceSystem.GuidanceSystemType> {

    public BlockGuidanceSystem() {
        super(Material.IRON);
        setTranslationKey("guidance_system");
        setHardness(5f);
        setResistance(15f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(BlockGuidanceSystem.GuidanceSystemType.SOYUZ));
        setHarvestLevel("wrench", 4);
    }

    public enum GuidanceSystemType implements IStringSerializable, IStateHarvestLevel {

        SOYUZ("soyuz", 4);

        String name;
        int harvest;

        GuidanceSystemType(String name, int harvest) {
            this.name = name;
            this.harvest = harvest;
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
            return IStateHarvestLevel.super.getHarvestTool(state);
        }
    }
}
