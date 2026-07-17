package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import static io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials.MetallizedBoPET;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import io.github.symmetricdevs.supersymmetry.api.blocks.VariantDirectionalCoverableBlock;

public class BlockSpacecraftHull extends VariantDirectionalCoverableBlock<BlockSpacecraftHull.HullType> {

    public BlockSpacecraftHull() {
        super(net.minecraft.world.level.material.Material.IRON);
        setTranslationKey("spacecraft_hull");
        setHardness(5f);
        setResistance(15f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 4);
        setDefaultState(getState(HullType.AL_LI));
        validCover = (ItemStack i) -> i
                .isItemEqualIgnoreDurability(OreDictUnifier.get(OrePrefix.foil, MetallizedBoPET));
    }

    public enum HullType implements IStringSerializable {

        AL_LI("al_li");

        public String name;

        HullType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
