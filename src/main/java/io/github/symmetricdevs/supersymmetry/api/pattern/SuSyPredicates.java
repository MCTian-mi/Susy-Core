package io.github.symmetricdevs.supersymmetry.api.pattern;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.lowdragmc.lowdraglib.utils.BlockInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;

/**
 * SuSy multiblock pattern predicates, ported from the 1.12.2
 * {@code supersymmetry.api.metatileentity.multiblock.SuSyPredicates}.
 *
 * <p>
 * <b>Modern predicates are pure</b> — they may not call {@code world.setBlockState}
 * during matching. The 1.12.2 orientation predicates ({@code orientation},
 * {@code horizontalOrientation}, {@code eccentricRolls}, {@code hiddenGearTooth})
 * side-effected the world to auto-rotate a rotatable block into its working facing as
 * the structure formed. Here they are re-derived as two halves:
 * <ol>
 * <li>the predicate only <em>checks the block type</em> (ignoring facing) and records
 * the position + the controller-relative target direction in the multiblock match
 * context under {@link #ORIENTATION_KEY};</li>
 * <li>the controller, in {@code onStructureFormed()}, reads those entries via
 * {@link #getOrientationFixups}, resolves the relative direction against its own
 * front/upwards facing, and calls {@code level.setBlockAndUpdate(pos, orientedState)}.</li>
 * </ol>
 *
 * <p>
 * The type-tracking predicates (cooling coils, sintering bricks, conveyor belts, coils
 * or beds, metal sheets) port the same way Modern {@code Predicates.heatingCoils}
 * writes {@code "CoilType"} to the match context; they land here with the Bucket D
 * machines that need them. Only the orientation family is needed for Bucket C.
 */
public final class SuSyPredicates {

    /**
     * Match-context key under which orientation predicates accumulate
     * {@code List<Pair<BlockPos, RelativeDirection>>} — the position of each rotatable
     * block and the controller-relative direction its front must face. The controller
     * resolves the relative direction against its own orientation on structure form.
     */
    public static final String ORIENTATION_KEY = "SuSyOrientationFixups";

    private SuSyPredicates() {}

    /**
     * Modern re-derivation of 1.12.2 {@code horizontalOrientation}. Checks that the
     * matched block is {@code expectedBlock} (any facing) and records
     * {@code (pos, direction)} for the controller to apply post-form.
     *
     * @param expectedBlock the rotatable casing block (rotor / alternator coil /
     *                      crankshaft)
     * @param direction     the controller-relative direction the block's front must face
     *                      (e.g. {@link RelativeDirection#RIGHT} for turbine
     *                      rotors/coils, {@link RelativeDirection#UP} for the ICE
     *                      crankshaft)
     */
    public static TraceabilityPredicate horizontalOrientation(Block expectedBlock, RelativeDirection direction) {
        return recordOrientation(expectedBlock, direction);
    }

    /**
     * Pure replacement for the 1.12.2 six-way {@code orientation} predicate. Unlike
     * {@link #horizontalOrientation}, the controller must resolve and apply the exact
     * direction to a block carrying {@link BlockStateProperties#FACING}.
     */
    public static TraceabilityPredicate orientation(Block expectedBlock, RelativeDirection direction) {
        return recordOrientation(expectedBlock, direction);
    }

    /**
     * Curtain-coater conveyor predicate. Only one belt tier exists in the port, so the
     * legacy same-type check is implicit; the important behaviour is recording the
     * controller-relative travel direction for the post-form facing fixup.
     */
    public static TraceabilityPredicate conveyorBelt(Block expectedBlock, RelativeDirection direction) {
        return recordOrientation(expectedBlock, direction);
    }

    private static TraceabilityPredicate recordOrientation(Block expectedBlock, RelativeDirection direction) {
        return new TraceabilityPredicate(
                (MultiblockState state) -> {
                    if (state.getBlockState().getBlock() != expectedBlock)
                        return false;
                    List<Pair<BlockPos, RelativeDirection>> fixups = state.getMatchContext()
                            .getOrPut(ORIENTATION_KEY, new LinkedList<Pair<BlockPos, RelativeDirection>>());
                    fixups.add(Pair.of(state.getPos().immutable(), direction));
                    return true;
                },
                () -> new BlockInfo[] { BlockInfo.fromBlockState(expectedBlock.defaultBlockState()) });
    }

