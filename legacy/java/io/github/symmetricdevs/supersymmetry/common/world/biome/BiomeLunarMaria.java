package io.github.symmetricdevs.supersymmetry.common.world.biome;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.MobCategory;

import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.blocks.StoneVariantBlock;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockRegolith;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class BiomeLunarMaria extends PlanetaryBiome {

    public BiomeLunarMaria(BiomeProperties properties) {
        super(properties);

        this.topBlock = SuSyBlocks.REGOLITH.getState(BlockRegolith.BlockRegolithType.LOWLAND);
        this.fillerBlock = MetaBlocks.STONE_BLOCKS.get(StoneVariantBlock.StoneVariant.SMOOTH)
                .getState(StoneVariantBlock.StoneType.BASALT);
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
