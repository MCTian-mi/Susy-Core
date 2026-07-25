package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
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
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityMixerSettlerV2}: a dynamic-width
 * mixer settler whose structure holds 2..20 settling cells (step 2). The cell count
 * is detected at form-time by scanning for the PTFE pipe edge behind the controller
 * (the {@link com.gregtechceu.gtceu.common.machine.multiblock.electric.CleanroomMachine}
 * variable-dimension idiom), and the pattern is built to size on every
 * {@link #getPattern()} call.
 * <p>
 * Per-recipe behaviour (the 1.12.2 {@code MixerSettlerRecipeLogic}: cell-count gate,
 * atan duration scaling, pure-parallel x16) lives in the registration's
 * {@code .recipeModifiers(...)} list because {@code doModifyRecipe} is final on
 * {@code WorkableMultiblockMachine} — see {@link #cellModifier}.
 */
public class MixerSettlerMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MixerSettlerMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int MIN_CELLS = 2;
    public static final int MAX_CELLS = 20;

    /** Number of settling cells in the formed structure. Replaces the 1.12.2 NBT +
     * writeCustomData(UPDATE_STRUCTURE_SIZE) + initial-sync triplet. */
    @Persisted
    @DescSynced
    private int sDist;

    public MixerSettlerMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public int getSDist() {
        return sDist;
    }

    /**
     * Verbatim port of the 1.12.2 {@code updateStructureDimensions()}: walk from the
     * controller one back and one to the (pattern-space) left edge, then step right
     * three blocks at a time counting cells until a block is not the PTFE pipe casing.
     * Read-only; runs on the async pattern-check thread (same constraint as
     * CleanroomMachine — no world mutation here).
     */
    private boolean updateStructureDimensions() {
        Level level = getLevel();
        if (level == null) {
            return false;
        }
        // controller is facing outwards so right is left (1.12.2 comment). Modern
        // RelativeDirection is a 1:1 port of the 1.12.2 enum, and getUpwardsFacing()
        // returns NORTH for allowExtendedFacing(false) in both versions, so the same
        // call chain yields the same world direction.
        Direction left = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction right = left.getOpposite();
        Direction back = left.getClockWise();

        BlockPos.MutableBlockPos pos = getPos().mutable();
        pos.move(back);
        pos.move(left); // start on left edge so we only need to move 3 at a time

        int cells = 0;
        for (int i = MIN_CELLS; i <= MAX_CELLS; i += 2) {
            pos.move(right, 3);
            // checking for a MTE with fluid output multiblock ability seems annoying,
            // so we go until we don't see a PTFE pipe
            if (!level.getBlockState(pos).is(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get())) {
                cells = i;
                break;
            }
        }

        if (cells < MIN_CELLS) {
            // 1.12.2 invalidateStructure(): the (clamped) minimum-size pattern below
            // simply won't match a broken/undersized structure, so the async check
            // fails and the structure un-forms on its own.
            return false;
        }

        this.sDist = cells;
        return true;
    }

    // TODO)) Optimization: cache the built BlockPattern keyed by sDist instead of
    // rescanning the world and rebuilding the pattern on every getPattern() call
    // (structure re-checks, previews). Invalidate on structure invalidation. Behaviour
    // is identical; this is a pure perf deferral (same as EvaporationPoolMachine).
    @Override
    public @NotNull BlockPattern getPattern() {
        if (getLevel() != null) {
            updateStructureDimensions();
        }
        // Clamp for JEI/autobuild and for the not-yet-scanned case (sDist == 0), so a
        // representative minimum-size pattern always exists (CleanroomMachine idiom).
        int cells = Math.max(sDist, MIN_CELLS);
        return buildPattern(cells, getDefinition());
    }

    /**
     * Builds the full pattern for a given cell count. Single source of truth for the
     * {@code .where(...)} mapping — used by the definition's nominal
     * {@code .pattern(...)}/{@code .shapeInfos(...)} and by the runtime
     * {@link #getPattern()} override.
     * <p>
     * The explicit {@code (RIGHT, UP, BACK)} start directions reproduce the 1.12.2
     * {@code FactoryBlockPattern.start()} defaults; Modern's no-arg default is
     * {@code (LEFT, UP, FRONT)}, which would rotate the structure 180° about the
     * controller relative to the 1.12.2 layout (and break the scan-direction
     * agreement in {@link #updateStructureDimensions()}).
     */
    public static @NotNull BlockPattern buildPattern(int cells, @NotNull MultiblockMachineDefinition definition) {
        String[][] layers = buildPatternStrings(cells);
        String pad = " ".repeat((cells / 2 - 1) * 3 + 1);
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK)
                .aisle(layers[0][0], layers[1][0], layers[2][0])
                .aisle(layers[0][1], layers[1][1], layers[2][1])
                .aisle(layers[0][2], layers[1][2], layers[2][2])
                .aisle(layers[0][3], layers[1][3], layers[2][3])
                .aisle(layers[0][4], layers[1][4], layers[2][4])
                .aisle(" XX" + pad, " SX" + pad, " XX" + pad) // control panel
                .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                // 1.12.2: states(casing).or(autoAbilities()) where autoAbilities() =
                // super.autoAbilities(maintenance=true, muffler=false) + INPUT_ENERGY
                // min1/max2/preview1 + IMPORT_ITEMS max1/preview1 + EXPORT_ITEMS
                // max1/preview1. Fluids are deliberately NOT on 'X' — they live only in
                // the dedicated 'I'/'O' slots. Modern mapping by meaning:
                .where('X', Predicates.blocks(GTBlocks.CASING_STAINLESS_CLEAN.get())
                        .or(Predicates.autoAbilities(true, false, false)) // maintenance hatch
                        .or(Predicates.abilities(PartAbility.INPUT_ENERGY)
                                .setMinGlobalLimited(1).setMaxGlobalLimited(2).setPreviewCount(1))
                        .or(Predicates.abilities(PartAbility.IMPORT_ITEMS)
                                .setMaxGlobalLimited(1).setPreviewCount(1))
                        .or(Predicates.abilities(PartAbility.EXPORT_ITEMS)
                                .setMaxGlobalLimited(1).setPreviewCount(1)))
                .where('I', Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                .where('O', Predicates.abilities(PartAbility.EXPORT_FLUIDS))
                .where('P', Predicates.blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                .where('C', Predicates.blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                .where('T', Predicates.blocks(SusyBlocks.COALESCENCE_PLATE.get()))
                .where('G', Predicates.blocks(GTBlocks.CASING_STAINLESS_STEEL_GEARBOX.get()))
                .where('F', Predicates.frames(GTMaterials.StainlessSteel))
                .where(' ', Predicates.any())
                .build();
    }

    /** Verbatim port of the 1.12.2 {@code buildPatternStrings(cells)}. */
    private static @NotNull String[][] buildPatternStrings(int cells) {
        // 1 2 3
        // FF CC
        // CC CC
        // CC CC
        // .. PP
        // CC CC GG
        final int width = cells / 2 * 3 + 1;
        final int depth = 5;
        char[][] layer0 = new char[depth][width];
        char[][] layer1 = new char[depth][width];
        char[][] layer2 = new char[depth][width];

        for (int x = 0; x < width; x++) {
            layer0[0][x] = (x == 0 || x == width - 1) ? ' ' : 'F'; // frames at the bottom back

            // tank casings/coalescence plates
            layer0[1][x] = (x % 3 == 0) ? ' ' : 'C';
            layer0[2][x] = (x % 3 == 0) ? ' ' : 'C';
            layer1[0][x] = (x % 3 == 0) ? ' ' : 'C';
            layer1[1][x] = (x % 3 == 0) ? ' ' : 'C';
            layer1[2][x] = (x % 3 == 0) ? ' ' : 'T';

            // mixer-tank connecting pipes
            layer1[3][x] = (x % 3 == 0) ? ' ' : 'P';

            if (x == 0) {
                // top input, bottom output on left
                layer0[4][x] = 'O';
                layer1[4][x] = 'I';
            } else if (x == width - 1) {
                // top output, bottom input on right
                layer0[4][x] = 'I';
                layer1[4][x] = 'O';
            } else {
                // mixer casings & connecting pipes
                layer0[4][x] = (x % 3 == 0) ? 'P' : 'C';
                layer1[4][x] = (x % 3 == 0) ? 'P' : 'C';
            }

            // top row with casing on left, then 2 gearbox + frame for each cell pair
            layer2[4][x] = x == 0 ? 'C' : (x % 3 == 0 ? 'F' : 'G');
        }

        // empty sections under the pipes and above the tanks
        Arrays.fill(layer0[3], ' ');
        Arrays.fill(layer2[0], ' ');
        Arrays.fill(layer2[1], ' ');
        Arrays.fill(layer2[2], ' ');
        Arrays.fill(layer2[3], ' ');

        var layers = new String[3][depth];
        for (int z = 0; z < depth; z++) {
            layers[0][z] = new String(layer0[z]);
            layers[1][z] = new String(layer1[z]);
            layers[2][z] = new String(layer2[z]);
        }
        return layers;
    }

    /**
     * Port of the 1.12.2 {@code MixerSettlerRecipeLogic}: {@code checkRecipe} gate
     * (the machine must hold at least as many cells as the recipe requires) fused with
     * {@code modifyOverclockPost} (duration divided by
     * {@code atan((sDist - cells)/2 + 1) * 4/PI}, a speed-up approaching 2x as the cell
     * surplus grows). Registered after {@code GTRecipeModifiers.OC_NON_PERFECT} so the
     * multiplier composes on top of the standard OC duration, mirroring
     * modifyOverclockPost-after-calculateOverclock.
     */
    public static @NotNull ModifierFunction cellModifier(@NotNull MetaMachine machine,
                                                         @NotNull GTRecipe recipe) {
        if (!(machine instanceof MixerSettlerMachine settler)) {
            return ModifierFunction.NULL;
        }
        // 1.12.2 MixerSettlerCellsProperty fallback default is 2 cells; the
        // contains-guard is mandatory because CompoundTag.getInt returns 0 when absent.
        int recipeCells = recipe.data.contains(SuSyRecipePropertyKeys.MIXER_SETTLER_CELLS) ?
                recipe.data.getInt(SuSyRecipePropertyKeys.MIXER_SETTLER_CELLS) : MIN_CELLS;
        if (settler.getSDist() - recipeCells < 0) {
            return ModifierFunction.cancel(Component.translatable(
                    "susy.recipe_modifier.mixer_settler_insufficient_cells",
                    recipeCells, settler.getSDist()));
        }
        // 1.12.2: cellsOff = (sDist - cells) / 2 in integer math, then
        // duration /= atan(cellsOff + 1) * 4 / PI. durationMultiplier multiplies, so
        // pass the reciprocal. cellsOff == 0 yields exactly atan(1)*4/PI == 1 (no change).
        int cellsOff = (settler.getSDist() - recipeCells) / 2;
        double speedup = Math.atan(cellsOff + 1) * 4.0 / Math.PI;
        return ModifierFunction.builder().durationMultiplier(1.0 / speedup).build();
    }
}
