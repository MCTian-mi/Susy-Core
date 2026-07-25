package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import io.github.symmetricdevs.supersymmetry.api.recipes.logic.SuSyParallelLogic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityGreenhouse}: a variable-length
 * glasshouse whose cell count is {@code length / 4}, capped at 25. The length is
 * detected at form-time by scanning backward from the controller; the pattern is
 * rebuilt to size on every {@link #getPattern()} call. Recipe parallelism is
 * capped at the cell count.
 * <p>
 * Modern notes:
 * <ul>
 * <li>{@code doModifyRecipe} is final on multiblocks, so the cell-based parallel
 * limit is implemented via a {@link ModifierFunction} in the registration that
 * delegates to {@link SuSyParallelLogic#pureParallel(MetaMachine, GTRecipe, int)}.</li>
 * <li>Structure size sync uses {@code @Persisted @DescSynced} fields, replacing the
 * 1.12.2 NBT + writeCustomData/initial-sync triplet.</li>
 * </ul>
 */
public class GreenhouseMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            GreenhouseMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int MAX_LENGTH = 25;
    public static final int MIN_LENGTH = 4;
    public static final int CELLS_PER_SECTION = 4;

    @Persisted
    @DescSynced
    private int length;
    @Persisted
    @DescSynced
    private int cellCount;

    public GreenhouseMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public int getLength() {
        return length;
    }

    public int getCellCount() {
        return cellCount;
    }

    /**
     * Verbatim port of the 1.12.2 {@code updateStructureDimensions()}: walk backward
     * from the controller (world space) and stop at the first casing/machine edge. The
     * length must be a multiple of {@value #CELLS_PER_SECTION}.
     */
    private boolean updateStructureDimensions() {
        Level level = getLevel();
        if (level == null) {
            return false;
        }
        Direction back = getFrontFacing().getOpposite();
        BlockPos.MutableBlockPos pos = getPos().mutable();

        int scanned = 0;
        for (int i = 1; i <= MAX_LENGTH; i++) {
            if (isEdgeBlock(level, pos.move(back))) {
                scanned = i;
                break;
            }
        }

        if (scanned < MIN_LENGTH || (scanned % CELLS_PER_SECTION) != 0) {
            return false;
        }

        this.length = scanned;
        this.cellCount = scanned / CELLS_PER_SECTION;
        return true;
    }

    private boolean isEdgeBlock(@NotNull Level level, @NotNull BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        if (level.getBlockState(pos).is(GTBlocks.CASING_STEEL_SOLID.get())) {
            return true;
        }
        return MetaMachine.getMachine(level, pos) instanceof com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
    }

    // TODO)) Optimization: cache the built BlockPattern keyed by length instead of
    // rebuilding it on every getPattern() call. Invalidate on structure invalidation.
    @Override
    public @NotNull BlockPattern getPattern() {
        if (getLevel() != null) {
            updateStructureDimensions();
        }
        int cells = Math.max(cellCount, 1);
        return buildPattern(cells, getDefinition());
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (getLevel() != null) {
            updateStructureDimensions();
        }
    }

    /**
     * Recipe modifier that gates the greenhouse by its measured cell count and applies
     * pure-parallel batching up to that count.
     */
    public static @NotNull ModifierFunction cellParallelModifier(@NotNull MetaMachine machine,
                                                                  @NotNull GTRecipe recipe) {
        if (!(machine instanceof GreenhouseMachine greenhouse)) {
            return ModifierFunction.NULL;
        }
        int cells = Math.max(greenhouse.getCellCount(), 1);
        return SuSyParallelLogic.pureParallel(machine, recipe, cells);
    }

    /**
     * Builds the full pattern for a given cell count. The layout is a 5-wide, 4-tall
     * tunnel; the middle {@code cells - 1} sections repeat the 4-aisle cell block.
     */
    public static @NotNull BlockPattern buildPattern(int cells, @NotNull MultiblockMachineDefinition definition) {
        var builder = FactoryBlockPattern
                .start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT);

        builder.aisle("CCSCC", "FGGGF", "FGGGF", " FFF ");
        builder.aisle("CDDDC", "G###G", "G###G", " GGG ");
        builder.aisle("CDDDC", "G###G", "G###G", " GGG ");
        builder.aisle("CDDDC", "G###G", "G###G", " GGG ");

        for (int i = 1; i < cells; i++) {
            builder.aisle("CDDDC", "F###F", "F###F", " FFF ");
            builder.aisle("CDDDC", "G###G", "G###G", " GGG ");
            builder.aisle("CDDDC", "G###G", "G###G", " GGG ");
            builder.aisle("CDDDC", "G###G", "G###G", " GGG ");
        }

        return builder.aisle("CCCCC", "FGGGF", "FGGGF", " FFF ")
                .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                .where('C', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                        .or(Predicates.autoAbilities(definition.getRecipeTypes())))
                .where('D', Predicates.blocks(Blocks.DIRT, Blocks.GRASS_BLOCK))
                .where('G', Predicates.blocks(Blocks.GLASS))
                .where('F', Predicates.frames(GTMaterials.Steel))
                .where(' ', Predicates.any())
                .where('#', Predicates.air())
                .build();
    }

    /**
     * One preview per cell count 1..5 (matching the 1.12.2 {@code getMatchingShapes()}).
     */
    public static @NotNull List<MultiblockShapeInfo> buildShapeInfos(
            @NotNull MultiblockMachineDefinition definition) {
        List<MultiblockShapeInfo> shapes = new ArrayList<>();
        for (int cells = 1; cells <= 5; cells++) {
            BlockPattern pattern = buildPattern(cells, definition);
            int[] repetition = new int[pattern.aisleRepetitions.length];
            java.util.Arrays.fill(repetition, 1);
            shapes.add(new MultiblockShapeInfo(pattern.getPreview(repetition)));
        }
        return shapes;
    }
}
