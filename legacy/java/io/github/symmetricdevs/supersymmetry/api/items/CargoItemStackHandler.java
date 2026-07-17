package io.github.symmetricdevs.supersymmetry.api.items;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class CargoItemStackHandler implements IItemHandlerModifiable {

    private final ItemStack[] stacks;

    public CargoItemStackHandler(int size) {
        this.stacks = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            this.stacks[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public int getSlots() {
        return stacks.length;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return stacks[slot];
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        validateSlotIndex(slot);
        stacks[slot] = stack;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        validateSlotIndex(slot);
        ItemStack existing = stacks[slot];
        int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameTags(existing, stack)) {
                return stack;
            }
            limit = Math.min(limit, existing.getMaxStackSize() - existing.getCount());
            if (limit <= 0) {
                return stack;
            }
        }
        boolean reachedLimit = stack.getCount() > limit;
        int toInsert = reachedLimit ? limit : stack.getCount();
        if (!simulate) {
            if (existing.isEmpty()) {
                stacks[slot] = stack.copy();
                stacks[slot].setCount(toInsert);
            } else {
                existing.grow(toInsert);
            }
        }
        if (reachedLimit) {
            ItemStack remaining = stack.copy();
            remaining.shrink(toInsert);
            return remaining;
        }
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }
        validateSlotIndex(slot);
        ItemStack existing = stacks[slot];
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int toExtract = Math.min(amount, existing.getCount());
        ItemStack extracted = existing.copy();
        extracted.setCount(toExtract);
        if (!simulate) {
            if (toExtract >= existing.getCount()) {
                stacks[slot] = ItemStack.EMPTY;
            } else {
                existing.shrink(toExtract);
            }
        }
        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    private void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.length) {
            throw new IllegalArgumentException("Slot " + slot + " not in valid range - [0," + stacks.length + ")");
        }
    }
}
