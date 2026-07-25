package io.github.symmetricdevs.supersymmetry.api.recycling;

import net.minecraft.world.item.ItemStack;

import org.apache.commons.lang3.math.Fraction;

import java.util.Map;
import java.util.Objects;

/**
 * A {@link Recyclable} that represents a material by its registry name.
 * <p>
 * This is a pure-Java replacement for the old {@code MaterialRecyclable}
 * that held a GTCEu {@code Material}. Materials are referenced as
 * {@link String} names.
 * </p>
 */
public final class MaterialRecyclable extends Recyclable {

    private final String materialName;

    /**
     * Creates a new MaterialRecyclable wrapping the given material name.
     *
     * @param materialName the material's registry name
     */
    public MaterialRecyclable(String materialName) {
        this.materialName = Objects.requireNonNull(materialName, "materialName");
    }

    /**
     * Returns the material name this recyclable wraps.
     *
     * @return the material registry name
     */
    public String getMaterialName() {
        return materialName;
    }

    @Override
    public ItemStack asStack(int size) {
        throw new UnsupportedOperationException(
                "Cannot create an ItemStack from a MaterialRecyclable instance!");
    }

    @Override
    public void addToMStack(Map<String, Fraction> mStacks, Fraction count) {
        if (Fraction.ZERO.equals(count)) return;
        mStacks.merge(materialName, count, Fraction::add);
    }

    @Override
    public int hashCode() {
        return materialName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof MaterialRecyclable other) {
            return materialName.equals(other.materialName);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("M[%s]", materialName);
    }
}
