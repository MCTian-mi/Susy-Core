package io.github.symmetricdevs.supersymmetry.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.block.VariantActiveBlock;

public class BlockSerpentine extends VariantActiveBlock<BlockSerpentine.SerpentineType> {

    public BlockSerpentine() {
        super(Material.IRON);
        setTranslationKey("serpentine");
        setHardness(0.5f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(SerpentineType.BASIC));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull BlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    @NotNull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    public enum SerpentineType implements IStringSerializable {

        BASIC("basic");

        public final String name;

        SerpentineType(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.getName();
        }
    }
}