    /**
     * Read the accumulated orientation fixups from a formed structure's match context.
     * The controller calls this in {@code onStructureFormed()}, resolves each
     * {@link RelativeDirection} against its own facing via
     * {@link #resolveAxialFacing}, and applies it with
     * {@code level.setBlockAndUpdate(pos, withHorizontalFacing(state, facing))}.
     *
     * @return the recorded {@code (pos, direction)} pairs, or an empty list if none.
     */
    public static List<Pair<BlockPos, RelativeDirection>> getOrientationFixups(IMultiController controller) {
        Object stored = controller.self().getMultiblockState().getMatchContext().get(ORIENTATION_KEY);
        if (stored instanceof List<?> list) {
            @SuppressWarnings("unchecked")
            List<Pair<BlockPos, RelativeDirection>> fixups = (List<Pair<BlockPos, RelativeDirection>>) list;
            return fixups;
        }
        return List.of();
    }

    /**
     * Resolve a {@link RelativeDirection} against the controller and — exactly as the
     * 1.12.2 {@code horizontalOrientation} — collapse it to the positive X/Z axis
     * (SOUTH/WEST) so the horizontal rotatable block lands on its axial orientation.
     */
    public static Direction resolveAxialFacing(IMultiController controller, RelativeDirection direction) {
        var self = controller.self();
        Direction facing = direction.getRelative(self.getFrontFacing(), self.getUpwardsFacing(), self.isFlipped());
        return facing.get3DDataValue() < 4 ? Direction.SOUTH : Direction.WEST;
    }

    /** Convenience: apply a facing fixup to a blockstate that carries HORIZONTAL_FACING. */
    public static BlockState withHorizontalFacing(BlockState state, Direction facing) {
        return state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                ? state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                : state;
    }

    /** Convenience: apply an exact six-way facing fixup to a directional blockstate. */
    public static BlockState withFacing(BlockState state, Direction facing) {
        return state.hasProperty(BlockStateProperties.FACING)
                ? state.setValue(BlockStateProperties.FACING, facing)
                : state;
    }

    /** Resolve a relative direction without the turbine axial collapse. */
    public static Direction resolveFacing(IMultiController controller, RelativeDirection direction) {
        var self = controller.self();
        return direction.getRelative(self.getFrontFacing(), self.getUpwardsFacing(), self.isFlipped());
    }

    // ==================================================================
    // Type-tracking predicates (Bucket D). 1.12.2 enforced all-matched-blocks-same-type
    // via a match-context key + PatternStringError on mismatch, and recorded positions
    // under a "VABlock" list. Modern ports write the type to the match context exactly
    // like Predicates.heatingCoils writes "CoilType".
    // ==================================================================

    /**
     * Match-context key under which {@link #sameTypeVariant} records the matched block
     * (the "type" that all matched blocks must share). Distinct predicate instances in
     * one pattern use distinct keys via {@code keyPrefix}.
     */
    public static final String TYPE_KEY_SUFFIX = "Type";

    /**
     * A predicate that accepts any of {@code allowedBlocks} but requires every matched
     * block in the structure to be the <em>same one</em>. Ports the 1.12.2
     * coolingCoils/sinteringBricks/metalSheets/conveyorBelts "same-type" idiom. The
     * chosen block is written to the match context under {@code keyPrefix + "Type"} for
     * the controller to read in {@code onStructureFormed()} (e.g. coil temperature,
     * brick tier). A mismatch sets a {@link PatternStringError}.
     *
     * @param keyPrefix     match-context key prefix (e.g. "CoolingCoil", "SinteringBrick")
     * @param errorKey      lang key for the mismatch error
     * @param allowedBlocks the block variants that may be used (one must be chosen)
     */
    public static TraceabilityPredicate sameTypeVariant(String keyPrefix, String errorKey, Block... allowedBlocks) {
        String key = keyPrefix + TYPE_KEY_SUFFIX;
        return new TraceabilityPredicate(
                (MultiblockState state) -> {
                    Block matched = state.getBlockState().getBlock();
                    boolean isAllowed = false;
                    for (Block b : allowedBlocks) {
                        if (matched == b) {
                            isAllowed = true;
                            break;
                        }
                    }
                    if (!isAllowed)
                        return false;
                    Object current = state.getMatchContext().getOrPut(key, matched);
                    if (!current.equals(matched)) {
                        state.setError(new PatternStringError(errorKey));
                        return false;
                    }
                    return true;
                },
                () -> java.util.Arrays.stream(allowedBlocks)
                        .map(b -> BlockInfo.fromBlockState(b.defaultBlockState()))
                        .toArray(BlockInfo[]::new))
                .addTooltips(net.minecraft.network.chat.Component.translatable(errorKey));
    }

    /**
     * Read the block chosen by a {@link #sameTypeVariant} predicate from a formed
     * structure's match context, or {@code null} if none was recorded.
     */
    public static Block getChosenVariant(IMultiController controller, String keyPrefix) {
        Object stored = controller.self().getMultiblockState().getMatchContext().get(keyPrefix + TYPE_KEY_SUFFIX);
        return stored instanceof Block b ? b : null;
    }
}
