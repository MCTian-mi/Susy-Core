package supersymmetry.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;
import supersymmetry.Supersymmetry;

@Mod.EventBusSubscriber(modid = Supersymmetry.MODID)
public class SuSyCapabilities {

    @CapabilityInject(IElytraFlyingProvider.class)
    public static Capability<IElytraFlyingProvider> ELYTRA_FLYING_PROVIDER;
}
