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

public class BlockMultiblockTank extends VariantActiveBlock<BlockMultiblockTank.MultiblockTankType> {

    public BlockMultiblockTank() {
        super(net.minecraft.world.level.material.Material.IRON);
        setTranslationKey("multiblock_tank");
        setHardness(3.0f);
        setResistance(3.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 1);
        setDefaultState(getState(MultiblockTankType.CLARIFIER));
    }

    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public boolean canCreatureSpawn(@NotNull BlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    public enum MultiblockTankType implements IStringSerializable, IStateHarvestLevel {

        CLARIFIER("clarifier", 1),
        FLOTATION("flotation", 1);

        private final String name;
        private final int harvestLevel;

        MultiblockTankType(String name, int harvestLevel) {
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
            return "wrench";
        }
    }
}
