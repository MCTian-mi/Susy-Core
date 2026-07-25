package io.github.symmetricdevs.supersymmetry.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.models.GTModels;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tterrag.registrate.util.entry.BlockEntry;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.api.registry.SusyRegistration;
import io.github.symmetricdevs.supersymmetry.common.block.DirectionalOrientableBlock;
import io.github.symmetricdevs.supersymmetry.common.block.EccentricRollBlock;
import io.github.symmetricdevs.supersymmetry.common.block.HorizontalOrientableBlock;
import io.github.symmetricdevs.supersymmetry.common.block.InnerCasingBlock;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;

/**
 * SuSy block registry — <b>Phase 4c casing foundation + Phase 5 decorative blocks</b>.
 * Ported from the 1.12.2 {@code SuSyBlocks} / {@code SuSyMetaBlocks} (a reflection-registered
 * set of {@code VariantBlock} / {@code VariantActiveBlock} classes) onto the GTCEu-Modern
 * registrate idiom: one block per variant via {@code REGISTRATE.block(...)}.
 * <p>
 * <b>Phase 4c (existing):</b> casing blocks the multiblock controllers reference (Buckets B/C/D).
 * <b>Phase 5 (new):</b> decorative / structural / resource blocks (concrete, regolith, hardened,
 * deposit, resource, sheeted, structural, wool, bmrf, flares, support, stone variants).
 * Rocket-casing blocks (rocketry/) are noted but deferred — their scope is the rocket/launch
 * assembly in a later phase.
 * <p>
 * <b>Texture migration is partial.</b> Decorative families and core multiblock/grinder
 * casings use migrated 1.12.2 art in the active {@code supersymmetry:block/} domain.
 * Connected textures, active casing state, directional geometry, and the remaining
 * special-purpose blocks retain their Phase 6 TODOs.
 * <p>
 * Several 1.12.2 variants were <em>rotatable</em> (conveyor, separator rotor,
 * alternator coil, turbine rotor, metallurgy rolls, engine casing 2, girth gear) or
 * <em>active</em> (tank walls, electrodes, serpentine, sintering bricks, active
 * casing). They register here as plain cube-all / {@link ActiveBlock} blocks — the
 * rotation/animation behaviour is a {@code // TODO} } Phase 6 concern (modern model +
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
     * Full-collision casing with non-occluding inner faces. The two legacy users chose
     * reduced light blocking (3), supplied by {@link InnerCasingBlock#getLightBlock}.
     */
    private static BlockEntry<InnerCasingBlock> createInnerCasingBlock(String name, ResourceLocation texture) {
        return REGISTRATE.block(name, InnerCasingBlock::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false).noOcclusion())
                .addLayer(() -> RenderType::cutout)
                .blockstate((ctx, prov) -> {
                    var model = prov.models().withExistingParent(ctx.getName(),
                                    ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "block/cube_with_inner"))
                            .texture("all", texture);
                    prov.simpleBlock(ctx.getEntry(), model);
                })
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
     * front texture per-facing; that directional texture model is a {@code // TODO} }
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

    /**
     * Eccentric crusher roll: a directional {@link ActiveBlock} carrying an animated GeckoLib
     * block entity (see {@code EccentricRollBlock}). Its inactive state uses the legacy two-roll
     * baked model; only an active roll selects the block-entity renderer. The block-entity type is
     * registered in {@code SusyBlockEntities}.
     */
    private static BlockEntry<EccentricRollBlock> createEccentricRollBlock(String name, ResourceLocation texture) {
        return REGISTRATE.block(name, EccentricRollBlock::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false).noOcclusion())
                .addLayer(() -> RenderType::cutout)
                .blockstate((ctx, prov) -> {
                    var model = prov.models().getBuilder(ctx.getName())
                            .texture("all", SuSyValues.susyId("block/eccentric_roll/all"))
                            .texture("particle", texture)
                            .element()
                            .from(-3.99F, -3.99F, 0.01F)
                            .to(19.99F, 19.99F, 15.99F)
                            .face(Direction.NORTH).uvs(0, 3, 3, 6).texture("#all").end()
                            .face(Direction.EAST).uvs(2, 9, 0, 6).texture("#all").end()
                            .face(Direction.SOUTH).uvs(0, 0, 3, 3).texture("#all").end()
                            .face(Direction.WEST).uvs(6, 0, 8, 3).texture("#all").end()
                            .face(Direction.UP).uvs(2, 6, 4, 9).rotation(
                                    net.minecraftforge.client.model.generators.ModelBuilder.FaceRotation.CLOCKWISE_90)
                            .texture("#all").end()
                            .face(Direction.DOWN).uvs(6, 3, 8, 6).rotation(
                                    net.minecraftforge.client.model.generators.ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90)
                            .texture("#all").end()
                            .end()
                            .element()
                            .from(-3.98F, -3.98F, 0.02F)
                            .to(19.98F, 19.98F, 15.98F)
                            .rotation().origin(8, 8, 8).axis(Direction.Axis.Z).angle(-45).end()
                            .face(Direction.NORTH).uvs(3, 3, 6, 6).texture("#all").end()
                            .face(Direction.EAST).uvs(6, 9, 4, 6).texture("#all").end()
                            .face(Direction.SOUTH).uvs(3, 0, 6, 3).texture("#all").end()
                            .face(Direction.WEST).uvs(6, 6, 8, 9).texture("#all").end()
                            .face(Direction.UP).uvs(8, 0, 10, 3).rotation(
                                    net.minecraftforge.client.model.generators.ModelBuilder.FaceRotation.CLOCKWISE_90)
                            .texture("#all").end()
                            .face(Direction.DOWN).uvs(8, 3, 10, 6).rotation(
                                    net.minecraftforge.client.model.generators.ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90)
                            .texture("#all").end()
                            .end()
                            // The 1.12.2 block model supplied these transforms; the generated item
                            // model inherits them through its block-model parent. TODO)) Phase 6:
                            // reconcile their longitudinal-axis orientation with the formed roll.
                            .transforms()
                            .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                            .rotation(75, 45, 0).translation(0, 2.5F, 0).scale(0.375F).end()
                            .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                            .rotation(75, 45, 0).translation(0, 2.5F, 0).scale(0.375F).end()
                            .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                            .rotation(0, 45, 0).scale(0.4F).end()
                            .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                            .rotation(0, -135, 0).scale(0.4F).end()
                            .transform(ItemDisplayContext.GROUND)
                            .translation(0, 3, 0).scale(0.25F).end()
                            .transform(ItemDisplayContext.GUI)
                            .rotation(30, -135, 0).scale(0.625F).end()
                            .transform(ItemDisplayContext.FIXED)
                            .scale(0.5F).end()
                            .end();
                    prov.getVariantBuilder(ctx.getEntry())
                            .forAllStates(state -> {
                                Direction direction = state.getValue(DirectionalBlock.FACING);
                                // TODO)) Phase 6: reconcile the baked roll's longitudinal axis with
                                // the active GeckoLib model using a formed in-game reference.
                                int xRot = direction == Direction.DOWN ? 90 :
                                        direction == Direction.UP ? 270 : 0;
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

    private static ResourceLocation casingTexture(String family, String name) {
        return ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "block/casings/" + family + "/" + name);
    }

    // Placeholder textures (guaranteed to exist in GTCEu). The real SuSy texture for
    // each block is recorded in a trailing TODO) comment with its 1.12.2 path.
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
    // Static base art is migrated below. CTM for the indicated legacy casings remains a
    // Phase 6 follow-up; drone/heavy-duty pads remain in their dedicated scope.
    // ==================================================================
    public static final BlockEntry<Block> SILICON_CARBIDE_CASING = createCasingBlock("silicon_carbide_casing",
            casingTexture("multiblock", "silicon_carbide_casing")); // TODO)) CTM follow-up
    public static final BlockEntry<Block> SIEVE_TRAY = createCasingBlock("sieve_tray",
            casingTexture("multiblock", "sieve_tray"));
    public static final BlockEntry<Block> STRUCTURAL_PACKING = createCasingBlock("structural_packing",
            casingTexture("multiblock", "structural_packing"));
    public static final BlockEntry<Block> ULV_STRUCTURAL_CASING = createCasingBlock("ulv_structural_casing",
            casingTexture("multiblock", "ulv_structural_casing")); // TODO)) CTM follow-up
    public static final BlockEntry<Block> DRONE_PAD_CASING = createCasingBlock("drone_pad_casing", TEX_STEEL); // TODO)) tex .../multiblock_casing/drone_pad
    public static final BlockEntry<Block> MONEL_500_CASING = createCasingBlock("monel_casing",
            casingTexture("multiblock", "monel_casing")); // TODO)) CTM follow-up
    public static final BlockEntry<Block> MONEL_500_PIPE = createCasingBlock("monel_casing_pipe",
            casingTexture("multiblock", "monel_casing_pipe"));
    public static final BlockEntry<Block> COPPER_PIPE = createCasingBlock("copper_casing_pipe",
            casingTexture("multiblock", "copper_casing_pipe")); // TODO)) CTM follow-up
    public static final BlockEntry<Block> HEAVY_DUTY_PAD = createCasingBlock("heavy_duty_pad", TEX_STEEL); // TODO)) tex .../multiblock_casing/heavy_duty_pad (rocket/launch scope)
    public static final BlockEntry<Block> TABULAR_ALUMINA_REFRACTORY = createCasingBlock("tabular_alumina_refractory",
            casingTexture("multiblock", "tabular_alumina_refractory"));
    public static final BlockEntry<InnerCasingBlock> COALESCENCE_PLATE = createInnerCasingBlock("coalescence_plate",
            casingTexture("multiblock", "coalescence_plate"));
    public static final BlockEntry<Block> SYNTHETIC_MULLITE_REFRACTORY = createCasingBlock("synthetic_mullite_refractory",
            casingTexture("multiblock", "synthetic_mullite_refractory"));
    public static final BlockEntry<Block> HYDROSTATIC_CASING = createCasingBlock("hydrostatic_casing",
            casingTexture("multiblock", "hydrostatic_casing"));
    public static final BlockEntry<Block> ALUMINIUM_GEARBOX = createCasingBlock("aluminium_gearbox",
            casingTexture("multiblock", "aluminium_gearbox")); // TODO)) CTM follow-up

    // ==================================================================
    // BlockGrinderCasing ("grinder_casing", 5 variants) — BallMill/AttritionScrubber/
    // EccentricRollCrusher/RotaryKilnV2. Flat base art is restored; CTM remains deferred.
    // ==================================================================
    public static final BlockEntry<Block> ABRASION_RESISTANT_CASING = createCasingBlock("abrasion_resistant_casing",
            casingTexture("grinder_casing", "abrasion_resistant_casing")); // TODO)) CTM follow-up
    public static final BlockEntry<Block> HYDRAULIC_MECHANICAL_GEARBOX = createCasingBlock("hydraulic_mechanical_gearbox",
            casingTexture("grinder_casing", "hydraulic_mechanical_gearbox"));
    public static final BlockEntry<Block> WEAR_RESISTANT_LINED_MILL_SHELL = createCasingBlock("wear_resistant_lined_mill_shell",
            casingTexture("grinder_casing", "wear_resistant_lined_mill_shell"));
    public static final BlockEntry<Block> WEAR_RESISTANT_LINED_SHELL_HEAD = createCasingBlock("wear_resistant_lined_shell_head",
            casingTexture("grinder_casing", "wear_resistant_lined_shell_head"));
    public static final BlockEntry<InnerCasingBlock> INTERMEDIATE_DIAPHRAGM = createInnerCasingBlock("intermediate_diaphragm",
            casingTexture("grinder_casing", "intermediate_diaphragm"));

    // ==================================================================
    // Metallurgy family (strand line). 1.12.2 rotatable -> plain here; rotation is a
    // Phase 6 TODO. tex gregtech:blocks/casings/metallurgy*/<name>
    // ==================================================================
    public static final BlockEntry<DirectionalOrientableBlock> HYDRAULIC_CYLINDER = createDirectionalOrientableCasingBlock("hydraulic_cylinder", TEX_STEEL); // TODO)) directional front tex (BlockMetallurgy); tex .../metallurgy/hydraulic_cylinder
    public static final BlockEntry<HorizontalOrientableBlock> FLYING_SHEAR_SAW = createHorizontalOrientableCasingBlock("flying_shear_saw", TEX_STEEL); // TODO)) rotatable (BlockMetallurgy2, horizontal); tex .../metallurgy_2/flying_shear_saw
    public static final BlockEntry<Block> POLYSTYRENE_WALL = createCasingBlock("polystyrene_wall", TEX_STEEL); // TODO)) tex .../metallurgy_2/polystyrene_wall
    public static final BlockEntry<RotatedPillarBlock> METALLURGY_ROLL = createAxialOrientableCasingBlock("metallurgy_roll", TEX_STEEL); // TODO)) rotatable (BlockMetallurgyRoll, axial); tex .../metallurgy_roll/roll

    // ==================================================================
    // BlockMultiblockTank (active; "multiblock_tank", clarifier + flotation walls)
    // tex gregtech:blocks/casings/multiblock_tank/<name>[ / _on]
    // ==================================================================
    public static final BlockEntry<ActiveBlock> CLARIFIER_MULTIBLOCK_TANK = createActiveCasingBlock("clarifier_multiblock_tank",
            casingTexture("multiblock_tank", "clarifier"), casingTexture("multiblock_tank", "clarifier_on"));
    public static final BlockEntry<ActiveBlock> FLOTATION_MULTIBLOCK_TANK = createActiveCasingBlock("flotation_multiblock_tank",
            casingTexture("multiblock_tank", "flotation"), casingTexture("multiblock_tank", "flotation_on"));

    // ==================================================================
    // Electrodes / coils. tex gregtech:blocks/casings/<dir>/<name>
    // ==================================================================
    public static final BlockEntry<ActiveBlock> CARBON_ELECTRODE_ASSEMBLY = createActiveCasingBlock("carbon_electrode_assembly", TEX_ASSEMBLY, TEX_ASSEMBLY); // TODO)) tex .../electrode_assembly/carbon (+active)
    public static final BlockEntry<Block> COPPER_INDUCTION_COIL_ASSEMBLY = createCasingBlock("copper_induction_coil_assembly",
            casingTexture("coils", "induction_coil_assembly")); // TODO)) CTM follow-up
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
    public static final BlockEntry<ActiveBlock> BASIC_SERPENTINE = createActiveCasingBlock("basic_serpentine",
            casingTexture("serpentine", "serpentine"), casingTexture("serpentine", "serpentine_bloom"));

    // ==================================================================
    // Conveyor belt (CurtainCoater). 1.12.2 horizontal-rotatable + custom flat
    // rotation behaviour. tex gregtech:blocks/casings/conveyor_belt/lv
    // ==================================================================
    public static final BlockEntry<HorizontalOrientableBlock> LV_CONVEYOR_BELT = createHorizontalOrientableCasingBlock("lv_conveyor_belt",
            casingTexture("conveyor_belt/lv", "top")); // TODO)) Phase 5: quarter-height collision/outline shape and flat wrench rotation; Phase 6: directional belt texture at gregtech:blocks/casings/conveyor_belt/lv

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
    public static final BlockEntry<Block> WOODEN_COAGULATION_TANK_WALL = createBottomTopDecorativeBlock("wooden_coagulation_tank_wall",
            "casings/wooden_coagulation_tank_wall/side",
            "casings/wooden_coagulation_tank_wall/bottom",
            "casings/wooden_coagulation_tank_wall/top");
    public static final BlockEntry<Block> DIRT_EVAPORATION_BED = createCasingBlock("dirt_evaporation_bed", TEX_STEEL); // TODO)) shovel harvest; tex .../evaporation_bed/dirt
    public static final BlockEntry<Block> STEEL_DRILL_BIT = createCasingBlock("steel_drill_bit", TEX_STEEL); // TODO)) tex .../drill_bit/steel
    public static final BlockEntry<Block> STEEL_DRILL_HEAD = createCasingBlock("steel_drill_head", TEX_STEEL); // TODO)) tex .../drill_head/steel
    public static final BlockEntry<EccentricRollBlock> STEEL_ECCENTRIC_ROLL = createEccentricRollBlock("steel_eccentric_roll", TEX_STEEL); // animated crusher roll (GeckoLib block entity); static fallback tex .../eccentric_roll/steel
    public static final BlockEntry<RotatedPillarBlock> STEEL_GIRTH_GEAR_TOOTH = createAxialOrientableCasingBlock("steel_girth_gear_tooth", TEX_STEEL); // TODO)) Phase 6: real translucent axial model; tex .../girth_gear_tooth/steel
    public static final BlockEntry<Block> LAUNCH_PAD = createCasingBlock("launch_pad", TEX_STEEL); // TODO)) rocket scope (BlockSupport); tex .../support/lv

    // Sintering bricks (SinteringOven). 1.12.2 active + magnetoplated flag. Register the
    // two structural variants; bloom-deco variants are decorative (Phase 5).
    public static final BlockEntry<ActiveBlock> SINTERING_BRICK = createActiveCasingBlock("sintering_brick", TEX_STEEL, TEX_STEEL); // TODO)) Phase 6: migrate gregtech:blocks/casings/sintering_bricks/sintering_bricks(_bloom)
    public static final BlockEntry<ActiveBlock> MAGNETOPLATED_SINTERING_BRICK = createActiveCasingBlock("magnetoplated_sintering_brick", TEX_STEEL, TEX_STEEL); // TODO)) Phase 6: migrate gregtech:blocks/casings/sintering_bricks/sintering_bricks_magnetic(_bloom)

    // ==================================================================
    // PHASE 5: DECORATIVE / STRUCTURAL BLOCKS
    //
    // Ported from the 1.12.2 VariantBlock classes in
    // supersymmetry.common.blocks. Each legacy class held multiple
    // variants packed into blockstate meta; here each variant is its
    // own registrate entry with a placeholder texture (TEX_STEEL).
    // Real SuSy art texture paths are recorded in trailing TODO)
    // comments and will be migrated in Phase 6.
    // ==================================================================

    // ==================================================================
    // Helpers for decorative blocks.
    // ==================================================================

    private static ResourceLocation decorativeTexture(String name) {
        return ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "block/" + name);
    }

    private static BlockEntry<Block> createBottomTopDecorativeBlock(String name, String side, String bottom,
                                                                      String top) {
        return REGISTRATE.block(name, Block::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::solid)
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                        prov.models().cubeBottomTop(ctx.getName(), decorativeTexture(side), decorativeTexture(bottom),
                                decorativeTexture(top))))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    private static BlockEntry<Block> createStructuralBottomTopDecorativeBlock(String name) {
        return createBottomTopDecorativeBlock(name, name, "base_structural_block", "base_structural_block");
    }

    private static BlockEntry<Block> createEmissiveStructuralBlock(String name, String bloomSide, String bloomTop) {
        return REGISTRATE.block(name, Block::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::cutoutMipped)
                .blockstate((ctx, prov) -> {
                    var baseStructural = decorativeTexture("base_structural_block");
                    var model = prov.models().withExistingParent(ctx.getName(), GTCEu.id("block/cube_2_layer/bottom_top"))
                            .texture("bot_side", decorativeTexture(name))
                            .texture("bot_top", baseStructural)
                            .texture("bot_bottom", baseStructural)
                            .texture("top_side", decorativeTexture(bloomSide))
                            .texture("top_top", decorativeTexture(bloomTop))
                            .texture("top_bottom", decorativeTexture(bloomTop));
                    prov.simpleBlock(ctx.getEntry(), model);
                })
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    /** Stone-like block (sound/hardness from {@link Blocks#STONE}). */
    private static BlockEntry<Block> createStoneDecorativeBlock(String name) {
        return REGISTRATE.block(name, Block::new)
                .initialProperties(() -> Blocks.STONE)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::solid)
                .exBlockstate(GTModels.cubeAllModel(decorativeTexture(name)))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    /** Metal decorative block (sound/hardness from {@link Blocks#IRON_BLOCK}). */
    private static BlockEntry<Block> createMetalDecorativeBlock(String name) {
        return REGISTRATE.block(name, Block::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::solid)
                .exBlockstate(GTModels.cubeAllModel(decorativeTexture(name)))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    /** Cloth-like block (sound/hardness from {@link Blocks#WHITE_WOOL}). */
    private static BlockEntry<Block> createWoolDecorativeBlock(String name) {
        return REGISTRATE.block(name, Block::new)
                .initialProperties(() -> Blocks.WHITE_WOOL)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::solid)
                .exBlockstate(GTModels.cubeAllModel(decorativeTexture(name)))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    /** Sand-like block (sound/hardness from {@link Blocks#SAND}). */
    private static BlockEntry<Block> createSandDecorativeBlock(String name) {
        return REGISTRATE.block(name, Block::new)
                .initialProperties(() -> Blocks.SAND)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::solid)
                .exBlockstate(GTModels.cubeAllModel(decorativeTexture(name)))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    /** Unbreakable block (bedrock-like, for worldgen deposits). */
    private static BlockEntry<Block> createUnbreakableDecorativeBlock(String name) {
        return REGISTRATE.block(name, Block::new)
                .initialProperties(() -> Blocks.BEDROCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::solid)
                .exBlockstate(GTModels.cubeAllModel(decorativeTexture(name)))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    // ==================================================================
    // Random Concrete (1.12.2 BlockRandomConcrete, BlockRandomConcrete1)
    // 16 + 1 variants. 1.12.2: translationKey "random_concrete" / "random_concrete1"
    // tex gregtech:blocks/casings/random_concrete/<variant>
    // ==================================================================
    public static final BlockEntry<Block> GREY_INDUSTRIAL_CONCRETE = createStoneDecorativeBlock("grey_industrial_concrete"); // TODO)) tex greyindustrialconcrete
    public static final BlockEntry<Block> MOSSY_INDUSTRIAL_CONCRETE = createStoneDecorativeBlock("mossy_industrial_concrete"); // TODO)) tex mossyindustrialconcrete
    public static final BlockEntry<Block> SILVER_INDUSTRIAL_CONCRETE = createStoneDecorativeBlock("silver_industrial_concrete"); // TODO)) tex silverindustrialconcrete
    public static final BlockEntry<Block> WHITE_INDUSTRIAL_CONCRETE = createStoneDecorativeBlock("white_industrial_concrete"); // TODO)) tex whiteindustrialconcrete
    public static final BlockEntry<Block> DOTTED_PANEL = createStoneDecorativeBlock("dotted_panel"); // TODO)) tex dottedpanel
    public static final BlockEntry<Block> DOTTED_PANEL_BORDER = createStoneDecorativeBlock("dotted_panel_border"); // TODO)) tex dottedpanelborder
    public static final BlockEntry<Block> DOTTED_PANEL_COMB = createStoneDecorativeBlock("dotted_panel_comb"); // TODO)) tex dottedpanelcomb
    public static final BlockEntry<Block> DOTTED_PANEL_GRID = createStoneDecorativeBlock("dotted_panel_grid"); // TODO)) tex dottedpanelgrid
    public static final BlockEntry<Block> INDUSTRIAL_CINDER_BRICKS = createStoneDecorativeBlock("industrial_cinder_bricks"); // TODO)) tex industrialcinderbricks
    public static final BlockEntry<Block> INDUSTRIAL_CINDER_BRICKS_CEMENT = createStoneDecorativeBlock("industrial_cinder_bricks_cement"); // TODO)) tex industrialcinderbrickscement
    public static final BlockEntry<Block> INDUSTRIAL_CINDER_BRICKS_CEMENT_GRAY = createStoneDecorativeBlock("industrial_cinder_bricks_cement_gray"); // TODO)) tex industrialcinderbrickscementgray
    public static final BlockEntry<Block> INDUSTRIAL_CINDER_BRICKS_DARK = createStoneDecorativeBlock("industrial_cinder_bricks_dark"); // TODO)) tex industrialcinderbricksdark
    public static final BlockEntry<Block> INDUSTRIAL_CINDER_BRICKS_DARK_GRAY = createStoneDecorativeBlock("industrial_cinder_bricks_dark_gray"); // TODO)) tex industrialcinderbricksdarkgrey
    public static final BlockEntry<Block> INDUSTRIAL_CINDER_BRICKS_GRAY = createStoneDecorativeBlock("industrial_cinder_bricks_gray"); // TODO)) tex industrialcinderbricksgrey
    public static final BlockEntry<Block> SMOOTH_INDUSTRIAL_CONCRETE = createStoneDecorativeBlock("smooth_industrial_concrete"); // TODO)) tex smoothindustrialconcrete
    public static final BlockEntry<Block> SMOOTH_INDUSTRIAL_CONCRETE_GRAY = createStoneDecorativeBlock("smooth_industrial_concrete_gray"); // TODO)) tex smoothindustrialconcretegrey
    public static final BlockEntry<Block> SMOOTH_INDUSTRIAL_CONCRETE_WHITE = createStoneDecorativeBlock("smooth_industrial_concrete_white"); // TODO)) tex smoothindustrialconcretewhite

    // ==================================================================
    // Lunar Concrete (1.12.2 BlockLunarConcrete, 12 variants)
    // translationKey "lunar_concrete", pickaxe harvest lv 1.
    // Legacy: LUNAR_CONCRETE_SMOOTH drops LUNAR_CONCRETE_COBBLE.
    // tex gregtech:blocks/casings/lunar_concrete/<variant>
    // ==================================================================
    public static final BlockEntry<Block> LUNAR_CONCRETE_SMOOTH = createStoneDecorativeBlock("lunar_concrete_smooth"); // TODO)) tex lunar_concrete_smooth
    public static final BlockEntry<Block> LUNAR_CONCRETE_BRICKS = createStoneDecorativeBlock("lunar_concrete_bricks"); // TODO)) tex lunar_concrete_bricks
    public static final BlockEntry<Block> LUNAR_CONCRETE_BRICKS_CRACKED = createStoneDecorativeBlock("lunar_concrete_bricks_cracked"); // TODO)) tex lunar_concrete_bricks_cracked
    public static final BlockEntry<Block> LUNAR_CONCRETE_BRICKS_SMALL = createStoneDecorativeBlock("lunar_concrete_bricks_small"); // TODO)) tex lunar_concrete_bricks_small
    public static final BlockEntry<Block> LUNAR_CONCRETE_BRICKS_SQUARE = createStoneDecorativeBlock("lunar_concrete_bricks_square"); // TODO)) tex lunar_concrete_bricks_square
    public static final BlockEntry<Block> LUNAR_CONCRETE_CHISELED = createStoneDecorativeBlock("lunar_concrete_chiseled"); // TODO)) tex lunar_concrete_chiseled
    public static final BlockEntry<Block> LUNAR_CONCRETE_COBBLE = createStoneDecorativeBlock("lunar_concrete_cobble"); // TODO)) tex lunar_concrete_cobble
    public static final BlockEntry<Block> LUNAR_CONCRETE_POLISHED = createStoneDecorativeBlock("lunar_concrete_polished"); // TODO)) tex lunar_concrete_polished
    public static final BlockEntry<Block> LUNAR_CONCRETE_TILED = createStoneDecorativeBlock("lunar_concrete_tiled"); // TODO)) tex lunar_concrete_tiled
    public static final BlockEntry<Block> LUNAR_CONCRETE_TILED_SMALL = createStoneDecorativeBlock("lunar_concrete_tiled_small"); // TODO)) tex lunar_concrete_tiled_small
    public static final BlockEntry<Block> LUNAR_CONCRETE_WINDMILL_A = createStoneDecorativeBlock("lunar_concrete_windmill_a"); // TODO)) tex lunar_concrete_windmill_a
    public static final BlockEntry<Block> LUNAR_CONCRETE_WINDMILL_B = createStoneDecorativeBlock("lunar_concrete_windmill_b"); // TODO)) tex lunar_concrete_windmill_b

    // ==================================================================
    // Regolith (1.12.2 BlockRegolith extends VariantBlockFalling, 2 variants)
    // translationKey "regolith", Material.SAND, shovel harvest.
    // Legacy falling-block behaviour (VariantBlockFalling) is TODO.
    // tex gregtech:blocks/casings/regolith/<variant>
    // ==================================================================
    public static final BlockEntry<Block> HIGHLAND_REGOLITH = createSandDecorativeBlock("highland_regolith"); // TODO)) falling block; tex highland
    public static final BlockEntry<Block> LOWLAND_REGOLITH = createSandDecorativeBlock("lowland_regolith"); // TODO)) falling block; tex lowland

    // ==================================================================
    // Hardened Blocks (1.12.2 BlocksHardened, BlocksHardened1)
    // 5 + 3 variants. Legacy: HardenedBlockType with specific drop behaviour.
    // tex gregtech:blocks/casings/hardened_blocks/<variant>
    // ==================================================================
    public static final BlockEntry<Block> HARDENED_LAIR10 = createStoneDecorativeBlock("hardened_lair10"); // TODO)) tex lair10
    public static final BlockEntry<Block> HARDENED_KRYP8 = createStoneDecorativeBlock("hardened_kryp8"); // TODO)) tex kryp8
    public static final BlockEntry<Block> HARDENED_KRYP7 = createStoneDecorativeBlock("hardened_kryp7"); // TODO)) tex kryp7
    public static final BlockEntry<Block> HARDENED_LAIR11 = createStoneDecorativeBlock("hardened_lair11"); // TODO)) tex lair11
    public static final BlockEntry<Block> HARDENED_LAIR7 = createStoneDecorativeBlock("hardened_lair7"); // TODO)) tex lair7
    public static final BlockEntry<Block> INDUSTRIAL_CONCRETE_HARDENED = createStoneDecorativeBlock("industrial_concrete_hardened"); // TODO)) tex industrial_concrete_hardened; custom drop
    public static final BlockEntry<Block> MILITARY_CONCRETE_COBBLESTONE_HARDENED = createStoneDecorativeBlock("military_concrete_cobblestone_hardened"); // TODO)) tex military_concrete_cobblestone_hardened; custom drop
    public static final BlockEntry<Block> MILITARY_CONCRETE_HARDENED = createStoneDecorativeBlock("military_concrete_hardened"); // TODO)) tex military_concrete_hardened; custom drop

    // ==================================================================
    // Deposit Blocks (1.12.2 BlockDeposit, 8 variants)
    // translationKey "deposit_block", unbreakable, piston-proof, drops nothing.
    // For worldgen ore deposits.
    // tex gregtech:blocks/casings/deposit/<variant>
    // ==================================================================
    public static final BlockEntry<Block> ORTHOMAGMATIC_DEPOSIT = createUnbreakableDecorativeBlock("orthomagmatic_deposit"); // TODO)) tex orthomagmatic
    public static final BlockEntry<Block> METAMORPHIC_DEPOSIT = createUnbreakableDecorativeBlock("metamorphic_deposit"); // TODO)) tex metamorphic
    public static final BlockEntry<Block> SEDIMENTARY_DEPOSIT = createUnbreakableDecorativeBlock("sedimentary_deposit"); // TODO)) tex sedimentary
    public static final BlockEntry<Block> HYDROTHERMAL_DEPOSIT = createUnbreakableDecorativeBlock("hydrothermal_deposit"); // TODO)) tex hydrothermal
    public static final BlockEntry<Block> ALLUVIAL_DEPOSIT = createUnbreakableDecorativeBlock("alluvial_deposit"); // TODO)) tex alluvial
    public static final BlockEntry<Block> MAGMATIC_HYDROTHERMAL_DEPOSIT = createUnbreakableDecorativeBlock("magmatic_hydrothermal_deposit"); // TODO)) tex magmatic_hydrothermal
    public static final BlockEntry<Block> ICE_CAP_DEPOSIT = createUnbreakableDecorativeBlock("ice_cap_deposit"); // TODO)) recover or recreate missing ice_cap texture (Phase 6)
    public static final BlockEntry<Block> EVAPORITE_DEPOSIT = createUnbreakableDecorativeBlock("evaporite_deposit"); // TODO)) tex evaporite

    // ==================================================================
    // Resource Blocks (1.12.2 BlockResource, BlockResource1, 16 + 3 variants)
    // translationKey "resource_block" / "resource_block_1", Material.IRON,
    // pickaxe harvest lv 1. Mineral storage blocks.
    // tex gregtech:blocks/casings/resource_block/<variant>
    // ==================================================================
    public static final BlockEntry<Block> BAUXITE_BLOCK = createMetalDecorativeBlock("bauxite_block"); // TODO)) tex bauxite
    public static final BlockEntry<Block> CALICHE_BLOCK = createMetalDecorativeBlock("caliche_block"); // TODO)) tex caliche
    public static final BlockEntry<Block> NON_MARINE_EVAPORITE_BLOCK = createMetalDecorativeBlock("non_marine_evaporite_block"); // TODO)) tex non_marine_evaporite
    public static final BlockEntry<Block> HALIDE_EVAPORITE_BLOCK = createMetalDecorativeBlock("halide_evaporite_block"); // TODO)) tex halide_evaporite
    public static final BlockEntry<Block> SULFATE_EVAPORITE_BLOCK = createMetalDecorativeBlock("sulfate_evaporite_block"); // TODO)) tex sulfate_evaporite
    public static final BlockEntry<Block> CARBONATE_EVAPORITE_BLOCK = createMetalDecorativeBlock("carbonate_evaporite_block"); // TODO)) tex carbonate_evaporite
    public static final BlockEntry<Block> MONAZITE_ALLUVIAL_BLOCK = createMetalDecorativeBlock("monazite_alluvial_block"); // TODO)) tex monazite_alluvial
    public static final BlockEntry<Block> BASTNASITE_ALLUVIAL_BLOCK = createMetalDecorativeBlock("bastnasite_alluvial_block"); // TODO)) tex bastnasite_alluvial
    public static final BlockEntry<Block> EUXENITE_ALLUVIAL_BLOCK = createMetalDecorativeBlock("euxenite_alluvial_block"); // TODO)) tex euxenite_alluvial
    public static final BlockEntry<Block> XENOTIME_ALLUVIAL_BLOCK = createMetalDecorativeBlock("xenotime_alluvial_block"); // TODO)) tex xenotime_alluvial
    public static final BlockEntry<Block> PLATINUM_PLACER_BLOCK = createMetalDecorativeBlock("platinum_placer_block"); // TODO)) tex platinum_placer
    public static final BlockEntry<Block> GOLD_ALLUVIAL_BLOCK = createMetalDecorativeBlock("gold_alluvial_block"); // TODO)) tex gold_alluvial
    public static final BlockEntry<Block> PHOSPHORITE_BLOCK = createMetalDecorativeBlock("phosphorite_block"); // TODO)) tex phosphorite
    public static final BlockEntry<Block> POTASH_BLOCK = createMetalDecorativeBlock("potash_block"); // TODO)) tex potash
    public static final BlockEntry<Block> SULFUR_BLOCK = createMetalDecorativeBlock("sulfur_block"); // TODO)) tex sulfur
    public static final BlockEntry<Block> COAL_BLOCK = createMetalDecorativeBlock("coal_block"); // TODO)) tex coal; harvest lv 0
    public static final BlockEntry<Block> NATIVE_COPPER_BLOCK = createMetalDecorativeBlock("native_copper_block"); // TODO)) tex native_copper
    public static final BlockEntry<Block> ANTHRACITE_BLOCK = createMetalDecorativeBlock("anthracite_block"); // TODO)) tex anthracite; harvest lv 0
    public static final BlockEntry<Block> LIGNITE_BLOCK = createMetalDecorativeBlock("lignite_block"); // TODO)) tex lignite; harvest lv 0

    // ==================================================================
    // Custom (Sheeted Frame) Sheets (1.12.2 BlocksCustomSheets, 4 variants)
    // translationKey "custom_sheets", Material.IRON, metal sound.
    // These are static decorative metal sheets, NOT the material-packing
    // BlockSheetedFrame (that class handles per-material frame blocks).
    // tex gregtech:blocks/casings/custom_sheets/<variant>
    // ==================================================================
    public static final BlockEntry<Block> DARK_WHITE_METAL_SHEET = createMetalDecorativeBlock("dark_white_metal_sheet"); // TODO)) tex darkwhitemetalsheet
    public static final BlockEntry<Block> LIGHTER_GRAY_METAL_SHEET = createMetalDecorativeBlock("lighter_gray_metal_sheet"); // TODO)) tex lightergraymetalsheet
    public static final BlockEntry<Block> DECORATIVE_COPPER_SHEET = createMetalDecorativeBlock("decorative_copper_sheet"); // TODO)) tex decorativecopper
    public static final BlockEntry<Block> DECORATIVE_COPPER_BRICKS = createMetalDecorativeBlock("decorative_copper_bricks"); // TODO)) tex decorativecopperbricks

    // ==================================================================
    // Sheeted Frame (1.12.2 BlockSheetedFrame, material-packed variant)
    // Legacy: BlockSheetedFrame packed 4 materials per block via PropertyMaterial
    // + axis property, with custom SheetedFrameItemBlock for display names.
    // Modern: create per-material entries here as plain blocks.
    // The full BlockSheetedFrame reimplementation (axis collision, pipe
    // integration, material rendering) is a later-phase concern.
    // ==================================================================
    // TODO: implement BlockSheetedFrame modern equivalent.
    // When the materials that use sheeted frames are ported, register
    // per-material frame blocks here.
    // Currently deferred -- no recipes reference sheeted frames.

    // ==================================================================
    // Structural Blocks (1.12.2 BlockStructural, BlockStructural1, 16 + 10 variants)
    // translationKey "structural_block" / "structural_block_1", Material.IRON,
    // wrench harvest lv 2.
    // tex gregtech:blocks/casings/structural_block/<variant>
    // ==================================================================
    public static final BlockEntry<Block> BASE_STRUCTURAL_BLOCK = createMetalDecorativeBlock("base_structural_block"); // TODO)) tex base_structural_block
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_LOW = createStructuralBottomTopDecorativeBlock("structural_block_low");
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_LOWLIGHT = createEmissiveStructuralBlock("structural_block_lowlight", "structural_block_lowlight_bloom", "structural_block_lowlight_top_bloom");
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_DANGER_A = createMetalDecorativeBlock("structural_block_danger_a"); // TODO)) tex structural_block_danger_a
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_DANGER_B = createMetalDecorativeBlock("structural_block_danger_b"); // TODO)) tex structural_block_danger_b
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_DANGER_C = createMetalDecorativeBlock("structural_block_danger_c"); // TODO)) tex structural_block_danger_c
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_DANGER_D = createMetalDecorativeBlock("structural_block_danger_d"); // TODO)) tex structural_block_danger_d
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_COLUMN = createMetalDecorativeBlock("structural_block_column"); // TODO)) tex structural_block_column
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_COLUMN_OLD = createMetalDecorativeBlock("structural_block_column_old"); // TODO)) tex structural_block_column_old
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_LIGHT = createEmissiveStructuralBlock("structural_block_light", "structural_block_light_bloom", "structural_block_lowlight_top_bloom");
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_LIGHT_BROKEN = createStructuralBottomTopDecorativeBlock("structural_block_light_broken");
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_LIGHT_CABLE = createStructuralBottomTopDecorativeBlock("structural_block_light_cable");
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_INSTRUMENTS = createEmissiveStructuralBlock("structural_block_instruments", "structural_block_instruments_bloom", "structural_block_lowlight_top_bloom");
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_SIGN_0 = createStructuralBottomTopDecorativeBlock("structural_block_sign_0");
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_SIGN_1 = createStructuralBottomTopDecorativeBlock("structural_block_sign_1");
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_SIGN_2 = createStructuralBottomTopDecorativeBlock("structural_block_sign_2");
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_EXPOSED = createMetalDecorativeBlock("structural_block_exposed"); // TODO)) tex structural_block_exposed
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_EXPOSED_1 = createMetalDecorativeBlock("structural_block_exposed_1"); // TODO)) tex structural_block_exposed_1
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_EXPOSED_2 = createMetalDecorativeBlock("structural_block_exposed_2"); // TODO)) tex structural_block_exposed_2
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_DANGER_SIGN = createMetalDecorativeBlock("structural_block_danger_sign"); // TODO)) tex structural_block_danger_sign
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_CABLE = createMetalDecorativeBlock("structural_block_cable"); // TODO)) tex structural_block_cable
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_CABLE_HORIZONTAL = createMetalDecorativeBlock("structural_block_cable_horizontal"); // TODO)) tex structural_block_cable_horizontal
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_CABLE_JUNCTION = createMetalDecorativeBlock("structural_block_cable_junction"); // TODO)) tex structural_block_cable_junction
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_PIPOCALYPSE = createMetalDecorativeBlock("structural_block_pipocalypse"); // TODO)) tex structural_block_pipocalypse
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_VENT = createMetalDecorativeBlock("structural_block_vent"); // TODO)) tex structural_block_vent
    public static final BlockEntry<Block> STRUCTURAL_BLOCK_VENT_BROKEN = createMetalDecorativeBlock("structural_block_vent_broken"); // TODO)) tex structural_block_vent_broken

    // ==================================================================
    // Fake Wool (1.12.2 BlocksFakeWool, 16 color variants)
    // translationKey "fake_wool", Material.ROCK with CLOTH sound.
    // tex gregtech:blocks/casings/fake_wool/<color>
    // ==================================================================
    public static final BlockEntry<Block> WHITE_FAKE_WOOL = createWoolDecorativeBlock("white_fake_wool"); // TODO)) tex whitefakewool
    public static final BlockEntry<Block> ORANGE_FAKE_WOOL = createWoolDecorativeBlock("orange_fake_wool"); // TODO)) tex orangefakewool
    public static final BlockEntry<Block> MAGENTA_FAKE_WOOL = createWoolDecorativeBlock("magenta_fake_wool"); // TODO)) tex magentafakewool
    public static final BlockEntry<Block> LIGHT_BLUE_FAKE_WOOL = createWoolDecorativeBlock("light_blue_fake_wool"); // TODO)) tex lightbluefakewool
    public static final BlockEntry<Block> YELLOW_FAKE_WOOL = createWoolDecorativeBlock("yellow_fake_wool"); // TODO)) tex yellowfakewool
    public static final BlockEntry<Block> LIME_FAKE_WOOL = createWoolDecorativeBlock("lime_fake_wool"); // TODO)) tex limefakewool
    public static final BlockEntry<Block> PINK_FAKE_WOOL = createWoolDecorativeBlock("pink_fake_wool"); // TODO)) tex pinkfakewool
    public static final BlockEntry<Block> GRAY_FAKE_WOOL = createWoolDecorativeBlock("gray_fake_wool"); // TODO)) tex grayfakewool
    public static final BlockEntry<Block> LIGHT_GRAY_FAKE_WOOL = createWoolDecorativeBlock("light_gray_fake_wool"); // TODO)) tex lightgrayfakewool
    public static final BlockEntry<Block> CYAN_FAKE_WOOL = createWoolDecorativeBlock("cyan_fake_wool"); // TODO)) tex cyanfakewool
    public static final BlockEntry<Block> PURPLE_FAKE_WOOL = createWoolDecorativeBlock("purple_fake_wool"); // TODO)) tex purplefakewool
    public static final BlockEntry<Block> BLUE_FAKE_WOOL = createWoolDecorativeBlock("blue_fake_wool"); // TODO)) tex bluefakewool
    public static final BlockEntry<Block> BROWN_FAKE_WOOL = createWoolDecorativeBlock("brown_fake_wool"); // TODO)) tex brownfakewool
    public static final BlockEntry<Block> GREEN_FAKE_WOOL = createWoolDecorativeBlock("green_fake_wool"); // TODO)) tex greenfakewool
    public static final BlockEntry<Block> RED_FAKE_WOOL = createWoolDecorativeBlock("red_fake_wool"); // TODO)) tex redfakewool
    public static final BlockEntry<Block> BLACK_FAKE_WOOL = createWoolDecorativeBlock("black_fake_wool"); // TODO)) tex blackfakewool

    // ==================================================================
    // BMRF Blocks (1.12.2 BlocksBMRF, 9 variants)
    // translationKey "bmrf_blocks", Material.ROCK, stone sound.
    // tex gregtech:blocks/casings/bmrf/<variant>
    // ==================================================================
    public static final BlockEntry<Block> BMRF1 = createStoneDecorativeBlock("bmrf1"); // TODO)) tex bmrf1
    public static final BlockEntry<Block> BMRF2 = createStoneDecorativeBlock("bmrf2"); // TODO)) tex bmrf2
    public static final BlockEntry<Block> BMRF3 = createStoneDecorativeBlock("bmrf3"); // TODO)) tex bmrf3
    public static final BlockEntry<Block> BMRF4 = createStoneDecorativeBlock("bmrf4"); // TODO)) tex bmrf4
    public static final BlockEntry<Block> BMRF5 = createStoneDecorativeBlock("bmrf5"); // TODO)) tex bmrf5
    public static final BlockEntry<Block> BMRF6 = createStoneDecorativeBlock("bmrf6"); // TODO)) tex bmrf6
    public static final BlockEntry<Block> BMRF7 = createStoneDecorativeBlock("bmrf7"); // TODO)) tex bmrf7
    public static final BlockEntry<Block> BMRF8 = createStoneDecorativeBlock("bmrf8"); // TODO)) tex bmrf8
    public static final BlockEntry<Block> BMRF9 = createStoneDecorativeBlock("bmrf9"); // TODO)) tex bmrf9

    // ==================================================================
    // Raid Flare (1.12.2 BlocksRaidFlare, 2 variants)
    // translationKey "raid_flare_block", Material.IRON, light level 1.
    // Has TileEntityFlare with faction-hate mechanics.
    // tex gregtech:blocks/casings/raid_flare/<variant>
    // ==================================================================
    public static final BlockEntry<Block> BANDIT_FLARE = createBottomTopDecorativeBlock("bandit_flare", "bandit_flare_side", "bandit_flare_bottom", "bandit_flare_top"); // TODO)) tile entity + faction hate
    public static final BlockEntry<Block> FED_FLARE = createBottomTopDecorativeBlock("fed_flare", "fed_flare_side", "fed_flare_bottom", "fed_flare_top"); // TODO)) tile entity + faction hate

    // ==================================================================
    // Support Block (1.12.2 BlockSupport, 1 variant)
    // translationKey "support", unbreakable, entity velocity clamp behaviour.
    // Legacy: used as launch-pad floor for rocket scope.
    // tex gregtech:blocks/casings/support/lv
    // ==================================================================
    public static final BlockEntry<Block> SUPPORT_BLOCK = createCasingBlock("support_block", TEX_STEEL); // TODO)) unbreakable + entity collision clamp; tex gregtech:blocks/casings/support/lv

    // ==================================================================
    // Stone Variant Blocks (1.12.2 SusyStoneVariantBlock, 3 stone variants)
    // Legacy: 3 block instances (SMOOTH, COBBLE, BRICKS) each holding 12
    // StoneType variants (gabbro, gneiss, limestone, etc.) with walking-speed
    // bonuses and custom COBBLE drop for SMOOTH.
    //
    // Modern: the full 3x12 = 36 individual blocks with material properties
    // are deferred. Register one representative per stone variant so the
    // enum strings exist and the BlockSheetedFrame drop tables can resolve
    // when needed. The per-StoneType textures are Phase 6.
    //
    // tex gregtech:blocks/casings/stone_variant/<stoneVariant>/<stoneType>
    // ==================================================================
    // Default representatives use legacy Gabbro art; the 12 per-StoneType registrations remain deferred.
    public static final BlockEntry<Block> SUSY_STONE_SMOOTH = createStoneDecorativeBlock("susy_stone_smooth"); // TODO)) 12 stone types
    public static final BlockEntry<Block> SUSY_STONE_COBBLE = createStoneDecorativeBlock("susy_stone_cobble"); // TODO)) 12 stone types
    public static final BlockEntry<Block> SUSY_STONE_BRICKS = createStoneDecorativeBlock("susy_stone_bricks"); // TODO)) 12 stone types

    // ==================================================================
    // Rocket Casing Blocks (deferred -- rocket/launch scope)
    //
    // These 1.12.2 classes live in supersymmetry.common.blocks.rocketry:
    //   BlockCombustionChamber  (3 variants: bipropellant/monopropellant/oxidiser)
    //   BlockTurboPump          (1 variant: basic)
    //   BlockRocketNozzle       (3 variants: bell/plug/expanding)
    //   BlockLifeSupport        (1 variant: oxygen_regen)
    //   BlockRoomPadding        (1 variant: padding)
    //   BlockSpacecraftHull     (1 variant: al_li)
    //   BlockFairingHull        (1 variant: al_7075)
    //   BlockFairingConnector   (inherits FairingType)
    //   BlockGuidanceSystem     (1 variant: soyuz)
    //   BlockTankShell          (2 variants: al_2219, steel)
    //   BlockTankShell1         (1 variant: carbon)
    //   BlockRocketControl      (1 variant: basic)
    //   BlockProcessorCluster   (1 variant: tier1)
    //   BlockOuterHatch         (1 variant: al_2219)
    //   BlockInterStage         (1 variant: al_7075)
    //   BlockRocketMultiblockCasing (4 variants: vinyl_ceiling_tile, ceiling_grid_filter_unit,
    //                                vinyl_composite_flooring, aerospace_gasket)
    //
    // Some are directional or weighted (rocket mass system). The entire
    // rocket assembly is a separate later-phase scope. For now these are
    // left as TODO entries so the enum names are documented.
    // ==================================================================
    // TODO: register rocket casing blocks with their full behaviour.

    public static void init() {}

    private SusyBlocks() {}
}
