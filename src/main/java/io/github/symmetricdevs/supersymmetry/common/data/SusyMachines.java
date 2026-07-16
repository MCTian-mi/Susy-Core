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
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
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
import com.gregtechceu.gtceu.common.data.*;
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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.DistillationTowerMachine;
import io.github.symmetricdevs.supersymmetry.api.recipes.logic.SuSyParallelLogic;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.EvaporationPoolMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.GreenhouseMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.HeatRadiatorMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.LowPressureCryogenicDistillationPlant;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.MagneticRefrigeratorMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.MetallurgicalConverterLogic;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.MiningDrillMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.MixerSettlerMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.MixerSettlerV1Machine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.RailroadEngineeringStationMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.ReverberatoryFurnaceMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.SingleColumnCryogenicDistillationPlantMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.SuSyOrientationFixupMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.SuSySinteringOvenMachine;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_PTFE_INERT;
import static com.gregtechceu.gtceu.common.data.GTBlocks.MACHINE_CASING_ULV;
import io.github.symmetricdevs.supersymmetry.api.pattern.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.BallMillMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.SuSyRotationGeneratorMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.AttritionScrubberMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.CurtainCoaterMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.DumperMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.FlareStackMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.HotIsostaticPressMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.SmokeStackMachine;

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
                    .overlayTieredHullModel(GTCEu.id("block/machine/part/energy_output_hatch_4a"))
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
                    .overlayTieredHullModel(GTCEu.id("block/machine/part/energy_output_hatch_16a"))
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
                    .overlayTieredHullModel(GTCEu.id("block/machine/part/energy_input_hatch_64a"))
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
                    .overlayTieredHullModel(GTCEu.id("block/machine/part/energy_output_hatch_64a"))
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
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/machine/overlay_item_hatch_input"),
                    GTCEu.id("block/overlay/machine/overlay_pipe"),
                    GTCEu.id("block/overlay/machine/overlay_pipe_in_emissive"))
            .tooltips(Component.translatable("gtceu.machine.item_bus.import.tooltip"))
            .allowCoverOnFront(true)
            .register();
    public static final MachineDefinition PRIMITIVE_ITEM_EXPORT = REGISTRATE
            .machine("primitive_item_export", holder -> new ItemBusPartMachine(holder, 0, IO.OUT))
            .langValue("Primitive Item Export Bus")
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.EXPORT_ITEMS)
            .modelProperty(IS_FORMED, false)
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/machine/overlay_item_hatch_output"),
                    GTCEu.id("block/overlay/machine/overlay_pipe"),
                    GTCEu.id("block/overlay/machine/overlay_pipe_out_emissive"))
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

    // ==================================================================
    // Phase 4c — Multiblock controllers (Bucket A1: GT-casing simple recipe multis)
    // Ported from 1.12.2 RecipeMapMultiblockController.createStructurePattern onto the
    // GTCEu-Modern .multiblock/.pattern idiom. Plain WorkableElectricMultiblockMachine
    // controllers (no bespoke class) — recipe behaviour via .recipeModifier. Front
    // overlays are GTCEu placeholders pending the Phase 6 SusyTextures port.
    // ==================================================================

    // ---- coking_tower (perfect OC (1.12.2 MultiblockRecipeLogic(this,true))) ----
    public static final MultiblockMachineDefinition COKING_TOWER = REGISTRATE
            .multiblock("coking_tower", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .recipeType(SuSyRecipeTypes.COKING_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_PERFECT)
            .pattern(definition -> FactoryBlockPattern
                    .start(RelativeDirection.RIGHT, RelativeDirection.FRONT, RelativeDirection.UP)
                    .aisle(" CCSCCF", "PCP PCP")
                    .aisle(" CFCFCF", "PCP PCP")
                    .aisle(" CFCFCF", "PCP PCP")
                    .aisle(" CFCFCF", "PPP PPP")
                    .aisle(" CFCFCF", "  P   P")
                    .aisle(" CCCCCF", "  P   P")
                    .aisle("  FFFFF", "  P   P")
                    .aisle("  FF FF", "  P   P")
                    .aisle("  FF FF", "  P   P")
                    .aisle("  FF FF", "  P   P")
                    .aisle("  FF FF", "       ")
                    .aisle("  FF FF", "       ")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('P', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                    .where('F', Predicates.frames(GTMaterials.Steel))
                    .where('C', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()).setMinGlobalLimited(20)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes())))
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ---- catalytic_reformer (muffler) ----
    public static final MultiblockMachineDefinition CATALYTIC_REFORMER = REGISTRATE
            .multiblock("catalytic_reformer", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false) // 1.12.2 RecipeMapMultiblockController default (not overridden)
            .appearanceBlock(() -> GTBlocks.CASING_STAINLESS_CLEAN.get())
            .recipeType(SuSyRecipeTypes.CATALYTIC_REFORMER_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("F   F", "XXXPX", "XXXPX", "XXXPX")
                    .aisle("     ", "XXXPX", "X###M", "XXXPX")
                    .aisle("F   F", "XXXPX", "XSXPX", "XXXPX")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('X', Predicates.blocks(GTBlocks.CASING_STAINLESS_CLEAN.get(), GTBlocks.CASING_TITANIUM_STABLE.get())
                            .setMinGlobalLimited(24)
                            // 1.12.2 autoAbilities(energyIn, maintenance, itemIn, itemOut, fluidIn, fluidOut, muffler)
                            // = (T,T,T,T,T,T,F): energy-in + item in/out + fluid in/out, no energy-out, no muffler
                            .or(Predicates.autoAbilities(definition.getRecipeTypes(), true, false, true, true, true, true))
                            // ... plus maintenance (no muffler/parallel); muffler is its own 'M' hatch below
                            .or(Predicates.autoAbilities(true, false, false)))
                    .where('P', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get(), GTBlocks.CASING_TITANIUM_PIPE.get()))
                    .where('F', Predicates.frames(GTMaterials.StainlessSteel, GTMaterials.Titanium))
                    .where('M', Predicates.abilities(PartAbility.MUFFLER))
                    .where(' ', Predicates.any())
                    .where('#', Predicates.air())
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
                    GTCEu.id("block/multiblock/blast_furnace")) // TODO)) Phase 6: SusyTextures.CATALYTIC_REFORMER_OVERLAY
            .register();

    // ---- electrolytic_cell (perfect OC) ----
    public static final MultiblockMachineDefinition ELECTROLYTIC_CELL = REGISTRATE
            .multiblock("electrolytic_cell", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.ELECTROLYTIC_CELL_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("CCCCC", "CCCCC", "CCCCC")
                    .aisle("CCCCC", "CPPPC", "CPPPC")
                    .aisle("CCCCC", "CPPPC", "CPPPC")
                    .aisle("CCCCC", "CCSCC", "CCCCC")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('P', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                    .where('C', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()).setMinGlobalLimited(30)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes())))
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ---- fermentation_vat (muffler) ----
public static final MultiblockMachineDefinition FERMENTATION_VAT = REGISTRATE
        .multiblock("fermentation_vat", WorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.NON_Y_AXIS)
        .appearanceBlock(MACHINE_CASING_ULV)
        .recipeType(SuSyRecipeTypes.FERMENTATION_VAT_RECIPES)
        .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
        .pattern(definition -> FactoryBlockPattern.start()
                .aisle("     ", "     ", " XXX ", " XXX ", " XXX ", "     ")
                .aisle(" F F ", " XXX ", "X###X", "X###X", "X###X", " XXX ")
                .aisle("     ", " XXX ", "X###X", "X###X", "X###X", " XMX ")
                .aisle(" F F ", " XXX ", "X###X", "X###X", "X###X", " XXX ")
                .aisle("     ", "     ", " XXX ", " XSX ", " XXX ", "     ")
                .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                .where('X', Predicates.blocks(MACHINE_CASING_ULV.get())
                        .setMinGlobalLimited(40)
                        .or(Predicates.autoAbilities(definition.getRecipeTypes(), true, true, true, true, true,
                                true)))
                .where('F', Predicates.frames(GTMaterials.Steel))
                .where('M', Predicates.abilities(PartAbility.MUFFLER))
                .where(' ', Predicates.any())
                .where('#', Predicates.air())
                .build())
        .workableCasingModel(GTCEu.id("block/casings/voltage/ulv/side"),
                GTCEu.id("block/multiblock/pyrolyse_oven"))
        .register();

    // ---- large_weapons_factory ----
    public static final MultiblockMachineDefinition LARGE_WEAPONS_FACTORY = REGISTRATE
            .multiblock("large_weapons_factory", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.LARGE_WEAPONS_FACTORY_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> {
                TraceabilityPredicate casingPredicate = Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                        .setMinGlobalLimited(4);
                return FactoryBlockPattern.start()
                        .aisle("FBF", "FFF")
                        .aisle("CBC", " A ")
                        .aisle("CBC", " A ")
                        .aisle("CBC", "EAE")
                        .aisle("CBC", "EAE")
                        .aisle("CBC", " A ")
                        .aisle("CBC", " A ")
                        .aisle("DDD", "DSD")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('A', casingPredicate)
                        .where('B', Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                        .where('C', Predicates.frames(GTMaterials.Steel)
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, true, false, false,
                                        false, false)
                                        .setExactLimit(1)))
                        .where('D', casingPredicate
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, false, true, false,
                                        true, false)))
                        .where('E', casingPredicate
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(), true, false, false, false,
                                        false, false)))
                        .where('F', casingPredicate
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, false, false, true,
                                        false, false)))
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/blast_furnace")) // TODO Phase 6: real overlay is
                                                                // SusyTextures.LARGE_WEAPONS_FACTORY_OVERLAY
            .register();

    // ---- multi_stage_flash_distiller (perfect OC) ----
    public static final MultiblockMachineDefinition MULTI_STAGE_FLASH_DISTILLER = REGISTRATE
            .multiblock("multi_stage_flash_distiller", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .recipeType(SuSyRecipeTypes.MULTI_STAGE_FLASH_DISTILLATION_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_PERFECT)
            .tooltipBuilder((stack, components) -> components.add(Component
                    .translatable("gtceu.machine.perfect_oc")
                    .withStyle(TooltipHelper.RAINBOW_HSL_SLOW)))
            .pattern(definition -> {
                // Casing (min 70) and the shared maintenance+energy predicate, reused
                // across several chars exactly as in the 1.12.2 createStructurePattern.
                TraceabilityPredicate casingPredicate = Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                        .setMinGlobalLimited(70);
                TraceabilityPredicate maintenanceEnergy = Predicates
                        .abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2)
                        .setPreviewCount(1)
                        .or(Predicates.abilities(PartAbility.MAINTENANCE).setMinGlobalLimited(1).setMaxGlobalLimited(1));
                return FactoryBlockPattern.start()
                        .aisle(" EEEB", " BEEB", " BEEB", " EEEB", "  BBB")
                        .aisle(" AAA ", " B#B ", " B#BB", " AAA ", "  B  ")
                        .aisle("CAAAB", "CAAAB", "CAAAB", "CAAAC", "CCBCC")
                        .aisle(" DDD ", " DDD ", " DDD ", " DDD ", "  B  ")
                        .aisle(" DDD ", " D#D ", " D#D ", " DDD ", "  B  ")
                        .aisle("CDDDC", "CDDDC", "CDDDC", "CDDDC", "CCBCC")
                        .aisle(" AAA ", " BAB ", " BAB ", " AAA ", "  B  ")
                        .aisle(" AAA ", " B#B ", " B#B ", " AAA ", "  B  ")
                        .aisle("CAAAC", "CAAAC", "CAAAC", "CAAAC", "CCBCC")
                        .aisle(" DDD ", " DDD ", " DDD ", " DDD ", "  B  ")
                        .aisle(" DDD ", " D#D ", " D#D ", " DDD ", "  B  ")
                        .aisle("CDDDC", "CDDDC", "CDDDC", "CDDDC", "CCBCC")
                        .aisle(" AAAB", " BABB", " BABB", " AAAB", "  BBB")
                        .aisle(" AAAC", " B#BC", " B#BC", " AAAC", "  BCC")
                        .aisle(" FFF ", " FSF ", " FFF ", " FFF ", "  B  ")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('B', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                        .where('C', Predicates.frames(GTMaterials.Steel))
                        .where('A', casingPredicate
                                .or(maintenanceEnergy))
                        .where('D', Predicates.blocks(GTBlocks.CASING_STAINLESS_CLEAN.get())
                                .or(maintenanceEnergy))
                        .where('E', casingPredicate
                                .or(maintenanceEnergy)
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS)
                                        .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS))))
                        .where('F', casingPredicate
                                .or(maintenanceEnergy)
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS)
                                        .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS))))
                        .where(' ', Predicates.any())
                        .where('#', Predicates.air())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ---- ore_sorter ----
    public static final MultiblockMachineDefinition ORE_SORTER = REGISTRATE
            .multiblock("ore_sorter", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.ORE_SORTER_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle(" C C ", " C C ", " C C ", " D D ")
                    .aisle("     ", "     ", "     ", " D D ")
                    .aisle("ABBBA", "ABBBA", "ABBBA", " D D ")
                    .aisle("ABBBA", "B###B", "ABBBA", " D D ")
                    .aisle("ABSBA", "ABBBA", "ABBBA", " D D ")
                    .where('S', Predicates.controller(Predicates.blocks(definition.get())))
                    .where('A', Predicates.frames(GTMaterials.Steel))
                    .where('B', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .setMinGlobalLimited(16)
                            // 1.12.2 autoAbilities(true,true,true,true,false,false,false) =
                            //   energyIn + maintenance + itemIn + itemOut (no fluids, no muffler)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes(), true, false, true, true, false, false))
                            .or(Predicates.autoAbilities(true, false, false)))
                    .where('C', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get())
                            // 1.12.2 autoAbilities(false,false,false,false,true,true,false) =
                            //   fluidIn + fluidOut only
                            .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, false, false, false, true, true)))
                    .where('D', Predicates.frames(GTMaterials.Aluminium))
                    .where(' ', Predicates.any())
                    .where('#', Predicates.air())
                    .build())
            // TODO)) Phase 6: real front overlay is SusyTextures.ORE_SORTER_OVERLAY
            // (1.12.2 ore_sorter overlay); blast_furnace is the placeholder.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ---- polymerization_tank (perfect OC) ----
