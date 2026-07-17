package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.StringRepresentable;

import io.github.symmetricdevs.supersymmetry.api.blocks.VariantDirectionalCoverableBlock;
import io.github.symmetricdevs.supersymmetry.common.item.SuSyMetaItems;

public class BlockRoomPadding extends VariantDirectionalCoverableBlock<BlockRoomPadding.CoveringType> {

    public BlockRoomPadding() {
        super(Material.IRON);
        setTranslationKey("spacecraft_room_padding");
        setHardness(5f);
        setResistance(15f);
        setSoundType(SoundType.CLOTH);
        setDefaultState(getState(CoveringType.PADDING));
        setHarvestLevel("wrench", 3);
        validCover = (ItemStack i) -> SuSyMetaItems.getItem("padding_cloth").isItemEqual(i);
    }

    public enum CoveringType implements IStringSerializable {

        PADDING("padding");

        public String name;

        CoveringType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
