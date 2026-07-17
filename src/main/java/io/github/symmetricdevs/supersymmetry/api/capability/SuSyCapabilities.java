package io.github.symmetricdevs.supersymmetry.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;


import io.github.symmetricdevs.supersymmetry.Supersymmetry;

@Mod.EventBusSubscriber(modid = Supersymmetry.MODID)
public class SuSyCapabilities {

    /* @CapabilityInject replaced - use RegisterCapabilitiesEvent */
    public static Capability<IElytraFlyingProvider> ELYTRA_FLYING_PROVIDER;

    @CapabilityInject(IStrandProvider.class)
    public static Capability<IStrandProvider> STRAND_PROVIDER;

    public static void init() { /* TODO: register capabilities via RegisterCapabilitiesEvent */ }
}
