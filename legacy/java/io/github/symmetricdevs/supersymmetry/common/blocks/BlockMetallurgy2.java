package io.github.symmetricdevs.supersymmetry.common.blocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import org.jetbrains.annotations.NotNull;

import io.github.symmetricdevs.supersymmetry.api.blocks.VariantHorizontalRotatableBlock;

public class BlockMetallurgy2 extends VariantHorizontalRotatableBlock<BlockMetallurgy2.BlockMetallurgy2Type> {

    public BlockMetallurgy2() {
        super(Material.IRON);
        setTranslationKey("metallurgy_2");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(BlockMetallurgy2Type.FLYING_SHEAR_SAW));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull BlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    public enum BlockMetallurgy2Type implements IStringSerializable {

        FLYING_SHEAR_SAW("flying_shear_saw"),
        POLYSTYRENE_WALL("polystyrene_wall");

        private String name;

        BlockMetallurgy2Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
