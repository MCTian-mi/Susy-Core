package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import io.github.symmetricdevs.supersymmetry.api.blocks.VariantDirectionalCoverableBlock;
import io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials;

public class BlockFairingHull extends VariantDirectionalCoverableBlock<BlockFairingHull.FairingType> {

    public BlockFairingHull() {
        super(Material.IRON);
        setTranslationKey("rocket_fairing");
        setHardness(5f);
        setResistance(15f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(FairingType.ALUMINIUM_FAIRING));
        validCover = itemStack -> OreDictUnifier.get(OrePrefix.plate, SusyMaterials.AluminiumAlloy7075)
                .isItemEqual(itemStack);
        setHarvestLevel("wrench", 4);
    }

    public enum FairingType implements IStringSerializable, IStateHarvestLevel {

        ALUMINIUM_FAIRING("al_7075", 4);

        String name;
        int harvest;

        FairingType(String name, int harvest) {
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
