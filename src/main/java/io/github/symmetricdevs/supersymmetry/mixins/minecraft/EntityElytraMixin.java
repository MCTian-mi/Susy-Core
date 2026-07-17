package io.github.symmetricdevs.supersymmetry.mixins.minecraft;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemFirework;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFirework.class)
public abstract class EntityElytraMixin {

    @Inject(
            method = "onItemRightClick",
            at = @At(
                     value = "INVOKE",
                     target = "Lnet/minecraft/entity/item/EntityFireworkRocket;<init>(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)V"),
            cancellable = true)
    public void onUse(World world, Player player, InteractionHand hand, CallbackInfoReturnable<ItemStack> cir) {
        if (player.isElytraFlying()) {
            ItemStack stack = player.getHeldItem(hand);

            // Consume item if not in creative
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }

            // Cancel the original firework launch
            cir.setReturnValue(stack);

        }
    }
}
