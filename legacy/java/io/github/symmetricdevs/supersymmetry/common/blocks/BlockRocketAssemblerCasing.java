package io.github.symmetricdevs.supersymmetry.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlockRocketAssemblerCasing extends VariantBlock<BlockRocketAssemblerCasing.RocketAssemblerCasingType> {

    public BlockRocketAssemblerCasing() {
        super(net.minecraft.world.level.material.Material.IRON);
        setTranslationKey("rocket_assembler_casing");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
    }

    @Override
    public boolean canCreatureSpawn(@NotNull BlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    public static enum RocketAssemblerCasingType implements IStringSerializable, IStateHarvestLevel {

        REINFORCED_FOUNDATION("reinforced_foundation", 1),
        FOUNDATION("foundation", 1),
        RAILS("rails", 1),
        STRUCTURAL_FRAME("structural_frame", 1);

        private final String name;
        private final int harvestLevel;

        private RocketAssemblerCasingType(String name, int harvestLevel) {
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
            return "wrench";
        }
    }
}
