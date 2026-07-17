package io.github.symmetricdevs.supersymmetry.api.stockinteraction;

import net.minecraft.world.phys.AABB;

// Is this really necessary?
public interface IStockInteractor {

    // Defines the area in which the machine can find and interact with stocks
    AABB getInteractionBoundingBox();
}
