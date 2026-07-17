package io.github.symmetricdevs.supersymmetry.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;

import java.util.Arrays;
import java.util.List;

public class CommandFactionHate {

    private static final SuggestionProvider<CommandSourceStack> SUBCOMMANDS = (ctx, builder) ->
            SharedSuggestionProvider.suggest(new String[]{"get", "add", "set"}, builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("factionHate")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("subcommand", StringArgumentType.word())
                        .suggests(SUBCOMMANDS)
                        .then(Commands.argument("faction", StringArgumentType.word())
                                .executes(ctx -> executeGet(ctx))
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> executeModify(ctx))))
                )
        );
    }

    private static int executeGet(CommandContext<CommandSourceStack> ctx) {
        String faction = StringArgumentType.getString(ctx, "faction");
        // TODO: Port FactionHateManager
        ctx.getSource().sendSuccess(() ->
                Component.translatable("susy.command.faction.get", faction, 0), false);
        return 1;
    }

    private static int executeModify(CommandContext<CommandSourceStack> ctx) {
        String subcommand = StringArgumentType.getString(ctx, "subcommand");
        String faction = StringArgumentType.getString(ctx, "faction");
        int value = IntegerArgumentType.getInteger(ctx, "value");

        // TODO: Port FactionHateManager
        ctx.getSource().sendSuccess(() ->
                Component.translatable("susy.command.faction." + subcommand, value, faction), false);
        return 1;
    }
}
