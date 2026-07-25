package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.steam;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.data.GTBlocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * Modern port of the 1.12.2 {@code SuSyBoilerType}: the bronze and steel large-boiler
 * variants, now expressed as block suppliers and GTCEu-Modern casing texture ids.
 */
public enum SuSyBoilerType {

    BRONZE(1536, 1200, 1,
            GTBlocks.CASING_BRONZE_BRICKS,
            GTBlocks.FIREBOX_BRONZE,
            GTBlocks.CASING_BRONZE_PIPE,
            GTCEu.id("block/casings/solid/machine_casing_bronze_plated_bricks"),
            GTCEu.id("block/multiblock/generator/large_bronze_boiler")),

    STEEL(3072, 1800, 1,
            GTBlocks.CASING_STEEL_SOLID,
            GTBlocks.FIREBOX_STEEL,
            GTBlocks.CASING_STEEL_PIPE,
            GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
            GTCEu.id("block/multiblock/generator/large_steel_boiler"));

    private final int steamPerTick;
    private final int ticksToBoiling;
    private final double efficiency;

    public final Supplier<? extends Block> casingState;
    public final Supplier<? extends Block> fireboxState;
    public final Supplier<? extends Block> pipeState;

    public final ResourceLocation casingTexture;
    public final ResourceLocation frontOverlay;

    SuSyBoilerType(int steamPerTick, int ticksToBoiling, double efficiency,
                   Supplier<? extends Block> casingState,
                   Supplier<? extends Block> fireboxState,
                   Supplier<? extends Block> pipeState,
                   ResourceLocation casingTexture,
                   ResourceLocation frontOverlay) {
        this.steamPerTick = steamPerTick;
        this.ticksToBoiling = ticksToBoiling;
        this.efficiency = efficiency;
        this.casingState = casingState;
        this.fireboxState = fireboxState;
        this.pipeState = pipeState;
        this.casingTexture = casingTexture;
        this.frontOverlay = frontOverlay;
    }

    public int steamPerTick() {
        return steamPerTick;
    }

    public int getTicksToBoiling() {
        return ticksToBoiling;
    }

    public int runtimeBoost(int ticks) {
        return (int) (efficiency * ticks);
    }
}
