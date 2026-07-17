package io.github.symmetricdevs.supersymmetry.integration.jei;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import io.github.symmetricdevs.supersymmetry.api.stockinteraction.StockHelperFunctions;

public class RollingStockSubtypeHandler implements ISubtypeInterpreter {

    @NotNull
    @Override
    public String apply(@NotNull ItemStack itemStack) {
        String additionalData = StockHelperFunctions.getDefinitionNameFromStack(itemStack);
        return String.format("%d;%s", itemStack.getMetadata(), additionalData == null ? "" : additionalData);
    }
}
