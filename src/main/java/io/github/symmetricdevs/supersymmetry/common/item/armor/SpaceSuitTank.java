package io.github.symmetricdevs.supersymmetry.common.item.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An oxygen tank for the space suit, worn in the chestplate slot.
 * See {@link AdvancedBreathingTank} for the general-purpose variant.
 */
public class SpaceSuitTank extends SpaceSuit {

    public static final double INFINITE_OXYGEN = -1;
    public final double maxOxygen;

    public SpaceSuitTank(int maxDurability, double hoursOfLife, String name, int tier, double relativeAbsorption,
                         double maxOxygen) {
        super(ArmorItem.Type.CHESTPLATE, maxDurability, hoursOfLife, name, tier, relativeAbsorption);
        this.maxOxygen = maxOxygen;
    }

    @Override
    public void addInformation(ItemStack stack, List<Component> tooltips) {
        double maxOxygen = getMaxOxygen(stack);
        if (maxOxygen == INFINITE_OXYGEN) {
            tooltips.add(Component.translatable("supersymmetry.unlimited_oxygen"));
        } else {
            int oxygen = (int) getOxygen(stack);
            tooltips.add(Component.translatable("supersymmetry.oxygen", oxygen, (int) maxOxygen));
        }
        super.addInformation(stack, tooltips);
    }

    @Override
    public void changeOxygen(ItemStack stack, double oxygenChange) {
        if (getMaxOxygen(stack) == INFINITE_OXYGEN) {
            return;
        }
        super.changeOxygen(stack, oxygenChange);
    }

    @Override
    public double getOxygen(ItemStack stack) {
        if (getMaxOxygen(stack) == INFINITE_OXYGEN) {
            return 12000;
        }
        return super.getOxygen(stack);
    }

    @Override
    double getMaxOxygen(ItemStack stack) {
        return this.maxOxygen;
    }

    @Override
    public void onArmorTick(@NotNull Level world, @NotNull Player player, @NotNull ItemStack itemStack) {
        if (player.isInWater()) {
            if (getOxygen(player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST)) > 0) {
                player.setAirSupply(300);
                if (!BreathabilityHelper.isInHazardousEnvironment(player)) {
                    changeOxygen(player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST), -1.0 / 20);
                }
            }
        }
    }
}
