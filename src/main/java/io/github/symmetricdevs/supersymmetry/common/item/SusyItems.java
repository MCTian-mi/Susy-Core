package io.github.symmetricdevs.supersymmetry.common.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.api.registry.SusyRegistration;
import io.github.symmetricdevs.supersymmetry.common.item.behavior.LocationCardBehavior;

import net.minecraft.network.chat.Component;

/**
 * SuSy items registered via GTCEu-Modern's registrate {@link ComponentItem} system.
 * <p>
 * Ported from the 1.12.2 {@code SuSyMetaItems} (which used {@code StandardMetaItem} /
 * {@code MetaOreDictItem} with numeric IDs). Material-driven items (catalyst beds,
 * mill balls, fibers, …) are generated automatically by their
 * {@link com.gregtechceu.gtceu.api.data.tag.TagPrefix} entries and do NOT appear
 * here.
 * <p>
 * Items with custom {@link com.gregtechceu.gtceu.api.item.component.IItemComponent}
 * behaviours attach them via the {@link #attach(IItemComponent...)} helper.
 */
@SuppressWarnings("unused")
public final class SusyItems {

    private static final GTRegistrate REGISTRATE = SusyRegistration.REGISTRATE;

    // ----------------------------------------------------------------
    // Simple items (plain ComponentItem with optional tooltip)
    // ----------------------------------------------------------------

    public static final ItemEntry<ComponentItem> CATALYST_BED_SUPPORT_GRID = REGISTRATE
            .item("catalyst_bed_support_grid", ComponentItem::create)
            .lang("Catalyst Bed Support Grid")
            .onRegister(attach((IAddInformation) (stack, level, tooltips, flag) ->
                    tooltips.add(Component.translatable("susy.item.catalyst_bed_support_grid.tooltip"))))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> CONVEYOR_STEAM = REGISTRATE
            .item("conveyor_steam", ComponentItem::create)
            .lang("Steam Conveyor Module")
            .onRegister(attach((IAddInformation) (stack, level, tooltips, flag) -> {
                tooltips.add(Component.translatable("metaitem.conveyor.module.tooltip"));
                tooltips.add(Component.translatable("gregtech.universal.tooltip.item_transfer_rate", 4));
            }))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> PUMP_STEAM = REGISTRATE
            .item("pump_steam", ComponentItem::create)
            .lang("Steam Pump Module")
            .onRegister(attach((IAddInformation) (stack, level, tooltips, flag) -> {
                tooltips.add(Component.translatable("metaitem.electric.pump.tooltip"));
                tooltips.add(Component.translatable("gregtech.universal.tooltip.fluid_transfer_rate", 32));
            }))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> AIR_VENT = REGISTRATE
            .item("air_vent", ComponentItem::create)
            .lang("Air Vent")
            .onRegister(attach((IAddInformation) (stack, level, tooltips, flag) ->
                    tooltips.add(Component.translatable("metaitem.air_vent.tooltip.1", 100))))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> TRACK_SEGMENT = REGISTRATE
            .item("track_segment", ComponentItem::create)
            .lang("Track Segment")
            .onRegister(attach((IAddInformation) (stack, level, tooltips, flag) ->
                    tooltips.add(Component.translatable("metaitem.track_segment.length_info"))))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> RESTRICTIVE_FILTER = REGISTRATE
            .item("restrictive_filter", ComponentItem::create)
            .lang("Restrictive Filter")
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> EARTH_ORBITAL_SCRAP = REGISTRATE
            .item("earth_orbital_scrap", ComponentItem::create)
            .lang("Earth Orbital Scrap")
            .properties(p -> p.stacksTo(8))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> CODE_BREACHER = REGISTRATE
            .item("code_breacher", ComponentItem::create)
            .lang("Code Breacher")
            .properties(p -> p.stacksTo(1))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> ENTITY_TAGGER = REGISTRATE
            .item("entity_tagger", ComponentItem::create)
            .lang("Entity Tagger")
            .properties(p -> p.stacksTo(1))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> FACTION_RADIO = REGISTRATE
            .item("faction_radio", ComponentItem::create)
            .lang("Faction Radio")
            .properties(p -> p.stacksTo(1))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> DATA_CARD = REGISTRATE
            .item("data_card", ComponentItem::create)
            .lang("Data Card")
            .properties(p -> p.stacksTo(1))
            .onRegister(attach((IAddInformation) (stack, level, tooltips, flag) ->
                    tooltips.add(Component.translatable("metaitem.data_card.tooltip.1"))))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> DATA_CARD_ACTIVE = REGISTRATE
            .item("data_card_active", ComponentItem::create)
            .lang("Active Data Card")
            .properties(p -> p.stacksTo(1))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> DATA_CARD_MASTER_BLUEPRINT = REGISTRATE
            .item("data_card_master_blueprint", ComponentItem::create)
            .lang("Master Blueprint Data Card")
            .properties(p -> p.stacksTo(1))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> TUNGSTEN_ELECTRODE = REGISTRATE
            .item("tungsten_electrode", ComponentItem::create)
            .lang("Tungsten Electrode")
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> ROCKET_CONFIGURER = REGISTRATE
            .item("rocket_configurer", ComponentItem::create)
            .lang("Rocket Configurer")
            .properties(p -> p.stacksTo(1))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> PADDING_CLOTH = REGISTRATE
            .item("padding_cloth", ComponentItem::create)
            .lang("Padding Cloth")
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> SHAPE_MOLD_TARGET = REGISTRATE
            .item("shape_mold_target", ComponentItem::create)
            .lang("Target Shape Mold")
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> BASIC_CARGO_DRONE = REGISTRATE
            .item("cargo_drone_basic", ComponentItem::create)
            .lang("Basic Cargo Drone")
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(ElectricStats.createRechargeableBattery(100_000L, GTValues.LV)))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> ADVANCED_CARGO_DRONE = REGISTRATE
            .item("cargo_drone_advanced", ComponentItem::create)
            .lang("Advanced Cargo Drone")
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(ElectricStats.createRechargeableBattery(1_000_000L, GTValues.HV)))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> LOCATION_CARD = REGISTRATE
            .item("location_card", ComponentItem::create)
            .lang("Location Card")
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(new LocationCardBehavior()))
            .defaultModel()
            .register();

    public static final ItemEntry<ComponentItem> ELITE_CARGO_DRONE = REGISTRATE
            .item("cargo_drone_elite", ComponentItem::create)
            .lang("Elite Cargo Drone")
            .properties(p -> p.stacksTo(1))
            .defaultModel()
            .register();

    // ----------------------------------------------------------------
    // Helper: attach IItemComponent(s) on register
    // ----------------------------------------------------------------

    private static <T extends ComponentItem> NonNullConsumer<T> attach(IItemComponent... components) {
        return item -> item.attachComponents(components);
    }

    public static void init() {}

    private SusyItems() {}
}
