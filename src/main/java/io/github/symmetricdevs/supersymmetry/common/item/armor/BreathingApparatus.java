package io.github.symmetricdevs.supersymmetry.common.item.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.gregtechceu.gtceu.api.item.armor.ArmorComponentItem;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.client.renderer.handler.BreathingArmorModels;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Breathing apparatus providing air capacity via NBT oxygen storage.
 * The helmet piece checks the chest-piece tank for available oxygen each tick.
 */
public class BreathingApparatus implements IBreathingArmorLogic {

    protected static final double DEFAULT_MAX_OXYGEN = 1200;

    protected final ArmorItem.Type slot;
    protected final int maxDurability;

    public BreathingApparatus(ArmorItem.Type slot, int maxDurability) {
        this.slot = slot;
        this.maxDurability = maxDurability;
    }

    @Override
    public ArmorItem.Type getArmorType() {
        return slot;
    }

    @Override
    public ResourceLocation getArmorTexture(@NotNull ItemStack stack, @Nullable Entity entity,
                                            @NotNull EquipmentSlot slot, @Nullable String type) {
        return switch (this.slot) {
            case HELMET -> ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID,
                    "textures/models/armor/gas_mask_layer_1.png");
            case CHESTPLATE -> ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID,
                    "textures/models/armor/gas_tank_layer_1.png");
            default -> null;
        };
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public HumanoidModel<?> getArmorModel(@NotNull LivingEntity entity, @NotNull ItemStack stack,
                                          @NotNull EquipmentSlot slot, @NotNull HumanoidModel<?> defaultModel) {
        return switch (slot) {
            case HEAD -> BreathingArmorModels.gasMask(defaultModel, slot);
            case CHEST -> BreathingArmorModels.gasTank(defaultModel, slot);
            default -> defaultModel;
        };
    }

    @Override
    public int getArmorDisplay(@NotNull Player player, @NotNull ItemStack armor, @NotNull EquipmentSlot slot) {
        return 0;
    }

    @Override
    public boolean mayBreatheWith(@NotNull ItemStack stack, @NotNull Player player) {
        if (!player.level().dimension().location().toString().equals("beneath")) {
            return false;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof ArmorComponentItem item) {
            if (item.getArmorLogic() instanceof BreathingApparatus tank) {
                return tank.getOxygen(chest) > 0;
            }
        }
        return false;
    }

    @Override
    public double getDamageAbsorbed(@NotNull ItemStack stack, @NotNull Player player) {
        if (!BreathabilityHelper.isInHazardousEnvironment(player)) {
            return BreathabilityHelper.ABSORB_ALL;
        }

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof ArmorComponentItem item) {
            if (item.getArmorLogic() instanceof BreathingApparatus tank) {
                tank.changeOxygen(chest, -1);
                return BreathabilityHelper.ABSORB_ALL;
            }
        }
        return 0;
    }

    @Override
    public void addInformation(ItemStack stack, List<Component> tooltips) {
        if (getArmorType() == ArmorItem.Type.CHESTPLATE) {
            int oxygen = (int) getOxygen(stack);
            int maxOxygen = (int) getMaxOxygen(stack);
            tooltips.add(Component.translatable("supersymmetry.oxygen", oxygen, maxOxygen));
            tooltips.add(Component.translatable("item.durability", getDurability(stack), maxDurability));
        }
    }

    @Override
    public void onArmorTick(@NotNull Level world, @NotNull Player player, @NotNull ItemStack itemStack) {
        if (player.getItemBySlot(EquipmentSlot.HEAD) != itemStack) return;

        if (player.isInWater()) {
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chest.getItem() instanceof ArmorComponentItem item) {
                if (item.getArmorLogic() instanceof BreathingApparatus tank) {
                    if (tank.getOxygen(chest) > 0) {
                        player.setAirSupply(300);
                        if (!BreathabilityHelper.isInHazardousEnvironment(player)) {
                            tank.changeOxygen(chest, -1.0 / 20);
                        }
                    }
                }
            }
        }
    }

    double getOxygen(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        if (!tag.contains("oxygen")) {
            tag.putDouble("oxygen", getMaxOxygen(stack));
        }
        return tag.getDouble("oxygen");
    }

    double getMaxOxygen(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        if (!tag.contains("maxOxygen")) {
            tag.putDouble("maxOxygen", DEFAULT_MAX_OXYGEN);
        }
        return tag.getDouble("maxOxygen");
    }

    void changeOxygen(ItemStack stack, double oxygenChange) {
        if (!stack.hasTag()) return;
        var tag = stack.getTag();
        double current = tag.contains("oxygen") ? tag.getDouble("oxygen") : getMaxOxygen(stack);
        tag.putDouble("oxygen", current + oxygenChange);
    }

    int getDurability(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        if (!tag.contains("durability")) {
            tag.putInt("durability", maxDurability);
        }
        return tag.getInt("durability");
    }

    void changeDurability(ItemStack stack, int durabilityChange) {
        if (!stack.hasTag()) return;
        var tag = stack.getTag();
        tag.putInt("durability", getDurability(stack) + durabilityChange);
    }
}
