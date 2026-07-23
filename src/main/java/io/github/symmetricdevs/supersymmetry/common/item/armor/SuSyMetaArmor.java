package io.github.symmetricdevs.supersymmetry.common.item.armor;

import com.gregtechceu.gtceu.api.item.armor.ArmorComponentItem;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.registry.SusyRegistration;
import io.github.symmetricdevs.supersymmetry.client.renderer.armor.SuSyGeoArmorRenderer;

import net.minecraft.world.item.ArmorItem;

import static com.gregtechceu.gtceu.common.item.armor.GTArmorMaterials.BAD_PPE_EQUIPMENT;

/**
 * Registry for custom SuSy armor items tied to {@link IBreathingArmorLogic}
 * implementations. Each entry is an {@link ArmorComponentItem} registered
 * via the mod's {@link GTRegistrate} instance.
 * <p>
 * For now we use the generic {@link BAD_PPE_EQUIPMENT} armour material for all
 * pieces (the original 1.12.2 code did not apply meaningful armour-vanilla
 * resistance, relying instead on the custom logic). A future pass can define
 * dedicated {@link net.minecraft.world.item.ArmorMaterial} entries.
 */
@SuppressWarnings("unused")
public final class SuSyMetaArmor {

    private static final GTRegistrate REGISTRATE = SusyRegistration.REGISTRATE;

