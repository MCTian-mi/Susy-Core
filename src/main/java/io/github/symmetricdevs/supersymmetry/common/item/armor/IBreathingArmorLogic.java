package io.github.symmetricdevs.supersymmetry.common.item.armor;

import com.gregtechceu.gtceu.api.item.armor.ArmorComponentItem;
import com.gregtechceu.gtceu.api.item.armor.IArmorLogic;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Extended armor logic interface for breathing/hazard-protection armors.
 * Adds methods the {@link BreathabilityHelper} queries when ticking the
 * player in hazardous environments.
 */
public interface IBreathingArmorLogic extends IArmorLogic {

    /**
     * @return true if the given stack permits the player to breathe in the
     *         current (hazardous) environment
     */
    boolean mayBreatheWith(ItemStack stack, Player player);

    /**
     * @return how much damage the stack absorbed from the environment this
     *         tick. Return {@link BreathabilityHelper#ABSORB_ALL}
     *         to cancel all damage.
     */
    double getDamageAbsorbed(ItemStack stack, Player player);

    /**
     * Add tooltip information to the given list.
     */
    void addInformation(ItemStack stack, List<Component> tooltips);

    /**
     * Right-click to equip the armour piece to its matching slot.
     */
    default InteractionResultHolder<ItemStack> onRightClick(Level world, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof ArmorComponentItem armorItem) {
            var slot = armorItem.getEquipmentSlot();
            if (player.getItemBySlot(slot).isEmpty() && !player.isShiftKeyDown()) {
                player.setItemSlot(slot, held.copy());
                held.setCount(0);
                return InteractionResultHolder.success(ItemStack.EMPTY);
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}
