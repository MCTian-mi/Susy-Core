package supersymmetry.common.item.armor;

import gregtech.api.items.armor.ArmorMetaItem;
import net.minecraft.item.ItemArmor;
import supersymmetry.common.item.SuSyMetaItems;

public class SuSyMetaArmor extends ArmorMetaItem<ArmorMetaItem<?>.ArmorMetaValueItem> {

    @Override
    public void registerSubItems() {
        SuSyMetaItems.ELYTRA = addItem(1, "elytra").setArmorLogic(new ElytraSuit(0, 80_000L, 2));
    }
}
