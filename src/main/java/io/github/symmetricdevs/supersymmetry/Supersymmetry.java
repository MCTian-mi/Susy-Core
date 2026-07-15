package io.github.symmetricdevs.supersymmetry;

import io.github.symmetricdevs.supersymmetry.api.registry.SusyRegistration;
import io.github.symmetricdevs.supersymmetry.common.data.SusyCreativeModeTabs;
import io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials;
import io.github.symmetricdevs.supersymmetry.config.SusyConfig;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.registry.MaterialRegistry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Mod entry point for the 1.20.1 port. Replaces the 1.12.2 {@code @Mod} +
 * {@code @SidedProxy}/{@code CommonProxy}/{@code ClientProxy} lifecycle
 * ({@code FMLConstructionEvent}/{@code FMLPreInitializationEvent}/...) with the
 * GTCEu-Modern addon bootstrap: register content holders, then hook the single
 * {@code GTRegistrate} to the mod event bus.
 * <p>
 * Content is registered "by initialization" — each holder exposes a no-op
 * {@code init()} that forces class loading so its field initializers run the
 * registrate/registry calls. GTCEu-driven content (machines, recipe types,
 * materials) is registered later on the appropriate {@code GTCEuAPI.RegisterEvent}
 * / {@code MaterialEvent} hooks (see {@code SupersymmetryGTAddon}).
 * <p>
 * The material lifecycle runs on the mod event bus: {@link MaterialRegistryEvent}
 * creates our material registry, {@link MaterialEvent} registers our materials,
 * and {@link PostMaterialEvent} mutates existing GTCEu materials — so this class
 * registers itself for those events.
 */
@Mod(Supersymmetry.MOD_ID)
public class Supersymmetry {

    public static final String MOD_ID = "supersymmetry";
    public static final String NAME = "Supersymmetry";

    public static MaterialRegistry MATERIAL_REGISTRY;

    public Supersymmetry() {
        // Config first — later registration may read it.
        SusyConfig.init();

        // Creative tab must exist before entries are registered so they are
        // grouped into it (an addon cannot inject into GTCEu's own tabs).
        SusyCreativeModeTabs.init();
        SusyRegistration.REGISTRATE.creativeModeTab(SusyCreativeModeTabs.SUPERSYMMETRY);

        // Content holders (added in later phases: blocks, items, machines, ...).

        // Register for the material lifecycle events fired on the mod bus
        // (MaterialRegistryEvent / MaterialEvent / PostMaterialEvent below).
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        // Hook the single GTRegistrate to the mod event bus.
        SusyRegistration.REGISTRATE.registerRegistrate();
    }

    @SubscribeEvent
    public void registerMaterialRegistry(MaterialRegistryEvent event) {
        MATERIAL_REGISTRY = GTCEuAPI.materialManager.createRegistry(MOD_ID);
    }

    @SubscribeEvent
    public void registerMaterials(MaterialEvent event) {
        SusyMaterials.init();
    }

    @SubscribeEvent
    public void modifyMaterials(PostMaterialEvent event) {
        SusyMaterials.changeProperties();
    }
}
