package io.github.symmetricdevs.supersymmetry.api.items;

import net.minecraft.world.item.ItemStack;

/**
 * Interface for items that provide breathable air.
 */
public interface IBreathingItem {

    int getAirAmount(ItemStack stack);
}
