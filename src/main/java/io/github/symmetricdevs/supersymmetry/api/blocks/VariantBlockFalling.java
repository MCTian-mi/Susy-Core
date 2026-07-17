package io.github.symmetricdevs.supersymmetry.api.blocks;

import net.minecraft.world.level.block.FallingBlock;

/**
 * A gravity-affected block base extending {@link FallingBlock}.
 * <p>
 * Subclasses inherit the falling behaviour (scheduled tick, {@code FallingBlockEntity},
 * dust particles) without needing to override any methods.
 */
public class VariantBlockFalling extends FallingBlock {

    public VariantBlockFalling(Properties properties) {
        super(properties);
    }
}
