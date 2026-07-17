package io.github.symmetricdevs.supersymmetry.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantActiveBlock;

/// This should actually be a [VariantBlock]. But changing it would void evaporation beds in existing saves.
/// Also, it will need me to do extra code for exposure block counting, so I'll just leave it here untouched.
public class BlockEvaporationBed extends VariantActiveBlock<BlockEvaporationBed.EvaporationBedType> {

    public BlockEvaporationBed() {
        super(net.minecraft.world.level.material.Material.CLAY);
        setTranslationKey("evaporation_bed");
        setHardness(0.5f);
        setResistance(0.5f);
        setSoundType(SoundType.GROUND);
        setHarvestLevel("shovel", 0);
        setDefaultState(getState(EvaporationBedType.DIRT));
    }

    @NotNull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public boolean canCreatureSpawn(@NotNull BlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    public enum EvaporationBedType implements IStringSerializable, IStateHarvestLevel {

        DIRT("dirt", 0);

        private final String name;
        private final int harvestLevel;

        EvaporationBedType(String name, int harvestLevel) {
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
            return "shovel";
        }
    }
}