public static final MultiblockMachineDefinition POLYMERIZATION_TANK = REGISTRATE
        .multiblock("polymerization_tank", WorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.NON_Y_AXIS)
        .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
        .recipeType(SuSyRecipeTypes.POLYMERIZATION_RECIPES)
        .recipeModifier(GTRecipeModifiers.OC_PERFECT)
        .pattern(definition -> FactoryBlockPattern.start()
                .aisle("F F", "XXX", "XXX", "XXX", "XXX")
                .aisle("   ", "XPX", "XPX", "XPX", "XPX")
                .aisle("F F", "XSX", "XXX", "XXX", "XXX")
                .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                .where('F', Predicates.frames(GTMaterials.Steel))
                .where('P', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                .where('X', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()).setMinGlobalLimited(20)
                        .or(Predicates.autoAbilities(definition.getRecipeTypes(), true, true, false, false, false, false))
                        .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, false, false, false, true, false).setMaxGlobalLimited(3))
                        .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, false, false, false, false, true).setMaxGlobalLimited(2))
                        .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, false, true, true, false, false).setMaxGlobalLimited(1)))
                .build())
        // TODO)) Phase 6: replace blast_furnace placeholder overlay with the real
        // polymerization_tank overlay (1.12.2 SusyTextures.POLYMERIZATION_TANK_OVERLAY).
        .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"), GTCEu.id("block/multiblock/blast_furnace"))
        .register();

    // ---- pressure_swing_adsorber ----
