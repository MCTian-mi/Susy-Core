package io.github.symmetricdevs.supersymmetry.api.registry;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

/**
 * Holder for the single {@link GTRegistrate} instance used to register all of
 * this mod's blocks, items, machines, sounds, fluids and creative tab.
 * <p>
 * Mirrors gcyr's {@code GCYRRegistries}. The instance is created once from the
 * mod id and must be hooked to the mod event bus via
 * {@link GTRegistrate#registerRegistrate()} from the {@code @Mod} class.
 */
public final class SusyRegistration {

    public static final GTRegistrate REGISTRATE = GTRegistrate.create(Supersymmetry.MOD_ID);

    private SusyRegistration() {}
}
