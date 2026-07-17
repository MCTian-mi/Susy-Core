package io.github.symmetricdevs.supersymmetry.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

public class CommandUntranslatedKeys {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("untranslatedkeys")
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> {
                    // Simplified: checks lang registry for common untranslated patterns
                    Set<String> untranslated = new HashSet<>();

                    // TODO: Check materials, fluids, items for untranslated keys
                    // This is a simplified stub that reports the command was run.

                    StringBuilder builder = new StringBuilder();
                    for (String str : untranslated) {
                        builder.append(str);
                        builder.append("=\n");
                    }

                    if (builder.isEmpty()) {
                        ctx.getSource().sendSuccess(() ->
                                Component.literal("No untranslated keys found (stub)."), false);
                    } else {
                        ctx.getSource().sendSuccess(() ->
                                Component.literal("Untranslated keys:\n" + builder), false);
                    }

                    return 1;
                })
        );
    }
}
