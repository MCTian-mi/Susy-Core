package io.github.symmetricdevs.supersymmetry.client.renderer.textures;

import net.minecraft.world.item.DyeColor;

/**
 * Texture handles for SuSy machine overlays and casings.
 * <p>
 * Ported from 1.12.2 which used GTCEu's legacy renderer classes
 * (SimpleOverlayRenderer, SimpleSidedCubeRenderer, OrientedOverlayRenderer,
 * SimpleCubeRenderer, SimpleOrientedCubeRenderer, DrumRenderer, ExtenderRender).
 * GTCEu-Modern 7.5.3 has replaced these with a new rendering pipeline.
 * <p>
 * These texture handles are stubbed and will be re-implemented when
 * machine rendering is fully ported to the Modern pipeline.
 */
public class SusyTextures {

    private SusyTextures() {}

    // --- Casing / block textures ---
    // These are placeholders for the old SimpleSidedCubeRenderer instances.
    // Re-implement as MaterialBlockRenderer or similar when rendering is ported.

    public static final Object WOODEN_COAGULATION_TANK_WALL = null;
    public static final Object PLASTIC_CAN_OVERLAY = null;
    public static final Object INV_BRIDGE = null;
    public static final Object TANK_BRIDGE = null;
    public static final Object INV_TANK_BRIDGE = null;
    public static final Object UNIVERSAL_BRIDGE = null;

    // --- Extender renders ---
    public static final Object INV_EXTENDER = null;
    public static final Object TANK_EXTENDER = null;
    public static final Object INV_TANK_EXTENDER = null;
    public static final Object UNIVERSAL_EXTENDER = null;

    // --- Machine overlay textures ---
    public static final Object VULCANIZING_PRESS_OVERLAY = null;
    public static final Object LATEX_COLLECTOR_OVERLAY = null;
    public static final Object ROASTER_OVERLAY = null;
    public static final Object MIXER_OVERLAY_STEAM = null;
    public static final Object CONTINUOUS_STIRRED_TANK_REACTOR_OVERLAY = null;
    public static final Object FIXED_BED_REACTOR_OVERLAY = null;
    public static final Object TRICKLE_BED_REACTOR_OVERLAY = null;
    public static final Object BUBBLE_COLUMN_REACTOR_OVERLAY = null;
    public static final Object BATCH_REACTOR_OVERLAY = null;
    public static final Object CRYSTALLIZER_OVERLAY = null;
    public static final Object DRYER_OVERLAY = null;
    public static final Object ION_EXCHANGE_COLUMN_OVERLAY = null;
    public static final Object ZONE_REFINER_OVERLAY = null;
    public static final Object TUBE_FURNACE_OVERLAY = null;
    public static final Object UV_LIGHT_BOX_OVERLAY = null;
    public static final Object CVD_OVERLAY = null;
    public static final Object ALD_OVERLAY = null;
    public static final Object ION_IMPLANTER_OVERLAY = null;
    public static final Object SPIN_COATER_OVERLAY = null;
    public static final Object SPUTTER_DEPOSITION_OVERLAY = null;
    public static final Object PHASE_SEPARATOR_OVERLAY = null;
    public static final Object BATH_CONDENSER_OVERLAY = null;
    public static final Object CATALYTIC_REFORMER_OVERLAY = null;
    public static final Object INCINERATOR_OVERLAY = null;
    public static final Object FUEL_CELL_OVERLAY = null;
    public static final Object FLUID_COMPRESSOR_OVERLAY = null;
    public static final Object FLUID_DECOMPRESSOR_OVERLAY = null;
    public static final Object ELECTROSTATIC_SEPARATOR_OVERLAY = null;
    public static final Object TEXTILE_SPINNER_OVERLAY = null;
    public static final Object POLISHING_MACHINE_OVERLAY = null;
    public static final Object ARC_FURNACE_OVERLAY = null;
    public static final Object CLARIFIER_OVERLAY = null;
    public static final Object CONDENSER_OVERLAY = null;
    public static final Object NATURAL_DRAFT_COOLING_TOWER_OVERLAY = null;
    public static final Object HPCDT_OVERLAY = null;
    public static final Object HTDT_OVERLAY = null;
    public static final Object LPCDT_OVERLAY = null;
    public static final Object VDT_OVERLAY = null;
    public static final Object DUMPER_OVERLAY = null;
    public static final Object ELECTROLYTIC_CELL_OVERLAY = null;
    public static final Object FLARE_STACK_OVERLAY = null;
    public static final Object FLUIDIZED_BED_OVERLAY = null;
    public static final Object FROTH_FLOTATION_OVERLAY = null;
    public static final Object HEAT_EXCHANGER_OVERLAY = null;
    public static final Object LARGE_GAS_TURBINE_OVERLAY = null;
    public static final Object LARGE_STEAM_TURBINE_OVERLAY = null;
    public static final Object ADVANCED_STEAM_TURBINE_OVERLAY = null;
    public static final Object LARGE_WEAPONS_FACTORY_OVERLAY = null;
    public static final Object MINING_DRILL_OVERLAY = null;
    public static final Object LARGE_FLUID_PUMP_OVERLAY = null;
    public static final Object ORE_SORTER_OVERLAY = null;
    public static final Object PRESSURE_SWING_ABSORBER_OVERLAY = null;
    public static final Object QUENCHER_OVERLAY = null;
    public static final Object RADIATOR_OVERLAY = null;
    public static final Object RAILROAD_ENGINEERING_STATION_OVERLAY = null;
    public static final Object ROTARY_KILN_OVERLAY = null;
    public static final Object SINTERING_OVERLAY = null;
    public static final Object SMOKE_STACK_OVERLAY = null;
    public static final Object PRIMITIVE_SMELTER_OVERLAY = null;
    public static final Object CUPOLA_FURNACE_OVERLAY = null;
    public static final Object TURNING_ZONE_OVERLAY = null;
    public static final Object MILLING_OVERLAY = null;
    public static final Object CLUSTER_MILL_OVERLAY = null;
    public static final Object ROLLING_MILL_OVERLAY = null;
    public static final Object FLYING_SHEAR_OVERLAY = null;
    public static final Object BILLET_MOLD_OVERLAY = null;
    public static final Object SLAB_MOLD_OVERLAY = null;
    public static final Object STRAND_COOLER_OVERLAY = null;
    public static final Object GAS_ATOMIZER_OVERLAY = null;
    public static final Object METALLURGICAL_CONVERTER_OVERLAY = null;
    public static final Object INJECTION_MOLDER_OVERLAY = null;
    public static final Object POLYMERIZATION_TANK_OVERLAY = null;
    public static final Object ECCENTRIC_ROLL_CRUSHER_OVERLAY = null;
    public static final Object BALL_MILL_OVERLAY = null;
    public static final Object ATTRITION_SCRUBBER_OVERLAY = null;
    public static final Object SCREEN_PRINTER_OVERLAY = null;
    public static final Object EDM_OVERLAY = null;

