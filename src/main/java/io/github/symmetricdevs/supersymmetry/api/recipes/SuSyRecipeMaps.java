package io.github.symmetricdevs.supersymmetry.api.recipes;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import io.github.symmetricdevs.supersymmetry.api.recipes.builders.SuSyRecipeData;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.SuSyRecipePropertyKeys;
import io.github.symmetricdevs.supersymmetry.common.data.SuSyRecipeTypes;
import io.github.symmetricdevs.supersymmetry.common.data.SuSyWorldgenRecipeTypes;

/**
 * Delegate layer that maps the 1.12.2 {@code SuSyRecipeMaps} field names onto
 * their Modern-registered equivalents in {@link SuSyRecipeTypes} and
 * {@link SuSyWorldgenRecipeTypes}.
 * <p>
 * All ~100 recipe types are now non-generic {@link GTRecipeType}s registered via
 * {@link SuSyRecipeTypes#register(String, String)}. Old per-recipe-type builder
 * subclasses ({@code CatalystRecipeBuilder}, {@code NoEnergyRecipeBuilder}, etc.)
 * have been replaced by per-recipe data helpers ({@link SuSyRecipePropertyKeys},
 * {@link SuSyRecipeData}) applied during datagen.
 * <p>
 * Old {@code RecipeProperty} subclasses have been replaced by thin bridge classes
 * that read the same data from {@code GTRecipe.data} via
 * {@code SuSyRecipePropertyKeys} — see {@code api/recipes/properties/*.java}.
 * <p>
 * The 1.12.2 {@code onRecipeBuild} static-block recipe derivation (cross-map
 * mirrors, boiler fuel expansion) has been removed — it is a datagen concern
 * and will be re-expressed in the recipe provider layer.
 * <p>
 * Fields whose recipe type is gated on worldgen context (biome / dimension) are
 * delegated to {@link SuSyWorldgenRecipeTypes}. Fields in the deferred rocketry /
 * space scope are registered inline and will move to their proper holders when
 * that scope is ported.
 */
@SuppressWarnings("unused")
public final class SuSyRecipeMaps {

    // ==================================================================
    // Thermal / metallurgical — delegated to SuSyRecipeTypes
    // ==================================================================
    public static final GTRecipeType COOLING_RECIPES = SuSyRecipeTypes.COOLING_RECIPES;
    public static final GTRecipeType SINTERING_RECIPES = SuSyRecipeTypes.SINTERING_RECIPES;
    public static final GTRecipeType COAGULATION_RECIPES = SuSyRecipeTypes.COAGULATION_RECIPES;
    public static final GTRecipeType REACTION_FURNACE_RECIPES = SuSyRecipeTypes.REACTION_FURNACE_RECIPES;
    public static final GTRecipeType ADVANCED_ARC_FURNACE = SuSyRecipeTypes.ADVANCED_ARC_FURNACE_RECIPES;
    public static final GTRecipeType METALLURGICAL_CONVERTER = SuSyRecipeTypes.METALLURGICAL_CONVERTER_RECIPES;
    public static final GTRecipeType INDUCTION_FURNACE = SuSyRecipeTypes.INDUCTION_FURNACE_RECIPES;
    public static final GTRecipeType REVERBERATORY_FURNACE = SuSyRecipeTypes.REVERBERATORY_FURNACE_RECIPES;
    public static final GTRecipeType CUPOLA_FURNACE = SuSyRecipeTypes.CUPOLA_FURNACE_RECIPES;
    public static final GTRecipeType PRIMITIVE_SMELTER = SuSyRecipeTypes.PRIMITIVE_SMELTER_RECIPES;
    public static final GTRecipeType HOT_ISOSTATIC_PRESS = SuSyRecipeTypes.HOT_ISOSTATIC_PRESS_RECIPES;
    public static final GTRecipeType GAS_ATOMIZER = SuSyRecipeTypes.GAS_ATOMIZER_RECIPES;
    public static final GTRecipeType ROTARY_KILN = SuSyRecipeTypes.ROTARY_KILN_RECIPES;
    public static final GTRecipeType COKING_RECIPES = SuSyRecipeTypes.COKING_RECIPES;

