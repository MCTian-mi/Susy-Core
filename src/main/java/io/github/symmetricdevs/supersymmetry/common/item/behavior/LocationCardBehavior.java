package io.github.symmetricdevs.supersymmetry.common.item.behavior;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Records a block position when the player right-clicks a block with this item.
 * Stores the position in NBT and shows it in the tooltip.
 */
public class LocationCardBehavior implements IInteractionItem, IAddInformation {

    private static final String TAG_ROOT = "susy";
    private static final String TAG_X = "x";
    private static final String TAG_Y = "y";
    private static final String TAG_Z = "z";
    private static final String TAG_DIM = "dimension";

    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player,
                                                  net.minecraft.world.InteractionHand usedHand) {
        // No-op on right-click air; block interaction handled by useOn.
        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            Player player = context.getPlayer();
            if (player == null) return InteractionResult.PASS;

            ItemStack stack = context.getItemInHand();
            BlockPos pos = context.getClickedPos();

            CompoundTag tag = stack.getOrCreateTagElement(TAG_ROOT);
            tag.putInt(TAG_X, pos.getX());
            tag.putInt(TAG_Y, pos.getY());
            tag.putInt(TAG_Z, pos.getZ());
            tag.putString(TAG_DIM, level.dimension().location().toString());

            player.displayClientMessage(
                    Component.translatable("susy.location_card.recorded",
                            pos.getX(), pos.getY(), pos.getZ()),
                    true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getTagElement(TAG_ROOT);
        if (tag != null && tag.contains(TAG_X) && tag.contains(TAG_Y) && tag.contains(TAG_Z)) {
            tooltipComponents.add(Component.translatable("susy.location_card.tooltip.coords",
                    tag.getInt(TAG_X), tag.getInt(TAG_Y), tag.getInt(TAG_Z)));
        } else {
            tooltipComponents.add(Component.translatable("susy.location_card.tooltip.no_coords"));
        }
    }
}