public static final MultiblockMachineDefinition PRESSURE_SWING_ADSORBER = REGISTRATE
        .multiblock("pressure_swing_adsorber", WorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.NON_Y_AXIS)
        .appearanceBlock(() -> GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
        .recipeType(SuSyRecipeTypes.PRESSURE_SWING_ADSORBER_RECIPES)
        .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
        .pattern(definition -> FactoryBlockPattern.start()
                .aisle("AAA", "AAA", "AAA", "AAA")
                .aisle("AAA", "ABA", "ABA", "AAA")
                .aisle("AAA", "ASA", "AAA", "AAA")
                .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                .where('A', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
                        .setMinGlobalLimited(25)
                        .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                        .or(Predicates.autoAbilities(true, false, false)))
                .where('B', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                .build())
        // TODO(Phase 6): swap placeholder overlay for SusyTextures.PRESSURE_SWING_ABSORBER_OVERLAY
        .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_frost_proof"),
                GTCEu.id("block/multiblock/blast_furnace"))
        .register();

    // ---- quencher (complex ability layout) ----
    public static final MultiblockMachineDefinition QUENCHER = REGISTRATE
            .multiblock("quencher", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_STAINLESS_CLEAN.get())
            .recipeType(SuSyRecipeTypes.QUENCHER_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> {
                // Common casing constraint (1.12.2: states(getCasingState()).setMinGlobalLimited(15)).
                TraceabilityPredicate casingPredicate = Predicates.blocks(GTBlocks.CASING_STAINLESS_CLEAN.get())
                        .setMinGlobalLimited(15);
                return FactoryBlockPattern.start()
                        .aisle("GGBBF", "GG   ", "     ")
                        .aisle("GABC ", "GG D ", " CDD ")
                        .aisle("GGB F", "GG   ", "     ")
                        .aisle("  AAA", "  AAA", "     ")
                        .aisle("  ASA", "  AAA", "     ")
                        .where('S', Predicates.controller(Predicates.blocks(definition.get())))
                        .where('A', casingPredicate)
                        .where('B', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                        .where('C', Predicates.blocks(GTBlocks.CASING_STAINLESS_STEEL_GEARBOX.get()))
                        .where('D', Predicates.frames(GTMaterials.StainlessSteel))
                        // 1.12.2 autoAbilities 7-arg order: (energyIn, maintenance, itemIn, itemOut,
                        // fluidIn, fluidOut, muffler). 'F' = exactly one FLUID_OUT hatch OR exactly
                        // one FLUID_IN hatch (coolant in / exhaust out through the pipe ring).
                        .where('F', Predicates
                                .autoAbilities(definition.getRecipeTypes(), false, false, false, false, false, true)
                                .setExactLimit(1)
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, false, false, false,
                                        true, false)
                                        .setExactLimit(1)))
                        // 'G' = casing + energyIn + maintenance + itemIn + itemOut (no muffler, no fluid).
                        .where('G', casingPredicate
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(), true, false, true, true,
                                        false, false))
                                .or(Predicates.autoAbilities(true, false, false)))
                        .where(' ', Predicates.any())
                        .build();
            })
            // TODO)) Phase 6: front overlay placeholder is blast_furnace; real 1.12.2 overlay is
            // SusyTextures.QUENCHER_OVERLAY ("machines/multiblocks/quencher").
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ---- reaction_furnace (muffler) ----
    public static final MultiblockMachineDefinition REACTION_FURNACE = REGISTRATE
            .multiblock("reaction_furnace", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_INVAR_HEATPROOF.get())
            .recipeType(SuSyRecipeTypes.REACTION_FURNACE_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("     ", "     ", " P P ", " P P ", " P P ")
                    .aisle("F   F", "FBBBF", "XPXPX", "XXXXX", " P P ")
                    .aisle("     ", "XBBBX", "XP#PX", "XPMPX", " P P ")
                    .aisle("F   F", "FBBBF", "XXSXX", "XXXXX", "     ")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('X', Predicates.blocks(GTBlocks.CASING_INVAR_HEATPROOF.get()).setMinGlobalLimited(13)
                            // 1.12.2 autoAbilities(true, true, true, true, true, true, false):
                            // (energyIn, maintenance, itemIn, itemOut, fluidIn, fluidOut, muffler=false).
                            // Muffler is handled by the explicit 'M' below, so it is not re-added here.
                            .or(Predicates.autoAbilities(definition.getRecipeTypes(), true, false, true, true, true, true))
                            .or(Predicates.autoAbilities(true, false, false)))
                    .where('P', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                    .where('F', Predicates.frames(GTMaterials.Invar))
                    .where('M', Predicates.abilities(PartAbility.MUFFLER))
                    .where('B', Predicates.blocks(GTBlocks.FIREBOX_STEEL.get()))
                    .where('#', Predicates.air())
                    .where(' ', Predicates.any())
                    .build())
            // TODO)) Phase 6: swap the front overlay to the real SuSy 'pyrolyse_oven'
            // overlay (1.12.2 getFrontOverlay() -> Textures.PYROLYSE_OVEN_OVERLAY);
            // blast_furnace is the placeholder. Base = HEAT_PROOF_CASING.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_heatproof"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ---- rotary_kiln ----
    public static final MultiblockMachineDefinition ROTARY_KILN = REGISTRATE
            .multiblock("rotary_kiln", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.ROTARY_KILN_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> {
                // Different characters use common constraints. Copied from GCyM (1.12.2).
                TraceabilityPredicate casingPredicate = Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                        .setMinGlobalLimited(8);
                TraceabilityPredicate maintenance = Predicates.abilities(PartAbility.MAINTENANCE)
                        .setMaxGlobalLimited(1);
                return FactoryBlockPattern.start()
                        .aisle("A    A    A", "A    A    A", "L    A    R", "LCCCCMCCCCR", "L    A    R")
                        .aisle("A    A    A", "A    A    A", "LCCCCMCCCCR", "L#########R", "LCCCCMCCCCR")
                        .aisle("A    A    A", "A    A    A", "L    A    R", "LCCCCSCCCCR", "L    A    R")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('A', Predicates.frames(GTMaterials.Steel))
                        .where('C', Predicates.blocks(GTBlocks.LIGHT_CONCRETE.get()))
                        .where('L', casingPredicate
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, false, true, false, false, true))
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(), true, false, false, false, false, false).setMinGlobalLimited(0))
                                .or(maintenance))
                        .where('R', casingPredicate
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, false, false, true, true, false))
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(), true, false, false, false, false, false).setMinGlobalLimited(0))
                                .or(maintenance))
                        .where('M', casingPredicate
                                .or(maintenance))
                        .where(' ', Predicates.any())
                        .where('#', Predicates.air())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/blast_furnace")) // TODO Phase 6: real overlay is machines/multiblocks/rotary_kiln (SusyTextures.ROTARY_KILN_OVERLAY)
            .register();

    // ---- rotary_kiln_v2 ----
    public static final MultiblockMachineDefinition ROTARY_KILN_V2 = REGISTRATE
            .multiblock("rotary_kiln_v2", SuSyOrientationFixupMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.ROTARY_KILN_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> {
                TraceabilityPredicate casing = Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                        .setMinGlobalLimited(6);
                TraceabilityPredicate maintenance = Predicates.abilities(PartAbility.MAINTENANCE)
                        .setExactLimit(1);
                return FactoryBlockPattern.start()
                        .aisle("F         F", "LD   B   DR", "LDCCCBCCCDR", "GD   B   DG")
                        .aisle("     F     ", "LDCCCBCCCDR", "L#########R", "LDCCCBCCCDR")
                        .aisle("F         F", "LD   B   DR", "LDCCCSCCCDR", "GD   B   DG")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('B', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()))
                        .where('C', Predicates.blocks(SusyBlocks.WEAR_RESISTANT_LINED_MILL_SHELL.get()))
                        .where('D', SuSyPredicates.axialOrientation(
                                SusyBlocks.STEEL_GIRTH_GEAR_TOOTH.get(), RelativeDirection.LEFT))
                        .where('F', Predicates.frames(GTMaterials.Steel))
                        .where('G', Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                        .where('L', casing
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                        false, false, true, false, false, true))
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                        true, false, false, false, false, false).setMinGlobalLimited(0))
                                .or(maintenance))
                        .where('R', casing
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                        false, false, false, true, true, false))
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                        true, false, false, false, false, false).setMinGlobalLimited(0))
                                .or(maintenance))
                        .where(' ', Predicates.any())
                        .where('#', Predicates.air())
                        .build();
            })
            // TODO)) Phase 6: hide the rotating shell/gear blocks and restore the
            // Gecko-equivalent drum/gear animation and real rotary-kiln overlay.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ---- scrap_recycler (GT casings only; register even though in rocket pkg) ----
    public static final MultiblockMachineDefinition SCRAP_RECYCLER = REGISTRATE
            .multiblock("scrap_recycler", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_TITANIUM_STABLE.get())
            .recipeType(SuSyRecipeTypes.SCRAP_RECYCLER_RECIPES)
            // 1.12.2 used `new MultiblockRecipeLogic(this, true)` -> perfect overclocking.
            .recipeModifier(GTRecipeModifiers.OC_PERFECT)
            .pattern(definition -> {
                TraceabilityPredicate casing = Predicates.blocks(GTBlocks.CASING_TITANIUM_STABLE.get());
                return FactoryBlockPattern.start()
                        .aisle(" CCC ", "CCCCC", "COOOC", "CCCCC", " CCC ")
                        .aisle(" CCC ", "PAAAP", "PAAAP", "PAAAP", " CDC ")
                        .aisle(" CCC ", "PAAAP", "PAAAP", "PAAAP", " CDC ")
                        .aisle(" CCC ", "PAAAP", "PAAAP", "PAAAP", " CDC ")
                        .aisle(" CCC ", "PAAAP", "PAAAP", "PAAAP", " CDC ")
                        .aisle(" CCC ", "PAAAP", "PAAAP", "PAAAP", " CDC ")
                        .aisle(" CCC ", "CISIC", "CCCCC", "PAAAP", " CCC ")
                        .where(' ', Predicates.any())
                        .where('A', Predicates.air())
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('P', Predicates.blocks(GTBlocks.CASING_TITANIUM_PIPE.get()))
                        .where('C', casing)
                        // 1.12.2 autoAbilities(false, true, true, false, false, false, false)
                        //   = itemImport + maintenance (no energy/muffler).
                        .where('I', casing.or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                        false, false, true, false, false, false))
                                .or(Predicates.autoAbilities(true, false, false)))
                        // 1.12.2 autoAbilities(false, true, false, true, false, false, false)
                        //   = itemExport + maintenance.
                        .where('O', casing.or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                        false, false, false, true, false, false))
                                .or(Predicates.autoAbilities(true, false, false)))
                        // 1.12.2 autoAbilities(true, true, false, false, false, false, false)
                        //   = energyIn + maintenance.
                        .where('D', casing.or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                        true, false, false, false, false, false))
                                .or(Predicates.autoAbilities(true, false, false)))
                        .build();
            })
            // Base casing matches the 1.12.2 getBaseTexture (Textures.STABLE_TITANIUM_CASING).
            // Front overlay is a PLACEHOLDER: 1.12.2 did not override getFrontOverlay for this
            // machine, so it used the default RecipeMapMultiblockController overlay. Phase 6
            // should point this at the correct scrap-recycler / default-multiblock overlay.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_stable_titanium"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ---- condenser (NoEnergy: recipes EUt(0); verify no INPUT_ENERGY ability) ----
    public static final MultiblockMachineDefinition CONDENSER = REGISTRATE
            .multiblock("condenser", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
            .recipeType(SuSyRecipeTypes.CONDENSER_RECIPES)
            // NOENERGY: CONDENSER_RECIPES is EUt(0) and has no EU IO; the pattern exposes no
            // INPUT_ENERGY hatch (checkEnergyIn=false below), so a plain
            // WorkableElectricMultiblockMachine runs these without energy. OC_NON_PERFECT
            // matches the 1.12.2 NoEnergyMultiblockRecipeLogic's standardOverclockingLogic.
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .allowExtendedFacing(false) // 1.12.2 allowsExtendedFacing() == false
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("CCC", "CCC", "CCC", "CCC")
                    .aisle("CCC", "C C", "C C", "CCC")
                    .aisle("CCC", "CSC", "CCC", "CCC")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('C',
                            Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
                                    .setMinGlobalLimited(27)
                                    .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, true, false,
                                            false, true, true)))
                    .where(' ', Predicates.air())
                    .build())
            // base texture confirmed (frostproof casing, same as Vacuum Freezer); front overlay
            // is a PLACEHOLDER — 1.12.2 used SusyTextures.CONDENSER_OVERLAY (Phase 6 swap).
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_frost_proof"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ---- heat_exchanger (NoEnergy: recipes EUt(0); verify no INPUT_ENERGY ability) ----
    public static final MultiblockMachineDefinition HEAT_EXCHANGER = REGISTRATE
            .multiblock("heat_exchanger", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.HEAT_EXCHANGER_RECIPES)
            // NoEnergy multiblock: HEAT_EXCHANGER_RECIPES runs at EUt(0) (no setEUIO(IO.IN)),
            // so the pattern grants NO INPUT_ENERGY ability. 1.12.2 spoofed energy via
            // NoEnergyMultiblockRecipeLogic; Modern handles EUt(0) recipes natively, so a plain
            // WorkableElectricMultiblockMachine + non-perfect OC modifier is sufficient.
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("CCC", "BCB", "ACA")
                    .aisle("CCC", "CDC", "ACA")
                    .aisle("CCC", "CDC", "ACA")
                    .aisle("CCC", "CDC", "ACA")
                    .aisle("CCC", "CDC", "ACA")
                    .aisle("CCC", "CDC", "ACA")
                    .aisle("CCC", "CDC", "ACA")
                    .aisle("CCC", "CDC", "ACA")
                    .aisle("CCC", "BSB", "ACA")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('A', Predicates.frames(GTMaterials.Steel))
                    // 1.12.2 autoAbilities(EIn,maintenance,IIn,IOut,FIn,FOut,muffler):
                    //   (F,F,F,F,F,T,F) -> fluid-out only, min 2  OR
                    //   (F,F,F,F,T,F,F) -> fluid-in  only, min 2
                    .where('B', Predicates.autoAbilities(definition.getRecipeTypes(), false, false, false, false, false, true)
                            .setMinGlobalLimited(2)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, false, false, false, true, false)
                                    .setMinGlobalLimited(2)))
                    //   (F,T,F,F,F,F,F) -> maintenance only          OR
                    //   (F,F,T,F,F,F,F) -> item-in only, max 1
                    .where('C', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .or(Predicates.autoAbilities(true, false, false))
                            .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, false, true, false, false, false)
                                    .setMaxGlobalLimited(1)))
                    .where('D', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                    .build())
            // TODO)) Phase 6: swap the blast_furnace overlay placeholder for the real SuSy
            // front overlay (1.12.2 SusyTextures.HEAT_EXCHANGER_OVERLAY, "machines/multiblocks/heat_exchanger").
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ---- fluidized_bed_reactor (Continuous: re-derive — OC_PERFECT + a custom RecipeModifier applying SuSyParallelLogic.pureParallel(machine, recipe, 256)) ----
    public static final MultiblockMachineDefinition FLUIDIZED_BED_REACTOR = REGISTRATE
            .multiblock("fluidized_bed_reactor", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(CASING_PTFE_INERT)
            .recipeType(SuSyRecipeTypes.FLUIDIZED_BED_REACTOR_RECIPES)
            // 1.12.2 ContinuousMultiblockRecipeLogic(this, true): perfect OC layered with a
            // SuSy pure-parallel batch (limit 256). OC first, then batch.
            .recipeModifiers(GTRecipeModifiers.OC_PERFECT,
                    (machine, recipe) -> SuSyParallelLogic.pureParallel(machine, recipe, 256))
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("F F", "XXX", "XXX", "XXX", "XXX")
                    .aisle("   ", "XPX", "XPX", "XPX", "XPX")
                    .aisle("F F", "XSX", "XXX", "XXX", "XXX")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('F', Predicates.frames(GTMaterials.Steel))
                    .where('P', Predicates.blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                    .where('X', Predicates.blocks(GTBlocks.CASING_PTFE_INERT.get()).setMinGlobalLimited(17)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes(), true, true, true, true, true,
                                    true)))
                    .build())
            // TODO)) Phase 6: real overlay is SusyTextures.FLUIDIZED_BED_OVERLAY
            // (block/multiblock/fluidized_bed); blast_furnace is a placeholder.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_inert_ptfe"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ---- blender (perfect OC + FluidRender — register plain, add // TODO)) for the fluid-render client layer (Phase 6)) ----
    public static final MultiblockMachineDefinition BLENDER = REGISTRATE
            .multiblock("blender", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(GTBlocks.CASING_PTFE_INERT)
            .recipeType(SuSyRecipeTypes.BLENDER_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle(" XXX ", " XPX ", " XXX ", "  X  ")
                    .aisle("XXXXX", "X D X", "X   X", "  X  ")
                    .aisle("XXXXX", "PDDDP", "X E X", "XXCXX")
                    .aisle("XXXXX", "X D X", "X   X", "  X  ")
                    .aisle(" XXX ", " XSX ", " XXX ", "  X  ")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('X', Predicates.blocks(GTBlocks.CASING_PTFE_INERT.get()).setMinGlobalLimited(34)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes())))
                    .where('P', Predicates.blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                    .where('D', Predicates.blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                    .where('E', Predicates.frames(GTMaterials.StainlessSteel))
                    .where('C', Predicates.blocks(GTBlocks.CASING_STAINLESS_STEEL_GEARBOX.get()))
                    .where(' ', Predicates.any())
                    .build())
            // TODO)) Phase 6: front overlay is the 1.12.2 LARGE_CHEMICAL_REACTOR_OVERLAY
            // (block/multiblock/large_chemical_reactor); blast_furnace is the placeholder.
            // Also port the FluidRenderRecipeMapMultiBlock fluid-render client layer
            // (renders the recipe's output fluid in the 3x3 interior) — dropped here.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_inert_ptfe"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ---- injection_molder (SuSy pure-parallel x16: custom RecipeModifier -> SuSyParallelLogic.pureParallel(machine, recipe, 16)) ----
    public static final MultiblockMachineDefinition INJECTION_MOLDER = REGISTRATE
            .multiblock("injection_molder", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.INJECTION_MOLDER_RECIPES)
            // 1.12.2 InjectionMolderLogic: default (non-perfect) OC + SuSy pure-parallel
            // x16 (getParallelLimit()=16). pureParallel returns ModifierFunction (NULL =
            // no parallel possible -> recipe rejected); layered after OC_NON_PERFECT.
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT,
                    (machine, recipe) -> SuSyParallelLogic.pureParallel(machine, recipe, 16))
            .pattern(definition -> {
                TraceabilityPredicate casingPredicate = Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                        .setMinGlobalLimited(35);
                return FactoryBlockPattern.start()
                        .aisle("CCCCCC", "CCCCCC", "III   ")
                        .aisle("CCCCCC", "IPPKGO", "I#ICCC")
                        .aisle("CCCCCC", "CSCCCC", "III   ")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('C', casingPredicate.or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                true, true, false, false, false, false)))
                        .where('K', Predicates.blocks(GTBlocks.COIL_CUPRONICKEL.get()))
                        .where('G', Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                        .where('P', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                        .where('I', casingPredicate.or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                false, false, true, false, false, false)))
                        .where('O', casingPredicate.or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                false, false, false, true, false, false)))
                        .where(' ', Predicates.any())
                        .where('#', Predicates.air())
                        .build();
            })
            // TODO)) Phase 6: front overlay is SusyTextures.INJECTION_MOLDER_OVERLAY in
            // 1.12.2; blast_furnace is the placeholder.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ---- magnetohydrodynamic_generator (Generator: .generator(true), OUTPUT_ENERGY ability, GT fusion casing. Read GT LargeTurbineMachine pattern.) ----
    // ==================================================================
    // Phase 4c — Magnetohydrodynamic Generator (generator, plain: no rotor)
    // ==================================================================
    // 1.12.2 MetaTileEntityMagnetohydrodynamicGenerator. EU-out generator on the
    // MAGNETOHYDRODYNAMIC_FUELS map (IO.OUT, 1 fluid in / 1 fluid out). Plain
    // WorkableElectricMultiblockMachine — the source overrode nothing behavioural
    // (no custom RecipeLogic / canVoidRecipeOutputs / getRealRecipe), so no
    // controller class is needed. Front overlay texture is the 1.12.2
    // Textures.BLAST_FURNACE_OVERLAY; a GTCEu placeholder is used until Phase 6.
    public static final MultiblockMachineDefinition MAGNETOHYDRODYNAMIC_GENERATOR = REGISTRATE
            .multiblock("magnetohydrodynamic_generator", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get())
            .recipeType(SuSyRecipeTypes.MAGNETOHYDRODYNAMIC_FUELS)
            .generator(true)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle(" A ", " C ", " A ", "   ", "   ")
                    .aisle("   ", " B ", " B ", " B ", "   ")
                    .aisle(" B ", "AAA", "AAA", "AAA", " B ")
                    .aisle(" B ", "AAA", "EAE", "AAA", " B ")
                    .aisle(" B ", "AAA", "AAA", "AAA", " B ")
                    .aisle("   ", " B ", " B ", " B ", "   ")
                    .aisle(" A ", " S ", " A ", "   ", "   ")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('A', Predicates.blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get())
                            // 1.12.2 autoAbilities(false,true,false,false,false,false,false):
                            // (energyIn, maintenance, itemIn, itemOut, fluidIn, fluidOut, muffler)
                            // -> maintenance hatch only, exactly one.
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                    .where('B', Predicates.blocks(GTBlocks.FUSION_CASING.get()))
                    .where('C', Predicates.abilities(PartAbility.OUTPUT_ENERGY))
                    // 1.12.2 autoAbilities(..., fluidIn=true) OR autoAbilities(..., fluidOut=true)
                    // -> a fluid import OR export hatch (fuels in / exhaust out).
                    .where('E', Predicates.abilities(PartAbility.IMPORT_FLUIDS)
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS)))
                    .where(' ', Predicates.any())
                    .build())
            // Base casing tex = 1.12.2 Textures.ROBUST_TUNGSTENSTEEL_CASING. Front overlay:
            // 1.12.2 used Textures.BLAST_FURNACE_OVERLAY -> GTCEu blast_furnace placeholder
            // (swap for the real MHD overlay in Phase 6).
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_robust_tungstensteel"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ==================================================================
    // Phase 4c — Bucket B: ordered distillation towers
    // 1.12.2 MetaTileEntityOrderedDT (+ DistillationTowerRecipeLogic / per-layer
    // EXPORT_FLUIDS hatches) maps natively onto Modern DistillationTowerMachine:
    // output index == Y layer, parts Y-sorted via .partSorter. Dynamic height via
    // .setRepeatable. The two cryo plants carry a // TODO)) for the
    // ICryogenicProvider/Receiver cross-machine link (Bucket D, SuSyPredicates).
    // ==================================================================

    // ---- high_temperature_distillation_tower ----
    public static final MultiblockMachineDefinition HIGH_TEMPERATURE_DISTILLATION_TOWER = REGISTRATE
            .multiblock("high_temperature_distillation_tower", DistillationTowerMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> SusyBlocks.SILICON_CARBIDE_CASING.get())
            .recipeType(SuSyRecipeTypes.HIGH_TEMPERATURE_DISTILLATION_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> FactoryBlockPattern
                    .start(RelativeDirection.RIGHT, RelativeDirection.FRONT, RelativeDirection.UP)
                    .aisle("YSY", "YYY", "YYY")
                    .aisle("XXX", "X#X", "XXX").setRepeatable(1, 11)
                    .aisle("XXX", "XXX", "XXX")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('Y', Predicates.blocks(SusyBlocks.SILICON_CARBIDE_CASING.get())
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1)))
                    .where('X', Predicates.blocks(SusyBlocks.SILICON_CARBIDE_CASING.get())
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS_1X).setMinLayerLimited(1).setMaxLayerLimited(1))
                            .or(Predicates.autoAbilities(true, false, false)))
                    .where('#', Predicates.air())
                    .build())
            .partSorter(Comparator.comparingInt(p -> p.self().getPos().getY()))
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/distillation_tower")) // TODO)) Phase 6: real silicon-carbide casing tex + SusyTextures.HTDT_OVERLAY
            .register();

    // ---- sieve_distillation_tower (has sieve-tray interior blocks (SusyBlocks.SIEVE_TRAY) inside the tower) ----
    public static final MultiblockMachineDefinition SIEVE_DISTILLATION_TOWER = REGISTRATE
            .multiblock("sieve_distillation_tower", DistillationTowerMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> SusyBlocks.SIEVE_TRAY.get())
            .recipeType(SuSyRecipeTypes.SIEVE_DISTILLATION_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .allowExtendedFacing(false)
            .pattern(definition -> FactoryBlockPattern
                    .start(RelativeDirection.RIGHT, RelativeDirection.FRONT, RelativeDirection.UP)
                    .aisle("YSY", "YYY", "YYY")
                    .aisle("FXF", "X#X", "FXF").setRepeatable(1, 11)
                    .aisle("XXX", "XXX", "XXX")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('Y', Predicates.blocks(GTBlocks.CASING_STAINLESS_CLEAN.get())
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setMaxGlobalLimited(2)))
                    .where('X', Predicates.blocks(GTBlocks.CASING_STAINLESS_CLEAN.get())
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS_1X).setMaxLayerLimited(1))
                            .or(Predicates.autoAbilities(true, false, false)))
                    .where('#', Predicates.blocks(SusyBlocks.SIEVE_TRAY.get()))
                    .where('F', Predicates.frames(GTMaterials.StainlessSteel))
                    .build())
            .partSorter(java.util.Comparator.comparingInt(p -> p.self().getPos().getY()))
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/distillation_tower")) // TODO Phase 6: real casing tex + SusyTextures.SDT_OVERLAY
            .register();

    // ---- vacuum_distillation_tower (ExtendedDTLogicHandler (3 fluid outputs per layer) — verify Modern DistillationTowerMachine output-per-layer and note if it differs) ----
    public static final MultiblockMachineDefinition VACUUM_DISTILLATION_TOWER = REGISTRATE
            .multiblock("vacuum_distillation_tower", DistillationTowerMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.VACUUM_DISTILLATION_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.FRONT, RelativeDirection.UP)
                    .aisle(" CSC  ", "CCCCCC", "CCCCCC", "CCCCCC", " CCC  ")
                    .aisle(" CGC  ", "C#F#CC", "IFFF#P", "C#F#CC", " CCC  ")
                    .aisle(" CCC  ", "C#F#CC", "CFFFCC", "C#F#CC", " CCC  ")
                    .aisle(" XXX  ", "X#F#D ", "XFFFD ", "X#F#D ", " XXX  ").setRepeatable(1, 12)
                    .aisle(" DDD  ", "DDDDD ", "DDDDD ", "DDDDD ", " DDD  ")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('G', Predicates.blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .where('P', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                    .where('F', Predicates.frames(GTMaterials.Steel))
                    .where('C', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1)))
                    .where('I', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(1)))
                    .where('D', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()))
                    .where('X', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS_1X).setMaxLayerLimited(1))
                            .or(Predicates.autoAbilities(true, false, false)))
                    .where('#', Predicates.air())
                    .build())
            .partSorter(Comparator.comparingInt(p -> p.self().getPos().getY()))
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/distillation_tower"))  // TODO)) Phase 6: real casing tex + SusyTextures.VDT_OVERLAY
            .register();

    // ---- high_pressure_cryogenic_distillation_plant (ExtendedDTLogicHandler; high pressure variant) ----
    public static final MultiblockMachineDefinition HIGH_PRESSURE_CRYOGENIC_DISTILLATION_PLANT = REGISTRATE
            .multiblock("high_pressure_cryogenic_distillation_plant", DistillationTowerMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
            .recipeType(SuSyRecipeTypes.HIGH_PRESSURE_CRYOGENIC_DISTILLATION_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> FactoryBlockPattern
                    .start(RelativeDirection.RIGHT, RelativeDirection.FRONT, RelativeDirection.UP)
                    .aisle("CCC", "CCC", "CCC")
                    .aisle("CSC", "CFC", "CCC")
                    .aisle("XXX", "XFX", "XXX").setRepeatable(1, 16)
                    .aisle("DDD", "DDD", "DDD")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('C', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1)
                                    .setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                            .or(Predicates.autoAbilities(true, false, false).setExactLimit(1)))
                    .where('F', Predicates.blocks(SusyBlocks.SIEVE_TRAY.get()))
                    .where('X', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS_1X).setMaxLayerLimited(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS_1X).setMaxLayerLimited(4)))
                    .where('D', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()))
                    .where('#', Predicates.air())
                    .build())
            .allowExtendedFacing(false)
            .partSorter(java.util.Comparator.comparingInt(p -> p.self().getPos().getY()))
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_frost_proof"),
                    GTCEu.id("block/multiblock/distillation_tower"))  // TODO)) Phase 6: real casing tex + SusyTextures.HPCDT_OVERLAY
            .register();

    // ---- low_pressure_cryogenic_distillation_plant ----
