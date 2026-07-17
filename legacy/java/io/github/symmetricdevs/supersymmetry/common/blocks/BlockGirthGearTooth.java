package io.github.symmetricdevs.supersymmetry.common.blocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob.SpawnPlacementType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import org.jetbrains.annotations.NotNull;

import io.github.symmetricdevs.supersymmetry.api.blocks.VariantAxialRotatableBlock;

public class BlockGirthGearTooth extends VariantAxialRotatableBlock<BlockGirthGearTooth.Type> {

    public BlockGirthGearTooth() {
        super(Material.IRON);
        setTranslationKey("girth_gear_tooth");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(Type.STEEL));
    }

    @Override
    public boolean canCreatureSpawn(
                                    @NotNull BlockState state,
                                    @NotNull IBlockAccess world,
                                    @NotNull BlockPos pos,
                                    @NotNull SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isTranslucent(BlockState state) {
        return true;
    }

    public enum Type implements IStringSerializable {

        STEEL("steel"),
        ;

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @NotNull
        public String getName() {
            return name;
        }
    }
}
