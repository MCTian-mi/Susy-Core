package io.github.symmetricdevs.supersymmetry.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.item.DrumMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import com.gregtechceu.gtceu.common.machine.storage.CrateMachine;
import com.gregtechceu.gtceu.common.machine.storage.DrumMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import io.github.symmetricdevs.supersymmetry.common.machine.electric.CatalystSimpleMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.electric.ContinuousSimpleMachine;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;

import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidType;

import java.util.Locale;
import java.util.function.BiFunction;

import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.api.registry.SusyRegistration;
import io.github.symmetricdevs.supersymmetry.common.machine.storage.LockedCrateMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.storage.PlasticCanMachine;

import static com.gregtechceu.gtceu.api.GTValues.VN;
import static com.gregtechceu.gtceu.api.GTValues.VNF;
import static com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties.IS_FORMED;

/**
 * SuSy machine registry. Ported from the 1.12.2 {@code SuSyMetaTileEntities}
 * (numeric-ID {@code registerMetaTileEntity} calls) onto the GTCEu-Modern
 * registrate DSL. Old numeric IDs are gone — each machine is a
 * {@link MachineDefinition} (or a {@code MachineDefinition[]} over tiers) held
 * in a static field here; the 1.12.2 registry is the checklist.
 * <p>
 * <b>Registration timing:</b> machines must class-load during GTCEu's register
 * event (while {@code gtceu:recipe_category} etc. are unfrozen), so this holder
 * is {@link #init()}-ed from a mod-bus generic listener on
 * {@code MachineDefinition} in {@code Supersymmetry} — the same pattern as the
 * recipe types. It is NOT touched from {@code IGTAddon.initializeAddon()}, which
 * runs after the registries are frozen.
 * <p>
 * This file currently covers <b>Phase 4a</b> (storage + hatches/buses). Simple
 * tiered/steam/generator machines (4b) and multiblock controllers (4c) are added
 * in their own phases. Deferred with their parent scope: strand-casting buses
 * (need the strand capability, Phase 5), particle-beam hatches (need the beam
 * capability, Phase 5), rocket/space machines, and the ImmersiveRailroading
 * stock/rail interfaces.
 */
public final class SusyMachines {

    private static final GTRegistrate REGISTRATE = SusyRegistration.REGISTRATE;

    static {
        // Force the creative-tab item-group owner onto our registrate before any
        // machine field below registers a block+item (mirrors GTCEu/GCYR, whose
        // registrate is pre-configured by the @Mod class).
        REGISTRATE.creativeModeTab(() -> SusyCreativeModeTabs.SUPERSYMMETRY);
    }

    // ------------------------------------------------------------------
    // Tier arrays + tank sizing (mirrors GTMachineUtils, scoped to our REGISTRATE)
    // ------------------------------------------------------------------
    /** LV .. OpV when high-tier is enabled, else LV .. UV. */
    public static final int[] ELECTRIC_TIERS = GTValues.tiersBetween(GTValues.LV,
        GTCEuAPI.isHighTier() ? GTValues.OpV : GTValues.UV);
    /** LV .. HV — the tiers the multi-fluid import/export hatches exist in. */
    public static final int[] MULTI_FLUID_HATCH_TIERS = GTValues.tiersBetween(GTValues.LV, GTValues.HV);

    /** Generic per-tier fluid tank size (LV=8k .. UV+=64k), mB. */
    public static final it.unimi.dsi.fastutil.ints.Int2IntFunction defaultTankSizeFunction = tier ->
        (tier <= GTValues.LV ? 8 : tier == GTValues.MV ? 12 : tier == GTValues.HV ? 16 : tier == GTValues.EV ? 32 : 64)
                * FluidType.BUCKET_VOLUME;

    /** Reactor-line tank sizing (1.12.2 {@code SuSyUtility.reactorTankSizeFunction}), mB. */
    public static final it.unimi.dsi.fastutil.ints.Int2IntFunction reactorTankSizeFunction = tier ->
        tier <= GTValues.LV ? 12000 : tier == GTValues.MV ? 16000 : tier == GTValues.HV ? 20000
                : tier == GTValues.EV ? 36000 : 64000;

    /** Collector tank sizing (1.12.2 {@code SuSyUtility.collectorTankSizeFunction}), mB. */
    public static final it.unimi.dsi.fastutil.ints.Int2IntFunction collectorTankSizeFunction = tier ->
        tier <= GTValues.LV ? 16000 : tier == GTValues.MV ? 24000 : tier == GTValues.HV ? 32000 : 64000;

