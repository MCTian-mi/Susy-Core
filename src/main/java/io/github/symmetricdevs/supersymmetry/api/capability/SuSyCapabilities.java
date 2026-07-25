package io.github.symmetricdevs.supersymmetry.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

/**
 * Custom SuSy capabilities (1.12.2 {@code @CapabilityInject} replaced with the
 * Forge 1.20.1 {@link RegisterCapabilitiesEvent} + {@link CapabilityToken} idiom).
 * <p>
 * The token-based holders are initialized eagerly so consumers can reference them
 * statically; registration with the event is handled in {@link #register}.
 */
@Mod.EventBusSubscriber(modid = Supersymmetry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SuSyCapabilities {

    public static final Capability<IElytraFlyingProvider> ELYTRA_FLYING_PROVIDER =
            CapabilityManager.get(new CapabilityToken<>() {});

    public static final Capability<IStrandProvider> STRAND_PROVIDER =
            CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IElytraFlyingProvider.class);
        event.register(IStrandProvider.class);
    }

    public static void init() {}
}
