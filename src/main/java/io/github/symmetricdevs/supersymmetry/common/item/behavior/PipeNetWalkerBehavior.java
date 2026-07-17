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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Pipe net walker behavior for connecting/disconnecting/blocking/unblocking pipes.
 * Attached to tools via IItemComponent system.
 */
public class PipeNetWalkerBehavior implements IInteractionItem, IAddInformation {

    public static final PipeNetWalkerBehavior INSTANCE = new PipeNetWalkerBehavior();

    private PipeNetWalkerBehavior() {}

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();

        if (player == null) return InteractionResult.PASS;

        if (SyncedKeyMappings.TOOL_AOE_CHANGE.isKeyDown(player)) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof IPipeNode<?, ?> pipe) {

                // UseOnContext#getHitResult is protected in 1.20.1 — rebuild it from
                // the public accessors (same shape as GTCEu's PipeBlockItem).
                Direction gridSide = ICoverable.traceCoverSide(new BlockHitResult(context.getClickLocation(),
                        context.getClickedFace(), pos, false));
                if (gridSide == null) return InteractionResult.FAIL;

                TraverseOptions option = null;
                if (pipe.isConnected(gridSide)) {
                    if (player.isShiftKeyDown()) {
                        option = pipe.isBlocked(gridSide) ? TraverseOptions.UNBLOCKING : TraverseOptions.BLOCKING;
                    } else {
                        option = TraverseOptions.DISCONNECTING;
                    }
                } else if (!player.isShiftKeyDown()) {
                    option = TraverseOptions.CONNECTING;
                }

                if (option == null) return InteractionResult.FAIL;

                int maxWalks = stack.getMaxDamage() - stack.getDamageValue();
                if (maxWalks <= 0) return InteractionResult.FAIL;

                int walkedBlocks = PipeOperationWalker.collectPipeNet(level, pos, pipe, gridSide, option, maxWalks);

                if (!player.getAbilities().instabuild) {
                    int damageToApply = (int) Math.ceil(Math.sqrt(walkedBlocks));
                    int newDamage = stack.getDamageValue() + damageToApply;
                    if (newDamage >= stack.getMaxDamage()) {
                        stack.shrink(1);
                    } else {
                        stack.setDamageValue(newDamage);
                    }
                }

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.swing(context.getHand());

                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("item.susy.tool.behavior.pipeliner")
                .withStyle(ChatFormatting.GRAY));
    }
}
