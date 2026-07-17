package io.github.symmetricdevs.supersymmetry.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandHordeBase {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mobHorde")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("start")
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(() ->
                                    net.minecraft.network.chat.Component.translatable("susy.command.horde.start.usage"), false);
                            return 1;
                        }))
                .then(Commands.literal("stop")
                        .executes(ctx -> CommandHordeStop.executeStop(ctx)))
                .then(Commands.literal("status")
                        .executes(ctx -> CommandHordeStatus.executeStatus(ctx)))
                .then(Commands.literal("kill")
                        .executes(ctx -> CommandHordeKill.executeKill(ctx)))
                .then(Commands.literal("resetscripted")
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(() ->
                                    net.minecraft.network.chat.Component.translatable("susy.command.horde.resetscripted.usage"), false);
                            return 1;
                        }))
                .then(Commands.literal("help")
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(() ->
                                    net.minecraft.network.chat.Component.translatable("susy.command.horde.usage"), false);
                            return 1;
                        }))
        );
    }
}
