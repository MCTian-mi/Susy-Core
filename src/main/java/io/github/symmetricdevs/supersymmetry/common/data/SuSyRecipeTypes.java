package io.github.symmetricdevs.supersymmetry.common.data;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import com.lowdragmc.lowdraglib.utils.LocalizationUtils;

import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.SuSyRecipePropertyKeys;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.crafting.RecipeType;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ELECTRIC;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.GENERATOR;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MULTIBLOCK;
import static com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.DOWN_TO_UP;
import static com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT;

/**
 * SuSy recipe types. Ported from the 1.12.2 {@code SuSyRecipeMaps} (~100
 * {@code RecipeMap}s).
 * <p>
 * Each 1.12.2 {@code new RecipeMap<>(name, iIn, iOut, fIn, fOut, builder, hidden)}
 * becomes {@code register(name, group).setMaxIOSize(iIn, iOut, fIn, fOut)}; the
 * per-map {@code RecipeBuilder} subclass collapses into recipe data (see
 * {@code SuSyRecipePropertyKeys}) applied during datagen, and {@code isHidden}
 * becomes {@code .setXEIVisible(false)}. 1.12.2 custom slot overlays
 * ({@code SusyGuiTextures}) are not yet ported (Phase 6) — GTCEu default slot
 * rendering is used; progress bars and sounds are mapped onto GTCEu stock assets.
 * <p>
 * Deferred with their parent scope: rocket/space maps ({@code rocket_assembler},
 * {@code drone_pad}, {@code cargo_drone_pad}, {@code jet_wingpack_fuels}) and the
 * worldgen-gated biome/dimension maps ({@code large_fluid_pump}, {@code quarry} —
 * see {@code SuSyWorldgenRecipeTypes}, Phase 5/8). The GCYM cross-map mirrors
 * ({@code ADVANCED_ARC_FURNACE}, {@code METALLURGICAL_CONVERTER},
 * {@code ATTRITION_SCRUBBER}, {@code BALL_MILL}, {@code BOILER_RECIPES}) are
 * registered here but their recipe-generation {@code onRecipeBuild} wiring is
 * re-expressed during datagen in a later phase.
 */
public final class SuSyRecipeTypes {

    // ------------------------------------------------------------------
    // Thermal / metallurgical
    // ------------------------------------------------------------------
    public static final GTRecipeType COOLING_RECIPES = register("magnetic_refrigerator", ELECTRIC)
            .setMaxIOSize(3, 3, 0, 1).setEUIO(IO.IN)
            .setSound(GTSoundEntries.COOLING);

    public static final GTRecipeType SINTERING_RECIPES = register("sintering_oven", ELECTRIC)
            .setMaxIOSize(4, 4, 2, 2).setEUIO(IO.IN)
            .setSound(GTSoundEntries.COMBUSTION);

    public static final GTRecipeType COAGULATION_RECIPES = register("coagulation_tank", MULTIBLOCK)
            .setMaxIOSize(2, 1, 2, 0).setEUIO(IO.IN);

    public static final GTRecipeType VULCANIZATION_RECIPES = register("vulcanizing_press", ELECTRIC)
            .setMaxIOSize(4, 2, 2, 1).setEUIO(IO.IN)
            .setSound(GTSoundEntries.COMBUSTION);

    public static final GTRecipeType ROASTER_RECIPES = register("roaster", ELECTRIC)
            .setMaxIOSize(3, 2, 2, 3).setEUIO(IO.IN)
            .setSound(GTSoundEntries.COMBUSTION);

