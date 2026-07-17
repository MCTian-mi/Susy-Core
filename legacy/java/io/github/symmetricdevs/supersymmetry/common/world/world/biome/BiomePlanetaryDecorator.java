package io.github.symmetricdevs.supersymmetry.common.world.biome;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeDecorator;
import net.minecraft.world.gen.feature.WorldGenerator;

import io.github.symmetricdevs.supersymmetry.common.world.gen.MapGenLunarLavaTube;
import io.github.symmetricdevs.supersymmetry.common.world.gen.WorldGenPit;

public class BiomePlanetaryDecorator extends BiomeDecorator {

    public WorldGenerator pitGen = new WorldGenPit();

    @Override
    public void decorate(World worldIn, Random random, Biome biome, BlockPos pos) {
        this.mushroomBrownGen = NoGenerator.noGen;
        this.mushroomRedGen = NoGenerator.noGen;
        super.decorate(worldIn, random, biome, pos);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                BlockPos position = new BlockPos(pos.getX() + i, 0x60, pos.getZ() + j);
                if (worldIn.getBlockState(position).getBlock() == MapGenLunarLavaTube.PIT) {
                    pitGen.generate(worldIn, random, position);
                }
            }
        }
    }

    private static class NoGenerator extends WorldGenerator {

        public static WorldGenerator noGen = new NoGenerator();

        @Override
        public boolean generate(World worldIn, Random rand, BlockPos position) {
            return false;
        }
    }
}
