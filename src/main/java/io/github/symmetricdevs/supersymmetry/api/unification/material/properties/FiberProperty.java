package io.github.symmetricdevs.supersymmetry.api.unification.material.properties;

import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.IMaterialProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.MaterialProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;

import io.github.symmetricdevs.supersymmetry.api.unification.material.info.SuSyMaterialFlags;

/**
 * Marks a material as spinnable into fibers/threads, optionally via solution
 * (wet) or melt spinning, and optionally weavable into plates. Ported from the
 * 1.12.2 {@code FiberProperty}.
 */
public class FiberProperty implements IMaterialProperty {

    // For generating wet fibers
    public boolean solutionSpun;

    // To allow for fluid generation
    private final boolean meltSpun;

    // For plate making
    private final boolean weaving;

    public FiberProperty(boolean solutionSpun, boolean meltSpun, boolean weaving) {
        this.solutionSpun = solutionSpun;
        this.meltSpun = meltSpun;
        this.weaving = weaving;
    }

    // Default constructor
    public FiberProperty() {
        this(false, true, false);
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {
        if (properties.hasProperty(PropertyKey.FLUID) && !this.meltSpun) {
            throw new IllegalStateException("Material " + properties.getMaterial() +
                    " has both a fluid property and is not a melt spun fiber, which is not allowed!");
        }
        if (this.solutionSpun) {
            properties.getMaterial().addFlags(SuSyMaterialFlags.GENERATE_WET_FIBER);
        }
        if (this.weaving) {
            properties.getMaterial().addFlags(MaterialFlags.GENERATE_PLATE);
        }

        properties.getMaterial().addFlags(SuSyMaterialFlags.GENERATE_FIBER, SuSyMaterialFlags.GENERATE_THREAD,
                MaterialFlags.DISABLE_DECOMPOSITION);
    }
}