    public static final GTRecipeType VACUUM_CHAMBER_RECIPES = register("vacuum_chamber", ELECTRIC)
            .setMaxIOSize(4, 2, 2, 2).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_COMPRESS, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.CENTRIFUGE);

    public static final GTRecipeType REACTION_FURNACE_RECIPES = register("reaction_furnace", ELECTRIC)
            .setMaxIOSize(3, 3, 3, 3).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARC_FURNACE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ARC);

    public static final GTRecipeType ADVANCED_ARC_FURNACE_RECIPES = register("advanced_arc_furnace", MULTIBLOCK)
            .setMaxIOSize(9, 2, 4, 1).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARC_FURNACE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ARC);

    public static final GTRecipeType METALLURGICAL_CONVERTER_RECIPES = register("metallurgical_converter", MULTIBLOCK)
            .setMaxIOSize(3, 2, 3, 2).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.FURNACE);

    public static final GTRecipeType INDUCTION_FURNACE_RECIPES = register("induction_furnace", ELECTRIC)
            .setMaxIOSize(6, 3, 3, 3).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARC_FURNACE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ARC);

    public static final GTRecipeType RESISTANCE_FURNACE_RECIPES = register("resistance_furnace", ELECTRIC)
            .setMaxIOSize(6, 2, 1, 1).setEUIO(IO.IN)
            .setSound(GTSoundEntries.FURNACE);

    public static final GTRecipeType REVERBERATORY_FURNACE_RECIPES = register("reverberatory_furnace", MULTIBLOCK)
            .setMaxIOSize(3, 3, 3, 3)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARC_FURNACE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.FURNACE);

    public static final GTRecipeType CUPOLA_FURNACE_RECIPES = register("cupola_furnace", MULTIBLOCK)
            .setMaxIOSize(4, 1, 0, 0)
            .setSound(GTSoundEntries.FURNACE);

    public static final GTRecipeType PRIMITIVE_SMELTER_RECIPES = register("primitive_smelter", MULTIBLOCK)
            .setMaxIOSize(4, 2, 0, 0)
            .setSound(GTSoundEntries.FURNACE);

    public static final GTRecipeType HOT_ISOSTATIC_PRESS_RECIPES = register("hot_isostatic_press", ELECTRIC)
            .setMaxIOSize(3, 1, 1, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_COMPRESS, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COMPRESSOR);

    public static final GTRecipeType GAS_ATOMIZER_RECIPES = register("gas_atomizer", ELECTRIC)
            .setMaxIOSize(1, 1, 1, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.FURNACE);

    public static final GTRecipeType ROTARY_KILN_RECIPES = register("rotary_kiln", MULTIBLOCK)
            .setMaxIOSize(3, 2, 3, 3).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARC_FURNACE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MACERATOR);

    public static final GTRecipeType COKING_RECIPES = register("coking_tower", MULTIBLOCK)
            .setMaxIOSize(1, 1, 3, 2).setEUIO(IO.IN)
            .setSound(GTSoundEntries.COMBUSTION);

    // ------------------------------------------------------------------
    // Chemical reactors (catalyst-driven)
    // ------------------------------------------------------------------
    public static final GTRecipeType CSTR_RECIPES = register("continuous_stirred_tank_reactor", ELECTRIC)
            .setMaxIOSize(2, 0, 4, 2).setEUIO(IO.IN)
            .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType FIXED_BED_REACTOR_RECIPES = register("fixed_bed_reactor", ELECTRIC)
            .setMaxIOSize(2, 1, 3, 2).setEUIO(IO.IN)
            .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType TRICKLE_BED_REACTOR_RECIPES = register("trickle_bed_reactor", ELECTRIC)
            .setMaxIOSize(2, 0, 3, 2).setEUIO(IO.IN)
            .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType BUBBLE_COLUMN_REACTOR_RECIPES = register("bubble_column_reactor", ELECTRIC)
            .setMaxIOSize(1, 1, 3, 2).setEUIO(IO.IN)
            .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType BATCH_REACTOR_RECIPES = register("batch_reactor", ELECTRIC)
            .setMaxIOSize(3, 3, 3, 3).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MIXER, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType FLUIDIZED_BED_REACTOR_RECIPES = register("fluidized_bed_reactor", MULTIBLOCK)
            .setMaxIOSize(2, 3, 3, 3).setEUIO(IO.IN)
            .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType POLYMERIZATION_RECIPES = register("polymerization_tank", MULTIBLOCK)
            .setMaxIOSize(4, 1, 4, 2).setEUIO(IO.IN)
            .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType CATALYTIC_REFORMER_RECIPES = register("catalytic_reformer", ELECTRIC)
            .setMaxIOSize(1, 0, 2, 4).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_CRACKING, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.FURNACE);

    public static final GTRecipeType ELECTROLYTIC_CELL_RECIPES = register("electrolytic_cell", ELECTRIC)
            .setMaxIOSize(4, 3, 3, 4).setEUIO(IO.IN)
            .setSound(GTSoundEntries.ELECTROLYZER);

    public static final GTRecipeType FERMENTATION_VAT_RECIPES = register("vat_fermentation", MULTIBLOCK)
            .setMaxIOSize(3, 3, 3, 3).setEUIO(IO.IN)
            .setSound(GTSoundEntries.CHEMICAL);

    // ------------------------------------------------------------------
    // Separation / purification
    // ------------------------------------------------------------------
    public static final GTRecipeType CRYSTALLIZER_RECIPES = register("crystallizer", ELECTRIC)
            .setMaxIOSize(4, 3, 3, 3).setEUIO(IO.IN)
            .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType DRYER_RECIPES = register("dryer", ELECTRIC)
            .setMaxIOSize(2, 2, 2, 2).setEUIO(IO.IN)
            .setSound(GTSoundEntries.COOLING);

    public static final GTRecipeType ION_EXCHANGE_COLUMN_RECIPES = register("ion_exchange_column", ELECTRIC)
            .setMaxIOSize(2, 1, 2, 2).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_SIFT, DOWN_TO_UP)
            .setSound(GTSoundEntries.BATH);

    public static final GTRecipeType ZONE_REFINER_RECIPES = register("zone_refiner", ELECTRIC)
            .setMaxIOSize(4, 1, 2, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_CRYSTALLIZATION, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.FURNACE);

    public static final GTRecipeType ELECTROSTATIC_SEPARATOR_RECIPES = register("electrostatic_separator", ELECTRIC)
            .setMaxIOSize(1, 6, 1, 2).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MAGNET, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ARC);

    public static final GTRecipeType GRAVITY_SEPARATOR_RECIPES = register("gravity_separator", ELECTRIC)
            .setMaxIOSize(1, 6, 1, 3).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, DOWN_TO_UP)
            .setSound(GTSoundEntries.MACERATOR);

    public static final GTRecipeType ORE_SORTER_RECIPES = register("ore_sorter", ELECTRIC)
            .setMaxIOSize(2, 20, 1, 1).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_EXTRACT, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MACERATOR);

    public static final GTRecipeType CLARIFIER_RECIPES = register("clarifier", MULTIBLOCK)
            .setMaxIOSize(2, 2, 2, 2).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MIXER, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.CENTRIFUGE);

    public static final GTRecipeType FROTH_FLOTATION_RECIPES = register("froth_flotation", MULTIBLOCK)
            .setMaxIOSize(3, 2, 4, 2).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MIXER, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.BATH);

    public static final GTRecipeType PHASE_SEPARATOR_RECIPES = register("phase_separator", MULTIBLOCK)
            .setMaxIOSize(0, 1, 2, 3)
            .setProgressBar(GuiTextures.PROGRESS_BAR_EXTRACT, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.BATH);

    public static final GTRecipeType PRESSURE_SWING_ADSORBER_RECIPES = register("pressure_swing_adsorption", ELECTRIC)
            .setMaxIOSize(1, 1, 2, 2).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_SIFT, DOWN_TO_UP)
            .setSound(GTSoundEntries.COMPRESSOR);

    public static final GTRecipeType IN_SITU_LEACHER_RECIPES = register("in_situ_leacher", MULTIBLOCK)
            .setMaxIOSize(2, 2, 2, 2).setEUIO(IO.IN)
            .setSound(GTSoundEntries.COMPRESSOR);

    public static final GTRecipeType EVAPORATION_POOL_RECIPES = register("evaporation_pool", MULTIBLOCK)
            .setMaxIOSize(2, 4, 1, 1)
            .addDataInfo(data -> {
                if (!data.contains(SuSyRecipePropertyKeys.EVAPORATION_ENERGY, Tag.TAG_INT)) {
                    return "";
                }
                int energy = data.getInt(SuSyRecipePropertyKeys.EVAPORATION_ENERGY);
                return energy > 0 ? LocalizationUtils.format("susy.recipe.evaporation",
                        FormattingUtil.formatNumbers(energy)) : "";
            })
            .setProgressBar(GuiTextures.PROGRESS_BAR_SIFT, DOWN_TO_UP)
            .setSound(GTSoundEntries.CHEMICAL);

    // ------------------------------------------------------------------
    // Distillation
    // ------------------------------------------------------------------
    public static final GTRecipeType VACUUM_DISTILLATION_RECIPES = register("vacuum_distillation", MULTIBLOCK)
            .setMaxIOSize(1, 1, 2, 12).setEUIO(IO.IN)
            .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType SIEVE_DISTILLATION_RECIPES = register("sieve_distillation", MULTIBLOCK)
            .setMaxIOSize(1, 1, 2, 12).setEUIO(IO.IN)
            .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType MULTI_STAGE_FLASH_DISTILLATION_RECIPES = register(
            "multi_stage_flash_distillation", MULTIBLOCK)
            .setMaxIOSize(1, 0, 3, 3).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARC_FURNACE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COOLING);

    public static final GTRecipeType HIGH_TEMPERATURE_DISTILLATION_RECIPES = register("high_temperature_distillation",
            MULTIBLOCK)
            .setMaxIOSize(1, 1, 1, 12).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_SIFT, DOWN_TO_UP)
            .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType HIGH_PRESSURE_CRYOGENIC_DISTILLATION_RECIPES = register(
            "high_pressure_cryogenic_distillation", MULTIBLOCK)
            .setMaxIOSize(1, 0, 9, 9).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_SIFT, DOWN_TO_UP)
            .setSound(GTSoundEntries.COOLING);

    public static final GTRecipeType LOW_PRESSURE_CRYOGENIC_DISTILLATION_RECIPES = register(
            "low_pressure_cryogenic_distillation", MULTIBLOCK)
            .setMaxIOSize(1, 0, 9, 9).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_SIFT, DOWN_TO_UP)
            .setSound(GTSoundEntries.COOLING);

    public static final GTRecipeType SINGLE_COLUMN_CRYOGENIC_DISTILLATION_RECIPES = register(
            "single_column_cryogenic_distillation", MULTIBLOCK)
            .setMaxIOSize(1, 0, 9, 9).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_SIFT, DOWN_TO_UP)
            .setSound(GTSoundEntries.COOLING);

    // ------------------------------------------------------------------
    // Cryogenic / thermal exchange
    // ------------------------------------------------------------------
    public static final GTRecipeType BATH_CONDENSER_RECIPES = register("bath_condenser", MULTIBLOCK)
            .setMaxIOSize(0, 0, 2, 3)
            .setProgressBar(GuiTextures.PROGRESS_BAR_SIFT, DOWN_TO_UP)
            .setSound(GTSoundEntries.BATH);

    public static final GTRecipeType HEAT_EXCHANGER_RECIPES = register("heat_exchanger", MULTIBLOCK)
            .setMaxIOSize(1, 0, 2, 2)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COOLING);

    public static final GTRecipeType CONDENSER_RECIPES = register("condenser", ELECTRIC)
            .setMaxIOSize(0, 0, 2, 2)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COOLING);

    public static final GTRecipeType HEAT_RADIATOR_RECIPES = register("radiator", MULTIBLOCK)
            .setMaxIOSize(1, 0, 1, 1)
            .setProgressBar(GuiTextures.PROGRESS_BAR_COMPRESS, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COOLING);

    public static final GTRecipeType QUENCHER_RECIPES = register("quencher", ELECTRIC)
            .setMaxIOSize(2, 1, 2, 1).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COOLING);

    public static final GTRecipeType NATURAL_DRAFT_COOLING_TOWER_RECIPES = register("natural_draft_cooling_tower",
            MULTIBLOCK)
            .setMaxIOSize(1, 0, 1, 1).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_GAS_COLLECTOR, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COOLING);

    public static final GTRecipeType FLUID_COMPRESSOR_RECIPES = register("fluid_compressor", ELECTRIC)
            .setMaxIOSize(2, 0, 2, 2).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_COMPRESS, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COMPRESSOR);

    public static final GTRecipeType FLUID_DECOMPRESSOR_RECIPES = register("fluid_decompressor", ELECTRIC)
            .setMaxIOSize(1, 0, 2, 2).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_EXTRACT, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COMPRESSOR);

    public static final GTRecipeType TUBE_FURNACE_RECIPES = register("tube_furnace", ELECTRIC)
            .setMaxIOSize(6, 1, 3, 1).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARC_FURNACE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.CHEMICAL);

    // ------------------------------------------------------------------
    // Material shaping / processing
    // ------------------------------------------------------------------
    public static final GTRecipeType SPINNING_RECIPES = register("spinning", ELECTRIC)
            .setMaxIOSize(1, 1, 1, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MAGNET, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.CENTRIFUGE);

    public static final GTRecipeType POLISHING_MACHINE_RECIPES = register("polishing_machine", ELECTRIC)
            .setMaxIOSize(1, 1, 2, 1).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MIXER, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.CENTRIFUGE);

    public static final GTRecipeType LATEX_COLLECTOR_RECIPES = register("latex_collector", ELECTRIC)
            .setMaxIOSize(0, 2, 1, 2)
            .setProgressBar(GuiTextures.PROGRESS_BAR_EXTRACT, DOWN_TO_UP)
            .setSound(GTSoundEntries.DRILL_TOOL);

    public static final GTRecipeType CURTAIN_COATER_RECIPES = register("curtain_coater", ELECTRIC)
            .setMaxIOSize(1, 1, 1, 1).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.BATH);

    public static final GTRecipeType MILLING_RECIPES = register("milling", ELECTRIC)
            .setMaxIOSize(2, 1, 0, 0).setEUIO(IO.IN)
            .setSound(GTSoundEntries.CUT);

    public static final GTRecipeType MIXER_SETTLER_RECIPES = register("mixer_settler", ELECTRIC)
            .setMaxIOSize(2, 2, 3, 3).setEUIO(IO.IN)
            .setSound(GTSoundEntries.MIXER);

    public static final GTRecipeType BLENDER_RECIPES = register("blender", MULTIBLOCK)
            .setMaxIOSize(9, 1, 6, 2).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType ECCENTRIC_ROLL_CRUSHER_RECIPES = register("eccentric_roll_crusher", ELECTRIC)
            .setMaxIOSize(1, 4, 0, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MACERATOR);

    public static final GTRecipeType BALL_MILL_RECIPES = register("ball_mill", ELECTRIC)
            .setMaxIOSize(1, 4, 1, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MACERATOR);

    public static final GTRecipeType ATTRITION_SCRUBBER_RECIPES = register("attrition_scrubber", ELECTRIC)
            .setMaxIOSize(2, 3, 1, 1).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.BATH);

    public static final GTRecipeType INJECTION_MOLDER_RECIPES = register("injection_molder", ELECTRIC)
            .setMaxIOSize(3, 1, 0, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MIXER);

    public static final GTRecipeType MINING_DRILL_RECIPES = register("mining_drill", MULTIBLOCK)
            .setMaxIOSize(1, 1, 1, 1).setEUIO(IO.IN)
            .setSound(GTSoundEntries.MACERATOR);

    // ------------------------------------------------------------------
    // Electronics / lithography
    // ------------------------------------------------------------------
    public static final GTRecipeType UV_RECIPES = register("uv_light_box", ELECTRIC)
            .setMaxIOSize(2, 1, 1, 1).setEUIO(IO.IN)
            .setSound(GTSoundEntries.ARC);

    public static final GTRecipeType ION_IMPLANTATION_RECIPES = register("ion_implantation", ELECTRIC)
            .setMaxIOSize(3, 1, 2, 0).setEUIO(IO.IN)
            .setSound(GTSoundEntries.ELECTROLYZER);

    public static final GTRecipeType CVD_RECIPES = register("cvd", ELECTRIC)
            .setMaxIOSize(3, 1, 4, 2).setEUIO(IO.IN)
            .setSound(GTSoundEntries.ARC);

    public static final GTRecipeType SPUTTER_DEPOSITION_RECIPES = register("sputter_deposition", ELECTRIC)
            .setMaxIOSize(6, 1, 2, 2).setEUIO(IO.IN)
            .setSound(GTSoundEntries.ELECTROLYZER);

    public static final GTRecipeType EVAPORATION_DEPOSITION_RECIPES = register("evaporation_deposition", ELECTRIC)
            .setMaxIOSize(4, 1, 0, 0).setEUIO(IO.IN)
            .setSound(GTSoundEntries.ARC);

    public static final GTRecipeType EUV_LITHOGRAPHY_RECIPES = register("euv_lithography", ELECTRIC)
            .setMaxIOSize(3, 3, 3, 3).setEUIO(IO.IN)
            .setSound(GTSoundEntries.ELECTROLYZER);

    public static final GTRecipeType RIE_RECIPES = register("reactive_ion_etching", ELECTRIC)
            .setMaxIOSize(1, 1, 4, 1).setEUIO(IO.IN)
            .setSound(GTSoundEntries.ARC);

    public static final GTRecipeType RESIST_PROCESSOR_RECIPES = register("resist_processing", ELECTRIC)
            .setMaxIOSize(1, 1, 5, 0).setEUIO(IO.IN)
            .setSound(GTSoundEntries.CENTRIFUGE);

    public static final GTRecipeType PLASMA_ASHER_RECIPES = register("plasma_ashing", ELECTRIC)
            .setMaxIOSize(1, 1, 2, 0).setEUIO(IO.IN)
            .setSound(GTSoundEntries.ARC);

    public static final GTRecipeType ELECTRON_BEAM_LITHOGRAPHY_RECIPES = register("electron_beam_lithography", ELECTRIC)
            .setMaxIOSize(1, 1, 0, 0).setEUIO(IO.IN)
            .setSound(GTSoundEntries.ARC);

    public static final GTRecipeType WIRE_BONDING_RECIPES = register("wire_bonding", ELECTRIC)
            .setMaxIOSize(2, 1, 1, 0).setEUIO(IO.IN);

    public static final GTRecipeType SCREEN_PRINTER_RECIPES = register("screen_printer", ELECTRIC)
            .setMaxIOSize(2, 1, 1, 0).setEUIO(IO.IN);

    public static final GTRecipeType ALD_RECIPES = register("atomic_layer_deposition", ELECTRIC)
            .setMaxIOSize(1, 1, 4, 2).setEUIO(IO.IN);

    public static final GTRecipeType EDM_RECIPES = register("edm", ELECTRIC)
            .setMaxIOSize(3, 3, 1, 1).setEUIO(IO.IN)
            .setSound(GTSoundEntries.ELECTROLYZER);

    // ------------------------------------------------------------------
    // Assembly / misc production
    // ------------------------------------------------------------------
    public static final GTRecipeType WEAPONS_FACTORY_RECIPES = register("weapons_factory", ELECTRIC)
            .setMaxIOSize(9, 1, 2, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_CIRCUIT, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ASSEMBLER);

    public static final GTRecipeType LARGE_WEAPONS_FACTORY_RECIPES = register("large_weapons_factory", MULTIBLOCK)
            .setMaxIOSize(9, 1, 3, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_CIRCUIT, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ASSEMBLER);

    public static final GTRecipeType RAILROAD_ENGINEERING_STATION_RECIPES = register("railroad_engineering_station",
            MULTIBLOCK)
            .setMaxIOSize(16, 1, 4, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ASSEMBLER);

    public static final GTRecipeType SCRAP_RECYCLER_RECIPES = register("scrap_recycler", ELECTRIC)
            .setMaxIOSize(2, 9, 0, 3).setEUIO(IO.IN)
            .setSound(GTSoundEntries.ASSEMBLER);

    public static final GTRecipeType SALVAGING_RECIPES = register("salvaging", ELECTRIC)
            .setMaxIOSize(1, 9, 0, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_RECYCLER, LEFT_TO_RIGHT);

    public static final GTRecipeType GREENHOUSE_PLANT_RECIPES = register("greenhouse_plant", MULTIBLOCK)
            .setMaxIOSize(2, 4, 3, 1).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT);

    // ------------------------------------------------------------------
    // Fuel / power generation (generator group, EU out)
    // ------------------------------------------------------------------
    public static final GTRecipeType MAGNETOHYDRODYNAMIC_FUELS = register("magnetohydrodynamic_generator", GENERATOR)
            .setMaxIOSize(0, 0, 1, 1).setEUIO(IO.OUT)
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ARC);

    public static final GTRecipeType LARGE_STEAM_TURBINE_FUELS = register("large_steam_turbine", GENERATOR)
            .setMaxIOSize(0, 0, 1, 1).setEUIO(IO.OUT)
            .setProgressBar(GuiTextures.PROGRESS_BAR_GAS_COLLECTOR, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.TURBINE);

    public static final GTRecipeType ADVANCED_STEAM_TURBINE_FUELS = register("advanced_steam_turbine", GENERATOR)
            .setMaxIOSize(0, 0, 1, 1).setEUIO(IO.OUT)
            .setProgressBar(GuiTextures.PROGRESS_BAR_GAS_COLLECTOR, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.TURBINE);

    public static final GTRecipeType BOILER_RECIPES = register("boiler", GENERATOR)
            .setMaxIOSize(1, 0, 0, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.BOILER);

    public static final GTRecipeType FUEL_CELL_RECIPES = register("fuel_cell", GENERATOR)
            .setMaxIOSize(0, 0, 2, 0).setEUIO(IO.OUT)
            .setProgressBar(GuiTextures.PROGRESS_BAR_HAMMER, DOWN_TO_UP)
            .setSound(GTSoundEntries.ELECTROLYZER);

    public static GTRecipeType register(String name, String group, RecipeType<?>... proxyRecipes) {
        var recipeType = new GTRecipeType(SuSyValues.susyId(name), group, proxyRecipes);
        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, recipeType.registryName, recipeType);
        GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, recipeType.registryName, new GTRecipeSerializer());
        GTRegistries.RECIPE_TYPES.register(recipeType.registryName, recipeType);
        return recipeType;
    }

    public static void init() {}

    private SuSyRecipeTypes() {}
}
