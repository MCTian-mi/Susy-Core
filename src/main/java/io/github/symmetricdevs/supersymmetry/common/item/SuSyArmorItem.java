package io.github.symmetricdevs.supersymmetry.common.item;

import com.gregtechceu.gtceu.api.item.armor.ArmorComponentItem;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

/**
 * Modern port of the 1.12.2 {@code SuSyArmorItem}.
 * <p>
 * In 1.12.2 this extended {@code ArmorMetaItem<SuSyArmorMetaValueItem>} and
 * implemented {@code IBreathingItem}. In GTCEu-Modern, the armour system is
 * based on {@link ArmorComponentItem} (which extends {@link ArmorItem}) with
 * pluggable {@link com.gregtechceu.gtceu.api.item.armor.IArmorLogic}.
 * <p>
 * For now this is a minimal extension of {@link ArmorComponentItem} that
 * accepts material, slot type and properties. Additional breathing/item
 * component capability (the {@code IBreathingItem} interface from 1.12.2)
 * will be wired via {@link com.gregtechceu.gtceu.api.item.component.IItemComponent}
 * implementations in Phase 6+.
 */
public class SuSyArmorItem extends ArmorComponentItem {

    public SuSyArmorItem(ArmorMaterial material, ArmorItem.Type type, Properties properties) {
        super(material, type, properties);
    }
}