    // --- Simple overlays ---
    public static final Object SLAG_HOT = null;
    public static final Object RESTRICTIVE_FILTER_FILTER_OVERLAY = null;
    public static final Object STRAND_BUS_OVERLAY = null;
    public static final Object CODE_BREACHER_OVERLAY = null;
    public static final Object REDSTONE_CONTROLLER_OVERLAY = null;
    public static final Object DRONE_BASKET_OVERLAY = null;

    // --- Cube renderers ---
    public static final Object MASONRY_BRICK = null;
    public static final Object BALL_MILL_SHELL = null;
    public static final Object SILICON_CARBIDE_CASING = null;
    public static final Object MONEL_500_CASING = null;
    public static final Object CONDUCTIVE_COPPER_PIPE = null;
    public static final Object ULV_STRUCTURAL_CASING = null;
    public static final Object HYDROSTATIC_CASING = null;
    public static final Object ALUMINIUM_GEARBOX = null;
    public static final Object STEEL_TURBINE_CASING = null;
    public static final Object TITANIUM_TURBINE_CASING = null;
    public static final Object AEROSPACE_GASKET = null;
    public static final Object ABRASION_RESISTANT_CASING = null;

    public static final Object PLASTIC_CAN = null;

    // --- Stock / rail interface textures ---
    // No GTCEu-Modern equivalent exists for the legacy voltage-casing renderer.
    // A machine-specific model will replace this during the renderer rewrite.
    public static final Object STOCK_MACHINE_CASING = null;

    public static final Object STOCK_FLUID_EXCHANGER = null;
    public static final Object STOCK_ITEM_EXCHANGER = null;
    public static final Object STOCK_DETECTOR_NEITHER = null;
    public static final Object STOCK_DETECTOR_DETECTING = null;
    public static final Object STOCK_DETECTOR_FILTER = null;
    public static final Object STOCK_DETECTOR_BOTH = null;
    public static final Object STOCK_READER_ITEM = null;
    public static final Object STOCK_READER_FLUID = null;
    public static final Object STOCK_CONTROLLER = null;
    public static final Object[] METAL_SHEETS = new Object[32];

    static {
        for (DyeColor color : DyeColor.values()) {
            METAL_SHEETS[color.ordinal()] = null;
            METAL_SHEETS[color.ordinal() + 16] = null;
        }
    }
}
