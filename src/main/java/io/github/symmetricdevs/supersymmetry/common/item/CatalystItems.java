package io.github.symmetricdevs.supersymmetry.common.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystInfo;
import io.github.symmetricdevs.supersymmetry.api.registry.SusyRegistration;
import io.github.symmetricdevs.supersymmetry.common.recipes.CatalystGroups;

import net.minecraft.network.chat.Component;

/**
 * Tiered catalyst bed items, registered via GTCEu-Modern's REGISTRATE as
 * {@link ComponentItem} entries.
 * <p>
 * Ported from the 1.12.2 {@code CatalystItems} which used
 * {@code MetaOreDictItem.OreDictValueItem} arrays. In modern the catalyst
 * beds are not material-generated (the {@code TagPrefix} condition is
 * {@code mat -> false}) -- each is a plain REGISTRATE item with a tier
 * tooltip. After registration, {@link #initCatalysts()} populates the
 * corresponding {@link io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystGroup}
 * instances.
 */
public final class CatalystItems {

    private static final GTRegistrate REGISTRATE = SusyRegistration.REGISTRATE;

    /** Number of tiers for oxidation and reduction catalyst beds. */
    private static final int TIER_COUNT = 14;

    // ----------------------------------------------------------------
    // Tiered catalyst bed arrays
    // ----------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static final ItemEntry<ComponentItem>[] OXIDATION_CATALYST_BED = new ItemEntry[TIER_COUNT];

    @SuppressWarnings("unchecked")
    public static final ItemEntry<ComponentItem>[] REDUCTION_CATALYST_BED = new ItemEntry[TIER_COUNT];

    public static ItemEntry<ComponentItem> CRACKING_CATALYST_BED;

    private CatalystItems() {}

    // ----------------------------------------------------------------
    // Registration
    // ----------------------------------------------------------------

    /**
     * Initialises all REGISTRATE entries. Safe to call from the
     * {@code @Mod} constructor (alongside {@code SusyItems.init()}).
     */
    public static void init() {
        registerTieredCatalysts();
        CRACKING_CATALYST_BED = REGISTRATE
                .item("cracking_catalyst_bed", ComponentItem::create)
                .lang("Cracking Catalyst Bed")
                .onRegister(attach((IAddInformation) (stack, level, tooltips, flag) ->
                        tooltips.add(Component.translatable(
                                "susy.universal.catalysts.tooltip.tier",
                                GTValues.V[0], GTValues.VN[0]))))
                .defaultModel()
                .register();
    }

    /**
     * Associates the registered items with their {@code CatalystGroup}s.
     * Must be called <em>after</em> REGISTRATE has finished registering
     * (e.g. in a deferred or post-registration callback) so that the
     * {@code ItemEntry#get()} handles are live.
     */
    public static void initCatalysts() {
        for (int i = 0; i < TIER_COUNT; i++) {
            CatalystGroups.OXIDATION_CATALYST_BEDS.add(
                    OXIDATION_CATALYST_BED[i].get().getDefaultInstance(),
                    new CatalystInfo(i, 1, 0.95, 1.25));
            CatalystGroups.REDUCTION_CATALYST_BEDS.add(
                    REDUCTION_CATALYST_BED[i].get().getDefaultInstance(),
                    new CatalystInfo(i, 1, 0.95, 1.25));
        }
        CatalystGroups.CRACKING_CATALYST_BEDS.add(
                CRACKING_CATALYST_BED.get().getDefaultInstance(),
                new CatalystInfo(CatalystInfo.NO_TIER, 1, 0.95, 1.25));
    }

    // ----------------------------------------------------------------
    // Internal helpers
    // ----------------------------------------------------------------

    private static void registerTieredCatalysts() {
        for (int i = 0; i < TIER_COUNT; i++) {
            final int tier = i;
            String tierName = GTValues.VN[tier].toLowerCase();

            OXIDATION_CATALYST_BED[i] = REGISTRATE
                    .item("oxidation_catalyst_bed_" + tierName, ComponentItem::create)
                    .lang("Oxidation Catalyst Bed (" + GTValues.VN[tier] + ")")
                    .onRegister(attach((IAddInformation) (stack, level, tooltips, flag) ->
                            tooltips.add(Component.translatable(
                                    "susy.universal.catalysts.tooltip.tier",
                                    GTValues.V[tier], GTValues.VN[tier]))))
                    .defaultModel()
                    .register();

            REDUCTION_CATALYST_BED[i] = REGISTRATE
                    .item("reduction_catalyst_bed_" + tierName, ComponentItem::create)
                    .lang("Reduction Catalyst Bed (" + GTValues.VN[tier] + ")")
                    .onRegister(attach((IAddInformation) (stack, level, tooltips, flag) ->
                            tooltips.add(Component.translatable(
                                    "susy.universal.catalysts.tooltip.tier",
                                    GTValues.V[tier], GTValues.VN[tier]))))
                    .defaultModel()
                    .register();
        }
    }

    private static <T extends ComponentItem> NonNullConsumer<T> attach(IItemComponent... components) {
        return item -> item.attachComponents(components);
    }
}
