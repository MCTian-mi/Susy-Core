package io.github.symmetricdevs.supersymmetry.common.item.behavior;

import com.gregtechceu.gtceu.api.item.component.IInteractionItem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Simplified port of the faction radio behavior.
 * On right-click, displays a chat message indicating the action.
 * The full implementation would query faction hate values.
 */
public class FactionRadioBehaviour implements IInteractionItem {

    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        player.displayClientMessage(Component.translatable("chat.susy.radio.use"), true);
        return InteractionResultHolder.success(stack);
    }
}