    /** Bulk tank sizing (1.12.2 {@code SuSyUtility.bulkTankSizeFunction}), mB. */
    public static final it.unimi.dsi.fastutil.ints.Int2IntFunction bulkTankSizeFunction = tier ->
        tier <= GTValues.LV ? 12000 : tier == GTValues.MV ? 18000 : tier == GTValues.HV ? 24000
                : tier == GTValues.EV ? 48000 : 64000;

    /** Bigger "large" tank sizing (GTCEu {@code GTMachineUtils.largeTankSizeFunction}), mB. */
    public static final it.unimi.dsi.fastutil.ints.Int2IntFunction largeTankSizeFunction = tier ->
        (tier <= GTValues.LV ? 32 : tier == GTValues.MV ? 48 : 64) * FluidType.BUCKET_VOLUME;

    // ==================================================================
    // Phase 4a — Storage: drums, plastic cans, crates, locked crates
    // ==================================================================

    public static final MachineDefinition LEAD_DRUM = registerDrum("lead_drum", GTMaterials.Lead, 32_000, "Lead Drum");
    public static final MachineDefinition BRASS_DRUM = registerDrum("brass_drum", GTMaterials.Brass, 16_000, "Brass Drum");

    public static final MachineDefinition PE_CAN = registerPlasticCan("pe_can", GTMaterials.Polyethylene, 64_000,
            "Polyethylene Can");
    public static final MachineDefinition PTFE_CAN = registerPlasticCan("ptfe_can", GTMaterials.Polytetrafluoroethylene,
            512_000, "PTFE Can");
    // TODO)) polypropylene + UHMWPE cans: those materials are SuSy GroovyScript bulk
    // data, not yet ported to Java (deferred). registerPlasticCan("pp_can", ...) and
    // ("uhmwpe_can", ...) once SusyMaterials exposes them.

    public static final MachineDefinition HERMETICALLY_SEALED_CRATE = registerCrate("hermetically_sealed_crate",
            GTMaterials.Polyethylene, 54, "Hermetically Sealed Crate");

    // Locked loot crates (sealed; drop with contents; opened by code breacher — see
    // LockedCrateMachine TODO).
    public static final MachineDefinition LOCKED_HERMETICALLY_SEALED_CRATE = registerLockedCrate(
            "locked_hermetically_sealed_crate", GTMaterials.Polyethylene, 54, "Locked Hermetically Sealed Crate");
    public static final MachineDefinition LOCKED_WOODEN_CRATE = registerLockedCrate("locked_wooden_crate",
            GTMaterials.Wood, 27, "Locked Wooden Crate");
    public static final MachineDefinition LOCKED_BRONZE_CRATE = registerLockedCrate("locked_bronze_crate",
            GTMaterials.Bronze, 54, "Locked Bronze Crate");
    public static final MachineDefinition LOCKED_STEEL_CRATE = registerLockedCrate("locked_steel_crate",
            GTMaterials.Steel, 72, "Locked Steel Crate");
    public static final MachineDefinition LOCKED_ALUMINIUM_CRATE = registerLockedCrate("locked_aluminium_crate",
            GTMaterials.Aluminium, 90, "Locked Aluminium Crate");
    public static final MachineDefinition LOCKED_STAINLESS_STEEL_CRATE = registerLockedCrate(
            "locked_stainless_steel_crate", GTMaterials.StainlessSteel, 108, "Locked Stainless Steel Crate");
    public static final MachineDefinition LOCKED_TITANIUM_CRATE = registerLockedCrate("locked_titanium_crate",
            GTMaterials.Titanium, 126, "Locked Titanium Crate");
    public static final MachineDefinition LOCKED_TUNGSTENSTEEL_CRATE = registerLockedCrate(
            "locked_tungstensteel_crate", GTMaterials.TungstenSteel, 144, "Locked Tungstensteel Crate");

    // Drone deposit baskets (drone item drop-off crates). 1.12.2 sized them 4 / 16.
    // TODO)) the drone-deposit behaviour (accepting drone payloads) is part of the
    // rocketry/drone scope — these currently register as plain storage crates.
    public static final MachineDefinition DRONE_DEPOSIT_BASKET = registerCrate("drone_deposit_basket",
            GTMaterials.Polyethylene, 4, "Drone Deposit Basket");
    public static final MachineDefinition ADVANCED_DRONE_DEPOSIT_BASKET = registerCrate("advanced_drone_deposit_basket",
            GTMaterials.Epoxy, 16, "Advanced Drone Deposit Basket");

