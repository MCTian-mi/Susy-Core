package io.github.symmetricdevs.supersymmetry.api.recycling;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

/**
 * A {@link Recyclable} that wraps an {@link ItemLike} (item, block, etc.)
 * and outputs its {@link ItemStack} form.
 */
public final class ItemLikeRecyclable extends Recyclable {

    private final ItemStack stack;

    /**
     * Creates an ItemLikeRecyclable from an existing {@link ItemStack}.
     * A defensive copy is made.
     *
     * @param itemStack the item stack to wrap
     */
    public ItemLikeRecyclable(ItemStack itemStack) {
        this.stack = Objects.requireNonNull(itemStack, "itemStack").copy();
    }

    /**
     * Creates an ItemLikeRecyclable from an {@link ItemLike}.
     * The {@link ItemStack} is derived from the given item-like.
     *
     * @param itemLike the item-like to wrap
     */
    public ItemLikeRecyclable(ItemLike itemLike) {
        this.stack = new ItemStack(Objects.requireNonNull(itemLike, "itemLike"));
    }

    /**
     * Returns the internal {@link ItemStack} (defensive copy).
     *
     * @return a copy of the wrapped item stack
     */
    public ItemStack getStack() {
        return stack.copy();
    }

    @Override
    public ItemStack asStack(int size) {
        ItemStack result = stack.copy();
        result.setCount(size);
        return result;
    }

    @Override
    public int hashCode() {
        // Item + damage value for 1.20.1 compatibility (hashItemAndComponents is 1.20.5+)
        return stack.getItem().hashCode() * 31 + stack.getDamageValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ItemLikeRecyclable other) {
            return ItemStack.isSameItemSameTags(stack, other.stack);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("I[%s]", stack);
    }
}
