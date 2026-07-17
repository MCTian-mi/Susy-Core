package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

import io.github.symmetricdevs.supersymmetry.api.blocks.VariantHorizontalRotatableBlock;

public class BlockProcessorCluster extends VariantHorizontalRotatableBlock<BlockProcessorCluster.TierType> {

    public BlockProcessorCluster() {
        super(Material.IRON);
        setTranslationKey("processor_cluster");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(TierType.TIER_1));
    }

    @Override
    public boolean canCreatureSpawn(@Nonnull BlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
                                    @Nonnull Mob.SpawnPlacementType type) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(@NotNull BlockState state, @NotNull BlockRenderLayer layer) {
        return super.canRenderInLayer(state, layer);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(@NotNull BlockState state) {
        return super.isOpaqueCube(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightOpacity(@NotNull BlockState state) {
        return super.getLightOpacity(state);
    }

    public enum TierType implements IStringSerializable {

        TIER_1("tier1");

        private final String name;

        TierType(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return this.name;
        }
    }
}
