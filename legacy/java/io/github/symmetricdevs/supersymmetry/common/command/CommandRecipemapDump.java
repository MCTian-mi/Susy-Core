package io.github.symmetricdevs.supersymmetry.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CommandRecipemapDump {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("recipemapdump")
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> {
                    // Simplified: dump-file functionality is removed in modern.
                    // This is a stub that acknowledges the command.
                    ctx.getSource().sendSuccess(() ->
                            Component.translatable("susy.command.recipemapdump.usage"), false);
                    ctx.getSource().sendSuccess(() ->
                            Component.literal("Recipe map dump is not available in this version."), false);
                    return 1;
                })
        );
    }
}
