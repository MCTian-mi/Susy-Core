package io.github.symmetricdevs.supersymmetry.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public final class SusyLangHandler {

    public static void init(RegistrateLangProvider provider) {
        provider.add("susy.machine.parallel_pure", "Can parallel to output up to §b%s§r§7 items at once");
        provider.add("susy.multiblock.magnetic_refrigerator.min_temperature", "§bTemperature Limit: %s");
        provider.add("susy.multiblock.sintering_oven.can_use_plasma", "Plasma enabled: %s");
        provider.add("susy.multiblocks.sintering_oven.use_plasma.affirmative", "Yes");
        provider.add("susy.multiblocks.sintering_oven.use_plasma.negative", "No");
        provider.add("susy.multiblock.pattern.error.sintering_bricks", "All sintering bricks must be the same type");
        provider.add("susy.multiblock.pattern.error.metal_sheets", "All metal sheets must be the same type");
        provider.add("susy.multiblock.pattern.error.coils_or_bed",
                "All heating coils or evaporation beds must be the same type");
        provider.add("susy.recipe.evaporation", "Required Energy: %s J/t");
        provider.add("susy.recipe_modifier.evaporation_energy_required",
                "This evaporation recipe does not define positive evaporation energy");
        provider.add("susy.recipe_modifier.evaporation_tick_io_unsupported",
                "Evaporation recipes cannot use per-tick inputs or outputs");
        provider.add("susy.multiblock.evaporation_pool.dimensions", "Pool Size: %s × %s");
        provider.add("susy.multiblock.evaporation_pool.tooltip.size",
                "Variable size (%s to %s); heat with sunlight and/or heating coils");
        provider.add("susy.multiblock.evaporation_pool.exposed_beds", "Sunlit Evaporation Beds: %s");
        provider.add("susy.multiblock.evaporation_pool.solar_heat", "Solar Heat: %s J/t");
        provider.add("susy.multiblock.evaporation_pool.coils", "Heating Coils: %s (%s K)");
        provider.add("susy.multiblock.evaporation_pool.coil_capacity", "Coil Heat Capacity: %s J/t");
        provider.add("susy.multiblock.evaporation_pool.thermal_remainder", "Buffered Heat: %s J");
        provider.add("susy.multiblock.evaporation_pool.halted", "Insufficient usable heat");
        provider.add("susy.multiblock.ball_mill.tooltip.mill_balls",
                "Requires %s mill balls to function.");
        provider.add("susy.multiblock.ball_mill.error.missing_mill_balls",
                "Not enough mill balls were found for the recipe!");
        provider.add("tagprefix.mill_ball", "%s Mill Ball");
        provider.add("susy.recipe_modifier.plasma_metadata_required",
                "This sintering recipe does not declare whether it uses plasma");
        provider.add("susy.recipe_modifier.plasma_resistant_bricks_required",
                "Plasma recipes require magneto-plated sintering bricks");
        provider.add("susy.recipe_modifier.cooling_temperature_required",
                "This cooling recipe does not define a required temperature");
        provider.add("susy.recipe_modifier.coil_temperature_too_high",
                "The installed cooling coils cannot reach the recipe's required temperature");
    }

    private SusyLangHandler() {}
}
