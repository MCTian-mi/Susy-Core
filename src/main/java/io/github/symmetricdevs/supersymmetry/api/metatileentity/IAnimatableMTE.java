package io.github.symmetricdevs.supersymmetry.api.metatileentity;

import io.github.symmetricdevs.supersymmetry.SuSyValues;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

/**
 * Interface for animated machines.
 * GeckoLib integration is a Phase 6 concern; this interface is kept abstract for now.
 */
public interface IAnimatableMTE {

    /**
     * @return set of hidden block positions for this machine
     */
    Collection<BlockPos> getHiddenBlocks();

    @SuppressWarnings("unchecked")
    default <T> T self() {
        return (T) this;
    }

    /**
     * @return the path segment used for geo model lookups
     */
    default String getGeoName() {
        return ""; // placeholder
    }

    default ResourceLocation modelRL() {
        return SuSyValues.susyId("geo/" + getGeoName() + ".geo.json");
    }

    default ResourceLocation textureRL() {
        return SuSyValues.susyId("textures/geo/" + getGeoName() + "/all.png");
    }

    default ResourceLocation animationRL() {
        return SuSyValues.susyId("animations/" + getGeoName() + ".animation.json");
    }
}