    // ==================================================================
    // Chemical reactors (catalyst-driven) — delegated
    // ==================================================================
    public static final GTRecipeType CSTR_RECIPES = SuSyRecipeTypes.CSTR_RECIPES;
    public static final GTRecipeType FIXED_BED_REACTOR_RECIPES = SuSyRecipeTypes.FIXED_BED_REACTOR_RECIPES;
    public static final GTRecipeType TRICKLE_BED_REACTOR_RECIPES = SuSyRecipeTypes.TRICKLE_BED_REACTOR_RECIPES;
    public static final GTRecipeType BUBBLE_COLUMN_REACTOR_RECIPES = SuSyRecipeTypes.BUBBLE_COLUMN_REACTOR_RECIPES;
    public static final GTRecipeType BATCH_REACTOR_RECIPES = SuSyRecipeTypes.BATCH_REACTOR_RECIPES;
    public static final GTRecipeType FLUIDIZED_BED_REACTOR_RECIPES = SuSyRecipeTypes.FLUIDIZED_BED_REACTOR_RECIPES;
    public static final GTRecipeType POLYMERIZATION_RECIPES = SuSyRecipeTypes.POLYMERIZATION_RECIPES;
    public static final GTRecipeType CATALYTIC_REFORMER_RECIPES = SuSyRecipeTypes.CATALYTIC_REFORMER_RECIPES;
    public static final GTRecipeType ELECTROLYTIC_CELL_RECIPES = SuSyRecipeTypes.ELECTROLYTIC_CELL_RECIPES;
    public static final GTRecipeType FERMENTATION_VAT_RECIPES = SuSyRecipeTypes.FERMENTATION_VAT_RECIPES;
    public static final GTRecipeType VULCANIZATION_RECIPES = SuSyRecipeTypes.VULCANIZATION_RECIPES;
    public static final GTRecipeType ROASTER_RECIPES = SuSyRecipeTypes.ROASTER_RECIPES;

    // ==================================================================
    // Separation / purification — delegated
    // ==================================================================
    public static final GTRecipeType CRYSTALLIZER_RECIPES = SuSyRecipeTypes.CRYSTALLIZER_RECIPES;
    public static final GTRecipeType DRYER_RECIPES = SuSyRecipeTypes.DRYER_RECIPES;
    public static final GTRecipeType ION_EXCHANGE_COLUMN_RECIPES = SuSyRecipeTypes.ION_EXCHANGE_COLUMN_RECIPES;
    public static final GTRecipeType ZONE_REFINER_RECIPES = SuSyRecipeTypes.ZONE_REFINER_RECIPES;
    public static final GTRecipeType ELECTROSTATIC_SEPARATOR = SuSyRecipeTypes.ELECTROSTATIC_SEPARATOR_RECIPES;
    public static final GTRecipeType GRAVITY_SEPARATOR_RECIPES = SuSyRecipeTypes.GRAVITY_SEPARATOR_RECIPES;
    public static final GTRecipeType ORE_SORTER_RECIPES = SuSyRecipeTypes.ORE_SORTER_RECIPES;
    public static final GTRecipeType CLARIFIER = SuSyRecipeTypes.CLARIFIER_RECIPES;
    public static final GTRecipeType FROTH_FLOTATION = SuSyRecipeTypes.FROTH_FLOTATION_RECIPES;
    public static final GTRecipeType PHASE_SEPARATOR = SuSyRecipeTypes.PHASE_SEPARATOR_RECIPES;
    public static final GTRecipeType PRESSURE_SWING_ADSORBER_RECIPES = SuSyRecipeTypes.PRESSURE_SWING_ADSORBER_RECIPES;
    public static final GTRecipeType IN_SITU_LEACHER = SuSyRecipeTypes.IN_SITU_LEACHER_RECIPES;
    public static final GTRecipeType EVAPORATION_POOL = SuSyRecipeTypes.EVAPORATION_POOL_RECIPES;

