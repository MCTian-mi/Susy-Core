package io.github.symmetricdevs.supersymmetry.api.rocketry.components;

import io.github.symmetricdevs.supersymmetry.common.entities.EntityAbstractRocket;

public interface Instrument {

    void act(int count, EntityAbstractRocket rocket);
}
