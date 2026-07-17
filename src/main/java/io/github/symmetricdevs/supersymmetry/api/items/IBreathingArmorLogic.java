package io.github.symmetricdevs.supersymmetry.api.items;

/**
 * Interface for breathing armor logic.
 * Implementations provide air management for space-capable armor.
 */
public interface IBreathingArmorLogic {

    int getAir();

    void setAir(int air);

    int getMaxAir();

    boolean canBreathe();

    int getAirPerTick();
}
