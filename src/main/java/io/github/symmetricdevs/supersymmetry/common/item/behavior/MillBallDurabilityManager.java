package io.github.symmetricdevs.supersymmetry.common.item.behavior;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IDurabilityBar;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Durability manager for mill balls.
 * Displays durability based on NBT-stored damage value and the material's MillBallProperty.
 */
public class MillBallDurabilityManager implements IDurabilityBar, IAddInformation {

    public static final MillBallDurabilityManager INSTANCE = new MillBallDurabilityManager();

    private static final String MILL_BALL_STATS_TAG = "GT.MillBallStats";
    private static final String DAMAGE_KEY = "Damage";

    private MillBallDurabilityManager() {}

    /**
     * Gets the mill ball stats NBT tag (read-only).
     */
    protected static CompoundTag getMillBallStatsTag(ItemStack itemStack) {
        return itemStack.getTagElement(MILL_BALL_STATS_TAG);
    }

    /**
     * Gets or creates the mill ball stats NBT tag (for writing).
     */
    protected static CompoundTag getOrCreateMillBallStatsTag(ItemStack itemStack) {
        return itemStack.getOrCreateTagElement(MILL_BALL_STATS_TAG);
    }

    /**
     * Gets the current damage value from NBT.
     */
    public static int getMillBallDamage(ItemStack itemStack) {
        CompoundTag compound = getMillBallStatsTag(itemStack);
        if (compound == null || !compound.contains(DAMAGE_KEY)) {
            return 0;
        }
        return compound.getInt(DAMAGE_KEY);
    }

    /**
     * Sets the damage value in NBT.
     */
    public static void setMillBallDamage(ItemStack itemStack, int damage) {
        int maxDurability = getMillBallMaxDurability(itemStack);
        CompoundTag compound = getOrCreateMillBallStatsTag(itemStack);
        compound.putInt(DAMAGE_KEY, Math.min(maxDurability, damage));
    }

    /**
     * Gets the maximum durability for this mill ball based on its material.
     */
    public static int getMillBallMaxDurability(ItemStack itemStack) {
        // Mill balls are material-based items; the max durability is derived from the material property.
        // This is a simplified default; real implementations should look up the material property.
        return 1600; // default fallback
    }

    /**
     * Applies damage to the mill ball. If damage exceeds durability, returns true (consumed).
     */
    public static boolean applyMillBallDamage(ItemStack itemStack, int damageApplied) {
        int maxDurability = getMillBallMaxDurability(itemStack);
        int currentDamage = getMillBallDamage(itemStack);
        int resultDamage = currentDamage + damageApplied;

        if (resultDamage >= maxDurability) {
            setMillBallDamage(itemStack, 0);
            return true; // broken, consume the item
        } else {
            setMillBallDamage(itemStack, resultDamage);
            return false;
        }
    }

    // -- IDurabilityBar --

    @Override
    public float getDurabilityForDisplay(ItemStack stack) {
        int maxDurability = getMillBallMaxDurability(stack);
        int currentDamage = getMillBallDamage(stack);
        if (maxDurability <= 0) return 1.0F;
        return (float) (maxDurability - currentDamage) / (float) maxDurability;
    }

    @Override
    public boolean showEmptyBar(ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean showFullBar(ItemStack itemStack) {
        return false;
    }

    // -- IAddInformation --

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        int maxDurability = getMillBallMaxDurability(stack);
        int currentDamage = getMillBallDamage(stack);
        tooltipComponents.add(Component.translatable("item.durability", maxDurability - currentDamage, maxDurability));
    }
}
