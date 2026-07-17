package io.github.symmetricdevs.supersymmetry.mixins.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.gregtechceu.gtceu.api.items.toolitem.IGTToolDefinition;
import com.gregtechceu.gtceu.api.items.toolitem.ToolBuilder;

@Mixin(value = ToolBuilder.class, remap = false)
public interface ToolBuilderAccessor {

    @Accessor("toolStats")
    IGTToolDefinition getToolStats();
}
