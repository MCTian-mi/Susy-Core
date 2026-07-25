package io.github.symmetricdevs.supersymmetry.api.recipes.properties;

/**
 * Bridge for the 1.12.2 {@code RecipeProperty<BiomePropertyList>} for biome-based
 * recipe gating (Large Fluid Pump).
 * <p>
 * The biome-matching system is deferred to Phase 8 (worldgen). This bridge
 * preserves compilation for the old MTE file.
 */
public final class BiomeProperty {

    private static final BiomeProperty INSTANCE = new BiomeProperty();

    private BiomeProperty() {}

    public static BiomeProperty getInstance() {
        return INSTANCE;
    }
}
