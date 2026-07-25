package io.github.symmetricdevs.supersymmetry.api.recipes.catalysts;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;

/**
 * A named set of catalyst items valid for a class of recipes, each mapped to its
 * {@link CatalystInfo} modifiers. Direct port of the 1.12.2 {@code CatalystGroup};
 * construction self-registers into a global list (used to resolve a group by name
 * from a recipe's data tag).
 */
public class CatalystGroup {

    private static final List<CatalystGroup> catalystGroups = new ArrayList<>();

    private final String name;
    private final CatalystInfos catalystInfos = new CatalystInfos();

    public CatalystGroup(@NotNull String registryName) {
        this.name = registryName;
        catalystGroups.add(this);
    }

    public CatalystInfos getCatalystInfos() {
        return this.catalystInfos;
    }

    public void add(@NotNull ItemStack itemStack, @NotNull CatalystInfo catalystInfo) {
        if (itemStack.isEmpty()) return;
        this.catalystInfos.put(itemStack, catalystInfo);
    }

    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Resolve a group by its registry name, or {@code null} if none matches.
     * Used to look up the group referenced by a recipe's {@code catalyst_group}
     * data entry.
     */
    public static CatalystGroup byName(@NotNull String name) {
        for (CatalystGroup group : catalystGroups) {
            if (group.name.equals(name)) return group;
        }
        return null;
    }

    public static List<CatalystGroup> getCatalystGroups() {
        return catalystGroups;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatalystGroup that = (CatalystGroup) o;
        return name.equals(that.name);
    }

    @Override
    public String toString() {
        return "CatalystGroup{" + "name='" + name + '\'' + '}';
    }
}
