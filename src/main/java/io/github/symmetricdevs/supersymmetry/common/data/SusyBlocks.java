package io.github.symmetricdevs.supersymmetry.common.data;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.models.GTModels;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tterrag.registrate.util.entry.BlockEntry;

import io.github.symmetricdevs.supersymmetry.api.registry.SusyRegistration;
import io.github.symmetricdevs.supersymmetry.common.block.DirectionalOrientableBlock;
import io.github.symmetricdevs.supersymmetry.common.block.HorizontalOrientableBlock;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;

import java.util.function.Supplier;

import static io.github.symmetricdevs.supersymmetry.SuSyValues.susyId;

/**
 * SuSy block registry — <b>Phase 4c casing foundation</b>. Ported from the 1.12.2
 * {@code SuSyBlocks} / {@code SuSyMetaBlocks} (a reflection-registered set of
 * {@code VariantBlock} / {@code VariantActiveBlock} classes) onto the
 * GTCEu-Modern registrate idiom: one block per casing variant via
 * {@code REGISTRATE.block(...)}.
 * <p>
 * <b>Scope:</b> this file currently registers only the <em>casing blocks the
 * multiblock controllers reference</em> (Buckets B/C/D of the 4c port). Decorative
 * / non-casing blocks (concrete, regolith, hardened, wool, structural, deposit,
 * resource, stone variants, sheeted frames, rocket casings, …) port with their own
 * Phase 5 scope. Sheet/resource/stone blocks that hang off materials or worldgen are
 * deliberately left out here.
 * <p>
 * <b>Textures are placeholders.</b> The 1.12.2 art lives under the legacy
 * {@code assets/gregtech/textures/blocks/**} namespace with {@code assets/susy/}
 * forge_marker blockstates — both invalid in 1.20.1. Every block below points at a
 * GTCEu stock texture so it registers and renders sanely; the real SuSy texture
 * paths (1.12.2 {@code gregtech:blocks/...}) are recorded next to each entry as a
 * {@code // TODO))} for the Phase 6 texture/blockstate/CTM migration.
 * <p>
 * Several 1.12.2 variants were <em>rotatable</em> (conveyor, separator rotor,
 * alternator coil, turbine rotor, metallurgy rolls, engine casing 2, girth gear) or
 * <em>active</em> (tank walls, electrodes, serpentine, sintering bricks, active
 * casing). They register here as plain cube-all / {@link ActiveBlock} blocks — the
 * rotation/animation behaviour is a {@code // TODO))} Phase 6 concern (modern model +
 * optional {@code BlockEntity} rotation), matching how the controllers only need the
 * block to exist in the pattern.
 */
public final class SusyBlocks {

    private static final GTRegistrate REGISTRATE = SusyRegistration.REGISTRATE;

    // ==================================================================
    // Registration helpers (GTCEu createCasingBlock/createActiveCasing,
    // reimplemented scoped to the SuSy registrate — GTCEu's own helpers use its
    // REGISTRATE internally, so we can't reuse them).
    // ==================================================================

