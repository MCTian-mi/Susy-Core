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
import io.github.symmetricdevs.supersymmetry.api.blocks.VariantHorizontalRotatableBlock;

public class BlockTurbineRotor extends VariantHorizontalRotatableBlock<BlockTurbineRotor.BlockTurbineRotorType> {

    public BlockTurbineRotor() {
        super(net.minecraft.world.level.material.Material.IRON);
        setTranslationKey("turbine_rotor");
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

    public enum BlockTurbineRotorType implements IStringSerializable, IStateHarvestLevel {

        STEEL("steel", 1),
        LOW_PRESSURE("low_pressure", 1),
        HIGH_PRESSURE("high_pressure", 1),
        COMBUSTION("combustion", 1);

        private final String name;
        private final int harvestLevel;

        BlockTurbineRotorType(String name, int harvestLevel) {
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
