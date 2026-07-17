package io.github.symmetricdevs.supersymmetry.common.item.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

/**
 * Simple gas mask for LV-tier protection in the Beneath.
 * Tracks wear as a double "damage" value in NBT, lasting {@link #LIFETIME} seconds.
 */
public class SimpleGasMask implements IBreathingArmorLogic {

    public static final double LIFETIME = 600;

    @Override
    public ArmorItem.Type getArmorType() {
        return ArmorItem.Type.HELMET;
    }

    @Override
    public ResourceLocation getArmorTexture(@NotNull ItemStack stack, @Nullable Entity entity,
                                            @NotNull EquipmentSlot slot, @Nullable String type) {
        return ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "textures/armor/simple_gas_mask.png");
    }

    @Override
    public int getArmorDisplay(@NotNull Player player, @NotNull ItemStack armor, @NotNull EquipmentSlot slot) {
        return 0;
    }

    @Override
    public boolean mayBreatheWith(@NotNull ItemStack stack, @NotNull Player player) {
        return BreathabilityHelper.isInHazardousEnvironment(player) && getDamage(stack) < 1;
    }

    @Override
    public double getDamageAbsorbed(@NotNull ItemStack stack, @NotNull Player player) {
        if (BreathabilityHelper.isInHazardousEnvironment(player)) {
            changeDamage(stack, 1.0 / LIFETIME);
        }
        if (getDamage(stack) >= 1) {
            // Destroy the item
            stack.shrink(1);
            player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        }
        return BreathabilityHelper.ABSORB_ALL;
    }

    @Override
    public void addInformation(ItemStack stack, List<Component> tooltips) {
        int secondsRemaining = (int) (LIFETIME - getDamage(stack) * LIFETIME);
        tooltips.add(Component.translatable("supersymmetry.seconds_left", secondsRemaining));
    }

    private double getDamage(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        if (!tag.contains("damage")) {
            tag.putDouble("damage", 0);
        }
        return tag.getDouble("damage");
    }

    private void changeDamage(ItemStack stack, double damageChange) {
        var tag = stack.getOrCreateTag();
        tag.putDouble("damage", getDamage(stack) + damageChange);
    }
}
