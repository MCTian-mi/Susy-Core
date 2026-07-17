package io.github.symmetricdevs.supersymmetry.asm;

/**
 * REMOVED: {@code IClassTransformer} / ASM class transformer is a 1.12.2-only concept.
 * <p>
 * In Modern Forge (1.20.1), class transformations are done via mixins
 * declared in the mixin config JSON.
 * <p>
 * The old transformer modified ImmersiveRailroading classes (DefinitionManager,
 * StockLoader) to add SuSy stock-loading. IR has no 1.20.1 port, and this
 * transformation is deferred until IR is available or a replacement is designed.
 */
public final class SusyTransformer {

    private SusyTransformer() {}
}
