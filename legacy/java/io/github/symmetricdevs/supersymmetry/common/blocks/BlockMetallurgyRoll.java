package io.github.symmetricdevs.supersymmetry.common.blocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import org.jetbrains.annotations.NotNull;

import io.github.symmetricdevs.supersymmetry.api.blocks.VariantAxialRotatableBlock;

public class BlockMetallurgyRoll extends VariantAxialRotatableBlock<BlockMetallurgyRoll.BlockMetallurgyRollType> {

    public BlockMetallurgyRoll() {
        super(Material.IRON);
        setTranslationKey("metallurgy_roll");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(BlockMetallurgyRollType.ROLL));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull BlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    public enum BlockMetallurgyRollType implements IStringSerializable {

        ROLL("roll");

        private String name;

        BlockMetallurgyRollType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
