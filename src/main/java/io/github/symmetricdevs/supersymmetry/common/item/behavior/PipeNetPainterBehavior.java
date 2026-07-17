package io.github.symmetricdevs.supersymmetry.common.item.behavior;

import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.pipenet.IPipeNode;
import com.gregtechceu.gtceu.utils.input.SyncedKeyMappings;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Pipe net painter behavior.
 * Right-click on a pipe while holding the TOOL_AOE_CHANGE key to paint the connected pipe network.
 */
public class PipeNetPainterBehavior implements IInteractionItem, IAddInformation {

    private final int color;

    public PipeNetPainterBehavior(int color) {
        this.color = color;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (player == null) return InteractionResult.PASS;

        if (SyncedKeyMappings.TOOL_AOE_CHANGE.isKeyDown(player)) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof IPipeNode<?, ?> pipe) {
                ItemStack toolStack = context.getItemInHand();

                Direction gridSide = ICoverable.traceCoverSide(context.getHitResult());
                if (gridSide == null) return InteractionResult.FAIL;

                int maxWalks = toolStack.getMaxDamage() - toolStack.getDamageValue();
                if (maxWalks <= 0) return InteractionResult.FAIL;

                int walkedBlocks = PipeOperationWalker.collectPipeNet(level, pos, pipe, gridSide,
                        TraverseOptions.COLORING.get(color), maxWalks);

                if (!player.getAbilities().instabuild) {
                    int newDamage = toolStack.getDamageValue() + walkedBlocks;
                    if (newDamage >= toolStack.getMaxDamage()) {
                        toolStack.shrink(1);
                    } else {
                        toolStack.setDamageValue(newDamage);
                    }
                }

                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("item.susy.tool.tooltip.pipeliner", "V")
                .withStyle(ChatFormatting.GRAY));
    }
}
