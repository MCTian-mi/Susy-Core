package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlockLifeSupport extends VariantBlock<BlockLifeSupport.Ability> {

    public BlockLifeSupport() {
        super(net.minecraft.world.level.material.Material.IRON);
        setTranslationKey("spacecraft_life_support");
        setHardness(5f);
        setResistance(15f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(Ability.OXYGEN_REGEN));
        setHarvestLevel("wrench", 4);
    }

    public enum Ability implements IStringSerializable, IStateHarvestLevel {

        OXYGEN_REGEN("oxygen_regen", 4);

        private String name;
        private int harvest;

        Ability(String name, int h) {
            this.name = name;
            this.harvest = h;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getHarvestLevel(BlockState BlockState) {
            return harvest;
        }
    }
}