    /** Plain cube-all casing (mirrors {@code GTBlocks.createCasingBlock}). */
    private static BlockEntry<Block> createCasingBlock(String name, ResourceLocation texture) {
        return REGISTRATE.block(name, Block::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::solid)
                .exBlockstate(GTModels.cubeAllModel(texture))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    /**
     * Active casing: an {@link ActiveBlock} with an {@code ACTIVE} boolean state,
     * switching between an inactive and an {@code _active} cube-all texture.
     * Generates the two cube-all models directly (no pre-authored JSON model file
     * needed, unlike {@code GTModels.createActiveModel}).
     */
    private static BlockEntry<ActiveBlock> createActiveCasingBlock(String name, ResourceLocation inactive,
                                                                   ResourceLocation active) {
        return REGISTRATE.block(name, ActiveBlock::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::cutoutMipped)
                .blockstate((ctx, prov) -> {
                    var inactiveModel = prov.models().cubeAll(ctx.getName(), inactive);
                    var activeModel = prov.models().cubeAll(ctx.getName() + "_active", active);
                    prov.getVariantBuilder(ctx.getEntry())
                            .partialState().with(GTBlockStateProperties.ACTIVE, false)
                            .modelForState().modelFile(inactiveModel).addModel()
                            .partialState().with(GTBlockStateProperties.ACTIVE, true)
                            .modelForState().modelFile(activeModel).addModel();
                })
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    /**
     * Horizontal-orientable casing: a {@link HorizontalDirectionalBlock} carrying a
     * {@code FACING} property. Registers a single cube-all model whose front face is
     * rotated by the FACING state, so the block forms multiblock structures now and the
     * controller can auto-orient it in {@code onStructureFormed} (Bucket C/D
     * {@code horizontalOrientation} predicates). The 1.12.2 art orients a distinct
     * front texture per-facing; that directional texture model is a {@code // TODO))}
     * Phase 6 concern — for now the same texture shows on all faces.
     */
    private static BlockEntry<HorizontalOrientableBlock> createHorizontalOrientableCasingBlock(String name, ResourceLocation texture) {
        return REGISTRATE.block(name, HorizontalOrientableBlock::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::solid)
                .blockstate((ctx, prov) -> {
                    var model = prov.models().cubeAll(ctx.getName(), texture);
                    prov.getVariantBuilder(ctx.getEntry())
                            .forAllStates(state -> {
                                int yRot = switch (state.getValue(HorizontalDirectionalBlock.FACING)) {
                                    case EAST -> 90;
                                    case SOUTH -> 180;
                                    case WEST -> 270;
                                    default -> 0; // NORTH
                                };
                                return net.minecraftforge.client.model.generators.ConfiguredModel.builder()
                                        .modelFile(model)
                                        .rotationY(yRot)
                                        .build();
                            });
                })
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    /** Six-way directional casing used by vertical structure parts. */
    private static BlockEntry<DirectionalOrientableBlock> createDirectionalOrientableCasingBlock(String name,
                                                                                                    ResourceLocation texture) {
        return REGISTRATE.block(name, DirectionalOrientableBlock::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::solid)
                .blockstate((ctx, prov) -> {
                    var model = prov.models().cubeAll(ctx.getName(), texture);
                    prov.getVariantBuilder(ctx.getEntry())
                            .forAllStates(state -> {
                                var direction = state.getValue(DirectionalBlock.FACING);
                                int xRot = direction == net.minecraft.core.Direction.DOWN ? 90 :
                                        direction == net.minecraft.core.Direction.UP ? 270 : 0;
                                int yRot = direction.getAxis().isVertical() ? 0 :
                                        ((int) direction.toYRot() + 180) % 360;
                                return net.minecraftforge.client.model.generators.ConfiguredModel.builder()
                                        .modelFile(model)
                                        .rotationX(xRot)
                                        .rotationY(yRot)
                                        .build();
                            });
                })
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    /** Axial casing whose working axis is corrected after multiblock formation. */
    private static BlockEntry<RotatedPillarBlock> createAxialOrientableCasingBlock(String name,
                                                                                   ResourceLocation texture) {
        return REGISTRATE.block(name, RotatedPillarBlock::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::solid)
                .blockstate((ctx, prov) -> prov.axisBlock(ctx.get(), texture, texture))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    // Placeholder textures (guaranteed to exist in GTCEu). The real SuSy texture for
    // each block is recorded in a trailing TODO)) comment with its 1.12.2 path.
    private static final ResourceLocation TEX_STEEL = com.gregtechceu.gtceu.GTCEu
            .id("block/casings/solid/machine_casing_solid_steel");
    private static final ResourceLocation TEX_STEEL_PIPE = com.gregtechceu.gtceu.GTCEu
            .id("block/casings/pipe/machine_casing_pipe_steel");
    private static final ResourceLocation TEX_GEARBOX = com.gregtechceu.gtceu.GTCEu
            .id("block/casings/gearbox/machine_casing_gearbox_steel");
    private static final ResourceLocation TEX_FROSTPROOF = com.gregtechceu.gtceu.GTCEu
            .id("block/casings/solid/machine_casing_frost_proof");
    private static final ResourceLocation TEX_GRATE = com.gregtechceu.gtceu.GTCEu
            .id("block/casings/pipe/machine_casing_grate");
    private static final ResourceLocation TEX_ASSEMBLY = com.gregtechceu.gtceu.GTCEu
            .id("block/casings/mechanic/machine_casing_assembly_control");

    // ==================================================================
    // BlockSuSyMultiblockCasing (1.12.2 "susy_multiblock_casing", 14 variants)
    // 1.12.2 textures: gregtech:blocks/multiblock_casing/<name>
    // ==================================================================
    public static final BlockEntry<Block> SILICON_CARBIDE_CASING = createCasingBlock("silicon_carbide_casing", TEX_STEEL); // TODO)) tex gregtech:blocks/multiblock_casing/silicon_carbide_casing
    public static final BlockEntry<Block> SIEVE_TRAY = createCasingBlock("sieve_tray", TEX_STEEL); // TODO)) tex .../multiblock_casing/sieve_tray
    public static final BlockEntry<Block> STRUCTURAL_PACKING = createCasingBlock("structural_packing", TEX_STEEL); // TODO)) tex .../multiblock_casing/structural_packing
    public static final BlockEntry<Block> ULV_STRUCTURAL_CASING = createCasingBlock("ulv_structural_casing", TEX_STEEL); // TODO)) tex .../multiblock_casing/ulv_structural_casing
    public static final BlockEntry<Block> DRONE_PAD = createCasingBlock("drone_pad", TEX_STEEL); // TODO)) tex .../multiblock_casing/drone_pad
    public static final BlockEntry<Block> MONEL_500_CASING = createCasingBlock("monel_casing", TEX_STEEL); // TODO)) tex .../multiblock_casing/monel_500_casing
    public static final BlockEntry<Block> MONEL_500_PIPE = createCasingBlock("monel_casing_pipe", TEX_STEEL_PIPE); // TODO)) tex .../multiblock_casing/monel_500_casing_pipe
    public static final BlockEntry<Block> COPPER_PIPE = createCasingBlock("copper_casing_pipe", TEX_STEEL_PIPE); // TODO)) tex .../multiblock_casing/copper_casing_pipe
    public static final BlockEntry<Block> HEAVY_DUTY_PAD = createCasingBlock("heavy_duty_pad", TEX_STEEL); // TODO)) tex .../multiblock_casing/heavy_duty_pad (rocket/launch scope)
    public static final BlockEntry<Block> TABULAR_ALUMINA_REFRACTORY = createCasingBlock("tabular_alumina_refractory", TEX_STEEL); // TODO)) tex .../multiblock_casing/tabular_alumina_refractory
    public static final BlockEntry<Block> COALESCENCE_PLATE = createCasingBlock("coalescence_plate", TEX_STEEL); // TODO)) tex .../multiblock_casing/coalescence_plate
    public static final BlockEntry<Block> SYNTHETIC_MULLITE_REFRACTORY = createCasingBlock("synthetic_mullite_refractory", TEX_STEEL); // TODO)) tex .../multiblock_casing/synthetic_mullite_refractory
    public static final BlockEntry<Block> HYDROSTATIC_CASING = createCasingBlock("hydrostatic_casing", TEX_STEEL); // TODO)) tex .../multiblock_casing/hydrostatic_casing
    public static final BlockEntry<Block> ALUMINIUM_GEARBOX = createCasingBlock("aluminium_gearbox", TEX_GEARBOX); // TODO)) tex .../multiblock_casing/aluminium_gearbox

    // ==================================================================
    // BlockGrinderCasing ("grinder_casing", 5 variants) — BallMill/AttritionScrubber/
    // EccentricRollCrusher/RotaryKilnV2. tex gregtech:blocks/casings/grinder_casing/<name>
    // ==================================================================
    public static final BlockEntry<Block> ABRASION_RESISTANT_CASING = createCasingBlock("abrasion_resistant_casing", TEX_STEEL); // TODO)) tex .../grinder_casing/abrasion_resistant_casing
    public static final BlockEntry<Block> HYDRAULIC_MECHANICAL_GEARBOX = createCasingBlock("hydraulic_mechanical_gearbox", TEX_GEARBOX); // TODO)) tex .../grinder_casing/hydraulic_mechanical_gearbox
    public static final BlockEntry<Block> WEAR_RESISTANT_LINED_MILL_SHELL = createCasingBlock("wear_resistant_lined_mill_shell", TEX_STEEL); // TODO)) tex .../grinder_casing/wear_resistant_lined_mill_shell
    public static final BlockEntry<Block> WEAR_RESISTANT_LINED_SHELL_HEAD = createCasingBlock("wear_resistant_lined_shell_head", TEX_STEEL); // TODO)) tex .../grinder_casing/wear_resistant_lined_shell_head
    public static final BlockEntry<Block> INTERMEDIATE_DIAPHRAGM = createCasingBlock("intermediate_diaphragm", TEX_STEEL); // TODO)) tex .../grinder_casing/intermediate_diaphragm

    // ==================================================================
    // Metallurgy family (strand line). 1.12.2 rotatable -> plain here; rotation is a
    // Phase 6 TODO. tex gregtech:blocks/casings/metallurgy*/<name>
    // ==================================================================
    public static final BlockEntry<DirectionalOrientableBlock> HYDRAULIC_CYLINDER = createDirectionalOrientableCasingBlock("hydraulic_cylinder", TEX_STEEL); // TODO)) directional front tex (BlockMetallurgy); tex .../metallurgy/hydraulic_cylinder
    public static final BlockEntry<Block> FLYING_SHEAR_SAW = createCasingBlock("flying_shear_saw", TEX_STEEL); // TODO)) rotatable (BlockMetallurgy2, horizontal); tex .../metallurgy_2/flying_shear_saw
    public static final BlockEntry<Block> POLYSTYRENE_WALL = createCasingBlock("polystyrene_wall", TEX_STEEL); // TODO)) tex .../metallurgy_2/polystyrene_wall
    public static final BlockEntry<Block> METALLURGY_ROLL = createCasingBlock("metallurgy_roll", TEX_STEEL); // TODO)) rotatable (BlockMetallurgyRoll, axial); tex .../metallurgy_roll/roll

    // ==================================================================
    // BlockMultiblockTank (active; "multiblock_tank", clarifier + flotation walls)
    // tex gregtech:blocks/casings/multiblock_tank/<name>[ / _on]
    // ==================================================================
    public static final BlockEntry<ActiveBlock> CLARIFIER_MULTIBLOCK_TANK = createActiveCasingBlock("clarifier_multiblock_tank", TEX_STEEL, TEX_STEEL); // TODO)) tex .../multiblock_tank/clarifier + clarifier_on (CTM)
    public static final BlockEntry<ActiveBlock> FLOTATION_MULTIBLOCK_TANK = createActiveCasingBlock("flotation_multiblock_tank", TEX_STEEL, TEX_STEEL); // TODO)) tex .../multiblock_tank/flotation + flotation_on (CTM)

    // ==================================================================
    // Electrodes / coils. tex gregtech:blocks/casings/<dir>/<name>
    // ==================================================================
    public static final BlockEntry<ActiveBlock> CARBON_ELECTRODE_ASSEMBLY = createActiveCasingBlock("carbon_electrode_assembly", TEX_ASSEMBLY, TEX_ASSEMBLY); // TODO)) tex .../electrode_assembly/carbon (+active)
    public static final BlockEntry<Block> COPPER_INDUCTION_COIL_ASSEMBLY = createCasingBlock("copper_induction_coil_assembly", TEX_STEEL); // TODO)) tex .../casings/coils/induction_coil_assembly (CTM)
    public static final BlockEntry<Block> COPPER_TUNGSTEN_EDM_ELECTRODE = createCasingBlock("copper_tungsten_edm_electrode", TEX_STEEL); // TODO)) tex .../edm_electrode/copper_tungsten

    // Cooling coils (MagneticRefrigerator). 1.12.2 BlockCoolingCoil is ACTIVE and carries
    // a per-type coil temperature used to gate recipes (CoilingCoilTemperatureProperty).
    // TODO)) cooling-coil temperature stats + the recipe gate port with the machine (4c-D).
    public static final BlockEntry<ActiveBlock> MANGANESE_IRON_ARSENIC_PHOSPHIDE_COOLING_COIL = createActiveCasingBlock("manganese_iron_arsenic_phosphide_cooling_coil", TEX_FROSTPROOF, TEX_FROSTPROOF); // TODO)) temp 160; tex .../coils/machine_coil_manganese_iron_arsenic_phosphide
    public static final BlockEntry<ActiveBlock> PRASEODYMIUM_NICKEL_COOLING_COIL = createActiveCasingBlock("praseodymium_nickel_cooling_coil", TEX_FROSTPROOF, TEX_FROSTPROOF); // TODO)) temp 50; tex .../coils/machine_coil_praseodymium_nickel
    public static final BlockEntry<ActiveBlock> GADOLINIUM_SILICON_GERMANIUM_COOLING_COIL = createActiveCasingBlock("gadolinium_silicon_germanium_cooling_coil", TEX_FROSTPROOF, TEX_FROSTPROOF); // TODO)) temp 1; tex .../coils/machine_coil_gadolinium_silicon_germanium

    // ==================================================================
    // Serpentine (HeatRadiator, InternalCombustionEngine). Active.
    // tex gregtech:blocks/casings/serpentine/serpentine (+_bloom)
    // ==================================================================
    public static final BlockEntry<ActiveBlock> BASIC_SERPENTINE = createActiveCasingBlock("basic_serpentine", TEX_STEEL, TEX_STEEL); // TODO)) tex .../serpentine/serpentine + serpentine_bloom (CTM)

    // ==================================================================
    // Conveyor belt (CurtainCoater). 1.12.2 horizontal-rotatable + custom flat
    // rotation behaviour. tex gregtech:blocks/casings/conveyor_belt/lv
    // ==================================================================
    public static final BlockEntry<HorizontalOrientableBlock> LV_CONVEYOR_BELT = createHorizontalOrientableCasingBlock("lv_conveyor_belt", TEX_STEEL); // TODO)) Phase 5: quarter-height collision/outline shape and flat wrench rotation; Phase 6: directional belt texture at gregtech:blocks/casings/conveyor_belt/lv

    // ==================================================================
    // Rotors / coils for generators (Bucket C). 1.12.2 horizontal-rotatable
    // (VariantHorizontalRotatableBlock) -> HorizontalDirectionalBlock here so the
    // multiblock controller can auto-orient them on structure form. The per-facing
    // directional front texture is Phase 6. tex gregtech:blocks/casings/<dir>/<name>
    // ==================================================================
    public static final BlockEntry<HorizontalOrientableBlock> STEEL_SEPARATOR_ROTOR = createHorizontalOrientableCasingBlock("steel_separator_rotor", TEX_STEEL); // TODO)) rotatable front tex (BlockSeparatorRotor); tex .../separator_rotor/steel
    public static final BlockEntry<HorizontalOrientableBlock> COPPER_ALTERNATOR_COIL = createHorizontalOrientableCasingBlock("copper_alternator_coil", TEX_STEEL); // TODO)) rotatable front tex (BlockAlternatorCoil); tex .../alternator_coil/copper
    public static final BlockEntry<HorizontalOrientableBlock> STEEL_TURBINE_ROTOR = createHorizontalOrientableCasingBlock("steel_turbine_rotor", TEX_STEEL); // TODO)) rotatable front tex (BlockTurbineRotor); tex .../turbine_rotor/steel
    public static final BlockEntry<HorizontalOrientableBlock> LOW_PRESSURE_TURBINE_ROTOR = createHorizontalOrientableCasingBlock("low_pressure_turbine_rotor", TEX_STEEL); // TODO)) rotatable front tex; tex .../turbine_rotor/low_pressure
    public static final BlockEntry<HorizontalOrientableBlock> HIGH_PRESSURE_TURBINE_ROTOR = createHorizontalOrientableCasingBlock("high_pressure_turbine_rotor", TEX_STEEL); // TODO)) rotatable front tex; tex .../turbine_rotor/high_pressure
    public static final BlockEntry<HorizontalOrientableBlock> COMBUSTION_TURBINE_ROTOR = createHorizontalOrientableCasingBlock("combustion_turbine_rotor", TEX_STEEL); // TODO)) rotatable front tex; tex .../turbine_rotor/combustion

    // ==================================================================
    // Engine casings (InternalCombustionEngine). tex gregtech:blocks/casings/engine_casing/...
    // ==================================================================
    public static final BlockEntry<Block> PISTON_BLOCK = createCasingBlock("piston_block", TEX_STEEL); // TODO)) tex .../engine_casing/piston_block
    public static final BlockEntry<HorizontalOrientableBlock> CRANKSHAFT_ENGINE_CASING = createHorizontalOrientableCasingBlock("crankshaft_engine_casing", TEX_STEEL); // TODO)) rotatable front tex (BlockEngineCasing2, horizontal); tex .../engine_casing_2/crankshaft
    public static final BlockEntry<ActiveBlock> BASIC_INTAKE_CASING = createActiveCasingBlock("basic_intake_casing", TEX_ASSEMBLY, TEX_ASSEMBLY); // TODO)) tex .../engine_casing/basic_intake_casing/basic_intake_casing (+_active)

    // ==================================================================
    // Misc single-variant casings. tex gregtech:blocks/casings/<dir>/<name>
    // ==================================================================
    public static final BlockEntry<Block> WOODEN_COAGULATION_TANK_WALL = createCasingBlock("wooden_coagulation_tank_wall", TEX_STEEL); // TODO)) tex .../coagulation_tank_wall/wooden_coagulation_tank_wall
    public static final BlockEntry<Block> DIRT_EVAPORATION_BED = createCasingBlock("dirt_evaporation_bed", TEX_STEEL); // TODO)) shovel harvest; tex .../evaporation_bed/dirt
    public static final BlockEntry<Block> STEEL_DRILL_BIT = createCasingBlock("steel_drill_bit", TEX_STEEL); // TODO)) tex .../drill_bit/steel
    public static final BlockEntry<Block> STEEL_DRILL_HEAD = createCasingBlock("steel_drill_head", TEX_STEEL); // TODO)) tex .../drill_head/steel
    public static final BlockEntry<Block> STEEL_ECCENTRIC_ROLL = createCasingBlock("steel_eccentric_roll", TEX_STEEL); // TODO)) custom collision box + animated part (BlockEccentricRoll, IAnimatablePartBlock); tex .../eccentric_roll/steel
    public static final BlockEntry<RotatedPillarBlock> STEEL_GIRTH_GEAR_TOOTH = createAxialOrientableCasingBlock("steel_girth_gear_tooth", TEX_STEEL); // TODO)) Phase 6: real translucent axial model; tex .../girth_gear_tooth/steel
    public static final BlockEntry<Block> LAUNCH_PAD = createCasingBlock("launch_pad", TEX_STEEL); // TODO)) rocket scope (BlockSupport); tex .../support/lv

    // Sintering bricks (SinteringOven). 1.12.2 active + magnetoplated flag. Register the
    // two structural variants; bloom-deco variants are decorative (Phase 5).
    public static final BlockEntry<ActiveBlock> SINTERING_BRICK = createActiveCasingBlock("sintering_brick", TEX_STEEL, TEX_STEEL); // TODO)) Phase 6: migrate gregtech:blocks/casings/sintering_bricks/sintering_bricks(_bloom)
    public static final BlockEntry<ActiveBlock> MAGNETOPLATED_SINTERING_BRICK = createActiveCasingBlock("magnetoplated_sintering_brick", TEX_STEEL, TEX_STEEL); // TODO)) Phase 6: migrate gregtech:blocks/casings/sintering_bricks/sintering_bricks_magnetic(_bloom)

    public static void init() {}

    private SusyBlocks() {}
}
