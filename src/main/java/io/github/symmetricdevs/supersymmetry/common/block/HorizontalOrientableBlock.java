package io.github.symmetricdevs.supersymmetry.common.block;

import net.minecraft.world.level.block.HorizontalDirectionalBlock;

/**
 * A concrete, cube-all {@link HorizontalDirectionalBlock} for SuSy casing blocks that
 * carry a horizontal {@code FACING} property but no other behaviour. Used by the
 * generator rotor / alternator-coil / crankshaft casings so a multiblock controller can
 * auto-orient the block on structure form (the modern replacement for the 1.12.2
 * side-effecting {@code horizontalOrientation} pattern predicate). The per-facing front
 * texture is a Phase 6 rendering concern; for now the block renders the same texture on
 * all faces, rotated by {@code FACING}.
 */
public class HorizontalOrientableBlock extends HorizontalDirectionalBlock {

    public HorizontalOrientableBlock(Properties properties) {
        super(properties);
    }
}
