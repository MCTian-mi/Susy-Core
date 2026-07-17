package io.github.symmetricdevs.supersymmetry.api.blocks;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Marker interface for animated blocks. Provides the {@code ACTIVE} state property
 * used by animated multiblock casings to switch between idle and running render states.
 * <p>
 * In 1.20.1, animated rendering is handled through Forge's {@code IClientBlockExtensions}
 * or a {@code BlockEntity} with a custom renderer, not through this interface directly.
 */
public interface IAnimatablePartBlock {

    BooleanProperty ACTIVE = BooleanProperty.create("active");
}
