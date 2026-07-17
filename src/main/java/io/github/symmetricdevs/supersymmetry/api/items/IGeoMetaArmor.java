package io.github.symmetricdevs.supersymmetry.api.items;

/**
 * Marker interface for GeckoLib armor.
 * GeckoLib integration is a Phase 6 concern.
 */
public interface IGeoMetaArmor {

    default String getArmorTexturePath() {
        return "supersymmetry:textures/models/armor/";
    }
}
