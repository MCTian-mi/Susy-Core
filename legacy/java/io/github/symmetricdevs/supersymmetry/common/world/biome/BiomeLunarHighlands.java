package io.github.symmetricdevs.supersymmetry.common.world.biome;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.MobCategory;

import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;
import io.github.symmetricdevs.supersymmetry.common.blocks.SusyStoneVariantBlock;

public class BiomeLunarHighlands extends PlanetaryBiome {

    public BiomeLunarHighlands(BiomeProperties properties) {
        super(properties);
        this.topBlock = SuSyBlocks.REGOLITH.getDefaultState();
        this.fillerBlock = SuSyBlocks.SUSY_STONE_BLOCKS.get(SusyStoneVariantBlock.StoneVariant.SMOOTH)
                .getState(SusyStoneVariantBlock.StoneType.ANORTHOSITE);
    }

    @Override
    @Nonnull
    public List<SpawnListEntry> getSpawnableList(MobCategory type) {
        return new LinkedList<>();
    }

    @Override
    public float getSpawningChance() {
        return 0f; // Nothing spawns
    }
}
