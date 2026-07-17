package io.github.symmetricdevs.supersymmetry.api.space;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Porting stub — the sussypatches mod (dev.tianmi.sussypatches) does not exist on 1.20.1,
 * so DimDisplayRegistry calls are removed. Rocketry/space is deferred to Phase 8.
 */
public class Planetoid extends CelestialObject {

    private PlanetType planetType;
    private int dimension;
    public static BiMap<Planetoid, Integer> PLANETOIDS = HashBiMap.create();

    public Planetoid(String translationKey, double mass, double posT, double posX, double posY, double posZ,
                     @Nullable CelestialObject parentBody, PlanetType planetType) {
        super(translationKey, posT, posX, posY, posZ, mass, CelestialBodyType.PLANETOID, parentBody);
        this.planetType = planetType;
    }

    public PlanetType getPlanetType() {
        return planetType;
    }

    public void setPlanetType(PlanetType planetType) {
        this.planetType = planetType;
    }

    public Planetoid setDimension(int dimension) {
        this.dimension = dimension;
        PLANETOIDS.put(this, dimension);
        return this;
    }

    public int getDimension() {
        return dimension;
    }

    public ItemStack getDisplayItem() {
        // DimDisplayRegistry is 1.12.2-only (sussypatches); deferred to Phase 8.
        return ItemStack.EMPTY;
    }
}
