package io.github.symmetricdevs.supersymmetry.common.blocks;

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

public class BlockSinteringBrick extends VariantActiveBlock<BlockSinteringBrick.SinteringBrickType> {

    public BlockSinteringBrick() {
        super(Material.ROCK);
        setTranslationKey("sintering_brick");
        setHardness(0.5f);
        setSoundType(SoundType.STONE);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(SinteringBrickType.BRICK));
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

    public enum SinteringBrickType implements IStringSerializable {

        BRICK("sintering_block_brick", false),
        MAGNETOPLATED("sintering_block_magnetoplated", true),
        BRICK_BLOOM("sintering_block_brick_bloom_deco", false),
        MAGNETOPLATED_BLOOM("sintering_block_magnetoplated_bloom_deco", true);

        public final String name;
        public final boolean canResistPlasma;

        SinteringBrickType(String name, boolean canResistPlasma) {
            this.name = name;
            this.canResistPlasma = canResistPlasma;
        }

        @NotNull
        @Override
        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.getName();
        }
    }
}