    // LV-tier
    public static final ItemEntry<? extends ArmorComponentItem> SIMPLE_GAS_MASK = REGISTRATE
            .item("simple_gas_mask", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.HELMET, p,
                    () -> new SuSyGeoArmorRenderer("simple_gas_mask",
                            "textures/models/armor/simple_gas_mask.png")))
            .onRegister(item -> item.setArmorLogic(new SimpleGasMask()))
            .defaultModel()
            .register();

    public static final ItemEntry<? extends ArmorComponentItem> GAS_MASK = REGISTRATE
            .item("gas_mask", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.HELMET, p,
                    () -> new SuSyGeoArmorRenderer("gas_mask",
                            "textures/models/armor/gas_mask.png")))
            .onRegister(item -> item.setArmorLogic(new BreathingApparatus(ArmorItem.Type.HELMET, 300)))
            .defaultModel()
            .register();

    // MV-tier
    public static final ItemEntry<? extends ArmorComponentItem> GAS_TANK = REGISTRATE
            .item("gas_tank", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.CHESTPLATE, p,
                    () -> new SuSyGeoArmorRenderer("gas_tank",
                            "textures/models/armor/gas_tank.png")))
            .onRegister(item -> item.setArmorLogic(new BreathingApparatus(ArmorItem.Type.CHESTPLATE, 500)))
            .defaultModel()
            .register();

    // Asbestos set (full suit)
    public static final ItemEntry<? extends ArmorComponentItem> ASBESTOS_MASK = REGISTRATE
            .item("asbestos_mask", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.HELMET, p,
                    () -> new SuSyGeoArmorRenderer("asbestos_armor",
                            "textures/models/armor/asbestos_layer_1.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingApparatus(ArmorItem.Type.HELMET, 250, 1, "asbestos", 0, 0.3)))
            .defaultModel()
            .register();

    public static final ItemEntry<? extends ArmorComponentItem> ASBESTOS_CHESTPLATE = REGISTRATE
            .item("asbestos_chestplate", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.CHESTPLATE, p,
                    () -> new SuSyGeoArmorRenderer("asbestos_armor",
                            "textures/models/armor/asbestos_layer_1.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingTank(400, 1, "asbestos", 0, 0.3, 1200)))
            .defaultModel()
            .register();

    public static final ItemEntry<? extends ArmorComponentItem> ASBESTOS_LEGGINGS = REGISTRATE
            .item("asbestos_leggings", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.LEGGINGS, p,
                    () -> new SuSyGeoArmorRenderer("asbestos_armor",
                            "textures/models/armor/asbestos_layer_2.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingApparatus(ArmorItem.Type.LEGGINGS, 350, 1, "asbestos", 0, 0.3)))
            .defaultModel()
            .register();

    public static final ItemEntry<? extends ArmorComponentItem> ASBESTOS_BOOTS = REGISTRATE
            .item("asbestos_boots", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.BOOTS, p,
                    () -> new SuSyGeoArmorRenderer("asbestos_armor",
                            "textures/models/armor/asbestos_layer_2.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingApparatus(ArmorItem.Type.BOOTS, 300, 1, "asbestos", 0, 0.3)))
            .defaultModel()
            .register();

    // Rebreather tank
    public static final ItemEntry<? extends ArmorComponentItem> REBREATHER_TANK = REGISTRATE
            .item("rebreather_tank", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.CHESTPLATE, p,
                    () -> new SuSyGeoArmorRenderer("rebreather_armor",
                            "textures/models/armor/rebreather_layer_1.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingTank(405, 1, "rebreather", 0, 0.3, 3600)))
            .defaultModel()
            .register();

    // Reflective set
    public static final ItemEntry<? extends ArmorComponentItem> REFLECTIVE_MASK = REGISTRATE
            .item("reflective_mask", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.HELMET, p,
                    () -> new SuSyGeoArmorRenderer("reflective_armor",
                            "textures/models/armor/reflective_layer_1.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingApparatus(ArmorItem.Type.HELMET, 250, 5, "reflective", 0, 0.4)))
            .defaultModel()
            .register();

    public static final ItemEntry<? extends ArmorComponentItem> REFLECTIVE_CHESTPLATE = REGISTRATE
            .item("reflective_chestplate", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.CHESTPLATE, p,
                    () -> new SuSyGeoArmorRenderer("reflective_armor",
                            "textures/models/armor/reflective_layer_1.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingTank(400, 5, "reflective", 0, 0.4, 1200)))
            .defaultModel()
            .register();

    public static final ItemEntry<? extends ArmorComponentItem> REFLECTIVE_LEGGINGS = REGISTRATE
            .item("reflective_leggings", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.LEGGINGS, p,
                    () -> new SuSyGeoArmorRenderer("reflective_armor",
                            "textures/models/armor/reflective_layer_2.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingApparatus(ArmorItem.Type.LEGGINGS, 350, 5, "reflective", 0, 0.4)))
            .defaultModel()
            .register();

    public static final ItemEntry<? extends ArmorComponentItem> REFLECTIVE_BOOTS = REGISTRATE
            .item("reflective_boots", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.BOOTS, p,
                    () -> new SuSyGeoArmorRenderer("reflective_armor",
                            "textures/models/armor/reflective_layer_2.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingApparatus(ArmorItem.Type.BOOTS, 300, 5, "reflective", 0, 0.4)))
            .defaultModel()
            .register();

    // Filtered tank (infinite oxygen)
    public static final ItemEntry<? extends ArmorComponentItem> FILTERED_TANK = REGISTRATE
            .item("filtered_tank", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.CHESTPLATE, p,
                    () -> new SuSyGeoArmorRenderer("filtered_armor",
                            "textures/models/armor/filtered_layer_1.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingTank(415, 5, "filtered", 0, 0.4, AdvancedBreathingTank.INFINITE_OXYGEN)))
            .defaultModel()
            .register();

    // Nomex set (fire resistant)
    public static final ItemEntry<? extends ArmorComponentItem> NOMEX_MASK = REGISTRATE
            .item("nomex_mask", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.HELMET, p,
                    () -> new SuSyGeoArmorRenderer("nomex_armor",
                            "textures/models/armor/nomex_layer_1.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingApparatus(ArmorItem.Type.HELMET, 700, 0, "nomex", 1, 0.6)))
            .defaultModel()
            .register();

    public static final ItemEntry<? extends ArmorComponentItem> NOMEX_CHESTPLATE = REGISTRATE
            .item("nomex_chestplate", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.CHESTPLATE, p,
                    () -> new SuSyGeoArmorRenderer("nomex_armor",
                            "textures/models/armor/nomex_layer_1.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingTank(1000, 0, "nomex", 1, 0.6, AdvancedBreathingTank.INFINITE_OXYGEN)))
            .defaultModel()
            .register();

    public static final ItemEntry<? extends ArmorComponentItem> NOMEX_LEGGINGS = REGISTRATE
            .item("nomex_leggings", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.LEGGINGS, p,
                    () -> new SuSyGeoArmorRenderer("nomex_armor",
                            "textures/models/armor/nomex_layer_2.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingApparatus(ArmorItem.Type.LEGGINGS, 900, 0, "nomex", 1, 0.6)))
            .defaultModel()
            .register();

    public static final ItemEntry<? extends ArmorComponentItem> NOMEX_BOOTS = REGISTRATE
            .item("nomex_boots", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.BOOTS, p,
                    () -> new SuSyGeoArmorRenderer("nomex_armor",
                            "textures/models/armor/nomex_layer_2.png")))
            .onRegister(item -> item.setArmorLogic(
                    new AdvancedBreathingApparatus(ArmorItem.Type.BOOTS, 850, 0, "nomex", 1, 0.6)))
            .defaultModel()
            .register();

    // Jet wingpack (HV)
    public static final ItemEntry<? extends ArmorComponentItem> JET_WINGPACK = REGISTRATE
            .item("jet_wingpack", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.CHESTPLATE, p,
                    () -> new SuSyGeoArmorRenderer("jet_wingpack",
                            "textures/models/armor/jet_wingpack.png")))
            .onRegister(item -> item.setArmorLogic(new JetWingpack()))
            .defaultModel()
            .register();

    // Space suit set (EV)
    // TODO)) inventory item textures for astronaut pieces are 16x16 generated placeholders;
    // replace with proper 1.12.2-style icons once the legacy metaitem assets are recovered.
    public static final ItemEntry<? extends ArmorComponentItem> ASTRONAUT_HELMET = REGISTRATE
            .item("astronaut_helmet", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.HELMET, p,
                    () -> new SuSyGeoArmorRenderer("astronaut_armor",
                            "textures/models/armor/astronaut_layer_1.png")))
            .onRegister(item -> item.setArmorLogic(
                    new SpaceSuit(ArmorItem.Type.HELMET, 100, 0, "astronaut", 1, 0.6)))
            .defaultModel()
            .register();

    public static final ItemEntry<? extends ArmorComponentItem> ASTRONAUT_CHESTPLATE = REGISTRATE
            .item("astronaut_chestplate", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.CHESTPLATE, p,
                    () -> new SuSyGeoArmorRenderer("astronaut_armor",
                            "textures/models/armor/astronaut_layer_1.png")))
            .onRegister(item -> item.setArmorLogic(
                    new SpaceSuitTank(200, 0, "astronaut", 1, 0.6, AdvancedBreathingTank.INFINITE_OXYGEN)))
            .defaultModel()
            .register();

    public static final ItemEntry<? extends ArmorComponentItem> ASTRONAUT_LEGGINGS = REGISTRATE
            .item("astronaut_leggings", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.LEGGINGS, p,
                    () -> new SuSyGeoArmorRenderer("astronaut_armor",
                            "textures/models/armor/astronaut_layer_2.png")))
            .onRegister(item -> item.setArmorLogic(
                    new SpaceSuit(ArmorItem.Type.LEGGINGS, 175, 0, "astronaut", 1, 0.6)))
            .defaultModel()
            .register();

    public static final ItemEntry<? extends ArmorComponentItem> ASTRONAUT_BOOTS = REGISTRATE
            .item("astronaut_boots", p -> new SuSyGeoArmorItem(
                    BAD_PPE_EQUIPMENT, ArmorItem.Type.BOOTS, p,
                    () -> new SuSyGeoArmorRenderer("astronaut_armor",
                            "textures/models/armor/astronaut_layer_2.png")))
            .onRegister(item -> item.setArmorLogic(
                    new SpaceSuit(ArmorItem.Type.BOOTS, 150, 0, "astronaut", 1, 0.6)))
            .defaultModel()
            .register();

    public static void init() {}

    private SuSyMetaArmor() {}
}
