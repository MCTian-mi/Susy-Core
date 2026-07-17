package io.github.symmetricdevs.supersymmetry.common.command;

import java.util.stream.Collectors;

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

public class CommandHordeResetScripted {

    private static final SuggestionProvider<CommandSourceStack> EVENTS_SUGGESTIONS = (ctx, builder) -> {
        SharedSuggestionProvider.suggest(
                MobHordeEvent.EVENTS.values().stream().map(e -> e.KEY),
                builder
        );
        return SharedSuggestionProvider.suggest(new String[]{"all"}, builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("resetscripted")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("event", StringArgumentType.word())
                        .suggests(EVENTS_SUGGESTIONS)
                        .executes(ctx -> executeReset(ctx)))
        );
    }

    private static int executeReset(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) {
            ctx.getSource().sendFailure(Component.literal("Command can only be used by a player"));
            return 0;
        }

        MobHordePlayerData playerData = MobHordeWorldData.get(player.level())
                .getPlayerData(player.getUUID());

        String name = StringArgumentType.getString(ctx, "event");

        if (name.equalsIgnoreCase("all")) {
            playerData.completedScriptedEvents.clear();

            ctx.getSource().sendSuccess(() ->
                    Component.translatable("susy.command.horde.resetscripted.all"), false);
            return 1;
        }

        MobHordeEvent event = MobHordeEvent.EVENTS.get(name);

        if (event == null) {
            ctx.getSource().sendFailure(
                    Component.translatable("susy.command.horde.resetscripted.no_such_horde", name));
            return 0;
        }

        if (!playerData.hasCompleted(event.KEY)) {
            ctx.getSource().sendSuccess(() ->
                    Component.translatable("susy.command.horde.resetscripted.not_set", event.KEY), false);
            return 1;
        }

        playerData.completedScriptedEvents.remove(event.KEY);

        ctx.getSource().sendSuccess(() ->
                Component.translatable("susy.command.horde.resetscripted.success", event.KEY), false);
        return 1;
    }
}
