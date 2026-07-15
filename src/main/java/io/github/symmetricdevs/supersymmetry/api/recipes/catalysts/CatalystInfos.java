package io.github.symmetricdevs.supersymmetry.api.recipes.catalysts;

import java.util.Map;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;

import com.gregtechceu.gtceu.utils.ItemStackHashStrategy;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;

/**
 * Item → {@link CatalystInfo} lookup keyed by item + components but
 * <em>ignoring stack count</em> (a catalyst matches regardless of how many are in
 * the slot). Direct port of the 1.12.2 {@code CatalystInfos}; the hash strategy
 * moved from {@code gregtech.api.util} to {@code com.gregtechceu.gtceu.utils}.
 */
public class CatalystInfos {

    private final Map<ItemStack, CatalystInfo> map = new Object2ObjectOpenCustomHashMap<>(
            ItemStackHashStrategy.comparingAllButCount());

    public void put(@NotNull ItemStack itemStack, @NotNull CatalystInfo catalystInfo) {
        map.put(itemStack, catalystInfo);
    }

    public CatalystInfo get(@NotNull ItemStack itemStack) {
        return map.get(itemStack);
    }

    @NotNull
    public Stream<Map.Entry<ItemStack, CatalystInfo>> streamEntries() {
        return map.entrySet().stream();
    }
}
