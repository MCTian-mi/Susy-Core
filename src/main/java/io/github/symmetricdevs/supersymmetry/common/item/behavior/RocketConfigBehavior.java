package io.github.symmetricdevs.supersymmetry.common.item.behavior;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Simplified stub for the rocket configuration behavior.
 * Logs the interaction and returns success.
 * A full implementation would open a configuration UI via IItemUIFactory.
 */
public class RocketConfigBehavior implements IInteractionItem {

    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            GTCEu.LOGGER.info("RocketConfigBehavior: opened config by player {}", player.getName().getString());
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("susy.rocket_config.opened"), true);
        }
        return InteractionResultHolder.success(stack);
    }
}
