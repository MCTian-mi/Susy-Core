package io.github.symmetricdevs.supersymmetry.api.item;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.TagPrefixItem;

import io.github.symmetricdevs.supersymmetry.api.unification.material.properties.MillBallProperty;
import io.github.symmetricdevs.supersymmetry.api.unification.material.properties.SuSyPropertyKey;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/** A material-prefix mill ball whose wear is stored independently from vanilla item damage. */
public final class MillBallItem extends TagPrefixItem {

    private static final String MILL_BALL_STATS_TAG = "GT.MillBallStats";
    private static final String DAMAGE_KEY = "Damage";

    public MillBallItem(Properties properties, TagPrefix tagPrefix, Material material) {
        super(properties, tagPrefix, material);
    }

    public static int getMillBallDamage(ItemStack stack) {
        CompoundTag stats = stack.getTagElement(MILL_BALL_STATS_TAG);
        if (stats == null || !stats.contains(DAMAGE_KEY, Tag.TAG_ANY_NUMERIC)) {
            return 0;
        }
        return Math.max(0, stats.getInt(DAMAGE_KEY));
    }

    public static int getMaxDurability(ItemStack stack) {
        if (!(stack.getItem() instanceof TagPrefixItem item) ||
                !item.material.hasProperty(SuSyPropertyKey.MILL_BALL)) {
            return 1;
        }
        MillBallProperty property = item.material.getProperty(SuSyPropertyKey.MILL_BALL);
        return Math.max(1, property.durability());
    }

    public static boolean isUsable(ItemStack stack) {
        return !stack.isEmpty() && getMillBallDamage(stack) < getMaxDurability(stack);
    }

    /**
     * Applies wear and returns {@code true} when the ball reaches its material durability.
     */
    public static boolean applyDamage(ItemStack stack, int damageApplied) {
        if (damageApplied <= 0) {
            return false;
        }
        int maxDurability = getMaxDurability(stack);
        long resultDamage = (long) getMillBallDamage(stack) + damageApplied;
        if (resultDamage >= maxDurability) {
            return true;
        }
        setMillBallDamage(stack, (int) resultDamage);
        return false;
    }

    private static void setMillBallDamage(ItemStack stack, int damage) {
        stack.getOrCreateTagElement(MILL_BALL_STATS_TAG)
                .putInt(DAMAGE_KEY, Math.max(0, Math.min(getMaxDurability(stack), damage)));
    }

    private static float remainingFraction(ItemStack stack) {
        return Mth.clamp((float) (getMaxDurability(stack) - getMillBallDamage(stack)) /
                (float) getMaxDurability(stack), 0.0F, 1.0F);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getMillBallDamage(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * remainingFraction(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb(remainingFraction(stack) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        int maxDurability = getMaxDurability(stack);
        tooltipComponents.add(Component.translatable("item.durability", maxDurability - getMillBallDamage(stack),
                maxDurability));
    }
}
