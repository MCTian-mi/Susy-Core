package io.github.symmetricdevs.supersymmetry.api.unification.material.properties;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;

public final class SuSyPropertyKey {

    public static final PropertyKey<FiberProperty> FIBER = new PropertyKey<>("fiber", FiberProperty.class);
    public static final PropertyKey<MillBallProperty> MILL_BALL = new PropertyKey<>("mill_ball",
            MillBallProperty.class);

    private SuSyPropertyKey() {}
}
