package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import io.github.symmetricdevs.supersymmetry.api.recipes.properties.SuSyRecipePropertyKeys;
import io.github.symmetricdevs.supersymmetry.common.data.SusyBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityMixerSettler} — the V1 "Mixer Settler
 * (Old)", NOT the V2 machine ({@code MixerSettlerMachine}). V1 is a 5x5-aisle structure
 * whose width (2*sDist+1) is detected at form time by scanning left AND right of the
 * controller for the steel-solid edge casing; V2 has a different layout, scan axis, and
 * ability placement, so it cannot be reused here.
 * <p>
 * The variable-width pattern is supplied by overriding {@link #getPattern()} (the
 * CleanroomMachine/EvaporationPoolMachine dynamic-pattern idiom): the definition's
 * nominal {@code .pattern(...)} is only the MIN_RADIUS fallback the builder requires.
 * sDist persistence + client sync (1.12.2 NBT/writeCustomData/initial-sync) is handled
 * by the single {@code @Persisted @DescSynced} field below + the chained field holder.
 * <p>
 * Per-recipe behavior (cell gate, cell-surplus duration scaling, pure-parallel x16)
 * lives in the registration's RecipeModifierList ({@link #cellModifier} +
 * SuSyParallelLogic.pureParallel) because doModifyRecipe is final on multiblocks.
 */
