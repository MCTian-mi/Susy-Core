package io.github.symmetricdevs.supersymmetry;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mod-wide constants and small helpers. Ported from the 1.12.2 {@code SuSyValues}
 * / {@code Supersymmetry} pair — the {@code susyId(...)} ResourceLocation helper
 * carries over with the namespace now {@code supersymmetry}. The 1.12.2 tier-material
 * array and rocket-hull/model handles are reintroduced with the subsystems that use
 * them (materials, rocketry).
 */
public final class SuSyValues {

    public static final Logger LOGGER = LogManager.getLogger(Supersymmetry.MOD_ID);

    private SuSyValues() {}

    public static ResourceLocation susyId(String path) {
        return ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, path);
    }
}
