package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.BlockInfo;

import io.github.symmetricdevs.supersymmetry.common.data.SusyBlocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityHeatRadiator}: a dynamically-sized,
 * no-energy multiblock that radiates heat. The structure is a flat wall of steel
 * casing with a serpentine core; its size is measured from the world (radius
 * {@code sDist} 1..5 to each side, height {@code bDist} 1..14+ middle rows) and the
 * parallel count is the dissipation area {@code bDist * (2 * sDist - 1)}, i.e. the
 * number of serpentine blocks.
 * <p>
 * The 1.12.2 controller built its {@code BlockPattern} from the measured dimensions
 * inside {@code createStructurePattern()} and re-measured once a second while
 * unformed. Modern invokes {@link #getPattern()} fresh on every structure check
 * (and re-checks every 4 periods while errored/unformed), so overriding it to
 * re-scan and rebuild reproduces the legacy behavior with no extra ticking. The
 * definition-level {@code .pattern(...)} is only the memoized fallback the builder
 * requires; {@link #onStructureFormed()} re-runs the same scan to publish
 * {@code sDist}/{@code bDist}/{@code area} for the recipe modifier and display.
 */
public class HeatRadiatorMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            HeatRadiatorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int MIN_RADIUS = 1;
    public static final int MIN_HEIGHT = 1;
    /** Largest radius the legacy side scan could find; also the preview width bound. */
    public static final int MAX_RADIUS = 5;
    /** Legacy preview height bound (the runtime scan itself allows up to 15 middle rows). */
    public static final int MAX_HEIGHT = 14;

    @Persisted
    @DescSynced
    private int sDist;
    @Persisted
    @DescSynced
    private int bDist;
    @Persisted
    @DescSynced
    private int area;

    public HeatRadiatorMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public int getSDist() {
        return sDist;
    }

    public int getBDist() {
        return bDist;
    }

    public int getArea() {
        return area;
    }

    // TODO)) Optimization: cache the built BlockPattern + scanned dimensions keyed by
    // (sDist, bDist) instead of rescanning the world and rebuilding the pattern on every
    // getPattern() call (structure re-checks). Invalidate on structure invalidation.
    // Behaviour is identical; pure perf deferral (same as EvaporationPoolMachine).
    @Override
    public BlockPattern getPattern() {
        Level level = getLevel();
        if (level == null) {
            return buildPattern(getDefinition(), MIN_RADIUS, MIN_HEIGHT);
        }
        int[] dims = scanDimensions(level);
        if (dims == null) {
            return createInvalidPattern();
        }
        return buildPattern(getDefinition(), dims[0], dims[1]);
    }

    /**
     * Builds the radiator pattern for the given measured dimensions. A faithful port of
     * the 1.12.2 {@code createStructurePattern()}: chars run along RIGHT, one row per
     * aisle, aisles stack UP (bottom casing row with the controller, {@code 1..bDist}
     * serpentine middle rows, casing top row).
     */
    public static BlockPattern buildPattern(@NotNull MultiblockMachineDefinition definition, int sDist,
                                            int bDist) {
        return FactoryBlockPattern
                .start(RelativeDirection.RIGHT, RelativeDirection.FRONT, RelativeDirection.UP)
                .aisle(rowPattern(RowType.BOTTOM, sDist))
                .aisle(rowPattern(RowType.MIDDLE, sDist)).setRepeatable(1, bDist)
                .aisle(rowPattern(RowType.TOP, sDist))
                .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                // 1.12.2 (F,T,T,F,F,F,F) by meaning: item input bus + maintenance hatch.
                .where('A', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                        .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                false, false, true, false, false, false))
                        .or(Predicates.autoAbilities(true, false, false)))
                .where('B', Predicates.blocks(SusyBlocks.BASIC_SERPENTINE.get()))
                // 1.12.2 fluid-in exactly 1 + fluid-out exactly 1; no energy, no muffler.
                .where('C', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                        .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                false, false, false, false, true, false).setExactLimit(1))
                        .or(Predicates.autoAbilities(definition.getRecipeTypes(),
                                false, false, false, false, false, true).setExactLimit(1)))
                .build();
    }

    private BlockPattern createInvalidPattern() {
        TraceabilityPredicate never = new TraceabilityPredicate(state -> false, () -> new BlockInfo[0]);
        return FactoryBlockPattern
                .start(RelativeDirection.RIGHT, RelativeDirection.FRONT, RelativeDirection.UP)
                .aisle("S!")
                .where('S', Predicates.controller(Predicates.blocks(getDefinition().getBlock())))
                .where('!', never)
                .build();
    }

    /**
     * Port of the 1.12.2 {@code updateStructureDimensions()} scan: find the first
     * casing/machine "edge" block above the controller (height) and the first pair of
     * edge blocks equidistant to the left and right on the first middle row (radius).
     * Returns {@code [sDist, bDist]}, or null when below the minimum size (the legacy
     * invalidated the structure in that case; here the caller serves an unmatchable
     * pattern instead, which has the same effect on the next structure check).
     */
    private int @Nullable [] scanDimensions(@NotNull Level level) {
        Direction front = getFrontFacing();
        // The structure is left/right symmetric, so the legacy rotateAround-based
        // "left" vs Modern's RelativeDirection.LEFT (mirrored) is immaterial.
        Direction up = RelativeDirection.UP.getRelative(front, getUpwardsFacing(), isFlipped());
        Direction left = RelativeDirection.LEFT.getRelative(front, getUpwardsFacing(), isFlipped());
        Direction right = left.getOpposite();

        BlockPos selfPos = getPos();
        int sDist = 0;
        int bDist = 0;

        // Legacy loop bound 16: tests distances 1..16 above the controller (bDist up to 15).
        for (int i = 0; i < 16; i++) {
            if (isEdgeBlock(level, selfPos.relative(up, i + 1))) {
                bDist = i;
                break;
            }
        }

        BlockPos sideBase = selfPos.relative(up);
        for (int i = 1; i <= MAX_RADIUS; i++) {
            if (isEdgeBlock(level, sideBase.relative(left, i)) &&
                    isEdgeBlock(level, sideBase.relative(right, i))) {
                sDist = i;
                break;
            }
        }

        if (sDist < MIN_RADIUS || bDist < MIN_HEIGHT) {
            return null;
        }
        return new int[] { sDist, bDist };
    }

    /** 1.12.2 edge test: steel casing or any machine block entity (hatches count as edges). */
    private static boolean isEdgeBlock(@NotNull Level level, @NotNull BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        if (level.getBlockState(pos).is(GTBlocks.CASING_STEEL_SOLID.get())) {
            return true;
        }
        return level.getBlockEntity(pos) instanceof IMachineBlockEntity;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Level level = getLevel();
        if (level == null) {
            return;
        }
        // The pattern supplier is pure w.r.t. machine fields (it runs inside async
        // pattern matching), so the measured dimensions are published here instead.
        int[] dims = scanDimensions(level);
        if (dims != null) {
            sDist = dims[0];
            bDist = dims[1];
            area = bDist * (2 * sDist - 1);
        }
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        // 1.12.2 replaced the stock display (energy usage/tier + parallel-hatch lines)
        // with working status, progress, and the measured dissipation area shown as the
        // parallel count.
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(recipeLogic.isWorkingEnabled(), recipeLogic.isActive())
                .addWorkingStatusLine()
                .addProgressLine(recipeLogic);
        if (isFormed()) {
            MutableComponent parallelLine = Component
                    .translatable("susy.machine.heat_radiator.parallel",
                            Component.literal(FormattingUtil.formatNumbers(area))
                                    .withStyle(ChatFormatting.DARK_PURPLE))
                    .withStyle(ChatFormatting.GRAY);
            parallelLine.withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Component.translatable("susy.machine.heat_radiator.parallel_hover")
                            .withStyle(ChatFormatting.GRAY))));
            textList.add(parallelLine);
        }
        // Keep the per-part lines (e.g. maintenance problems) the stock override appends.
        for (IMultiPart part : getParts()) {
            part.addMultiText(textList);
        }
    }

    /**
     * Port of the 1.12.2 {@code getMatchingShapes()}: one 3-high preview per radius
     * 1..5, then one preview per height 2..14 at the maximum radius, with fluid in/out
     * and maintenance hatches on the left/right edges of the first two rows.
     */
    public static @NotNull List<MultiblockShapeInfo> buildShapeInfos(@NotNull MultiblockMachineDefinition definition) {
        List<MultiblockShapeInfo> shapes = new ArrayList<>();
        for (int radius = MIN_RADIUS; radius <= MAX_RADIUS; radius++) {
            shapes.add(shapeBuilder(definition)
                    .aisle(rowPattern(RowType.BOTTOM_PREVIEW, radius),
                            rowPattern(RowType.MIDDLE_PREVIEW, radius),
                            rowPattern(RowType.TOP, radius))
                    .build());
        }
        for (int height = 2; height <= MAX_HEIGHT; height++) {
            String[] rows = new String[height + 2];
            Arrays.fill(rows, rowPattern(RowType.MIDDLE, MAX_RADIUS));
            rows[0] = rowPattern(RowType.BOTTOM_PREVIEW, MAX_RADIUS);
            rows[1] = rowPattern(RowType.MIDDLE_PREVIEW, MAX_RADIUS);
            rows[height + 1] = rowPattern(RowType.TOP, MAX_RADIUS);
            shapes.add(shapeBuilder(definition).aisle(rows).build());
        }
        return shapes;
    }

    private static MultiblockShapeInfo.ShapeInfoBuilder shapeBuilder(@NotNull MultiblockMachineDefinition definition) {
        return MultiblockShapeInfo.builder()
                .where('S', definition, Direction.SOUTH)
                .where('M', GTMachines.MAINTENANCE_HATCH, Direction.NORTH)
                .where('I', GTMachines.FLUID_IMPORT_HATCH[GTValues.LV], Direction.NORTH)
                .where('O', GTMachines.FLUID_EXPORT_HATCH[GTValues.LV], Direction.NORTH)
                .where('A', GTBlocks.CASING_STEEL_SOLID.get())
                .where('C', GTBlocks.CASING_STEEL_SOLID.get())
                .where('B', SusyBlocks.BASIC_SERPENTINE.get());
    }

    /** Row kinds of the 1.12.2 {@code rowPattern}; the *_PREVIEW rows carry the preview hatches. */
    public enum RowType {
        BOTTOM,
        MIDDLE,
        TOP,
        BOTTOM_PREVIEW,
        MIDDLE_PREVIEW
    }

    /**
     * Faithful port of the 1.12.2 {@code rowPattern()}: a string of width
     * {@code 2 * radius + 1} with the center char, symmetric inner chars, and one
     * (possibly different) edge char on each end.
     */
    public static String rowPattern(@NotNull RowType rowType, int radius) {
        char center;
        char left;
        char right;
        char other;

        // A: metal casing; S: controller; C: casing or fluid hatches; B: serpentine;
        // I/O/M: preview fluid-in / fluid-out / maintenance hatches.
        switch (rowType) {
            case BOTTOM -> {
                center = 'S';
                left = 'A';
                right = 'A';
                other = 'A';
            }
            case MIDDLE -> {
                center = 'B';
                left = 'C';
                right = 'C';
                other = 'B';
            }
            case TOP -> {
                center = 'A';
                left = 'A';
                right = 'A';
                other = 'A';
            }
            case BOTTOM_PREVIEW -> {
                center = 'S';
                left = 'M';
                right = 'A';
                other = 'A';
            }
            case MIDDLE_PREVIEW -> {
                center = 'B';
                left = 'I';
                right = 'O';
                other = 'B';
            }
            default -> throw new IllegalArgumentException("Invalid rowType: " + rowType);
        }

        StringBuilder rowBuilder = new StringBuilder();
        for (int i = 0; i < radius; i++) {
            if (i == 0) {
                rowBuilder.append(center);
            } else {
                rowBuilder.append(other);
                rowBuilder.insert(0, other);
            }
        }
        rowBuilder.append(right);
        rowBuilder.insert(0, left);

        return rowBuilder.toString();
    }
}
