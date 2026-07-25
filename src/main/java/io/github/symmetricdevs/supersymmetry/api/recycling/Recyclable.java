package io.github.symmetricdevs.supersymmetry.api.recycling;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import org.apache.commons.lang3.math.Fraction;

import java.util.Map;

/**
 * An abstract base class representing something that can be recycled.
 * <p>
 * Subclasses represent different kinds of recyclable objects:
 * <ul>
 *   <li>{@link MaterialRecyclable} — wraps a material (by name) for material-level recycling</li>
 *   <li>{@link ItemLikeRecyclable} — wraps an item-like for item-level recycling</li>
 * </ul>
 * </p>
 * This API is intentionally free of GTCEu dependencies. Materials are
 * referenced by their {@link String} registry name.
 */
public abstract class Recyclable {

    /** A no-op recyclable that always produces an empty stack. */
    public static final Recyclable EMPTY = new Recyclable() {
        @Override
        public ItemStack asStack(int size) {
            return ItemStack.EMPTY;
        }

        @Override
        public String toString() {
            return "Recyclable.EMPTY";
        }
    };

    /**
     * Converts an arbitrary object into a {@link Recyclable} if recognised.
     *
     * @param obj the object to convert
     * @return a matching Recyclable, or {@link #EMPTY} if unrecognised
     */
    public static Recyclable from(Object obj) {
        return switch (obj) {
            case ItemStack itemStack -> new ItemLikeRecyclable(itemStack);
            case ItemLike itemLike -> new ItemLikeRecyclable(itemLike);
            case String materialName -> new MaterialRecyclable(materialName);
            case null -> EMPTY;
            default -> EMPTY;
        };
    }

    /**
     * Returns an integer "value" for the given object relative to this recyclable.
     * The base implementation always returns {@code 1}.
     */
    public int value(Object obj) {
        return 1;
    }

    /**
     * Returns {@code true} if this is the {@link #EMPTY} instance.
     */
    public boolean isEmpty() {
        return this == EMPTY;
    }

    /**
     * Produces an {@link ItemStack} of the given size from this recyclable.
     *
     * @param size the stack size
     * @return the resulting ItemStack
     */
    public abstract ItemStack asStack(int size);

    /**
     * Produces a single {@link ItemStack} from this recyclable.
     */
    public ItemStack asStack() {
        return asStack(1);
    }

    /**
     * Adds this recyclable's material contributions to the given map.
     * <p>
     * Subclasses should override this to contribute their material content.
     * The base implementation is a no-op.
     * </p>
     *
     * @param mStacks map from material name to accumulated fraction
     * @param count   the count to add
     */
    public void addToMStack(Map<String, Fraction> mStacks, Fraction count) {
        if (Fraction.ZERO.equals(count)) return;
    }
}
