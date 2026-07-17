package io.github.symmetricdevs.supersymmetry.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlockCoagulationTankWall extends VariantBlock<BlockCoagulationTankWall.CoagulationTankWallType> {

    public BlockCoagulationTankWall() {
        super(Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(5.0f, 10.0f));
        setTranslationKey("coagulation_tank_wall");
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(CoagulationTankWallType.WOODEN_COAGULATION_TANK_WALL));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    public enum CoagulationTankWallType implements StringRepresentable, IStateHarvestLevel {

        WOODEN_COAGULATION_TANK_WALL("wooden_coagulation_tank_wall", 1);

        private final String name;
        private final int harvestLevel;

        CoagulationTankWallType(String name, int harvestLevel) {
            this.name = name;
            this.harvestLevel = harvestLevel;
        }

        @Nonnull
        @Override
        public String getSerializedName() {
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
