package io.github.symmetricdevs.supersymmetry.common.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import io.github.symmetricdevs.supersymmetry.common.event.MobHordePlayerData;
import io.github.symmetricdevs.supersymmetry.common.event.MobHordeWorldData;

public class CommandHordeStatus {

    public static int executeStatus(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getEntity() instanceof ServerPlayer player) {
            MobHordePlayerData playerData = MobHordeWorldData.get(player.level())
                    .getPlayerData(player.getUUID());

            if (playerData.hasActiveInvasion) {
                ctx.getSource().sendSuccess(() ->
                        Component.translatable("susy.command.horde.status.has_active_invasion",
                                player.getDisplayName(),
                                playerData.currentInvasion,
                                playerData.ticksActive,
                                playerData.timeoutPeriod - playerData.ticksActive), false);
            } else {
                ctx.getSource().sendSuccess(() ->
                        Component.translatable("susy.command.horde.status.no_active_invasion",
                                player.getDisplayName()), false);
            }
        }
        return 1;
    }
}
