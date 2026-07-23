package io.github.symmetricdevs.supersymmetry.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side registry of block positions that must be skipped during chunk meshing because a
 * formed multiblock draws a unified GeckoLib model over them.
 *
 * <p>Ported from the legacy Susy-Core {@code RenderMaskManager}. Positions are keyed by the
 * controller position so multiple formed machines can coexist and be updated independently.
 * The union of all controllers' hidden positions is kept as a flat {@link LongSet} for O(1)
 * lookups from the chunk-mesh mixins.
 */
@OnlyIn(Dist.CLIENT)
public final class RenderMaskManager {

    private RenderMaskManager() {}

    /** Flat union of every controller's hidden positions, packed via {@link BlockPos#asLong()}. */
    private static final LongSet MASKED = new LongOpenHashSet();
    /** Per-controller hidden positions, so a controller's contribution can be replaced/removed. */
    private static final Map<Long, long[]> BY_CONTROLLER = new HashMap<>();

    /**
     * Replace the hidden positions contributed by a controller and re-mesh the affected sections.
     * An empty/zero-length array removes the controller's contribution.
     */
    public static synchronized void update(BlockPos controllerPos, long[] hidden) {
        long key = controllerPos.asLong();
        long[] previous = (hidden == null || hidden.length == 0) ?
                BY_CONTROLLER.remove(key) : BY_CONTROLLER.put(key, hidden);

        rebuildUnion();

        // Re-mesh sections spanning both the old and new positions so removed blocks reappear.
        if (previous != null) markSectionsDirty(previous);
        if (hidden != null) markSectionsDirty(hidden);
    }

    /** Drop every controller's contribution (e.g. on world unload). */
    public static synchronized void clear() {
        MASKED.clear();
        BY_CONTROLLER.clear();
    }

    /** True if the block at {@code posLong} ({@link BlockPos#asLong()}) is currently masked. */
    public static boolean isMasked(long posLong) {
        return MASKED.contains(posLong);
    }

    public static boolean isMasked(BlockPos pos) {
        return MASKED.contains(pos.asLong());
    }

    /**
     * Context-aware mask query: only consults the live mask when the rendering context is the
     * real-world terrain (a {@code RenderChunkRegion}). Fake levels used by JEI/XEI multiblock
     * previews ({@code TrackedDummyWorld}, {@code RenderLevel}, {@code SchemaLevel}) are never
     * {@code RenderChunkRegion}, so they bypass render masking and render all structure blocks
     * normally with correct face culling.
     *
     * <p>Use this overload in any mixin that receives a generic {@link BlockGetter} / {@link
     * net.minecraft.world.level.BlockAndTintGetter} from a caller that could be either a real
     * chunk rebuild or a fake preview render.
     */
    public static boolean isMasked(BlockGetter level, BlockPos pos) {
        return level instanceof net.minecraft.client.renderer.chunk.RenderChunkRegion
                && MASKED.contains(pos.asLong());
    }

    private static void rebuildUnion() {
        MASKED.clear();
        for (long[] positions : BY_CONTROLLER.values()) {
            for (long p : positions) {
                MASKED.add(p);
            }
        }
    }

    private static void markSectionsDirty(long[] positions) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.levelRenderer == null) return;
        // Deduplicate to whole sections to avoid redundant rebuild scheduling.
        LongSet sections = new LongOpenHashSet();
        BlockPos.MutableBlockPos mbp = new BlockPos.MutableBlockPos();
        for (long p : positions) {
            mbp.set(BlockPos.getX(p), BlockPos.getY(p), BlockPos.getZ(p));
            long sec = BlockPos.asLong(mbp.getX() >> 4, mbp.getY() >> 4, mbp.getZ() >> 4);
            sections.add(sec);
        }
        for (long sec : sections) {
            mc.levelRenderer.setSectionDirtyWithNeighbors(BlockPos.getX(sec), BlockPos.getY(sec), BlockPos.getZ(sec));
        }
    }
}