public static final MultiblockMachineDefinition LOW_PRESSURE_CRYOGENIC_DISTILLATION_PLANT = REGISTRATE
        .multiblock("low_pressure_cryogenic_distillation_plant", LowPressureCryogenicDistillationPlant::new)
        .rotationState(RotationState.NON_Y_AXIS)
        .appearanceBlock(() -> GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
        .recipeType(SuSyRecipeTypes.LOW_PRESSURE_CRYOGENIC_DISTILLATION_RECIPES)
        .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT)
        .pattern(definition -> FactoryBlockPattern
                .start(RelativeDirection.RIGHT, RelativeDirection.FRONT, RelativeDirection.UP)
                .aisle("DDD", "DDD", "DDD")
                .aisle("CSC", "CFC", "CCC")
                .aisle("XXX", "XFX", "XXX").setRepeatable(1, 16)
                .aisle("DED", "EZE", "DED")
                .aisle("DDD", "DDD", "DDD")
                .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                .where('C', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
                        .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                        .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                        .or(Predicates.autoAbilities(true, false, false).setExactLimit(1)))
                .where('F', Predicates.blocks(SusyBlocks.STRUCTURAL_PACKING.get()))
                .where('X', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
                        .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS_1X).setMaxLayerLimited(1))
                        .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS_1X).setMaxLayerLimited(4)))
                .where('D', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()))
                .where('E', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
                        .or(Predicates.abilities(PartAbility.PASSTHROUGH_HATCH)))
                .where('Z', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()))
                .where('#', Predicates.air())
                .build())
        .allowExtendedFacing(false)
        .partSorter(java.util.Comparator.comparingInt(p -> p.self().getPos().getY()))
        .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_frost_proof"),
                GTCEu.id("block/multiblock/distillation_tower")) // TODO)) Phase 6: real casing tex + SusyTextures.LPCDT_OVERLAY
        .register();

    // ---- single_column_cryogenic_distillation_plant ----
    // ---- single_column_cryogenic_distillation_plant (ordered DT; cryo receiver link deferred) ----
    public static final MultiblockMachineDefinition SINGLE_COLUMN_CRYOGENIC_DISTILLATION_PLANT = REGISTRATE
            .multiblock("single_column_cryogenic_distillation_plant",
                    holder -> new SingleColumnCryogenicDistillationPlantMachine(holder))
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false) // 1.12.2 allowsExtendedFacing() == false
            .appearanceBlock(() -> GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
            .recipeType(SuSyRecipeTypes.SINGLE_COLUMN_CRYOGENIC_DISTILLATION_RECIPES)
            // 1.12.2 MetaTileEntityOrderedDT 2-arg ctor -> hasPerfectOC=false -> non-perfect OC.
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> FactoryBlockPattern
                    .start(RelativeDirection.RIGHT, RelativeDirection.FRONT, RelativeDirection.UP)
                    .aisle("CCC", "CCC", "CCC")
                    .aisle("CSC", "CFC", "CCC")
                    .aisle("XXX", "XFX", "XXX").setRepeatable(1, 16)
                    .aisle("CCC", "CCC", "CCC")
                    .aisle("CEC", "E E", "CEC")
                    .aisle("DDD", "DED", "DDD")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('C', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                            // 1.12.2 autoAbilities(false,true,false,false,false,false,false) = muffler only (no maintenance).
                            .or(Predicates.autoAbilities(false, true, false).setExactLimit(1)))
                    .where('F', Predicates.blocks(SusyBlocks.STRUCTURAL_PACKING.get()))
                    .where('X', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
                            // 1.12.2 EXPORT_FLUIDS excluding MetaTileEntityMultiFluidHatch (setMaxLayerLimited(1))
                            // -> Modern single-tank EXPORT_FLUIDS_1X.
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS_1X).setMaxLayerLimited(1))
                            // 1.12.2 IMPORT_FLUIDS excluding MetaTileEntityMultiFluidHatch (setMaxLayerLimited(4)).
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS_1X).setMaxLayerLimited(4)))
                    .where('D', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()))
                    .where('E', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
                            .or(Predicates.abilities(PartAbility.PASSTHROUGH_HATCH)))
                    .where('#', Predicates.air())
                    // 1.12.2 cryogenicRecieverPredicate() (SIDE-EFFECTING: linked the adjacent Bath
                    // Condenser / ICryogenicReceiver at match time). Modern predicates are pure, so
                    // this matches anything for now; the cross-machine cryo link is re-established in
                    // SingleColumnCryogenicDistillationPlantMachine.onStructureFormed — see its TODO))
                    // (Bucket D, with the ICryogenicProvider/ICryogenicReceiver capability port).
                    .where(' ', Predicates.any())
                    .build())
            // REQUIRED for per-Y-layer fluid outputs (output index = layer), matching the 1.12.2
            // multiblockPartSorter UP.getSorter: parts sorted bottom-up so DistillationTowerMachine
            // walks the fluid export hatches in ascending Y.
            .partSorter(java.util.Comparator.comparingInt(p -> p.self().getPos().getY()))
            // TODO)) Phase 6: real front overlay is the SuSy cryo-DT overlay (SusyTextures); 1.12.2
            // used Textures.BLAST_FURNACE_OVERLAY, so blast_furnace is the placeholder. Base casing
            // tex = frostproof (1.12.2 Textures.FROST_PROOF_CASING), confirmed.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_frost_proof"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    // ==================================================================
    // Phase 4c — Bucket C: rotation generators (turbines + internal combustion engine)
    //
    // Ported from 1.12.2 RotationGeneratorController / MetaTileEntitySUSYLargeTurbine /
    // MetaTileEntityGasTurbine / MetaTileEntityAdvancedLargeTurbine /
    // MetaTileEntityInternalCombustionEngine onto SuSyRotationGeneratorMachine (the
    // spool-ramp + 5-tier-lubricant + energy-void base). All are .generator(true) and
    // share SuSyRotationGeneratorMachine.recipeModifier (linear speed-scaled EU output).
    //
    // The rotor / alternator-coil / crankshaft blocks are HorizontalOrientableBlocks
    // (SusyBlocks); the patterns use the PURE SuSyPredicates.horizontalOrientation and
    // the controller orients them on structure form. 1.12.2 autoAbilities(...) arg
    // orders are mapped by meaning (see susy-kore-multiblock-registration-idiom).
    //
    // The 1.12.2 parameters (maxSpeed/accel/decel/tier) are preserved exactly:
    //   BASIC_STEAM_TURBINE  tier 1 (MV), 3600/1/1,  steel turbine casing + steel rotor
    //   GAS_TURBINE          tier 4 (EV), 7200/3/4,  titanium casing + combustion rotor (+ engine intake)
    //   ADVANCED_STEAM_TURBINE tier 4 (EV), 3600/2/2, titanium casing + LP/HP rotors (+ titanium pipe)
    //   INTERNAL_COMBUSTION_GENERATOR tier 3 (HV), 3600/18/24, steel solid + crankshaft/pistons/serpentine
    // ==================================================================

    public static final MultiblockMachineDefinition BASIC_STEAM_TURBINE = REGISTRATE
            .multiblock("basic_steam_turbine",
                    holder -> new SuSyRotationGeneratorMachine(holder, 1, 3600, 1, 1, SuSyLubricants.TIERS))
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false) // 1.12.2 allowsExtendedFacing() == false
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_TURBINE.get())
            .recipeType(SuSyRecipeTypes.LARGE_STEAM_TURBINE_FUELS)
            .generator(true)
            .recipeModifier(SuSyRotationGeneratorMachine::recipeModifier, true)
            .pattern(definition -> {
                var casing = Predicates.blocks(GTBlocks.CASING_STEEL_TURBINE.get());
                var maintenance = Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1);
                return FactoryBlockPattern.start()
                        .aisle("GAAAAAAAO", "GAAAAAAAO", "G   A   O")
                        .aisle("GAAAAAAAO", "GDDDDCCCF", "GAAAAAAAO")
                        .aisle("GAAAAAAAO", "GSAAAAAAO", "G   A   O")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        // 1.12.2 'A': casing + IMPORT_ITEMS + maintenance (7-arg autoAbilities all-false
                        // contributed no hatches).
                        .where('A', casing.setMinGlobalLimited(52)
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                                .or(maintenance))
                        // 1.12.2 'O': + IMPORT_FLUIDS (7-arg pos6=fluidIn).
                        .where('O', casing
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                                .or(maintenance))
                        // 1.12.2 'G': + EXPORT_FLUIDS (7-arg pos7=fluidOut).
                        .where('G', casing
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS))
                                .or(maintenance))
                        .where('C', SuSyPredicates.horizontalOrientation(SusyBlocks.COPPER_ALTERNATOR_COIL.get(),
                                RelativeDirection.RIGHT))
                        .where('D', SuSyPredicates.horizontalOrientation(SusyBlocks.STEEL_TURBINE_ROTOR.get(),
                                RelativeDirection.RIGHT))
                        .where('F', Predicates.abilities(PartAbility.OUTPUT_ENERGY))
                        .where(' ', Predicates.any())
                        .build();
            })
            .tooltipBuilder((stack, tooltip) -> {
                tooltip.add(Component.translatable("gregtech.universal.tooltip.max_voltage_out", GTValues.V[1 + 2],
                        GTValues.VNF[1 + 2]));
                tooltip.add(Component.translatable("susy.multiblock.rotation_generator.tooltip", 3600, 1, 1));
            })
            // TODO)) Phase 6: real SuSy LARGE_STEAM_TURBINE_OVERLAY; GTCEu large_steam_turbine is the placeholder.
            .workableCasingModel(GTCEu.id("block/casings/mechanic/machine_casing_turbine_steel"),
                    GTCEu.id("block/multiblock/generator/large_steam_turbine"))
            .register();

    public static final MultiblockMachineDefinition GAS_TURBINE = REGISTRATE
            .multiblock("gas_turbine",
                    holder -> new SuSyRotationGeneratorMachine(holder, 4, 7200, 3, 4, SuSyLubricants.TIERS))
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_TITANIUM_TURBINE.get())
            .recipeType(com.gregtechceu.gtceu.common.data.GTRecipeTypes.GAS_TURBINE_FUELS)
            .generator(true)
            .recipeModifier(SuSyRotationGeneratorMachine::recipeModifier, true)
            .pattern(definition -> {
                var casing = Predicates.blocks(GTBlocks.CASING_TITANIUM_TURBINE.get());
                var maintenance = Predicates.abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1);
                return FactoryBlockPattern.start()
                        .aisle("GAAAAAAAO", "GAAAAAAAO", "G   A   O")
                        .aisle("GAAAAAAAO", "IDDDDCCCF", "GAAAAAAAO")
                        .aisle("GAAAAAAAO", "GSAAAAAAO", "G   A   O")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('A', casing.setMinGlobalLimited(51)
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                                .or(maintenance))
                        .where('O', casing
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                                .or(maintenance))
                        .where('G', casing
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS))
                                .or(maintenance))
                        .where('C', SuSyPredicates.horizontalOrientation(SusyBlocks.COPPER_ALTERNATOR_COIL.get(),
                                RelativeDirection.RIGHT))
                        .where('D', SuSyPredicates.horizontalOrientation(SusyBlocks.COMBUSTION_TURBINE_ROTOR.get(),
                                RelativeDirection.RIGHT))
                        .where('F', Predicates.abilities(PartAbility.OUTPUT_ENERGY))
                        .where('I', Predicates.blocks(GTBlocks.CASING_ENGINE_INTAKE.get()))
                        .where(' ', Predicates.any())
                        .build();
            })
            .tooltipBuilder((stack, tooltip) -> {
                tooltip.add(Component.translatable("gregtech.universal.tooltip.max_voltage_out", GTValues.V[4 + 2],
                        GTValues.VNF[4 + 2]));
                tooltip.add(Component.translatable("susy.multiblock.rotation_generator.tooltip", 7200, 3, 4));
            })
            // TODO)) the 1.12.2 GasTurbineRecipeLogic outputs the flue gas fluid
            // progressively per second during the run (not all at completion). Port as a
            // per-tick fluid tickOutput on the recipe; for now the whole flue outputs at
            // recipe end via the standard EU/fluid tick-output handling.
            // TODO)) Phase 6: real SuSy LARGE_GAS_TURBINE_OVERLAY.
            .workableCasingModel(GTCEu.id("block/casings/mechanic/machine_casing_turbine_titanium"),
                    GTCEu.id("block/multiblock/generator/large_gas_turbine"))
            .register();

    public static final MultiblockMachineDefinition ADVANCED_STEAM_TURBINE = REGISTRATE
            .multiblock("advanced_steam_turbine",
                    holder -> new SuSyRotationGeneratorMachine(holder, 4, 3600, 2, 2, SuSyLubricants.TIERS))
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_TITANIUM_TURBINE.get())
            .recipeType(SuSyRecipeTypes.ADVANCED_STEAM_TURBINE_FUELS)
            .generator(true)
            .recipeModifier(SuSyRotationGeneratorMachine::recipeModifier, true)
            .pattern(definition -> {
                var casing = Predicates.blocks(GTBlocks.CASING_TITANIUM_TURBINE.get());
                var maintenance = Predicates.abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1);
                return FactoryBlockPattern.start()
                        .aisle("GAAAAAAAAAAAO", "GAAAAAAAAAAAO", "G   A   A   O")
                        .aisle("GAAAAAAAAAAAO", "GHHHPLLLLCCCF", "GAAAAAAAAAAAO")
                        .aisle("GAAAAAAAAAAAO", "GSAAAAAAAAAAO", "G   A   A   O")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('A', casing.setMinGlobalLimited(52)
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                                .or(maintenance))
                        .where('O', casing
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                                .or(maintenance))
                        .where('G', casing
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS))
                                .or(maintenance))
                        .where('C', SuSyPredicates.horizontalOrientation(SusyBlocks.COPPER_ALTERNATOR_COIL.get(),
                                RelativeDirection.RIGHT))
                        // 1.12.2: 'L' = low-pressure rotor (rotorOrientation), 'H' = high-pressure
                        // rotor (rotorOrientation2); both face controller-relative RIGHT.
                        .where('L', SuSyPredicates.horizontalOrientation(SusyBlocks.LOW_PRESSURE_TURBINE_ROTOR.get(),
                                RelativeDirection.RIGHT))
                        .where('H', SuSyPredicates.horizontalOrientation(SusyBlocks.HIGH_PRESSURE_TURBINE_ROTOR.get(),
                                RelativeDirection.RIGHT))
                        .where('F', Predicates.abilities(PartAbility.OUTPUT_ENERGY))
                        .where('P', Predicates.blocks(GTBlocks.CASING_TITANIUM_PIPE.get()))
                        .where(' ', Predicates.any())
                        .build();
            })
            .tooltipBuilder((stack, tooltip) -> {
                tooltip.add(Component.translatable("gregtech.universal.tooltip.max_voltage_out", GTValues.V[4 + 2],
                        GTValues.VNF[4 + 2]));
                tooltip.add(Component.translatable("susy.multiblock.rotation_generator.tooltip", 3600, 2, 2));
            })
            // TODO)) Phase 6: real SuSy ADVANCED_STEAM_TURBINE_OVERLAY.
            .workableCasingModel(GTCEu.id("block/casings/mechanic/machine_casing_turbine_titanium"),
                    GTCEu.id("block/multiblock/generator/large_steam_turbine"))
            .register();

    public static final MultiblockMachineDefinition INTERNAL_COMBUSTION_GENERATOR = REGISTRATE
            .multiblock("internal_combustion_generator",
                    holder -> new SuSyRotationGeneratorMachine(holder, 3, 3600, 18, 24, SuSyLubricants.TIERS))
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(com.gregtechceu.gtceu.common.data.GTRecipeTypes.COMBUSTION_GENERATOR_FUELS)
            .generator(true)
            .recipeModifier(SuSyRotationGeneratorMachine::recipeModifier, true)
            .pattern(definition -> {
                var casing = Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get());
                return FactoryBlockPattern.start()
                        .aisle("C CCCCCCC  F   F ", "C   FEF    F   F ", "C   FCF    F   F ", "C                ",
                                "C                ", "                 ")
                        .aisle("CFFFFFFFFFFFFFFF ", "R CCCCCCC CCCCCCC", "R CCCCCCC CCCCCCC", "R CCCCCCC CCCCCCC",
                                "C  F F F         ", "   HHHHH         ")
                        .aisle("CPPPPPPPC  F   F ", "R CCCCCCC CCCCCCC", "R CXXXXXXGAAAAAAD", "R CBBBBBC CCCCCCC",
                                "C  IPPPI         ", "   HHHHH         ")
                        .aisle("CFFFFFFFFFFFFFFF ", "R CCCCCCC CCCCCCC", "R CCCCCCC CCCCCCC", "R CCCCCCC CCCCCCC",
                                "C  F F F         ", "   HHHHH         ")
                        .aisle("C CCCCCCC  F   F ", "C   FSF    F   F ", "C   FMF    F   F ", "C                ",
                                "C                ", "                 ")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('C', casing)
                        .where('M', Predicates.abilities(PartAbility.MAINTENANCE))
                        .where('E', Predicates.abilities(PartAbility.MUFFLER))
                        .where('F', Predicates.frames(GTMaterials.Steel))
                        // 1.12.2 'H': casing + IMPORT_FLUIDS (7-arg autoAbilities pos6=fluidIn).
                        .where('H', casing.or(Predicates.abilities(PartAbility.IMPORT_FLUIDS)))
                        .where('P', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                        .where('R', Predicates.blocks(SusyBlocks.BASIC_SERPENTINE.get()))
                        .where('X', SuSyPredicates.horizontalOrientation(SusyBlocks.CRANKSHAFT_ENGINE_CASING.get(),
                                RelativeDirection.UP))
                        .where('G', Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                        .where('A', SuSyPredicates.horizontalOrientation(SusyBlocks.COPPER_ALTERNATOR_COIL.get(),
                                RelativeDirection.RIGHT))
                        .where('D', Predicates.abilities(PartAbility.OUTPUT_ENERGY))
                        .where('B', Predicates.blocks(SusyBlocks.PISTON_BLOCK.get()))
                        .where('I', Predicates.blocks(SusyBlocks.BASIC_INTAKE_CASING.get()))
                        .where(' ', Predicates.any())
                        .build();
            })
            .tooltipBuilder((stack, tooltip) -> {
                tooltip.add(Component.translatable("gregtech.universal.tooltip.max_voltage_out", GTValues.V[3 + 2],
                        GTValues.VNF[3 + 2]));
                tooltip.add(Component.translatable("susy.multiblock.rotation_generator.tooltip", 3600, 18, 24));
            })
            // TODO)) Phase 6: 1.12.2 front overlay was FLUID_COMPRESSOR_OVERLAY (solid steel casing base).
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/generator/large_combustion_engine"))
            .register();

    // ==================================================================
    // Phase 4c Bucket D1: plain recipe multiblocks.
    // Plain 1.12.2 RecipeMapMultiblockControllers with no bespoke logic — standard
    // WorkableElectricMultiblockMachine + an overclock RecipeModifier. Textures are
    // GTCEu placeholders pending Phase 6 (real SuSy paths recorded per machine).
    // ==================================================================

    // ---- advanced_arc_furnace (perfect OC: 1.12.2 MultiblockRecipeLogic(this,true)) ----
    public static final MultiblockMachineDefinition ADVANCED_ARC_FURNACE = REGISTRATE
            .multiblock("advanced_arc_furnace", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.ADVANCED_ARC_FURNACE_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_PERFECT)
            .pattern(definition -> FactoryBlockPattern
                    .start(RelativeDirection.RIGHT, RelativeDirection.FRONT, RelativeDirection.UP)
                    .aisle(" AAA ", " AAA ", " EEE ", "     ")
                    .aisle("AAAAA", "A#C#A", "E#C#E", " ACA ")
                    .aisle("CAAAC", "C###C", "C###C", "CAAAC")
                    .aisle("AAAAA", "A###A", "E###E", " AAA ")
                    .aisle(" AAA ", " ASA ", " EEE ", "     ")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('A', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()).setMinGlobalLimited(28)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes())))
                    .where('C', Predicates.blocks(SusyBlocks.CARBON_ELECTRODE_ASSEMBLY.get()))
                    .where('D', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                    .where('E', Predicates.blocks(GTBlocks.FIREBOX_STEEL.get()))
                    .where(' ', Predicates.any())
                    .where('#', Predicates.air())
                    .build())
            // TODO)) Phase 6: swap the front overlay to the real SuSy 'arc_furnace'
            // overlay (1.12.2 getFrontOverlay() -> SusyTextures.ARC_FURNACE_OVERLAY);
            // blast_furnace is the placeholder. Base = SOLID_STEEL_CASING.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    public static final MultiblockMachineDefinition ELECTRIC_DISCHARGE_MACHINE = REGISTRATE
            .multiblock("electric_discharge_machine", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .recipeType(SuSyRecipeTypes.EDM_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .appearanceBlock(() -> GCYMBlocks.CASING_NONCONDUCTING.get())
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("CCCCC", "CCCCC", "CCCCC", "CCCCC", " CCC ")
                    .aisle("CCCCC", "C C C", "C E C", "C C C", " CCC ")
                    .aisle("CCCCC", "C   C", "C   C", "C   C", " CCC ")
                    .aisle(" CSC ", " GGG ", " GGG ", " CCC ", "     ")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('C', Predicates.blocks(GCYMBlocks.CASING_NONCONDUCTING.get())
                            .setMinGlobalLimited(45)
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY)
                                    .setMinGlobalLimited(1).setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE)
                                    .setExactLimit(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS)
                                    .setMinGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS)
                                    .setMinGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS)
                                    .setMinGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS)))
                    .where('E', Predicates.blocks(SusyBlocks.COPPER_TUNGSTEN_EDM_ELECTRODE.get()))
                    .where('G', Predicates.blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                    .build())
            // TODO)) Phase 6: real textures — base gregtech:blocks/casings/gcym/nonconducting_casing,
            //  front overlay susy:blocks/multiblock/edm_overlay (SusyTextures.EDM_OVERLAY).
            .workableCasingModel(GTCEu.id("block/casings/gcym/nonconducting_casing"),
                    GTCEu.id("block/multiblock/gcym/large_electrolyzer"))
            .register();

    public static final MultiblockMachineDefinition GAS_ATOMIZER = REGISTRATE
            .multiblock("gas_atomizer", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.GAS_ATOMIZER_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("  O  ", "  P  ", "  P  ", "  M  ", "     ", "     ", " EIE ", "     ")
                    .aisle("     ", "  P  ", "     ", "     ", "     ", " HHH ", " EXE ", " HHH ")
                    .aisle("R   R", "R P R", "CCCCC", " HHH ", " HHH ", " HHH ", " HXH ", " HHH ")
                    .aisle(" CCC ", " CPC ", "CHHHC", "HHHHH", "HXXXH", "HXXXH", "HXXXH", " HHH ")
                    .aisle(" CCC ", " CXC ", "CHXHC", "HHXHH", "HXXXH", "HXXXH", "HXXXH", " HFH ")
                    .aisle(" CCC ", " CCC ", "CHHHC", "HHHHH", "HXXXH", "HXXXH", "HXXXH", " HHH ")
                    .aisle("R   R", "R   R", "CCSCC", " HHH ", " HHH ", " HHH ", " HHH ", "     ")
                    .where('P', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                    .where('H', Predicates.blocks(SusyBlocks.SILICON_CARBIDE_CASING.get()))
                    .where('C', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .or(Predicates.autoAbilities(definition.getRecipeTypes())))
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('M', Predicates.abilities(PartAbility.MUFFLER))
                    .where('O', Predicates.abilities(PartAbility.EXPORT_ITEMS))
                    .where('F', Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                    .where('I', Predicates.abilities(PartAbility.IMPORT_ITEMS))
                    .where('E', Predicates.blocks(SusyBlocks.CARBON_ELECTRODE_ASSEMBLY.get()))
                    .where('R', Predicates.frames(GTMaterials.Steel))
                    .where('X', Predicates.air())
                    .where(' ', Predicates.any())
                    .build())
            // TODO)) Phase 6: real textures — base .../solid/machine_casing_solid_steel,
            //  front overlay SusyTextures.GAS_ATOMIZER_OVERLAY (susy:blocks/multiblock/gas_atomizer_overlay).
            //  GTCEu has no gas_atomizer overlay; multiblock_workable is the placeholder.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    public static final MultiblockMachineDefinition PRECISE_MILLING_MACHINE = REGISTRATE
            .multiblock("precise_milling_machine", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STAINLESS_CLEAN.get())
            .recipeType(SuSyRecipeTypes.MILLING_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("BBBBBB", "CCCCCC", "CGGGGC", "CCCCCC")
                    .aisle("BBBBBB", "C    C", "CDDDDC", "CCCCCC")
                    .aisle("BBBBBB", "C    C", "C    C", "CCCCCC")
                    .aisle("BBBBBB", "CWWWWS", "CWWWWC", "CCCCCC")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('B', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()).setMinGlobalLimited(18)
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY)
                                    .setMinGlobalLimited(1).setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE)
                                    .setExactLimit(1)))
                    .where('C', Predicates.blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()).setMinGlobalLimited(35)
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS)
                                    .setMinGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS)
                                    .setMinGlobalLimited(1)))
                    .where('D', Predicates.blocks(SusyBlocks.STEEL_DRILL_BIT.get()))
                    .where('G', Predicates.blocks(GTBlocks.CASING_STAINLESS_STEEL_GEARBOX.get()))
                    .where('W', Predicates.blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .build())
            // TODO)) Phase 6: real base .../metal_casing/stainless_clean (+ drill-bit steel,
            //  tempered glass); overlay SusyTextures.MILLING_OVERLAY (gregtech:blocks/multiblock/milling).
            //  GTCEu has no milling overlay; multiblock_workable is the placeholder.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    // ==================================================================
    // Phase 4c Bucket D3a: orientation / same-type-variant multiblocks.
    // ==================================================================

    public static final MultiblockMachineDefinition ATTRITION_SCRUBBER = REGISTRATE
            .multiblock("attrition_scrubber", AttritionScrubberMachine::new)
            .rotationState(RotationState.ALL)
            .appearanceBlock(() -> SusyBlocks.ABRASION_RESISTANT_CASING.get())
            .recipeType(SuSyRecipeTypes.ATTRITION_SCRUBBER_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> {
                TraceabilityPredicate casing = Predicates.blocks(SusyBlocks.ABRASION_RESISTANT_CASING.get());
                return FactoryBlockPattern.start()
                        .aisle(" CCC CCC ", " CCCCCCC ", " CCCCCCC ", " CCCCCCC ", " CCC CCC ", " FGF FGF ")
                        .aisle("CCCCCCCCC", "W#B###B#S", "C###C###C", "I#B#C#B#O", "C###C###C", " FGF FGF ")
                        .aisle("CCCCCCCCC", "WBBB#BBBS", "C#A#C#A#C", "IBABCBABO", "C#A#C#A#C", " FGF FGF ")
                        .aisle("CCCCCCCCC", "W#B###B#S", "C###C###C", "I#B#C#B#O", "C###C###C", " F F F F ")
                        .aisle(" CCC CCC ", " CXCCCCC ", " CCCCCCC ", " CCCCCCC ", " CCC CCC ", " F F F F ")
                        .where('X', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('C', casing
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                        true, false, false, false, false, false))
                                .or(Predicates.autoAbilities(true, false, false)))
                        .where('I', casing.or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                false, false, true, false, false, false)))
                        .where('O', casing.or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                false, false, false, true, false, false)))
                        .where('W', casing.or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                false, false, false, false, true, false)))
                        .where('S', casing.or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                false, false, false, false, false, true)))
                        .where('G', Predicates.blocks(SusyBlocks.ALUMINIUM_GEARBOX.get()))
                        .where('B', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()))
                        .where('A', Predicates.frames(GTMaterials.Aluminium))
                        .where('F', Predicates.frames(GTMaterials.Steel))
                        .where('#', Predicates.air())
                        .where(' ', Predicates.any())
                        // 1.12.2 also declared hiddenGearTooth for 'M', but no aisle
                        // contains 'M'; omitting that dead predicate preserves behaviour.
                        .build();
            })
            // The 1.12.2 controller displayed a 32x-parallel claim but its recipe
            // logic never implemented parallel processing, so the misleading tooltip
            // is intentionally not carried forward.
            // TODO)) Decide whether attrition recipes should gain explicit pure parallel.
            // TODO)) Phase 6: real abrasion-resistant CTM + ATTRITION_SCRUBBER_OVERLAY.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/gcym/large_maceration_tower"))
            .register();

    public static final MultiblockMachineDefinition ECCENTRIC_ROLL_CRUSHER = REGISTRATE
            .multiblock("eccentric_roll_crusher", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.ECCENTRIC_ROLL_CRUSHER_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> {
                TraceabilityPredicate steelCasings = Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                        .setMinGlobalLimited(22);
                ArrayList<Block> sheetBlocks = new ArrayList<>();
                GTBlocks.METAL_SHEETS.values().forEach(entry -> sheetBlocks.add(entry.get()));
                GTBlocks.LARGE_METAL_SHEETS.values().forEach(entry -> sheetBlocks.add(entry.get()));
                TraceabilityPredicate metalSheets = SuSyPredicates.sameTypeVariant(
                        "EccentricRollCrusherMetalSheet",
                        "susy.multiblock.pattern.error.metal_sheets",
                        sheetBlocks.toArray(Block[]::new));

                return FactoryBlockPattern.start()
                        .aisle("  CCDC  ", "  CCGC  ", "  CCMX  ", "    MMX ", "     MMX")
                        .aisle("JJJJ#CD ", "JHJ#R#C ", "  J##M  ", "   J##M ", "     ##N")
                        .aisle("  BJ#CD ", " BJ#R#C ", " PJ##M  ", " P J##M ", "     ##N")
                        .aisle("JJJJ#CD ", "JHJ#R#C ", "  J##M  ", "   J##M ", "     ##N")
                        .aisle("  CCDC  ", "  CSGC  ", "  CCMX  ", "    MMX ", "     MMX")
                        .where(' ', Predicates.any())
                        .where('#', Predicates.air())
                        .where('B', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()))
                        .where('C', steelCasings
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                        true, false, false, false, false, false))
                                .or(Predicates.autoAbilities(true, false, false)))
                        .where('D', steelCasings.or(Predicates.abilities(PartAbility.EXPORT_ITEMS)))
                        .where('G', Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                        .where('H', Predicates.blocks(SusyBlocks.HYDRAULIC_MECHANICAL_GEARBOX.get()))
                        .where('J', Predicates.blocks(SusyBlocks.ABRASION_RESISTANT_CASING.get()))
                        .where('P', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                        .where('R', Predicates.blocks(SusyBlocks.STEEL_ECCENTRIC_ROLL.get()))
                        .where('X', Predicates.frames(GTMaterials.Steel))
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('M', metalSheets)
                        .where('N', metalSheets.or(Predicates.abilities(PartAbility.IMPORT_ITEMS)))
                        .build();
            })
            // TODO)) Phase 5: directional/active eccentric-roll block, reduced collision
            // box, and active collision damage. Phase 6: roll animation, selected-sheet
            // hatch appearance, and the dedicated controller overlay.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/gcym/large_maceration_tower"))
            .register();

    public static final MultiblockMachineDefinition BALL_MILL = REGISTRATE
            .multiblock("ball_mill", BallMillMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.BALL_MILL_RECIPES)
            .recipeModifiers(BallMillMachine::fixedParallel32, GTRecipeModifiers.OC_NON_PERFECT)
            .tooltips(Component.translatable("gtceu.universal.tooltip.parallel", BallMillMachine.PARALLEL_LIMIT),
                    Component.translatable("susy.multiblock.ball_mill.tooltip.mill_balls",
                            BallMillMachine.MILL_BALL_REQUIREMENT))
            .pattern(definition -> {
                TraceabilityPredicate shell = Predicates.blocks(SusyBlocks.WEAR_RESISTANT_LINED_MILL_SHELL.get());
                return FactoryBlockPattern.start()
                        .aisle(" XMMMXXXXXXXX", "  NMM        ", "             ", "  G          ", "  G          ",
                                "  G          ", "             ", "             ")
                        .aisle(" X          X", "             ", "  G          ", "  HCCCCCCCCH ", "  HCCCCCCCCH ",
                                "  HCCCCCCCCH ", "  G          ", "             ")
                        .aisle(" X          X", " XG         X", " XHCCCCCCCCHX", " XH#####D##HX", " XH#####D##HX",
                                " XH#####D##H ", "  HCCCCCCCCH ", "  G          ")
                        .aisle(" X          X", "  G          ", "  HCCCCCCCCH ", "OXH#####D##HY", "AA######D###Y",
                                "ZXH#####D##HI", "  HCCCCCCCCH ", "  G          ")
                        .aisle(" X          X", " XG         X", " XHCCCCCCCCHX", " XH#####D##HX", " XH#####D##HX",
                                " XH#####D##H ", "  HCCCCCCCCH ", "  G          ")
                        .aisle(" X          X", "             ", "  G          ", "  HCCCCCCCCH ", "  HCCCCCCCCH ",
                                "  HCCCCCCCCH ", "  G          ", "             ")
                        .aisle(" XMMMXXXXXXXX", "  NSM        ", "             ", "  G          ", "  G          ",
                                "  G          ", "             ", "             ")
                        .where('M', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                        true, false, false, false, false, false))
                                .or(Predicates.autoAbilities(true, false, false)))
                        .where('Y', Predicates.abilities(PartAbility.IMPORT_ITEMS).or(shell))
                        .where('Z', Predicates.abilities(PartAbility.EXPORT_FLUIDS).or(shell))
                        .where('I', Predicates.abilities(PartAbility.IMPORT_FLUIDS).or(shell))
                        .where('O', Predicates.abilities(PartAbility.EXPORT_ITEMS).or(shell))
                        .where('A', shell)
                        .where('C', shell)
                        .where('H', Predicates.blocks(SusyBlocks.WEAR_RESISTANT_LINED_SHELL_HEAD.get()))
                        .where('D', Predicates.blocks(SusyBlocks.INTERMEDIATE_DIAPHRAGM.get()))
                        .where('G', SuSyPredicates.axialOrientation(
                                SusyBlocks.STEEL_GIRTH_GEAR_TOOTH.get(), RelativeDirection.LEFT))
                        .where('N', Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                        .where('X', Predicates.frames(GTMaterials.Steel))
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('#', Predicates.air())
                        .where(' ', Predicates.any())
                        .build();
            })
            // TODO)) Phase 6: hide the shell/head/diaphragm blocks while formed and
            // restore the dedicated Ball Mill drum animation and controller overlay.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/gcym/large_maceration_tower"))
            .register();

    public static final MultiblockMachineDefinition CURTAIN_COATER = REGISTRATE
            .multiblock("curtain_coater", CurtainCoaterMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STAINLESS_CLEAN.get())
            .recipeType(SuSyRecipeTypes.CURTAIN_COATER_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("CCKCC", "CWGWC", "  G  ")
                    .aisle("CCCCC", "I>>>O", "CCGCC")
                    .aisle("CCSCC", "CWHWC", "  G  ")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('I', Predicates.abilities(PartAbility.IMPORT_ITEMS))
                    .where('O', Predicates.abilities(PartAbility.EXPORT_ITEMS))
                    .where('H', Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                    .where('K', Predicates.abilities(PartAbility.EXPORT_FLUIDS))
                    .where('C', Predicates.blocks(GTBlocks.CASING_STAINLESS_CLEAN.get())
                            .setMinGlobalLimited(17)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                    true, false, false, false, false, false))
                            .or(Predicates.autoAbilities(true, false, false)))
                    .where('G', Predicates.blocks(GTBlocks.CASING_STAINLESS_STEEL_GEARBOX.get()))
                    .where('W', Predicates.blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .where('>', SuSyPredicates.conveyorBelt(SusyBlocks.LV_CONVEYOR_BELT.get(),
                            RelativeDirection.LEFT))
                    .where(' ', Predicates.any())
                    .build())
            // TODO)) Phase 6: real conveyor directional model; old overlay was BLAST_FURNACE_OVERLAY.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
                    GTCEu.id("block/multiblock/blast_furnace"))
            .register();

    public static final MultiblockMachineDefinition HOT_ISOSTATIC_PRESS = REGISTRATE
            .multiblock("hot_isostatic_press", HotIsostaticPressMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> SusyBlocks.SILICON_CARBIDE_CASING.get())
            .recipeType(SuSyRecipeTypes.HOT_ISOSTATIC_PRESS_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("  SSS  ", "  SFS  ", "  SFS  ", "  SFS  ", "  SFS  ", "  SFS  ", "  SSS  ")
                    .aisle(" SSSSS ", " SIIIS ", " SIIIS ", " SIIIS ", " SIIIS ", " SIIIS ", " SSSSS ")
                    .aisle("SSSSSSS", "SIIIIIS", "SICCCIS", "SICCCIS", "SICCCIS", "SIIIIIS", "SSSSSSS")
                    .aisle("SSSPSSS", "SIIHIIS", "SICXCIS", "SICXCIS", "SICXCIS", "SIIhIIS", "SSSPSSS")
                    .aisle("SSSPSSS", "SIIIIIS", "SICXCIS", "SICXCIS", "SICXCIS", "SIIPIIS", "SSSPSSS")
                    .aisle(" SSPSS ", " SIIIS ", " SIIIS ", " SIIIS ", " SIIIS ", " SIPIS ", " SSPSS ")
                    .aisle("  SPS  ", "  SOS  ", "  SPS  ", "  SPS  ", "  SPS  ", "  SPS  ", "  SPS  ")
                    .where(' ', Predicates.any())
                    .where('O', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('S', Predicates.blocks(SusyBlocks.SILICON_CARBIDE_CASING.get())
                            .setMinGlobalLimited(27)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.autoAbilities(true, false, false)))
                    .where('I', Predicates.blocks(SusyBlocks.SILICON_CARBIDE_CASING.get()))
                    .where('P', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                    .where('C', Predicates.blocks(GTBlocks.COIL_NICHROME.get()))
                    .where('X', Predicates.air())
                    .where('H', SuSyPredicates.orientation(SusyBlocks.HYDRAULIC_CYLINDER.get(),
                            RelativeDirection.UP))
                    .where('h', SuSyPredicates.orientation(SusyBlocks.HYDRAULIC_CYLINDER.get(),
                            RelativeDirection.DOWN))
                    // TODO)) swap Invar to SuSy Incoloy 908 once that material is registered.
                    .where('F', Predicates.frames(GTMaterials.Invar))
                    .build())
            // TODO)) Phase 6: real silicon-carbide casing + FORMING_PRESS_OVERLAY.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    public static final MultiblockMachineDefinition MAGNETIC_REFRIGERATOR = REGISTRATE
            .multiblock("magnetic_refrigerator", MagneticRefrigeratorMachine::new)
            .rotationState(RotationState.ALL)
            .appearanceBlock(() -> GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
            .recipeType(SuSyRecipeTypes.COOLING_RECIPES)
            .recipeModifiers(MagneticRefrigeratorMachine::temperatureGate,
                    GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("XXX", "CCC", "CCC", "XXX")
                    .aisle("XXX", "C#C", "C#C", "XXX")
                    .aisle("XSX", "CCC", "CCC", "XXX")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('X', Predicates.blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get())
                            .setMinGlobalLimited(10)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.autoAbilities(true, false, false)))
                    .where('C', SuSyPredicates.sameTypeVariant("CoolingCoil",
                            "gtceu.multiblock.pattern.error.coils",
                            SusyBlocks.MANGANESE_IRON_ARSENIC_PHOSPHIDE_COOLING_COIL.get(),
                            SusyBlocks.PRASEODYMIUM_NICKEL_COOLING_COIL.get(),
                            SusyBlocks.GADOLINIUM_SILICON_GERMANIUM_COOLING_COIL.get()))
                    .where('#', Predicates.air())
                    .build())
            // TODO)) Phase 6: real cooling-coil textures + magnetic-refrigerator overlay.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_frost_proof"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    public static final MultiblockMachineDefinition SINTERING_OVEN = REGISTRATE
            .multiblock("sintering_oven", SuSySinteringOvenMachine::new)
            .rotationState(RotationState.ALL)
            .appearanceBlock(() -> SusyBlocks.ULV_STRUCTURAL_CASING.get())
            .recipeType(SuSyRecipeTypes.SINTERING_RECIPES)
            .recipeModifiers(SuSySinteringOvenMachine::plasmaGate,
                    GTRecipeModifiers.OC_NON_PERFECT)
            .pattern(definition -> {
                TraceabilityPredicate casing = Predicates.blocks(SusyBlocks.ULV_STRUCTURAL_CASING.get())
                        .setMinGlobalLimited(33);
                return FactoryBlockPattern.start()
                        .aisle("CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC")
                        .aisle("     ", " BBB ", " B#B ", " BBB ", "     ")
                        .aisle("FFFFF", "FBBBF", "FB#BF", " BBB ", "     ")
                        .aisle("     ", " BBB ", " B#B ", " BBB ", "     ")
                        .aisle("FFFFF", "FBBBF", "FB#BF", " BBB ", "     ")
                        .aisle("     ", " BBB ", " B#B ", " BBB ", "     ")
                        .aisle("FFFFF", "FBBBF", "FB#BF", " BBB ", "     ")
                        .aisle("     ", " BBB ", " B#B ", " BBB ", "     ")
                        .aisle("FFFFF", "FBBBF", "FB#BF", " BBB ", "     ")
                        .aisle("     ", " BBB ", " B#B ", " BBB ", "     ")
                        .aisle("DDDDD", "DDSDD", "DDDDD", "DDDDD", "DDDDD")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('D', casing
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                        true, false, false, true, true, false))
                                .or(Predicates.autoAbilities(true, false, false)))
                        .where('C', casing.or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                false, false, true, false, false, true)))
                        .where('F', Predicates.frames(GTMaterials.Steel))
                        .where('B', SuSyPredicates.sameTypeVariant("SinteringBrick",
                                "susy.multiblock.pattern.error.sintering_bricks",
                                SusyBlocks.SINTERING_BRICK.get(),
                                SusyBlocks.MAGNETOPLATED_SINTERING_BRICK.get()))
                        .where('#', Predicates.air())
                        .where(' ', Predicates.any())
                        .build();
            })
            // TODO)) Phase 6: real ULV structural CTM + SINTERING_OVERLAY.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    public static final MultiblockMachineDefinition EVAPORATION_POOL = REGISTRATE
            .multiblock("evaporation_pool", EvaporationPoolMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.LIGHT_CONCRETE.get())
            .recipeType(SuSyRecipeTypes.EVAPORATION_POOL_RECIPES)
            // Gate only: reject malformed/missing energy metadata and tick IO. The pool is
            // solar/coil heated and deliberately has no EU/t overclock modifier.
            .recipeModifier(EvaporationPoolMachine::evaporationEnergyGate)
            .tooltips(Component.translatable("susy.multiblock.evaporation_pool.tooltip.size",
                    EvaporationPoolMachine.MIN_DIAMETER, EvaporationPoolMachine.MAX_DIAMETER))
            .pattern(definition -> {
                TraceabilityPredicate optionalParts = Predicates.abilities(PartAbility.INPUT_ENERGY)
                        .setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1)
                        .or(Predicates.abilities(PartAbility.IMPORT_ITEMS)
                                .setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1))
                        .or(Predicates.abilities(PartAbility.EXPORT_ITEMS)
                                .setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1))
                        .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS)
                                .setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1))
                        .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS)
                                .setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1));
                // Canonical 7x7 fallback; the machine's getPattern() override builds the real
                // variable-size pattern at runtime, so this definition pattern is used only as
                // the memoized definition fallback (the builder requires one).
                return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP,
                        RelativeDirection.BACK)
                        .aisle("CCCCC", "     ")
                        .aisle("CCCCC", " CCC ")
                        .aisle("CBHBC", " C#C ")
                        .aisle("CCCCC", " CCC ")
                        .aisle("CCSCC", "     ")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('C', Predicates.blocks(GTBlocks.LIGHT_CONCRETE.get()).or(optionalParts))
                        .where('B', SuSyPredicates.evaporationBed())
                        .where('H', SuSyPredicates.evaporationCoilsOrBeds())
                        .where('#', Predicates.air())
                        .where(' ', Predicates.any())
                        .build();
            })
            .shapeInfos(definition -> {
                List<MultiblockShapeInfo> shapes = new ArrayList<>();
                MultiblockShapeInfo.ShapeInfoBuilder builder = MultiblockShapeInfo.builder()
                        .aisle("EIOTCCCCC", "         ")
                        .aisle("CCCCCCCCC", " CCCCCCC ")
                        .aisle("CCCBHBCCC", " CC###CC ")
                        .aisle("CCCBHBCCC", " CC###CC ")
                        .aisle("CCCBHBCCC", " CC###CC ")
                        .aisle("CCCBHBCCC", " CC###CC ")
                        .aisle("CCCBHBCCC", " CC###CC ")
                        .aisle("CCCCCCCCC", " CCCCCCC ")
                        .aisle("CCCCSCCCC", "         ")
                        .where('S', definition, Direction.SOUTH)
                        .where('C', GTBlocks.LIGHT_CONCRETE.get())
                        .where('B', SusyBlocks.DIRT_EVAPORATION_BED.get())
                        .where('#', Blocks.AIR.defaultBlockState())
                        .where(' ', Blocks.AIR.defaultBlockState())
                        .where('E', GTMachines.ENERGY_INPUT_HATCH[GTValues.LV], Direction.NORTH)
                        .where('I', GTMachines.FLUID_IMPORT_HATCH[GTValues.LV], Direction.NORTH)
                        .where('O', GTMachines.FLUID_EXPORT_HATCH[GTValues.LV], Direction.NORTH)
                        .where('T', GTMachines.ITEM_EXPORT_BUS[GTValues.LV], Direction.NORTH);
                // One bed-only shape, then one shape per heating-coil tier sorted by tier.
                shapes.add(builder.shallowCopy().where('H', SusyBlocks.DIRT_EVAPORATION_BED.get()).build());
                GTCEuAPI.HEATING_COILS.entrySet().stream()
                        .sorted(Comparator.comparingInt(entry -> entry.getKey().getTier()))
                        .forEach(entry -> shapes.add(
                                builder.shallowCopy().where('H', entry.getValue()).build()));
                return shapes;
            })
            // TODO)) Legacy used SOLID_STEEL_CASING base + BLAST_FURNACE_OVERLAY with a "change
            // to concrete / custom overlay?" TODO; the structural casing is light concrete.
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/electric_blast_furnace"))
            .register();

    // ==================================================================
    // Phase 4c Bucket D2: custom-logic multiblocks bundle.
    // Ported from 1.12.2 specialized controllers. HeatRadiator/Greenhouse/
    // MiningDrill are dynamic-size; the others are plain WorkableElectric
    // subclasses with recipe modifiers.
    // ==================================================================

    // ---- induction_furnace (perfect OC) ----
    public static final MultiblockMachineDefinition INDUCTION_FURNACE = REGISTRATE
            .multiblock("induction_furnace", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.INDUCTION_FURNACE_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_PERFECT)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle(" AAA ", " AAA ", " AAA ")
                    .aisle("AAAAA", "ACCCA", "AAAAA")
                    .aisle("AAAAA", "AC#CA", "AA#AA")
                    .aisle("AAAAA", "ACCCA", "AAAAA")
                    .aisle(" AAA ", " ASA ", " AAA ")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('A', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(3))
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMinGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1)))
                    .where('C', Predicates.blocks(SusyBlocks.COPPER_INDUCTION_COIL_ASSEMBLY.get()))
                    .where(' ', Predicates.any())
                    .where('#', Predicates.air())
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/pyrolyse_oven"))
            .register();

    // ---- metallurgical_converter (SuSy pure-parallel x64 + non-perfect OC; muffler) ----
    public static final MultiblockMachineDefinition METALLURGICAL_CONVERTER = REGISTRATE
            .multiblock("metallurgical_converter", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.METALLURGICAL_CONVERTER_RECIPES)
            .recipeModifiers(true, GTRecipeModifiers.OC_NON_PERFECT,
                    MetallurgicalConverterLogic::pureParallel)
            .tooltipBuilder((stack, tooltip) -> tooltip
                    .add(Component.translatable("susy.machine.parallel_pure", 64)))
            .pattern(definition -> FactoryBlockPattern
                    .start(RelativeDirection.BACK, RelativeDirection.UP, RelativeDirection.RIGHT)
                    .aisle(" F   F ", " F   F ", " F   F ", " F   F ", " FF FF ", "  FAF  ",
                            "  AGA  ", "   A   ", "       ", "       ")
                    .aisle("HHHHHH ", "HFFFFF ", "H ###  ", "  ###  ", "  BBB  ", "  VVV  ",
                            "  BBB  ", "  VVV  ", "  BBB  ", "       ")
                    .aisle(" HHHHHH", " H###FH", " ##### ", " #BBB# ", " BRRRB ", " VRRRV ",
                            " BRRRB ", " VRRRV ", " BRRRB ", "  BBB  ")
                    .aisle("  HHHHH", "  #P#FS", " ##P## ", " #BBB# ", " BRRRB ", " VR#RV ",
                            " BR#RB ", " VR#RV ", " BR#RB ", "  BMB  ")
                    .aisle(" HHHHHH", " H###FH", " ##### ", " #BBB# ", " BRRRB ", " VRRRV ",
                            " BRRRB ", " VRRRV ", " BRRRB ", "  BBB  ")
                    .aisle("HHHHHH ", "HFFFFF ", "H ###  ", "  ###  ", "  BBB  ", "  VVV  ",
                            "  BBB  ", "  VVV  ", "  BBB  ", "       ")
                    .aisle(" F   F ", " F   F ", " F   F ", " F   F ", " FF FF ", "  FAF  ",
                            "  AGA  ", "   A   ", "       ", "       ")
                    .where('#', Predicates.air())
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('R', Predicates.blocks(SusyBlocks.TABULAR_ALUMINA_REFRACTORY.get()))
                    .where('H', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .setMinGlobalLimited(31)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                    true, false, true, true, true, true))
                            .or(Predicates.autoAbilities(true, false, false)))
                    .where('A', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()))
                    .where('B', Predicates.blocks(GCYMBlocks.CASING_STRESS_PROOF.get()))
                    .where('F', Predicates.frames(GTMaterials.Steel))
                    .where('V', Predicates.blocks(GCYMBlocks.HEAT_VENT.get()))
                    .where('G', Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                    .where('P', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                    .where('M', Predicates.abilities(PartAbility.MUFFLER))
                    .where(' ', Predicates.any())
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    // ---- reverberatory_furnace (NoEnergy: recipes EUt(0); no INPUT_ENERGY ability) ----
    public static final MultiblockMachineDefinition REVERBERATORY_FURNACE = REGISTRATE
            .multiblock("reverberatory_furnace", ReverberatoryFurnaceMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(() -> GTBlocks.CASING_PRIMITIVE_BRICKS.get())
            .recipeType(SuSyRecipeTypes.REVERBERATORY_FURNACE_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .allowExtendedFacing(false)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("XXX", "XXX", "XXX", "YYY")
                    .aisle("XXX", "X#X", "X#X", "Y#Y")
                    .aisle("XXX", "X#X", "X#X", "YYY")
                    .aisle("XXX", "X#X", "X#X", "YYY")
                    .aisle("XXX", "X#X", "X#X", "YYY")
                    .aisle("XXX", "X#X", "X#X", "YYY")
                    .aisle("XXX", "XSX", "XXX", "YYY")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('Y', Predicates.blocks(GTBlocks.CASING_PRIMITIVE_BRICKS.get()))
                    .where('X', Predicates.blocks(GTBlocks.CASING_PRIMITIVE_BRICKS.get())
                            .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                    false, false, true, true, true, true)))
                    .where('#', Predicates.air())
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_primitive_bricks"),
                    GTCEu.id("block/multiblock/primitive_blast_furnace"))
            .register();

    // ---- railroad_engineering_station (deferred-scope IR placeholder + mining-fatigue aura) ----
    public static final MultiblockMachineDefinition RAILROAD_ENGINEERING_STATION = REGISTRATE
            .multiblock("railroad_engineering_station", RailroadEngineeringStationMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.RAILROAD_ENGINEERING_STATION_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .tooltips(Component.translatable("susy.machine.railroad_engineering_station.tooltip"),
                    Component.translatable("susy.machine.railroad_engineering_station.tooltip.1"))
            .pattern(definition -> {
                TraceabilityPredicate solidSteel = Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get());
                TraceabilityPredicate concrete = Predicates.blocks(GTBlocks.LIGHT_CONCRETE.get());
                return FactoryBlockPattern.start()
                        .aisle("                 ", "                 ", "                 ", "                 ",
                                "                 ", "                 ", "                 ", "                 ",
                                "       F F       ", "                 ")
                        .aisle("                 ", "                 ", "                 ", "                 ",
                                "                 ", "                 ", "                 ", "                 ",
                                "       F F       ", "                 ")
                        .aisle("                 ", "                 ", "                 ", "                 ",
                                "                 ", "                 ", "                 ", "                 ",
                                "       F F       ", "                 ")
                        .aisle("  CCC  BBB  CCC  ", "  CCC  BBB  CCC  ", "   C    B    C   ", "                 ",
                                "                 ", "                 ", "                 ", "       F F       ",
                                "       F F       ", "       F F       ")
                        .aisle("CCCCCCCCCCCCCCCCC", "CCCFCCCCFCCCCFCCC", "CCCFCCCCFCCCCFCCC", "   F    F    F   ",
                                "   F    F    F   ", "   F    F    F   ", "   F    F    F   ", " FFFFFFFFFFFFFFF ",
                                "  FF   FGF   FF  ", "       F F       ")
                        .aisle("CCCCCCCCCCCCCCCCC", "CCCCCCCCCCCCCCCCC", "   C    C    C   ", "                 ",
                                "                 ", "                 ", "                 ", "       F F       ",
                                "       F F       ", "       F F       ")
                        .aisle("CCCCCCCCCCCCCCCCC", "                 ", "                 ", "                 ",
                                "                 ", "                 ", "                 ", "                 ",
                                "       F F       ", "                 ")
                        .aisle("CCCCCCCCCCCCCCCCC", "RRRRRRRRRRRRRRRRR", "                 ", "                 ",
                                "                 ", "                 ", "                 ", "       MMM       ",
                                "       FGF       ", "       MMM       ")
                        .aisle("                 ", "RRRRRRRRRRRRRRRRR", "                 ", "                 ",
                                "                 ", "                 ", "                 ", "                 ",
                                "       FMF       ", "                 ")
                        .aisle("CCCCCCCCCCCCCCCCC", "RRRRRRRRRRRRRRRRR", "                 ", "                 ",
                                "                 ", "                 ", "                 ", "       MMM       ",
                                "       FGF       ", "       MAM       ")
                        .aisle("CCCCCCCCCCCCCCCCC", "                 ", "                 ", "                 ",
                                "                 ", "                 ", "                 ", "                 ",
                                "       F F       ", "                 ")
                        .aisle("CCCCCCCCCCCCCCCCC", "CCCCCCCCCCCCCCCCC", "   C    C    C   ", "                 ",
                                "                 ", "                 ", "                 ", "       F F       ",
                                "       F F       ", "       F F       ")
                        .aisle("CCCCCCCCCCCCCCCCC", "CCCFCCCCFCCCCFCCC", "CCCFCCCCFCCCCFCCC", "   F    F    F   ",
                                "   F    F    F   ", "   F    F    F   ", "   F    F    F   ", " FFFFFFFFFFFFFFF ",
                                "  FF   FGF   FF  ", "       F F       ")
                        .aisle("  CCC  CCC  CCC  ", "  CCC  CSC  CCC  ", "   C    C    C   ", "                 ",
                                "                 ", "                 ", "                 ", "       F F       ",
                                "       F F       ", "       F F       ")
                        .aisle("                 ", "                 ", "                 ", "                 ",
                                "                 ", "                 ", "                 ", "                 ",
                                "       F F       ", "                 ")
                        .aisle("                 ", "                 ", "                 ", "                 ",
                                "                 ", "                 ", "                 ", "                 ",
                                "       F F       ", "                 ")
                        .aisle("                 ", "                 ", "                 ", "                 ",
                                "                 ", "                 ", "                 ", "                 ",
                                "       F F       ", "                 ")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('F', Predicates.frames(GTMaterials.Steel))
                        .where('M', solidSteel)
                        .where('G', Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                        .where('C', concrete)
                        .where('A', solidSteel.or(Predicates.autoAbilities(true, false, false)))
                        .where('B', concrete.or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                true, false, true, false, true, false)))
                        .where('R', RailroadEngineeringStationMachine.rails())
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    // ---- heat_radiator (dynamic-size no-energy radiator) ----
    public static final MultiblockMachineDefinition HEAT_RADIATOR = REGISTRATE
            .multiblock("heat_radiator", HeatRadiatorMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.HEAT_RADIATOR_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
            .tooltips(Component.translatable("susy.machine.heat_radiator.tooltip.1"),
                    Component.translatable("susy.machine.heat_radiator.tooltip.2"))
            .pattern(definition -> HeatRadiatorMachine.buildPattern(definition,
                    HeatRadiatorMachine.MIN_HEIGHT, HeatRadiatorMachine.MIN_RADIUS))
            .shapeInfos(HeatRadiatorMachine::buildShapeInfos)
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    // ---- large_fluid_pump (perfect OC + pure-parallel x256) ----
    public static final MultiblockMachineDefinition LARGE_FLUID_PUMP = REGISTRATE
            .multiblock("large_fluid_pump", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyWorldgenRecipeTypes.PUMPING_RECIPES)
            .recipeModifiers(GTRecipeModifiers.OC_PERFECT,
                    (machine, recipe) -> SuSyParallelLogic.pureParallel(machine, recipe, 256))
            .tooltips(Component.translatable("susy.machine.large_fluid_pump.tooltip.1"),
                    Component.translatable("susy.machine.large_fluid_pump.tooltip.2"))
            .pattern(definition -> FactoryBlockPattern
                    .start(RelativeDirection.RIGHT, RelativeDirection.FRONT, RelativeDirection.UP)
                    .aisle("       ", "      P", "       ")
                    .aisle("       ", "      P", "       ")
                    .aisle("FCCCC  ", "CCCCC P", "FCECC  ")
                    .aisle("CCSGC  ", "OPPPPPP", "CCEGC  ")
                    .aisle("FCCC   ", "CCCCC  ", "FCEC   ")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('P', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                    .where('G', Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                    .where('F', Predicates.frames(GTMaterials.Steel))
                    .where('C', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setExactLimit(1))
                            .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                    false, false, true, true, true, true)))
                    .where('E', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY)
                                    .setMinGlobalLimited(1).setMaxGlobalLimited(2)))
                    .where('O', Predicates.abilities(PartAbility.EXPORT_FLUIDS))
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/fluid_drilling_rig"))
            .register();

    // ---- mixer_settler_v2 (dynamic-width cells 2..20; cell gate + pure-parallel x16) ----
    public static final MultiblockMachineDefinition MIXER_SETTLER_V2 = REGISTRATE
            .multiblock("mixer_settler_v2", MixerSettlerMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .langValue("Mixer Settler")
            .appearanceBlock(() -> GTBlocks.CASING_STAINLESS_CLEAN.get())
            .recipeType(SuSyRecipeTypes.MIXER_SETTLER_RECIPES)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT,
                    MixerSettlerMachine::cellModifier,
                    (machine, recipe) -> SuSyParallelLogic.pureParallel(machine, recipe, 16))
            .tooltips(Component.translatable("susy.machine.mixer_settler.tooltip.1"),
                    Component.translatable("susy.machine.mixer_settler.tooltip.2"))
            .pattern(definition -> MixerSettlerMachine.buildPattern(MixerSettlerMachine.MIN_CELLS, definition))
            .shapeInfos(definition -> {
                List<MultiblockShapeInfo> shapes = new ArrayList<>();
                for (int cells = MixerSettlerMachine.MIN_CELLS; cells <= MixerSettlerMachine.MAX_CELLS; cells += 2) {
                    shapes.add(new MultiblockShapeInfo(MixerSettlerMachine
                            .buildPattern(cells, definition)
                            .getPreview(new int[] { 1, 1, 1, 1, 1, 1 })));
                }
                return shapes;
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    // ---- mixer_settler (V1 "Mixer Settler (Old)": dynamic 5x5-aisle structure) ----
    public static final MultiblockMachineDefinition MIXER_SETTLER = REGISTRATE
            .multiblock("mixer_settler", MixerSettlerV1Machine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .langValue("Mixer Settler (Old)")
            .appearanceBlock(() -> GTBlocks.CASING_STAINLESS_CLEAN.get())
            .recipeType(SuSyRecipeTypes.MIXER_SETTLER_RECIPES)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT,
                    MixerSettlerV1Machine::cellModifier,
                    (machine, recipe) -> SuSyParallelLogic.pureParallel(machine, recipe,
                            MixerSettlerV1Machine.PARALLEL_LIMIT))
            .tooltips(Component.translatable("susy.machine.mixer_settler.tooltip.1"),
                    Component.translatable("susy.machine.mixer_settler.tooltip.2"))
            .pattern(definition -> MixerSettlerV1Machine.createPattern(MixerSettlerV1Machine.MIN_RADIUS,
                    definition))
            .shapeInfos(definition -> {
                List<MultiblockShapeInfo> shapes = new ArrayList<>();
                for (int radius = MixerSettlerV1Machine.MIN_RADIUS; radius <= MixerSettlerV1Machine.MAX_RADIUS;
                        radius += 2) {
                    String[] rows = MixerSettlerV1Machine.buildVoxelStrings(radius);
                    char[] controllerRow = rows[22].toCharArray();
                    controllerRow[radius - 1] = 'Y';
                    controllerRow[radius + 1] = 'B';
                    rows[22] = new String(controllerRow);
                    shapes.add(MultiblockShapeInfo.builder()
                            .aisle(rows[0], rows[1], rows[2], rows[3], rows[4])
                            .aisle(rows[5], rows[6], rows[7], rows[8], rows[9])
                            .aisle(rows[10], rows[11], rows[12], rows[13], rows[14])
                            .aisle(rows[15], rows[16], rows[17], rows[18], rows[19])
                            .aisle(rows[20], rows[21], rows[22], rows[23], rows[24])
                            .where('S', definition, Direction.SOUTH)
                            .where('Y', GTMachines.ENERGY_INPUT_HATCH[GTValues.LV], Direction.NORTH)
                            .where('B', GTMachines.ITEM_IMPORT_BUS[GTValues.LV], Direction.NORTH)
                            .where('I', GTMachines.FLUID_IMPORT_HATCH[GTValues.LV], Direction.NORTH)
                            .where('O', GTMachines.FLUID_EXPORT_HATCH[GTValues.LV], Direction.NORTH)
                            .where('T', SusyBlocks.COALESCENCE_PLATE.get())
                            .where('P', GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get())
                            .where('D', GTBlocks.CASING_STAINLESS_CLEAN.get())
                            .where('C', GTBlocks.CASING_STAINLESS_CLEAN.get())
                            .where('G', GTBlocks.CASING_STAINLESS_CLEAN.get())
                            .where('M', GTBlocks.CASING_STAINLESS_STEEL_GEARBOX.get())
                            .where('F', GTMaterialBlocks.MATERIAL_BLOCKS
                                    .get(TagPrefix.frameGt, GTMaterials.StainlessSteel).get())
                            .where('E', GTBlocks.CASING_STEEL_SOLID.get())
                            .where(' ', Blocks.AIR.defaultBlockState())
                            .where('#', Blocks.AIR.defaultBlockState())
                            .build());
                }
                return shapes;
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    // ---- mining_drill (perfect-OC deposit consumer) ----
    public static final MultiblockMachineDefinition MINING_DRILL = REGISTRATE
            .multiblock("mining_drill", MiningDrillMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.MINING_DRILL_RECIPES)
            .recipeModifier(GTRecipeModifiers.OC_PERFECT)
            .tooltips(Component.translatable("susy.machine.mining_drill.tooltip.1"),
                    Component.translatable("susy.machine.mining_drill.tooltip.2"))
            .pattern(MiningDrillMachine::buildPattern)
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    // ---- greenhouse (variable-length cells; cell-count parallel) ----
    public static final MultiblockMachineDefinition GREENHOUSE = REGISTRATE
            .multiblock("greenhouse", GreenhouseMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .recipeType(SuSyRecipeTypes.GREENHOUSE_PLANT_RECIPES)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT, GreenhouseMachine::cellParallelModifier)
            .tooltips(Component.translatable("susy.machine.greenhouse.tooltip.1"),
                    Component.translatable("susy.machine.greenhouse.tooltip.2"))
            .pattern(definition -> GreenhouseMachine.buildPattern(1, definition))
            .shapeInfos(GreenhouseMachine::buildShapeInfos)
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    // ---- dumper (liquid voiding) ----
    public static final MultiblockMachineDefinition DUMPER = REGISTRATE
            .multiblock("dumper", DumperMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .tooltips(Component.translatable("susy.machine.dumper.tooltip.1", 16000),
                    Component.translatable("susy.machine.dumper.tooltip.2"))
            .pattern(DumperMachine::buildPattern)
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    // ---- flare_stack (gas/liquid incinerator) ----
    public static final MultiblockMachineDefinition FLARE_STACK = REGISTRATE
            .multiblock("flare_stack", FlareStackMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .tooltips(Component.translatable("susy.machine.flare_stack.tooltip.1", 1000),
                    Component.translatable("susy.machine.flare_stack.tooltip.2"))
            .pattern(FlareStackMachine::buildPattern)
            .shapeInfo(definition -> {
                var pattern = FlareStackMachine.buildPattern(definition);
                int[] repetitions = new int[] { 1, 3, 1 };
                return new MultiblockShapeInfo(pattern.getPreview(repetitions));
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

    // ---- smoke_stack (gas vent) ----
    public static final MultiblockMachineDefinition SMOKE_STACK = REGISTRATE
            .multiblock("smoke_stack", SmokeStackMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .appearanceBlock(() -> GTBlocks.CASING_STEEL_SOLID.get())
            .tooltips(Component.translatable("susy.machine.smoke_stack.tooltip.1", 1000),
                    Component.translatable("susy.machine.smoke_stack.tooltip.2"))
            .pattern(SmokeStackMachine::buildPattern)
            .shapeInfo(definition -> {
                var pattern = SmokeStackMachine.buildPattern(definition);
                int[] repetitions = new int[] { 1, 3, 1 };
                return new MultiblockShapeInfo(pattern.getPreview(repetitions));
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/multiblock_workable"))
            .register();

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
        ResourceLocation pipeOverlay = GTCEu.id(slots >= 9 ?
                "block/overlay/machine/overlay_pipe_9x" :
                "block/overlay/machine/overlay_pipe_4x");
        ResourceLocation ioOverlay = GTCEu.id(io == IO.OUT ?
                "block/overlay/machine/overlay_fluid_hatch_output" :
                "block/overlay/machine/overlay_fluid_hatch_input");
        ResourceLocation emissiveOverlay = GTCEu.id(io == IO.OUT ?
                "block/overlay/machine/overlay_pipe_out_emissive" :
                "block/overlay/machine/overlay_pipe_in_emissive");
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