public class MixerSettlerV1Machine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MixerSettlerV1Machine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int MIN_RADIUS = 2;
    public static final int MAX_RADIUS = 20;
    /** 1.12.2 MixerSettlerRecipeLogic#getParallelLimit. */
    public static final int PARALLEL_LIMIT = 16;
    /** 1.12.2 MixerSettlerCellsProperty fallback when a recipe declares no cell count. */
    private static final int DEFAULT_RECIPE_CELLS = 2;

    /** Distance from the controller to the steel-solid edge casing on either side. */
    @Persisted
    @DescSynced
    private int sDist;

    public MixerSettlerV1Machine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public int getSDist() {
        return sDist;
    }

    // ------------------------------------------------------------------
    // Dynamic structure size (1.12.2 updateStructureDimensions)
    // ------------------------------------------------------------------

    // TODO)) Optimization: cache the built BlockPattern keyed by sDist instead of
    // rescanning the world and rebuilding 25 aisle strings on every getPattern()
    // call (structure re-checks). Invalidate on structure invalidation. Behaviour is
    // identical; this is a pure perf deferral (same as EvaporationPoolMachine).
    @Override
    public @NotNull BlockPattern getPattern() {
        // Same contract as CleanroomMachine.getPattern(): refresh the scanned size
        // (read-only blockstate gets, safe on the async pattern-check thread), clamp so
        // JEI/autobuild always see a buildable pattern, then build for that size. A
        // failed scan keeps the previous/clamped size, so the pattern simply won't
        // match and the structure un-forms — the 1.12.2 invalidateStructure() path.
        if (getLevel() != null) {
            updateStructureDimensions();
        }
        return createPattern(Math.max(sDist, MIN_RADIUS), getDefinition());
    }

    /**
     * Scan left and right of the controller for the steel-solid edge casing; the first
     * distance where BOTH sides hit it on the same step is sDist. Verbatim port of the
     * 1.12.2 {@code updateStructureDimensions()} (minus the custom-data packet, replaced
     * by the synced field).
     */
    private boolean updateStructureDimensions() {
        Level level = getLevel();
        if (level == null) {
            return false;
        }
        Direction left = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(),
                isFlipped());
        Direction right = left.getOpposite();

        BlockPos.MutableBlockPos lPos = getPos().mutable();
        BlockPos.MutableBlockPos rPos = getPos().mutable();

        int scanned = 0;
        for (int i = 1; i <= MAX_RADIUS; i++) {
            // The non-short-circuit & is load-bearing in the legacy source: both mutable
            // positions must advance on every iteration, so this must not be &&.
            if (isEdgeCasing(level, lPos.move(left)) & isEdgeCasing(level, rPos.move(right))) {
                scanned = i;
                break;
            }
        }

        if (scanned < MIN_RADIUS) {
            return false;
        }

        sDist = scanned;
        return true;
    }

    private static boolean isEdgeCasing(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(GTBlocks.CASING_STEEL_SOLID.get());
    }

    // ------------------------------------------------------------------
    // Pattern construction (1.12.2 createStructurePattern / buildVoxelStrings)
    // ------------------------------------------------------------------

    /**
     * Build the full 5-aisle pattern for a given radius. Used by {@link #getPattern()}
     * for the live structure check and by the definition's nominal {@code .pattern(...)}
     * fallback (MIN_RADIUS) and is the single source of truth for the where-mapping.
     */
    public static @NotNull BlockPattern createPattern(int radius,
                                                      @NotNull MultiblockMachineDefinition definition) {
        String[] strings = buildVoxelStrings(radius);
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK)
                .aisle(strings[0], strings[1], strings[2], strings[3], strings[4])
                .aisle(strings[5], strings[6], strings[7], strings[8], strings[9])
                .aisle(strings[10], strings[11], strings[12], strings[13], strings[14])
                .aisle(strings[15], strings[16], strings[17], strings[18], strings[19])
                .aisle(strings[20], strings[21], strings[22], strings[23], strings[24])
                .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                .where('I', Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                .where('O', Predicates.abilities(PartAbility.EXPORT_FLUIDS))
                .where('T', Predicates.blocks(SusyBlocks.COALESCENCE_PLATE.get()))
                .where('P', Predicates.blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                .where('D', Predicates.blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                .where('C', Predicates.blocks(GTBlocks.CASING_STAINLESS_CLEAN.get())
                        .or(autoAbilities()))
                .where('G', Predicates.blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                .where('M', Predicates.blocks(GTBlocks.CASING_STAINLESS_STEEL_GEARBOX.get()))
                .where('F', Predicates.frames(GTMaterials.StainlessSteel))
                .where('E', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                        .or(Predicates.abilities(PartAbility.IMPORT_ITEMS, PartAbility.EXPORT_ITEMS)))
                .where(' ', Predicates.air())
                .where('#', Predicates.any())
                .build();
    }

    /**
     * 1.12.2 V1 {@code super.autoAbilities(true, false)} resolves to the 2-arg
     * (maintenance, muffler) overload on MultiblockControllerMachine — maintenance only,
     * no muffler — plus INPUT_ENERGY (1..2, preview 1) and a single IMPORT_ITEMS bus.
     * Item OUTPUT is only accepted on the 'E' edge positions; fluids only on the fixed
     * 'I'/'O' positions. No parallel hatch (SuSy pure-parallel is recipe-side).
     */
    private static TraceabilityPredicate autoAbilities() {
        return Predicates.autoAbilities(true, false, false)
                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1)
                        .setMaxGlobalLimited(2).setPreviewCount(1))
                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1)
                        .setPreviewCount(1));
    }

    /**
     * 25 strings: 5 aisles of 5 rows each, width 2*radius+1. Verbatim port of the 1.12.2
     * {@code buildVoxelStrings}; also used by the registration's {@code .shapeInfos(...)}
     * to generate one preview per size.
     */
    public static String[] buildVoxelStrings(int radius) {
        String[] result = new String[25];
        int index = 0;
        for (int aisle = 0; aisle < 5; aisle++) {
            for (int layer = 0; layer < 5; layer++) {
                result[index++] = buildRow(aisle, layer, radius);
            }
        }
        return result;
    }

    private static String buildRow(int aisle, int layer, int radius) {
        return switch (aisle) {
            case 0 -> switch (layer) {
                case 0, 4 -> buildRepeatingRow('#', '#', "####", radius);
                case 1, 3 -> buildRepeatingRow('#', '#', "GGGG", radius);
                default -> buildRepeatingRow('E', 'E', "GGGG", radius);
            };
            case 1 -> switch (layer) {
                case 0 -> buildRepeatingRow('#', '#', "CCCC", radius);
                case 1 -> buildRepeatingRow('G', 'G', " P G", radius);
                case 2 -> buildRepeatingRow('E', 'E', "FG G", radius);
                case 3 -> buildRepeatingRow('I', 'O', "FG P", radius);
                default -> buildRepeatingRow('#', '#', "MCCC", radius);
            };
            case 2 -> switch (layer) {
                case 0, 4 -> buildRepeatingRow('#', '#', "CCCC", radius);
                case 2 -> buildRepeatingRow('E', 'E', "DGDG", radius);
                default -> {
                    // Layers 1 and 3: tank casings (D) vs coalescence plates (T).
                    char repeatChar = layer == 3 ? 'T' : 'D';
                    yield buildRepeatingRow('G', 'G', repeatChar + "G" + repeatChar + "G", radius);
                }
            };
            case 3 -> switch (layer) {
                case 0 -> buildRepeatingRow('#', '#', "CCCC", radius);
                case 1 -> buildRepeatingRow('O', 'I', " G P", radius);
                case 2 -> buildRepeatingRow('E', 'E', " GFG", radius);
                case 3 -> buildRepeatingRow('G', 'G', " PFG", radius);
                default -> buildRepeatingRow('#', '#', "CCMC", radius);
            };
            default -> switch (layer) { // aisle 4 — the controller aisle
                case 2 -> buildControllerRow(radius);
                case 1, 3 -> buildRepeatingRow('#', '#', "GGGG", radius);
                default -> buildRepeatingRow('#', '#', "####", radius);
            };
        };
    }

    private static String buildRepeatingRow(char first, char last, String repeatingUnit, int radius) {
        StringBuilder sb = new StringBuilder(2 * radius + 1);
        sb.append(first);
        for (int i = 0; i < radius / 2; i++) {
            if (i == radius / 2 - 1) {
                // Last repetition: skip the unit's last character.
                sb.append(repeatingUnit, 0, repeatingUnit.length() - 1);
            } else {
                sb.append(repeatingUnit);
            }
        }
        sb.append(last);
        return sb.toString();
    }

    private static String buildControllerRow(int radius) {
        // "ECCCSCCCE" widened: edge casing, radius-1 casings, controller, casings, edge.
        StringBuilder sb = new StringBuilder(2 * radius + 1);
        sb.append('E');
        sb.append("C".repeat(radius - 1));
        sb.append('S');
        sb.append("C".repeat(radius - 1));
        sb.append('E');
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Recipe behavior (1.12.2 MixerSettlerRecipeLogic)
    // ------------------------------------------------------------------

    /**
     * Cell gate + cell-surplus speedup. Layered AFTER the stock non-perfect OC in the
     * registration, mirroring 1.12.2 {@code modifyOverclockPost}, which post-processed
     * the finished overclock. Rejects (cancels) recipes whose required cell count
     * ({@code mixer_settler_cells} recipe data, default 2) exceeds the formed
     * structure's sDist — the 1.12.2 {@code checkRecipe} gate; otherwise multiplies the
     * duration by {@code 1 / (atan((sDist - cells)/2 + 1) * 4/PI)} (1.12.2 divided the
     * duration by that factor, so the multiplier is the reciprocal), approaching 0.5x
     * as the cell surplus grows. The atan argument keeps the legacy integer division.
     */
    public static @NotNull ModifierFunction cellModifier(@NotNull MetaMachine machine,
                                                         @NotNull GTRecipe recipe) {
        if (!(machine instanceof MixerSettlerV1Machine settler)) {
            return ModifierFunction.NULL;
        }
        int cells = recipe.data.contains(SuSyRecipePropertyKeys.MIXER_SETTLER_CELLS, Tag.TAG_INT) ?
                recipe.data.getInt(SuSyRecipePropertyKeys.MIXER_SETTLER_CELLS) : DEFAULT_RECIPE_CELLS;
        int cellsOff = settler.getSDist() - cells;
        if (cellsOff < 0) {
            return ModifierFunction.cancel(Component.translatable(
                    "susy.recipe_modifier.mixer_settler_insufficient_cells", cells, settler.getSDist()));
        }
        double factor = Math.atan(cellsOff / 2 + 1) * 4 / Math.PI;
        return ModifierFunction.builder().durationMultiplier(1.0 / factor).build();
    }
}
