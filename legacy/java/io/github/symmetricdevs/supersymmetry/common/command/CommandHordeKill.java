package io.github.symmetricdevs.supersymmetry.common.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import io.github.symmetricdevs.supersymmetry.common.event.MobHordePlayerData;
import io.github.symmetricdevs.supersymmetry.common.event.MobHordeWorldData;

public class CommandHordeKill {

    public static int executeKill(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getEntity() instanceof ServerPlayer player) {

            MobHordePlayerData playerData = MobHordeWorldData.get(player.level())
                    .getPlayerData(player.getUUID());

            if (!playerData.hasActiveInvasion) {
                ctx.getSource().sendSuccess(() ->
                        Component.translatable("susy.command.horde.kill.has_active_no_invasion"), false);
                return 1;
            }

            String invasion = playerData.currentInvasion;

            playerData.killInvasion(player);

            ctx.getSource().sendSuccess(() ->
                    Component.translatable("susy.command.horde.kill.killed", invasion), false);
        }
        return 1;
    }
}
