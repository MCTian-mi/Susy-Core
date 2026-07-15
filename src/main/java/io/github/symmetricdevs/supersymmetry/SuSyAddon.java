package io.github.symmetricdevs.supersymmetry;

import io.github.symmetricdevs.supersymmetry.api.registry.SusyRegistration;
import io.github.symmetricdevs.supersymmetry.api.unification.ore.SusyTagPrefixes;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

/**
 * Bridge into GTCEu-Modern. GTCEu auto-discovers this class (via
 * {@code AddonFinder} scanning for the {@link GTAddon} annotation) and calls its
 * hooks to pull this addon's content into GTCEu's own registries. Replaces the
 * 1.12.2 {@code gregtech.api.modules} module system ({@code SuSyModules}).
 * <p>
 * Materials are <em>not</em> registered here — they go through the MOD-bus
 * {@code MaterialRegistryEvent}/{@code MaterialEvent}/{@code PostMaterialEvent}
 * in later phases. The various register hooks and addRecipes are filled in as
 * the relevant subsystems are ported.
 */
@GTAddon
public class SuSyAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return SusyRegistration.REGISTRATE;
    }

    @Override
    public void initializeAddon() {}

    /**
     * Called by GTCEu during {@code TagPrefix.init()}, after icon types and
     * materials exist. Class-loading {@link SusyTagPrefixes} constructs (and thus
     * self-registers) every custom {@code TagPrefix}.
     */
    @Override
    public void registerTagPrefixes() {
        // Touch the holder so its static TagPrefix fields initialize + self-register.
        SusyTagPrefixes.init();
    }

    @Override
    public String addonModId() {
        return Supersymmetry.MOD_ID;
    }

    @Override
    public boolean requiresHighTier() {
        return false;
    }
}