    // ==================================================================
    // Distillation — delegated
    // ==================================================================
    public static final GTRecipeType VACUUM_DISTILLATION_RECIPES = SuSyRecipeTypes.VACUUM_DISTILLATION_RECIPES;
    public static final GTRecipeType SIEVE_DISTILLATION_RECIPES = SuSyRecipeTypes.SIEVE_DISTILLATION_RECIPES;
    public static final GTRecipeType MULTI_STAGE_FLASH_DISTILLATION = SuSyRecipeTypes.MULTI_STAGE_FLASH_DISTILLATION_RECIPES;
    public static final GTRecipeType HIGH_TEMPERATURE_DISTILLATION = SuSyRecipeTypes.HIGH_TEMPERATURE_DISTILLATION_RECIPES;
    public static final GTRecipeType HIGH_PRESSURE_CRYOGENIC_DISTILLATION = SuSyRecipeTypes.HIGH_PRESSURE_CRYOGENIC_DISTILLATION_RECIPES;
    public static final GTRecipeType LOW_PRESSURE_CRYOGENIC_DISTILLATION = SuSyRecipeTypes.LOW_PRESSURE_CRYOGENIC_DISTILLATION_RECIPES;
    public static final GTRecipeType SINGLE_COLUMN_CRYOGENIC_DISTILLATION = SuSyRecipeTypes.SINGLE_COLUMN_CRYOGENIC_DISTILLATION_RECIPES;

    // ==================================================================
    // Cryogenic / thermal exchange — delegated
    // ==================================================================
    public static final GTRecipeType BATH_CONDENSER = SuSyRecipeTypes.BATH_CONDENSER_RECIPES;
    public static final GTRecipeType HEAT_EXCHANGER_RECIPES = SuSyRecipeTypes.HEAT_EXCHANGER_RECIPES;
    public static final GTRecipeType CONDENSER_RECIPES = SuSyRecipeTypes.CONDENSER_RECIPES;
    public static final GTRecipeType HEAT_RADIATOR_RECIPES = SuSyRecipeTypes.HEAT_RADIATOR_RECIPES;
    public static final GTRecipeType QUENCHER_RECIPES = SuSyRecipeTypes.QUENCHER_RECIPES;
    public static final GTRecipeType NATURAL_DRAFT_COOLING_TOWER = SuSyRecipeTypes.NATURAL_DRAFT_COOLING_TOWER_RECIPES;
    public static final GTRecipeType FLUID_COMPRESSOR_RECIPES = SuSyRecipeTypes.FLUID_COMPRESSOR_RECIPES;
    public static final GTRecipeType FLUID_DECOMPRESSOR_RECIPES = SuSyRecipeTypes.FLUID_DECOMPRESSOR_RECIPES;
    public static final GTRecipeType TUBE_FURNACE_RECIPES = SuSyRecipeTypes.TUBE_FURNACE_RECIPES;

    // ==================================================================
    // Material shaping / processing — delegated
    // ==================================================================
    public static final GTRecipeType VACUUM_CHAMBER = SuSyRecipeTypes.VACUUM_CHAMBER_RECIPES;
    public static final GTRecipeType SPINNING_RECIPES = SuSyRecipeTypes.SPINNING_RECIPES;
    public static final GTRecipeType POLISHING_MACHINE = SuSyRecipeTypes.POLISHING_MACHINE_RECIPES;
    public static final GTRecipeType LATEX_COLLECTOR_RECIPES = SuSyRecipeTypes.LATEX_COLLECTOR_RECIPES;
    public static final GTRecipeType CURTAIN_COATER = SuSyRecipeTypes.CURTAIN_COATER_RECIPES;
    public static final GTRecipeType MILLING_RECIPES = SuSyRecipeTypes.MILLING_RECIPES;
    public static final GTRecipeType MIXER_SETTLER_RECIPES = SuSyRecipeTypes.MIXER_SETTLER_RECIPES;
    public static final GTRecipeType BLENDER_RECIPES = SuSyRecipeTypes.BLENDER_RECIPES;
    public static final GTRecipeType INJECTION_MOLDER = SuSyRecipeTypes.INJECTION_MOLDER_RECIPES;
    public static final GTRecipeType MINING_DRILL_RECIPES = SuSyRecipeTypes.MINING_DRILL_RECIPES;

