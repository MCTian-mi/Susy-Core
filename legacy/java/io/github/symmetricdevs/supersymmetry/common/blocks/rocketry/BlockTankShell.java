package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import io.github.symmetricdevs.supersymmetry.api.blocks.VariantDirectionalCoverableBlock;
import io.github.symmetricdevs.supersymmetry.api.rocketry.WeightedBlock;

public class BlockTankShell extends VariantDirectionalCoverableBlock<BlockTankShell.TankCoverType>
                            implements WeightedBlock {

    public BlockTankShell() {
        super(net.minecraft.world.level.material.Material.IRON);
        setTranslationKey("rocket_tank_shell");
        setHardness(5f);
        setResistance(15f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(TankCoverType.TANK_SHELL));
        setHarvestLevel("wrench", 2);
        validCover = itemStack -> OreDictUnifier.get(OrePrefix.plate, Materials.Aluminium).isItemEqual(itemStack);
    }

    public enum TankCoverType implements IStringSerializable, IStateHarvestLevel {

        TANK_SHELL("al_2219", 2),
        STEEL_SHELL("steel", 3);

        private String name;
        private int harvestLevel;

        TankCoverType(String name, int harvestLevel) {
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

    @Override
    public double getMass(BlockState state) {
        TankCoverType type = getState(state);
        int multiplier = switch (type) {
            case TANK_SHELL -> 5;
            case STEEL_SHELL -> 8;
        };
        return 25 + 50 * multiplier;
    }
}
