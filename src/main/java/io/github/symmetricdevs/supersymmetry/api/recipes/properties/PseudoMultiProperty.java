package io.github.symmetricdevs.supersymmetry.api.recipes.properties;

/**
 * Bridge for the 1.12.2 {@code RecipeProperty<PseudoMultiPropertyValues>} for
 * pseudo-multi-machine block state requirements.
 * <p>
 * Pseudo-multi machines are deferred (Phase 7 capability rewrite); this bridge
 * preserves compilation for the excluded {@code PseudoMultiRecipeLogic} classes.
 */
public final class PseudoMultiProperty {

    public static final String KEY = "blocks";

    private static final PseudoMultiProperty INSTANCE = new PseudoMultiProperty();

    private PseudoMultiProperty() {}

    public static PseudoMultiProperty getInstance() {
        return INSTANCE;
    }
}
