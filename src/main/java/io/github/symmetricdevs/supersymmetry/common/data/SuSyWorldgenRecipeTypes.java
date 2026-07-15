package io.github.symmetricdevs.supersymmetry.common.data;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MULTIBLOCK;
import static com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT;

/**
 * Recipe types whose matching is gated on worldgen context (biome / dimension)
 * rather than machine inputs alone. Split from {@link SuSyRecipeTypes} because the
 * machines that run them and the {@code RecipeCondition}s that gate them belong to
 * the worldgen scope (Phase 5 blocks/machines + Phase 8 dimensions).
 * <p>
 * The 1.12.2 {@code BiomeRecipeBuilder} / {@code DimensionRecipeBuilder} stored a
 * whitelist/blacklist as a recipe property; in Modern these become
 * {@code BiomeCondition}/{@code BiomeTagCondition}/{@code DimensionCondition} added
 * during datagen. The types are registered now (so ids are stable) but carry no
 * recipe generation until their phase.
 */
public final class SuSyWorldgenRecipeTypes {

    /** Large fluid pump; recipe output depends on the biome it sits over. */
    public static final GTRecipeType PUMPING_RECIPES = SuSyRecipeTypes.register("large_fluid_pump", MULTIBLOCK)
            .setMaxIOSize(1, 0, 0, 1).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_GAS_COLLECTOR, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MINER);

    /** Quarry; recipe availability/duration depends on the dimension. */
    public static final GTRecipeType QUARRY_RECIPES = SuSyRecipeTypes.register("quarry", MULTIBLOCK)
            .setMaxIOSize(1, 9, 0, 0).setEUIO(IO.IN)
            .setSound(GTSoundEntries.MINER);

    public static void init() {}

    private SuSyWorldgenRecipeTypes() {}
}
