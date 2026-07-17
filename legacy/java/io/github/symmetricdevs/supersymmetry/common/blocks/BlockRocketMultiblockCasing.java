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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlockRocketMultiblockCasing extends VariantBlock<BlockRocketMultiblockCasing.CasingType> {

    public BlockRocketMultiblockCasing() {
        super(Material.IRON);
        setTranslationKey("rocket_multiblock_casing");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(CasingType.VINYL_CEILING_TILE));
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

    public enum CasingType implements IStringSerializable {

        VINYL_CEILING_TILE("vinyl_ceiling_tile"),
        CEILING_GRID_FILTER_UNIT("ceiling_grid_filter_unit"),
        VINYL_COMPOSITE_FLOORING("vinyl_composite_flooring"),
        AEROSPACE_GASKET("aerospace_gasket");

        private final String name;

        CasingType(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return this.name;
        }
    }
}
