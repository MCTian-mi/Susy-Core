package io.github.symmetricdevs.supersymmetry.common.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import io.github.symmetricdevs.supersymmetry.common.event.MobHordePlayerData;
import io.github.symmetricdevs.supersymmetry.common.event.MobHordeWorldData;

public class CommandHordeStop {

    public static int executeStop(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getEntity() instanceof ServerPlayer player) {

            MobHordePlayerData playerData = MobHordeWorldData.get(player.level())
                    .getPlayerData(player.getUUID());

            if (!playerData.hasActiveInvasion) {
                ctx.getSource().sendSuccess(() ->
                        Component.translatable("susy.command.horde.stop.has_active_no_invasion"), false);
                return 1;
            }

            String invasion = playerData.currentInvasion;

            playerData.stopInvasion(player);

            ctx.getSource().sendSuccess(() ->
                    Component.translatable("susy.command.horde.stop.stopped", invasion), false);
        }
        return 1;
    }
}
