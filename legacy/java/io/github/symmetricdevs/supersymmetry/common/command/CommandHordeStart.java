package io.github.symmetricdevs.supersymmetry.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import io.github.symmetricdevs.supersymmetry.api.event.MobHordeEvent;
import io.github.symmetricdevs.supersymmetry.common.event.MobHordePlayerData;
import io.github.symmetricdevs.supersymmetry.common.event.MobHordeWorldData;

public class CommandHordeStart {

    private static final SuggestionProvider<CommandSourceStack> EVENTS_SUGGESTIONS = (ctx, builder) ->
            SharedSuggestionProvider.suggest(
                    MobHordeEvent.EVENTS.values().stream().map(e -> e.KEY),
                    builder
            );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hordeStart")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("event", StringArgumentType.word())
                        .suggests(EVENTS_SUGGESTIONS)
                        .executes(ctx -> executeStart(ctx, false))
                        .then(Commands.literal("overwrite")
                                .executes(ctx -> executeStart(ctx, true))))
        );
    }

    private static int executeStart(CommandContext<CommandSourceStack> ctx, boolean overwrite) {
        if (ctx.getSource().getEntity() instanceof ServerPlayer player) {
            String name = StringArgumentType.getString(ctx, "event");
            MobHordeEvent event = MobHordeEvent.EVENTS.get(name);

            MobHordePlayerData playerData = MobHordeWorldData.get(player.level())
                    .getPlayerData(player.getUUID());

            if (event == null) {
                ctx.getSource().sendFailure(
                        Component.translatable("susy.command.horde.start.no_such_horde", name));
                return 0;
            }

            if (!event.canRun(player)) {
                ctx.getSource().sendFailure(
                        Component.translatable("susy.command.horde.start.unable_to_run", name));
                return 0;
            }

            if (playerData.hasActiveInvasion) {
                if (overwrite) {
                    playerData.stopInvasion(player);
                } else {
                    ctx.getSource().sendSuccess(() ->
                            Component.translatable("susy.command.horde.start.has_active_invasion",
                                    playerData.currentInvasion), false);
                    return 1;
                }
            }

            if (!event.run(player, playerData::addEntity)) {
                ctx.getSource().sendFailure(
                        Component.translatable("susy.command.horde.start.error_executing_horde"));
                return 0;
            }

            playerData.setCurrentInvasion(event);
            ctx.getSource().sendSuccess(() ->
                    Component.translatable("susy.command.horde.start.started", event.KEY), false);
        }
        return 1;
    }
}
