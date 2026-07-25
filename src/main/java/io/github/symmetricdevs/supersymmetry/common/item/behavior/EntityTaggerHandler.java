package io.github.symmetricdevs.supersymmetry.common.item.behavior;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.item.component.IItemLifeCycle;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Entity tagger item behavior.
 * Right-click on an entity to tag it with a faction, or shift-right-click to clear.
 * Sneak + right-click air cycles the selected faction.
 * Holding the item highlights tagged entities within range.
 */
public class EntityTaggerHandler implements IInteractionItem, IItemLifeCycle {

    private static final String TAG_ROOT = "susy";
    private static final String TAG_FACTION = "faction";
    private static final String TAG_HATE = "hate";
    private static final double RADIUS = 32;

    private static final String[] FACTIONS = {"faction.blue", "faction.red", "faction.green"};

    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }
        // Sneak + right-click air: cycle faction
        if (!level.isClientSide) {
            CompoundTag tag = stack.getOrCreateTagElement(TAG_ROOT);
            String current = tag.getString(TAG_FACTION);
            String next = getNextFaction(current);
            tag.putString(TAG_FACTION, next);
            player.displayClientMessage(Component.literal("Faction set to: " + next), false);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget,
                                                   InteractionHand usedHand) {
        Level level = player.level();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) {
            // Shift right-click on entity: clear faction
            CompoundTag entityTag = interactionTarget.getPersistentData();
            entityTag.getCompound(TAG_ROOT).remove(TAG_FACTION);
            player.displayClientMessage(Component.literal("Faction cleared"), false);
        } else {
            // Right-click on entity: tag with current faction
            CompoundTag itemTag = stack.getOrCreateTagElement(TAG_ROOT);
            String faction = itemTag.getString(TAG_FACTION);
            if (faction.isEmpty()) {
                faction = FACTIONS[0];
                itemTag.putString(TAG_FACTION, faction);
            }

            CompoundTag entityData = interactionTarget.getPersistentData();
            CompoundTag susyTag = entityData.getCompound(TAG_ROOT);
            susyTag.putString(TAG_FACTION, faction);
            entityData.put(TAG_ROOT, susyTag);

            player.displayClientMessage(Component.literal("Set faction: " + faction), false);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player && !player.level().isClientSide) {
            CompoundTag entityData = target.getPersistentData();
            CompoundTag susyTag = entityData.getCompound(TAG_ROOT);
            int currentHate = susyTag.getInt(TAG_HATE);
            int amount = player.isShiftKeyDown() ? -1 : 1;
            int newHate = currentHate + amount;
            susyTag.putInt(TAG_HATE, newHate);
            entityData.put(TAG_ROOT, susyTag);
            player.displayClientMessage(Component.literal("Mob hate value: " + newHate), false);
        }
        return false; // prevent damage to the target
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (isSelected && entity instanceof Player player && !level.isClientSide) {
            tickHighlight(player, stack);
        }
    }

    /**
     * Highlights faction-tagged entities within range when holding the tagger.
     */
    private static void tickHighlight(Player player, ItemStack stack) {
        Level level = player.level();
        if (player.tickCount % 5 != 0) return;

        CompoundTag tag = stack.getTagElement(TAG_ROOT);
        String selectedFaction = tag != null ? tag.getString(TAG_FACTION) : "";

        AABB area = player.getBoundingBox().inflate(RADIUS);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player);

        boolean holdingTag = !stack.isEmpty() && !selectedFaction.isEmpty();

        for (LivingEntity entity : entities) {
            if (holdingTag) {
                CompoundTag susy = entity.getPersistentData().getCompound(TAG_ROOT);
                String faction = susy.getString(TAG_FACTION);
                entity.setGlowingTag(selectedFaction.equals(faction));
            } else {
                // 1.20.1: Entity#isGlowing() is gone; the tag state set via
                // setGlowingTag is read back with hasGlowingTag().
                if (entity.hasGlowingTag()) {
                    entity.setGlowingTag(false);
                }
            }
        }
    }

    private static String getNextFaction(String current) {
        for (int i = 0; i < FACTIONS.length; i++) {
            if (FACTIONS[i].equals(current)) {
                return FACTIONS[(i + 1) % FACTIONS.length];
            }
        }
        return FACTIONS[0];
    }
}
