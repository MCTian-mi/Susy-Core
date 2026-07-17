package io.github.symmetricdevs.supersymmetry.loaders;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.blocks.StoneVariantBlock;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;
import io.github.symmetricdevs.supersymmetry.common.blocks.SusyStoneVariantBlock;
import io.github.symmetricdevs.supersymmetry.common.item.SuSyMetaItems;

public class SusyOreDictionaryLoader {

    public static void init() {
        loadStoneOredict();
    }

    public static void loadStoneOredict() {
        for (SusyStoneVariantBlock.StoneType type : SusyStoneVariantBlock.StoneType.values()) {
            ItemStack smooth = SuSyBlocks.SUSY_STONE_BLOCKS.get(SusyStoneVariantBlock.StoneVariant.SMOOTH)
                    .getItemVariant(type);
            ItemStack cobble = SuSyBlocks.SUSY_STONE_BLOCKS.get(SusyStoneVariantBlock.StoneVariant.COBBLE)
                    .getItemVariant(type);
            OreDictUnifier.registerOre(smooth, type.getOrePrefix(), type.getMaterial());
        }

        for (StoneVariantBlock.StoneType type : StoneVariantBlock.StoneType.values()) {
            ItemStack smooth = MetaBlocks.STONE_BLOCKS.get(StoneVariantBlock.StoneVariant.SMOOTH).getItemVariant(type);
            ItemStack cobble = MetaBlocks.STONE_BLOCKS.get(StoneVariantBlock.StoneVariant.COBBLE).getItemVariant(type);
        }

        // For IR railbeds
        ItemStack concreteLightSmooth = MetaBlocks.STONE_BLOCKS.get(StoneVariantBlock.StoneVariant.SMOOTH)
                .getItemVariant(StoneVariantBlock.StoneType.CONCRETE_LIGHT);
        OreDictionary.registerOre("railBed", concreteLightSmooth);

        // For IR tracks
        ItemStack trackSegmentStack = SuSyMetaItems.TRACK_SEGMENT.getStackForm();
        OreDictionary.registerOre("trackMaglev", trackSegmentStack);
    }
}
