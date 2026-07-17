package io.github.symmetricdevs.supersymmetry.common.item.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import baubles.api.BaubleType;
import com.gregtechceu.gtceu.integration.baubles.BaubleBehavior;

public class ArmorBaubleBehavior extends BaubleBehavior {

    public ArmorBaubleBehavior(BaubleType baubleType) {
        super(baubleType);
    }

    @Override
    public void onWornTick(ItemStack stack, LivingEntity player) {
        if (stack != null && stack != ItemStack.EMPTY && player instanceof Player Player) {
            stack.getItem().onArmorTick(player.getEntityWorld(), Player, stack); // Redirects onWornTick() to
                                                                                       // onArmorTick()
        }
    }
}
