package io.github.symmetricdevs.supersymmetry.common.blocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlockRandomConcrete extends VariantBlock<BlockRandomConcrete.BlockRandomConcreteType> {

    public BlockRandomConcrete() {
        super(Material.ROCK);
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setSoundType(SoundType.STONE);
        this.setTranslationKey("random_concrete");
    }

    public static enum BlockRandomConcreteType implements IStringSerializable, IStateHarvestLevel {

        GREYINDUSTRIALCONCRETE("greyindustrialconcrete", 2),
        MOSSYINDUSTRIALCONCRETE("mossyindustrialconcrete", 2),
        SILVERINDUSTRIALCONCRETE("silverindustrialconcrete", 2),
        WHITEINDUSTRIALCONCRETE("whiteindustrialconcrete", 2),

        DOTTED_PANEL("dottedpanel", 2),
        DOTTED_PANEL_BORDER("dottedpanelborder", 2),
        DOTTED_PANEL_COMB("dottedpanelcomb", 2),
        DOTTED_PANEL_GRID("dottedpanelgrid", 2),

        INDUSTRIAL_CINDER_BRICKS("industrialcinderbricks", 2),
        INDUSTRIAL_CINDER_BRICKS_CEMENT("industrialcinderbrickscement", 2),
        INDUSTRIAL_CINDER_BRICKS_CEMENT_GREY("industrialcinderbrickscementgray", 2),
        INDUSTRIAL_CINDER_BRICKS_DARK("industrialcinderbricksdark", 2),

        INDUSTRIAL_CINDER_BRICKS_DARK_GREY("industrialcinderbricksdarkgrey", 2),
        INDUSTRIAL_CINDER_BRICKS_GREY("industrialcinderbricksgrey", 2),
        SMOOTH_INDUSTRIAL_CONCRETE("smoothindustrialconcrete", 2),
        SMOOTH_INDUSTRIAL_CONCRETE_GREY("smoothindustrialconcretegrey", 2);

        private final String name;
        private final int harvestLevel;

        private BlockRandomConcreteType(String name, int harvestLevel) {
            this.name = name;
            this.harvestLevel = harvestLevel;
        }

        @Override
        public int getHarvestLevel(BlockState BlockState) {
            return this.harvestLevel;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
