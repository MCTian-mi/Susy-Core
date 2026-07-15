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
