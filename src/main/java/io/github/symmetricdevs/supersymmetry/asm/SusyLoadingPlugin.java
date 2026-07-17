package io.github.symmetricdevs.supersymmetry.asm;

/**
 * REMOVED: {@code IFMLLoadingPlugin} / coremod loader is a 1.12.2-only concept.
 * <p>
 * In Modern Forge (1.20.1), mixins are declared via the mixin config JSON
 * ({@code mixins.supersymmetry.json}) and loaded by the MixinBooter / mixin
 * gradle plugin. There is no Java {@code IFMLLoadingPlugin} entry point.
 * <p>
 * The old {@code SusyTransformer} (ASM class transformation for ImmersiveRailroading)
 * is deferred until IR itself is ported to 1.20.1.
 */
public final class SusyLoadingPlugin {

    private SusyLoadingPlugin() {}
}