    // ==================================================================
    // Phase 4a — Energy hatches (dynamo / substation, hi-amp)
    // ==================================================================
    // SuSy runs a power-grid tier above stock GTCEu, so it needs extra output
    // (dynamo) and substation hatches across LV..EV where stock GTCEu stops at EV+.
    public static final MachineDefinition[] ENERGY_OUTPUT_HATCH_4A = registerTieredMachines("energy_hatch_output_4a",
            (holder, tier) -> new EnergyHatchPartMachine(holder, tier, IO.OUT, 4),
            (tier, builder) -> builder
                    .langValue(VNF[tier] + " 4A Dynamo Hatch")
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.OUTPUT_ENERGY)
                    .modelProperty(IS_FORMED, false)
                    .tooltips(Component.translatable("gtceu.universal.tooltip.voltage_out",
                                    FormattingUtil.formatNumbers(GTValues.V[tier]), VNF[tier]),
                            Component.translatable("gtceu.universal.tooltip.amperage_out", 4),
                            Component.translatable("gtceu.universal.tooltip.energy_storage_capacity",
                                    FormattingUtil.formatNumbers(EnergyHatchPartMachine.getHatchEnergyCapacity(tier, 4))))
                    .overlayTieredHullModel("energy_output_hatch_4a")
                    .register(),
            GTValues.tiersBetween(GTValues.LV, GTValues.HV));

    public static final MachineDefinition[] ENERGY_OUTPUT_HATCH_16A = registerTieredMachines("energy_hatch_output_16a",
            (holder, tier) -> new EnergyHatchPartMachine(holder, tier, IO.OUT, 16),
            (tier, builder) -> builder
                    .langValue(VNF[tier] + " 16A Dynamo Hatch")
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.OUTPUT_ENERGY)
                    .modelProperty(IS_FORMED, false)
                    .tooltips(Component.translatable("gtceu.universal.tooltip.voltage_out",
                                    FormattingUtil.formatNumbers(GTValues.V[tier]), VNF[tier]),
                            Component.translatable("gtceu.universal.tooltip.amperage_out", 16),
                            Component.translatable("gtceu.universal.tooltip.energy_storage_capacity",
                                    FormattingUtil.formatNumbers(EnergyHatchPartMachine.getHatchEnergyCapacity(tier, 16))))
                    .overlayTieredHullModel("energy_output_hatch_16a")
                    .register(),
            GTValues.tiersBetween(GTValues.LV, GTValues.EV));

    public static final MachineDefinition[] SUBSTATION_ENERGY_INPUT_HATCH_64A = registerTieredMachines(
            "substation_energy_hatch_input_64a",
            (holder, tier) -> new EnergyHatchPartMachine(holder, tier, IO.IN, 64),
            (tier, builder) -> builder
                    .langValue(VNF[tier] + " 64A Substation Energy Hatch")
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.SUBSTATION_INPUT_ENERGY)
                    .modelProperty(IS_FORMED, false)
                    .tooltips(Component.translatable("gtceu.universal.tooltip.voltage_in",
                                    FormattingUtil.formatNumbers(GTValues.V[tier]), VNF[tier]),
                            Component.translatable("gtceu.universal.tooltip.amperage_in", 64),
                            Component.translatable("gtceu.universal.tooltip.energy_storage_capacity",
                                    FormattingUtil.formatNumbers(EnergyHatchPartMachine.getHatchEnergyCapacity(tier, 64))))
                    .overlayTieredHullModel("energy_input_hatch_64a")
                    .register(),
            GTValues.tiersBetween(GTValues.LV, GTValues.EV));

    public static final MachineDefinition[] SUBSTATION_ENERGY_OUTPUT_HATCH_64A = registerTieredMachines(
            "substation_energy_hatch_output_64a",
            (holder, tier) -> new EnergyHatchPartMachine(holder, tier, IO.OUT, 64),
            (tier, builder) -> builder
                    .langValue(VNF[tier] + " 64A Substation Dynamo Hatch")
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.SUBSTATION_OUTPUT_ENERGY)
                    .modelProperty(IS_FORMED, false)
                    .tooltips(Component.translatable("gtceu.universal.tooltip.voltage_out",
                                    FormattingUtil.formatNumbers(GTValues.V[tier]), VNF[tier]),
                            Component.translatable("gtceu.universal.tooltip.amperage_out", 64),
                            Component.translatable("gtceu.universal.tooltip.energy_storage_capacity",
                                    FormattingUtil.formatNumbers(EnergyHatchPartMachine.getHatchEnergyCapacity(tier, 64))))
                    .overlayTieredHullModel("energy_output_hatch_64a")
                    .register(),
            GTValues.tiersBetween(GTValues.LV, GTValues.EV));

    // ==================================================================
    // Phase 4a — Multi-fluid import/export hatches (4x / 9x), LV..HV
    // ==================================================================
    public static final MachineDefinition[] QUADRUPLE_IMPORT_HATCH = registerMultiFluidHatch("fluid_hatch_import_4x",
            "Quadruple Input Hatch", IO.IN, 4, PartAbility.IMPORT_FLUIDS_4X);
    public static final MachineDefinition[] NONUPLE_IMPORT_HATCH = registerMultiFluidHatch("fluid_hatch_import_9x",
            "Nonuple Input Hatch", IO.IN, 9, PartAbility.IMPORT_FLUIDS_9X);
    public static final MachineDefinition[] QUADRUPLE_EXPORT_HATCH = registerMultiFluidHatch("fluid_hatch_export_4x",
            "Quadruple Output Hatch", IO.OUT, 4, PartAbility.EXPORT_FLUIDS_4X);
    public static final MachineDefinition[] NONUPLE_EXPORT_HATCH = registerMultiFluidHatch("fluid_hatch_export_9x",
            "Nonuple Output Hatch", IO.OUT, 9, PartAbility.EXPORT_FLUIDS_9X);

    // ==================================================================
    // Phase 4a — Primitive item buses (bronze/steel-age import/export)
    // ==================================================================
    // Tier-0 item buses for the primitive/steam multis; in 1.12.2 these fed the
    // Pyrotech primitive smelter line.
    public static final MachineDefinition PRIMITIVE_ITEM_IMPORT = REGISTRATE
            .machine("primitive_item_import", holder -> new ItemBusPartMachine(holder, 0, IO.IN))
            .langValue("Primitive Item Import Bus")
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.IMPORT_ITEMS)
            .modelProperty(IS_FORMED, false)
            .overlayTieredHullModel("item_import_bus")
            .tooltips(Component.translatable("gtceu.machine.item_bus.import.tooltip"))
            .allowCoverOnFront(true)
            .register();
    public static final MachineDefinition PRIMITIVE_ITEM_EXPORT = REGISTRATE
            .machine("primitive_item_export", holder -> new ItemBusPartMachine(holder, 0, IO.OUT))
            .langValue("Primitive Item Export Bus")
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.EXPORT_ITEMS)
            .modelProperty(IS_FORMED, false)
            .overlayTieredHullModel("item_export_bus")
            .tooltips(Component.translatable("gtceu.machine.item_bus.export.tooltip"))
            .allowCoverOnFront(true)
            .register();

    // TODO)) Phase 5+: strand-casting IMPORT/EXPORT buses (need the IStrandProvider
    // capability + custom PartAbility), particle-beam import/export hatches (need
    // IParticleBeamProvider capability), dumping hatch, component scanner/redstone
    // controller (rocket scope). Deferred with rocketry/space.

    // ==================================================================
    // Phase 4b — Simple tiered electric machines (LV..OpV)
    // ==================================================================
    // The bulk of 1.12.2's registerSimpleMTE / registerCatalystMTE /
    // registerContinuousMachineMTE calls. Each becomes a MachineDefinition[] across
    // ELECTRIC_TIERS. Custom SusyTextures overlays are not yet ported (Phase 6) — a
    // GTCEu machine model is used as the placeholder per type.

    // -- plain SimpleTieredMachine -------------------------------------------
    public static final MachineDefinition[] VACUUM_CHAMBER = registerSimpleMachines("vacuum_chamber",
            SuSyRecipeTypes.VACUUM_CHAMBER_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] BATCH_REACTOR = registerSimpleMachines("batch_reactor",
            SuSyRecipeTypes.BATCH_REACTOR_RECIPES, reactorTankSizeFunction);
    public static final MachineDefinition[] CRYSTALLIZER = registerSimpleMachines("crystallizer",
            SuSyRecipeTypes.CRYSTALLIZER_RECIPES, reactorTankSizeFunction);
    public static final MachineDefinition[] DRYER = registerSimpleMachines("dryer",
            SuSyRecipeTypes.DRYER_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] ION_EXCHANGE_COLUMN = registerSimpleMachines("ion_exchange_column",
            SuSyRecipeTypes.ION_EXCHANGE_COLUMN_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] ZONE_REFINER = registerSimpleMachines("zone_refiner",
            SuSyRecipeTypes.ZONE_REFINER_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] TUBE_FURNACE = registerSimpleMachines("tube_furnace",
            SuSyRecipeTypes.TUBE_FURNACE_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] UV_LIGHT_BOX = registerSimpleMachines("uv_light_box",
            SuSyRecipeTypes.UV_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] CVD = registerSimpleMachines("cvd",
            SuSyRecipeTypes.CVD_RECIPES, largeTankSizeFunction);
    public static final MachineDefinition[] ION_IMPLANTER = registerSimpleMachines("ion_implanter",
            SuSyRecipeTypes.ION_IMPLANTATION_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] ALD = registerSimpleMachines("ald",
            SuSyRecipeTypes.ALD_RECIPES, largeTankSizeFunction);
    public static final MachineDefinition[] SPUTTER_DEPOSITION = registerSimpleMachines("sputter_deposition",
            SuSyRecipeTypes.SPUTTER_DEPOSITION_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] SCREEN_PRINTER = registerSimpleMachines("screen_printer",
            SuSyRecipeTypes.SCREEN_PRINTER_RECIPES, defaultTankSizeFunction, GTValues.tiersBetween(GTValues.LV, GTValues.EV));
    public static final MachineDefinition[] EVAPORATION_DEPOSITION = registerSimpleMachines("evaporation_deposition",
            SuSyRecipeTypes.EVAPORATION_DEPOSITION_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] FLUID_COMPRESSOR = registerSimpleMachines("fluid_compressor",
            SuSyRecipeTypes.FLUID_COMPRESSOR_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] FLUID_DECOMPRESSOR = registerSimpleMachines("fluid_decompressor",
            SuSyRecipeTypes.FLUID_DECOMPRESSOR_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] WEAPONS_FACTORY = registerSimpleMachines("weapons_factory",
            SuSyRecipeTypes.WEAPONS_FACTORY_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] ELECTROSTATIC_SEPARATOR = registerSimpleMachines("electrostatic_separator",
            SuSyRecipeTypes.ELECTROSTATIC_SEPARATOR_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] POLISHING_MACHINE = registerSimpleMachines("polishing_machine",
            SuSyRecipeTypes.POLISHING_MACHINE_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] TEXTILE_SPINNER = registerSimpleMachines("textile_spinner",
            SuSyRecipeTypes.SPINNING_RECIPES, defaultTankSizeFunction);
    public static final MachineDefinition[] RESISTANCE_FURNACE = registerSimpleMachines("resistance_furnace",
            SuSyRecipeTypes.RESISTANCE_FURNACE_RECIPES, defaultTankSizeFunction);

    // -- catalyst machines (CatalystSimpleMachine) ---------------------------
    public static final MachineDefinition[] VULCANIZING_PRESS = registerCatalystMachines("vulcanizing_press",
            SuSyRecipeTypes.VULCANIZATION_RECIPES, defaultTankSizeFunction, GTValues.tiersBetween(GTValues.LV, GTValues.EV));
    public static final MachineDefinition[] ROASTER = registerCatalystMachines("roaster",
            SuSyRecipeTypes.ROASTER_RECIPES, bulkTankSizeFunction);

    // -- continuous machines (ContinuousSimpleMachine: catalyst + parallel) ---
    public static final MachineDefinition[] CONTINUOUS_STIRRED_TANK_REACTOR = registerContinuousMachines(
            "continuous_stirred_tank_reactor", SuSyRecipeTypes.CSTR_RECIPES, reactorTankSizeFunction);
    public static final MachineDefinition[] FIXED_BED_REACTOR = registerContinuousMachines("fixed_bed_reactor",
            SuSyRecipeTypes.FIXED_BED_REACTOR_RECIPES, reactorTankSizeFunction);
    public static final MachineDefinition[] TRICKLE_BED_REACTOR = registerContinuousMachines("trickle_bed_reactor",
            SuSyRecipeTypes.TRICKLE_BED_REACTOR_RECIPES, reactorTankSizeFunction);
    public static final MachineDefinition[] BUBBLE_COLUMN_REACTOR = registerContinuousMachines(
            "bubble_column_reactor", SuSyRecipeTypes.BUBBLE_COLUMN_REACTOR_RECIPES, reactorTankSizeFunction);

    // ==================================================================
    // Phase 4b — Steam machines (bronze LP / steel HP pairs)
    // ==================================================================
    // 1.12.2 registerSimpleSteamMTE calls -> GTCEu registerSimpleSteamMachines
    // (SimpleSteamMachine). Each field is a Pair<low-pressure bronze, high-pressure
    // steel> MachineDefinition.
    public static final it.unimi.dsi.fastutil.Pair<MachineDefinition, MachineDefinition> STEAM_VULCANIZING_PRESS =
            GTMachineUtils.registerSimpleSteamMachines(REGISTRATE, "vulcanizing_press",
                    SuSyRecipeTypes.VULCANIZATION_RECIPES);
    public static final it.unimi.dsi.fastutil.Pair<MachineDefinition, MachineDefinition> STEAM_ROASTER =
            GTMachineUtils.registerSimpleSteamMachines(REGISTRATE, "roaster", SuSyRecipeTypes.ROASTER_RECIPES);
    public static final it.unimi.dsi.fastutil.Pair<MachineDefinition, MachineDefinition> STEAM_MIXER =
            GTMachineUtils.registerSimpleSteamMachines(REGISTRATE, "mixer",
                    com.gregtechceu.gtceu.common.data.GTRecipeTypes.MIXER_RECIPES);
    public static final it.unimi.dsi.fastutil.Pair<MachineDefinition, MachineDefinition> STEAM_VACUUM_CHAMBER =
            GTMachineUtils.registerSimpleSteamMachines(REGISTRATE, "vacuum_chamber",
                    SuSyRecipeTypes.VACUUM_CHAMBER_RECIPES);
    public static final it.unimi.dsi.fastutil.Pair<MachineDefinition, MachineDefinition> STEAM_BATCH_REACTOR =
            GTMachineUtils.registerSimpleSteamMachines(REGISTRATE, "batch_reactor",
                    SuSyRecipeTypes.BATCH_REACTOR_RECIPES);
    public static final it.unimi.dsi.fastutil.Pair<MachineDefinition, MachineDefinition> STEAM_DISTILLER =
            GTMachineUtils.registerSimpleSteamMachines(REGISTRATE, "distiller",
                    com.gregtechceu.gtceu.common.data.GTRecipeTypes.DISTILLERY_RECIPES);

    // TODO)) Phase 4b generators — these are bespoke RecipeLogic machines, not config:
    //  - FuelCellGenerator (EV/IV): temperature simulation — preheat with hot gas to a
    //    threshold before it generates; gate checkRecipe on temperature.
    //  - SuSySingleCombustion (LV/MV/HV): lubricant + coolant tanks, drains per N ops,
    //    lubricant boosts duration; gate on sufficient fluids + not energy-full.
    // Both need a custom SimpleGeneratorMachine subclass with @Persisted state + a
    // gating RecipeLogic (and their fluids — SuSyFluidFilters/lubricant/coolant are
    // Phase 5). Port as bespoke machines; do NOT use registerSimpleGenerator.
    //  - Steam boilers (SuSyCoalBoiler/SuSyLiquidBoiler/LargeBoiler/LargeHammer) and
    //    pseudo-multi latex collectors port with the steam/boiler base + multiblock
    //    scope (4c).

    public static void init() {}

    private SusyMachines() {}

    // ==================================================================
    // Registration helpers (shared with Phases 4b/4c)
    // ==================================================================

    /**
     * Registers one machine per tier, named {@code <tier_vn>_<name>} (e.g.
     * {@code lv_roaster}). Mirrors {@code GTMachineUtils.registerTieredMachines}
     * but scoped to the SuSy {@link GTRegistrate}.
     */
    public static MachineDefinition[] registerTieredMachines(String name,
            BiFunction<IMachineBlockEntity, Integer, MetaMachine> factory,
            BiFunction<Integer, MachineBuilder<MachineDefinition, ?>, MachineDefinition> builder, int... tiers) {
        MachineDefinition[] definitions = new MachineDefinition[GTValues.TIER_COUNT];
        for (int tier : tiers) {
            var register = REGISTRATE
                    .machine(VN[tier].toLowerCase(Locale.ROOT) + "_" + name, holder -> factory.apply(holder, tier))
                    .tier(tier);
            definitions[tier] = builder.apply(tier, register);
        }
        return definitions;
    }

    /**
     * One {@link SimpleTieredMachine} per tier running {@code recipeType} with
     * stock non-perfect overclocking. Mirrors {@code GTMachineUtils.registerSimpleMachines}.
     * TODO)) Phase 6: point {@code workableTieredHullModel} at the SuSy machine
     * overlay textures (1.12.2 {@code SusyTextures}); GTCEu's hull model is the
     * placeholder.
     */
    public static MachineDefinition[] registerSimpleMachines(String name,
            GTRecipeType recipeType, Int2IntFunction tankScalingFunction, int... tiers) {
        return registerTieredMachines(name,
                (holder, tier) -> new SimpleTieredMachine(holder, tier, tankScalingFunction),
                (tier, builder) -> simpleTieredBuilder(name, recipeType, tankScalingFunction, tier, builder),
                tiers);
    }

    /** {@code registerSimpleMachines} across {@link #ELECTRIC_TIERS}. */
    public static MachineDefinition[] registerSimpleMachines(String name,
            GTRecipeType recipeType, Int2IntFunction tankScalingFunction) {
        return registerSimpleMachines(name, recipeType, tankScalingFunction, ELECTRIC_TIERS);
    }

    /**
     * One {@link CatalystSimpleMachine} per tier — the recipe modifier lives in the
     * machine's {@code doModifyRecipe} (catalyst gating + discount + yield on top of
     * the base non-perfect OC).
     */
    public static MachineDefinition[] registerCatalystMachines(String name,
            GTRecipeType recipeType, Int2IntFunction tankScalingFunction, int... tiers) {
        return registerTieredMachines(name,
                (holder, tier) -> new CatalystSimpleMachine(holder, tier, tankScalingFunction),
                (tier, builder) -> simpleTieredBuilder(name, recipeType, tankScalingFunction, tier, builder),
                tiers);
    }

    /**
     * One {@link ContinuousSimpleMachine} per tier — catalyst behaviour plus SuSy
     * pure-parallel batching, both applied in the machine's {@code doModifyRecipe}.
     */
    public static MachineDefinition[] registerContinuousMachines(String name,
            GTRecipeType recipeType, Int2IntFunction tankScalingFunction) {
        return registerTieredMachines(name,
                (holder, tier) -> new ContinuousSimpleMachine(holder, tier, tankScalingFunction),
                (tier, builder) -> simpleTieredBuilder(name, recipeType, tankScalingFunction, tier, builder),
                ELECTRIC_TIERS);
    }

    /** Shared builder body for simple/catalyst/continuous tiered machines. */
    private static MachineDefinition simpleTieredBuilder(String name, GTRecipeType recipeType,
            Int2IntFunction tankScalingFunction, int tier, MachineBuilder<MachineDefinition, ?> builder) {
        return builder
                .langValue("%s %s %s".formatted(GTValues.VLVH[tier], FormattingUtil.toEnglishName(name),
                        GTValues.VLVT[tier]))
                .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(SuSyValues.susyId(name), recipeType))
                .rotationState(RotationState.NON_Y_AXIS)
                .recipeType(recipeType)
                .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
                .workableTieredHullModel(GTCEu.id("block/machines/" + name))
                .tooltips(GTMachineUtils.workableTiered(tier, GTValues.V[tier], GTValues.V[tier] * 64, recipeType,
                        tankScalingFunction.applyAsInt(tier), true))
                .register();
    }

    /** Drum (fluid storage barrel). Mirrors {@code GTMachineUtils.registerDrum}. */
    public static MachineDefinition registerDrum(String name, Material material, int capacity, String lang) {
        boolean wooden = material.hasProperty(PropertyKey.WOOD);
        return REGISTRATE
                .machine(name, MachineDefinition::new,
                        holder -> new DrumMachine(holder, material, capacity),
                        MetaMachineBlock::new,
                        (holder, prop) -> DrumMachineItem.create(holder, prop, material),
                        MetaMachineBlockEntity::new)
                .langValue(lang)
                .rotationState(RotationState.NONE)
                .simpleModel(GTCEu.id("block/machine/template/drum/" + (wooden ? "wooden" : "metal") + "_drum"))
                .tooltips(Component.translatable("gtceu.machine.quantum_tank.tooltip"),
                        Component.translatable("gtceu.universal.tooltip.fluid_storage_capacity",
                                FormattingUtil.formatNumbers(capacity)))
                .paintingColor(wooden ? 0xFFFFFF : material.getMaterialRGB())
                .itemColor((s, i) -> wooden ? 0xFFFFFF : material.getMaterialRGB())
                .register();
    }

    /**
     * Plastic can (drum with the SuSy plastic-can look). Uses a stock drum model
     * for now.
     * TODO)) Phase 6: point this at a SuSy {@code plastic_can} model + texture
     * (1.12.2 {@code SusyTextures.PLASTIC_CAN}).
     */
    public static MachineDefinition registerPlasticCan(String name, Material material, int capacity, String lang) {
        return REGISTRATE
                .machine(name, MachineDefinition::new,
                        holder -> new PlasticCanMachine(holder, material, capacity),
                        MetaMachineBlock::new,
                        (holder, prop) -> DrumMachineItem.create(holder, prop, material),
                        MetaMachineBlockEntity::new)
                .langValue(lang)
                .rotationState(RotationState.NONE)
                .simpleModel(GTCEu.id("block/machine/template/drum/metal_drum"))
                .tooltips(Component.translatable("gtceu.machine.quantum_tank.tooltip"),
                        Component.translatable("gtceu.universal.tooltip.fluid_storage_capacity",
                                FormattingUtil.formatNumbers(capacity)))
                .paintingColor(material.getMaterialRGB())
                .itemColor((s, i) -> material.getMaterialRGB())
                .register();
    }

    /** Crate (item storage). Mirrors {@code GTMachineUtils.registerCrate}. */
    public static MachineDefinition registerCrate(String name, Material material, int capacity, String lang) {
        boolean wooden = material.hasProperty(PropertyKey.WOOD);
        return REGISTRATE
                .machine(name, holder -> new CrateMachine(holder, material, capacity))
                .langValue(lang)
                .rotationState(RotationState.NONE)
                .tooltips(Component.translatable("gtceu.universal.tooltip.item_storage_capacity", capacity))
                .modelProperty(GTMachineModelProperties.IS_TAPED, false)
                .model(GTMachineModels.createCrateModel(wooden))
                .paintingColor(wooden ? 0xFFFFFF : material.getMaterialRGB())
                .itemColor((s, t) -> wooden ? 0xFFFFFF : material.getMaterialRGB())
                .register();
    }

    /** Locked loot crate (sealed storage). See {@link LockedCrateMachine}. */
    public static MachineDefinition registerLockedCrate(String name, Material material, int capacity, String lang) {
        boolean wooden = material.hasProperty(PropertyKey.WOOD);
        return REGISTRATE
                .machine(name, holder -> new LockedCrateMachine(holder, material, capacity))
                .langValue(lang)
                .rotationState(RotationState.NONE)
                .tooltips(Component.translatable("gtceu.universal.tooltip.item_storage_capacity", capacity))
                .modelProperty(GTMachineModelProperties.IS_TAPED, true)
                .model(GTMachineModels.createCrateModel(wooden))
                .paintingColor(wooden ? 0xFFFFFF : material.getMaterialRGB())
                .itemColor((s, t) -> wooden ? 0xFFFFFF : material.getMaterialRGB())
                .register();
    }

    /** Multi-slot fluid import/export hatch across the given tiers (LV..HV). */
    private static MachineDefinition[] registerMultiFluidHatch(String name, String displayName, IO io, int slots,
                                                               PartAbility ability) {
        String pipeOverlay = slots >= 9 ? "overlay_pipe_9x" : "overlay_pipe_4x";
        String ioOverlay = io == IO.OUT ? "overlay_fluid_hatch_output" : "overlay_fluid_hatch_input";
        String emissiveOverlay = io == IO.OUT ? "overlay_pipe_out_emissive" : "overlay_pipe_in_emissive";
        return registerTieredMachines(name,
                (holder, tier) -> new FluidHatchPartMachine(holder, tier, io,
                        4 * (tier + 1) * (tier + 1) * 1000, slots),
                (tier, builder) -> builder
                        .langValue(VNF[tier] + " " + displayName)
                        .rotationState(RotationState.ALL)
                        .colorOverlayTieredHullModel(ioOverlay, pipeOverlay, emissiveOverlay)
                        .abilities(ability)
                        .modelProperty(IS_FORMED, false)
                        .tooltips(Component.translatable("gtceu.universal.tooltip.fluid_storage_capacity_mult", slots,
                                FormattingUtil.formatNumbers(
                                        FluidHatchPartMachine.getTankCapacity(4 * (tier + 1) * (tier + 1) * 1000, tier))))
                        .allowCoverOnFront(true)
                        .register(),
                MULTI_FLUID_HATCH_TIERS);
    }
}
