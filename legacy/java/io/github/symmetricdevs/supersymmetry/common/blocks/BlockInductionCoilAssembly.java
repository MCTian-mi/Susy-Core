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

public class BlockInductionCoilAssembly extends VariantBlock<BlockInductionCoilAssembly.InductionCoilAssemblyType> {

    public BlockInductionCoilAssembly() {
        super(net.minecraft.world.level.material.Material.IRON);
        setTranslationKey("induction_coil_assembly");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(InductionCoilAssemblyType.COPPER));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull BlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    public enum InductionCoilAssemblyType implements IStringSerializable, IStateHarvestLevel {

        COPPER("copper", 1);

        private final String name;
        private final int harvestLevel;

        InductionCoilAssemblyType(String name, int harvestLevel) {
            this.name = name;
            this.harvestLevel = harvestLevel;
        }

        @Nonnull
        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public int getHarvestLevel(BlockState state) {
            return this.harvestLevel;
        }

        @Override
        public String getHarvestTool(BlockState state) {
            return "wrench";
        }
    }
}
