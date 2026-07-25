package io.github.symmetricdevs.supersymmetry.api.recipes.builders;

import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import io.github.symmetricdevs.supersymmetry.api.recipes.properties.SuSyRecipePropertyKeys;

/** Typed helpers for SuSy recipe metadata carried by {@code GTRecipe.data}. */
public final class SuSyRecipeData {

    public static GTRecipeBuilder coolingTemperature(GTRecipeBuilder builder, int temperature) {
        if (temperature <= 0) {
            throw new IllegalArgumentException("Cooling temperature must be greater than zero");
        }
        return builder.addData(SuSyRecipePropertyKeys.COOLING_TEMPERATURE, temperature);
    }

    public static GTRecipeBuilder evaporationEnergy(GTRecipeBuilder builder, int joulesPerTick) {
        if (joulesPerTick <= 0) {
            throw new IllegalArgumentException("Evaporation energy must be greater than zero");
        }
        return builder.addData(SuSyRecipePropertyKeys.EVAPORATION_ENERGY, joulesPerTick);
    }

    public static GTRecipeBuilder plasmaEnabled(GTRecipeBuilder builder, boolean enabled) {
        return builder.addData(SuSyRecipePropertyKeys.PLASMA_ENABLED, enabled);
    }

    public static GTRecipeBuilder plasmaEnabled(GTRecipeBuilder builder) {
        return plasmaEnabled(builder, true);
    }

    private SuSyRecipeData() {}
}
