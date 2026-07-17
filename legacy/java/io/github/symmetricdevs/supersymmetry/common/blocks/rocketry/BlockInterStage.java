package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import io.github.symmetricdevs.supersymmetry.api.blocks.VariantDirectionalCoverableBlock;

public class BlockInterStage extends VariantDirectionalCoverableBlock<BlockInterStage.InterStageType> {

    public BlockInterStage() {
        super(Material.IRON);
        setTranslationKey("rocket_interstage");
        setHardness(5f);
        setResistance(15f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(InterStageType.AL_7075));
        setHarvestLevel("wrench", 4);
        validCover = itemStack -> OreDictUnifier.get(OrePrefix.plate, Materials.Aluminium).isItemEqual(itemStack);
    }

    public enum InterStageType implements IStringSerializable, IStateHarvestLevel {

        AL_7075("al_7075", 4);

        String name;
        int harvest;

        InterStageType(String name, int harvest) {
            this.name = name;
            this.harvest = harvest;
        }

        @Override
        public int getHarvestLevel(BlockState BlockState) {
            return harvest;
        }

        @Override
        public String getHarvestTool(BlockState state) {
            return IStateHarvestLevel.super.getHarvestTool(state);
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
