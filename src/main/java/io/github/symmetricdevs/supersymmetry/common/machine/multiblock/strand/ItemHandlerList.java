package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.strand;

import net.minecraftforge.items.IItemHandlerModifiable;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Simple combined item handler for strand-casting controllers that need to treat all
 * import (or export) item buses as one inventory.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemHandlerList implements IItemHandlerModifiable {

    private final List<? extends IItemHandlerModifiable> handlers;

    public ItemHandlerList(List<? extends IItemHandlerModifiable> handlers) {
        this.handlers = handlers;
    }

    @Override
    public int getSlots() {
        int count = 0;
        for (var handler : handlers) {
            count += handler.getSlots();
        }
        return count;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        int[] ref = globalToLocal(slot);
        return handlers.get(ref[0]).getStackInSlot(ref[1]);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        int[] ref = globalToLocal(slot);
        return handlers.get(ref[0]).insertItem(ref[1], stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        int[] ref = globalToLocal(slot);
        return handlers.get(ref[0]).extractItem(ref[1], amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        int[] ref = globalToLocal(slot);
        return handlers.get(ref[0]).getSlotLimit(ref[1]);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        int[] ref = globalToLocal(slot);
        return handlers.get(ref[0]).isItemValid(ref[1], stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        int[] ref = globalToLocal(slot);
        handlers.get(ref[0]).setStackInSlot(ref[1], stack);
    }

    private int[] globalToLocal(int globalSlot) {
        int remaining = globalSlot;
        for (int i = 0; i < handlers.size(); i++) {
            int slots = handlers.get(i).getSlots();
            if (remaining < slots) {
                return new int[] { i, remaining };
            }
            remaining -= slots;
        }
        throw new IndexOutOfBoundsException(globalSlot);
    }
}
