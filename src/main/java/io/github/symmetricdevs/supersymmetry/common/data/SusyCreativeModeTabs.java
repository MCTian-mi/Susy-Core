package io.github.symmetricdevs.supersymmetry.common.data;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

import com.tterrag.registrate.util.entry.RegistryEntry;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.registry.SusyRegistration;

import static io.github.symmetricdevs.supersymmetry.api.registry.SusyRegistration.REGISTRATE;

/**
 * The mod's single creative mode tab. GTCEu addons cannot inject into GTCEu's
 * own tabs, so everything registered through {@link SusyRegistration#REGISTRATE}
 * after this tab is made current is grouped here.
 */
public class SusyCreativeModeTabs {

    public static final RegistryEntry<CreativeModeTab> SUPERSYMMETRY = REGISTRATE
            .defaultCreativeTab(Supersymmetry.MOD_ID, builder -> builder
                    .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(Supersymmetry.MOD_ID,
                            REGISTRATE))
                    .title(Component.literal("Supersymmetry"))
                    .build())
            .register();

    public static void init() {}
}
