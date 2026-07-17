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

public class BlockTankShell1 extends VariantDirectionalCoverableBlock<BlockTankShell1.TankCoverType>
                             implements WeightedBlock {

    public BlockTankShell1() {
        super(net.minecraft.world.level.material.Material.IRON);
        setTranslationKey("rocket_tank_shell1");
        setHardness(5f);
        setResistance(15f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(BlockTankShell1.TankCoverType.CARBON_COMPOSITE));
        validCover = itemStack -> OreDictUnifier.get(OrePrefix.plate, Materials.Aluminium).isItemEqual(itemStack);
    }

    public enum TankCoverType implements IStringSerializable, IStateHarvestLevel {

        CARBON_COMPOSITE("carbon", 2);

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
        return 25 + 50 * 3;
    }
}