    // ==================================================================
    // Electronics / lithography — delegated
    // ==================================================================
    public static final GTRecipeType UV_RECIPES = SuSyRecipeTypes.UV_RECIPES;
    public static final GTRecipeType ION_IMPLANTATION_RECIPES = SuSyRecipeTypes.ION_IMPLANTATION_RECIPES;
    public static final GTRecipeType CVD_RECIPES = SuSyRecipeTypes.CVD_RECIPES;
    public static final GTRecipeType EUV_LITHOGRAPHY = SuSyRecipeTypes.EUV_LITHOGRAPHY_RECIPES;
    public static final GTRecipeType EDM_RECIPES = SuSyRecipeTypes.EDM_RECIPES;

    // ==================================================================
    // Assembly / misc production — delegated
    // ==================================================================
    public static final GTRecipeType WEAPONS_FACTORY_RECIPES = SuSyRecipeTypes.WEAPONS_FACTORY_RECIPES;
    public static final GTRecipeType LARGE_WEAPONS_FACTORY_RECIPES = SuSyRecipeTypes.LARGE_WEAPONS_FACTORY_RECIPES;
    public static final GTRecipeType RAILROAD_ENGINEERING_STATION_RECIPES = SuSyRecipeTypes.RAILROAD_ENGINEERING_STATION_RECIPES;
    public static final GTRecipeType SCRAP_RECYCLER = SuSyRecipeTypes.SCRAP_RECYCLER_RECIPES;
    public static final GTRecipeType GREENHOUSE_PLANT = SuSyRecipeTypes.GREENHOUSE_PLANT_RECIPES;

    // ==================================================================
    // Fuel / power generation — delegated
    // ==================================================================
    public static final GTRecipeType MAGNETOHYDRODYNAMIC_FUELS = SuSyRecipeTypes.MAGNETOHYDRODYNAMIC_FUELS;
    public static final GTRecipeType LARGE_STEAM_TURBINE = SuSyRecipeTypes.LARGE_STEAM_TURBINE_FUELS;
    public static final GTRecipeType LOW_PRESSURE_ADVANCED_STEAM_TURBINE = SuSyRecipeTypes.ADVANCED_STEAM_TURBINE_FUELS;
    public static final GTRecipeType HIGH_PRESSURE_ADVANCED_STEAM_TURBINE = SuSyRecipeTypes.ADVANCED_STEAM_TURBINE_FUELS;
    public static final GTRecipeType ADVANCED_STEAM_TURBINE = SuSyRecipeTypes.ADVANCED_STEAM_TURBINE_FUELS;
    public static final GTRecipeType BOILER_RECIPES = SuSyRecipeTypes.BOILER_RECIPES;
    public static final GTRecipeType FUEL_CELL_RECIPES = SuSyRecipeTypes.FUEL_CELL_RECIPES;

    // ==================================================================
    // Worldgen-gated (biome / dimension) — delegated to SuSyWorldgenRecipeTypes
    // ==================================================================
    public static final GTRecipeType PUMPING_RECIPES = SuSyWorldgenRecipeTypes.PUMPING_RECIPES;
    public static final GTRecipeType QUARRY_RECIPES = SuSyWorldgenRecipeTypes.QUARRY_RECIPES;

    // ==================================================================
    // Deferred: rocketry / space (drone, rocket, jetpack)
    // Registered inline to preserve compilation; types use "MULTIBLOCK"
    // or "GENERATOR" groups and carry minimal config. Full XEI setup and
    // recipe generation will be added when the rocketry scope is ported.
    // ==================================================================
    public static final GTRecipeType DRONE_PAD = registerDeferred("drone_pad");
    public static final GTRecipeType CARGO_DRONE_PAD = registerDeferred("cargo_drone_pad");
    public static final GTRecipeType ROCKET_ASSEMBLER = registerDeferred("rocket_assembler");
    public static final GTRecipeType ROCKET_LAUNCH_PAD = registerDeferred("rocket_launch_pad");
    public static final GTRecipeType JET_WINGPACK_FUELS = SuSyRecipeTypes.register(
            "jet_wingpack_fuels", GTRecipeTypes.GENERATOR)
            .setMaxIOSize(0, 0, 1, 0);

    private static GTRecipeType registerDeferred(String name) {
        return SuSyRecipeTypes.register(name, GTRecipeTypes.MULTIBLOCK);
    }

    public static void init() {}

    private SuSyRecipeMaps() {}
}
