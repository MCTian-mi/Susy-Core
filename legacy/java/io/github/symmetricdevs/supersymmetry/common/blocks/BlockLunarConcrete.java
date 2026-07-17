package io.github.symmetricdevs.supersymmetry.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlockLunarConcrete extends VariantBlock<BlockLunarConcrete.LunarConcreteType> {

    public BlockLunarConcrete() {
        super(net.minecraft.world.level.material.Material.ROCK);
        setTranslationKey("lunar_concrete");
        setHardness(3.0f);
        setResistance(5.0f);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(getState(LunarConcreteType.LUNAR_CONCRETE_SMOOTH));
    }

    @Override
    public int damageDropped(@NotNull BlockState state) {
        if (this.getState(state) == LunarConcreteType.LUNAR_CONCRETE_SMOOTH) {
            return LunarConcreteType.LUNAR_CONCRETE_COBBLE.ordinal();
        }
        return super.damageDropped(state);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, World world, BlockPos pos,
                                  Player player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state));
    }

    public enum LunarConcreteType implements IStringSerializable, IStateHarvestLevel {

        LUNAR_CONCRETE_SMOOTH("lunar_concrete_smooth", 1),
        LUNAR_CONCRETE_BRICKS("lunar_concrete_bricks", 1),
        LUNAR_CONCRETE_BRICKS_CRACKED("lunar_concrete_bricks_cracked", 1),
        LUNAR_CONCRETE_BRICKS_SMALL("lunar_concrete_bricks_small", 1),
        LUNAR_CONCRETE_BRICKS_SQUARE("lunar_concrete_bricks_square", 1),
        LUNAR_CONCRETE_CHISELED("lunar_concrete_chiseled", 1),
        LUNAR_CONCRETE_COBBLE("lunar_concrete_cobble", 1),
        LUNAR_CONCRETE_POLISHED("lunar_concrete_polished", 1),
        LUNAR_CONCRETE_TILED("lunar_concrete_tiled", 1),
        LUNAR_CONCRETE_TILED_SMALL("lunar_concrete_tiled_small", 1),
        LUNAR_CONCRETE_WINDMILL_A("lunar_concrete_windmill_a", 1),
        LUNAR_CONCRETE_WINDMILL_B("lunar_concrete_windmill_b", 1);

        private final String name;
        private final int harvestLevel;

        LunarConcreteType(String name, int harvestLevel) {
            this.name = name;
            this.harvestLevel = harvestLevel;
        }

        @Nonnull
        public String getName() {
            return this.name;
        }

        public int getHarvestLevel(BlockState state) {
            return this.harvestLevel;
        }

        public String getHarvestTool(BlockState state) {
            return "pickaxe";
        }
    }
}
