package io.github.symmetricdevs.supersymmetry.common.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import io.github.symmetricdevs.supersymmetry.api.registry.SusyRegistration;

import net.minecraft.network.chat.Component;

/**
 * Port of the 1.12.2 {@code SuSyMetaItems} to GTCEu-Modern's REGISTRATE +
 * {@link ComponentItem} system.
 * <p>
 * In 1.12.2 every item was a numeric {@code MetaValueItem} on a single
 * {@code StandardMetaItem}. In modern, each item is a distinct
 * {@link ComponentItem} registered through REGISTRATE. Simple items with
 * only a tooltip use {@link IAddInformation}; energy items use
 * {@link ElectricStats#createRechargeableBattery}; items with custom
 * behaviour register their own {@link IItemComponent} implementations.
 * <p>
 * Items that were already ported to {@link SusyItems} (catalyst support
 * grid, conveyors, pumps, vents, filters, track segments, scrap, tungsten
 * electrode, code breacher, entity tagger, faction radio, shape mold,
 * padding cloth, data cards, rocket configurer, cargo drones, location
 * card) are <strong>not</strong> duplicated here -- refer to
 * {@link SusyItems} for those registrations.
 * <p>
 * This class holds additional item registrations that did not exist in
 * the initial {@code SusyItems} pass, or that require more complex
 * component wiring.
 */
@SuppressWarnings("unused")
public final class SuSyMetaItems {

    private static final GTRegistrate REGISTRATE = SusyRegistration.REGISTRATE;

    // ----------------------------------------------------------------
    // Item entries that supplement SusyItems
    // ----------------------------------------------------------------

    /**
     * Carbon dioxide gas mask (cartridge-filter type).
     * Simple item with a tooltip.
     */
    public static final ItemEntry<ComponentItem> CARBON_MASK = REGISTRATE
            .item("carbon_mask", ComponentItem::create)
            .lang("Carbon Mask")
            .properties(p -> p.stacksTo(1))
            .onRegister(attach((IAddInformation) (stack, level, tooltips, flag) ->
                    tooltips.add(Component.translatable("susy.item.carbon_mask.tooltip"))))
            .defaultModel()
            .register();

    /**
     * Basic battery item (rechargeable, LV tier).
     */
    public static final ItemEntry<ComponentItem> BASIC_BATTERY = REGISTRATE
            .item("basic_battery", ComponentItem::create)
            .lang("Basic Battery")
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(ElectricStats.createRechargeableBattery(100_000L, GTValues.LV)))
            .defaultModel()
            .register();

    /**
     * Advanced battery item (rechargeable, HV tier).
     */
    public static final ItemEntry<ComponentItem> ADVANCED_BATTERY = REGISTRATE
            .item("advanced_battery", ComponentItem::create)
            .lang("Advanced Battery")
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(ElectricStats.createRechargeableBattery(1_000_000L, GTValues.HV)))
            .defaultModel()
            .register();

    // ----------------------------------------------------------------
    // Helper: attach IItemComponent(s) on register
    // ----------------------------------------------------------------

    private static <T extends ComponentItem> NonNullConsumer<T> attach(IItemComponent... components) {
        return item -> item.attachComponents(components);
    }

    /**
     * No-op init to force class-load so {@code static final} REGISTRATE
     * entries register themselves. Call from {@code @Mod} constructor.
     */
    public static void init() {}

    private SuSyMetaItems() {}
}
