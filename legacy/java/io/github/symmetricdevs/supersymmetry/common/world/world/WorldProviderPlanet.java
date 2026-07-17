package io.github.symmetricdevs.supersymmetry.common.world;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.LevelProvider;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.client.IRenderHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldProviderPlanet extends WorldProvider {

    @Override
    public @NotNull DimensionType getDimensionType() {
        return SuSyDimensions.planetType;
    }

    @Override
    protected void init() {
        this.hasSkyLight = true;
        biomeProvider = new PlanetBiomeProvider(world);
    }

    @Override
    public @NotNull IChunkGenerator createChunkGenerator() {
        return new PlanetChunkGenerator(world, world.getSeed());
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        return false;
    }

    @Override
    public boolean isSkyColored() {
        return false;
    }

    @Override
    public @Nullable IRenderHandler getSkyRenderer() {
        return SuSyDimensions.PLANETS.get(getDimension()).skyRenderer;
    }

    @Override
    public @NotNull Vec3 getFogColor(float p_76562_1_, float p_76562_2_) {
        return new Vec3(0.0D, 0.0D, 0.0D);
    }

    @Override
    public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {
        return new Vec3(0.0D, 0.0D, 0.0D);
    }

    @Override
    public @Nullable float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
        return new float[] { 0.0F, 0.0F, 0.0F, 0.0F };
    }

    public Planet getPlanet() {
        return SuSyDimensions.PLANETS.get(getDimension());
    }
}
