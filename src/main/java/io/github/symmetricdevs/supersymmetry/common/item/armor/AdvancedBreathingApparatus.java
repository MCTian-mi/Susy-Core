package io.github.symmetricdevs.supersymmetry.common.item.armor;

import com.gregtechceu.gtceu.api.item.armor.ArmorComponentItem;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.symmetricdevs.supersymmetry.Supersymmetry.MOD_ID;

/**
 * Advanced breathing apparatus with set-based armor (nominal 20 armor points =
 * full reduction) and environmental damage handling. Extended by the tank
 * variants and space-suit pieces.
 */
public class AdvancedBreathingApparatus extends BreathingApparatus {

    protected final double hoursOfLife;
    protected final String name;
    protected final int tier;
    protected final double relativeAbsorption;

    public AdvancedBreathingApparatus(ArmorItem.Type slot, int maxDurability, double hoursOfLife,
                                      String name, int tier, double relativeAbsorption) {
        super(slot, maxDurability);
        this.hoursOfLife = hoursOfLife;
        this.name = name;
        this.tier = tier;
        this.relativeAbsorption = relativeAbsorption;
    }

    @Override
    public boolean mayBreatheWith(@NotNull ItemStack stack, @NotNull Player player) {
        String dim = player.level().dimension().location().toString();
        return dim.equals("beneath") || dim.equals("minecraft:the_nether");
    }

    @Override
    public double getDamageAbsorbed(@NotNull ItemStack stack, @NotNull Player player) {
        handleDamage(stack, player);

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof ArmorComponentItem item &&
                item.getArmorLogic() instanceof AdvancedBreathingApparatus tank && tank.tier == tier) {
            tank.handleDamage(chest, player);

            int piecesCount = 0;
            ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
            if (leggings.getItem() instanceof ArmorComponentItem item2 &&
                    item2.getArmorLogic() instanceof AdvancedBreathingApparatus legLogic) {
                legLogic.handleDamage(leggings, player);
                piecesCount++;
            }

            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            if (boots.getItem() instanceof ArmorComponentItem item2 &&
                    item2.getArmorLogic() instanceof AdvancedBreathingApparatus bootLogic) {
                bootLogic.handleDamage(boots, player);
                piecesCount++;
            }

            if (tank.getOxygen(chest) <= 0) {
                return 0;
            } else {
                tank.changeOxygen(chest, -1.0);
            }
            return switch (piecesCount) {
                case 0 -> 0.5;
                case 1 -> 1.0;
                case 2 -> BreathabilityHelper.ABSORB_ALL;
                default -> 0;
            };
        }
        return 0;
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

    protected void handleDamage(ItemStack stack, Player player) {
        if (hoursOfLife == 0 || player.level().dimension().location().toString().equals("beneath")) {
            return;
        }
        double amount = 1.0 / (60.0 * 60.0 * hoursOfLife);
        changeDamage(stack, amount);
        if (getDamage(stack) >= 1) {
            stack.shrink(1);
            player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        }
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (getArmorType() == ArmorItem.Type.CHESTPLATE && getMaxOxygen(stack) != -1) {
            return getOxygen(stack) / getMaxOxygen(stack);
        } else {
            if (hoursOfLife > 0) {
                return 1 - getDamage(stack);
            }
        }
        return 1;
    }

    /**
     * @return the fill level for the durability bar (0 = full, 1 = empty).
     */
    public double getDurabilityForDisplayInternal(ItemStack stack) {
        return getDurabilityForDisplay(stack);
    }

    @Override
    public int getArmorDisplay(@NotNull Player player, @NotNull ItemStack armor, @NotNull EquipmentSlot slot) {
        return (int) Math.round(20.0F * getAbsorption(armor) * relativeAbsorption);
    }

    public int getArmorDisplayInternal(Player player, ItemStack armor, EquipmentSlot slot) {
        return getArmorDisplay(player, armor, slot);
    }

    @Override
    public ResourceLocation getArmorTexture(@NotNull ItemStack stack, @Nullable Entity entity,
                                            @NotNull EquipmentSlot slot, @Nullable String type) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/armor/" + name + "_" + slot.getName() + ".png");
    }

    @Override
    public void addInformation(ItemStack stack, List<Component> tooltips) {
        if (hoursOfLife > 0) {
            double lifetime = 60 * 60 * hoursOfLife;
            int secondsRemaining = (int) (lifetime - getDamage(stack) * lifetime);
            tooltips.add(Component.translatable("supersymmetry.seconds_left", secondsRemaining));
        } else {
            tooltips.add(Component.translatable("supersymmetry.unlimited"));
        }

        int armor = (int) Math.round(20.0F * getAbsorption(this.slot) * this.relativeAbsorption);
        if (armor > 0) {
            tooltips.add(Component.translatable("attribute.modifier.plus.0", armor,
                    Component.translatable("attribute.name.generic.armor")));
        }
    }

    protected float getAbsorption(ItemStack stack) {
        return getAbsorption(getArmorType());
    }

    protected float getAbsorption(ArmorItem.Type type) {
        return switch (type) {
            case HELMET, BOOTS -> 0.15F;
            case CHESTPLATE -> 0.4F;
            case LEGGINGS -> 0.3F;
            default -> 0.0F;
        };
    }
}
