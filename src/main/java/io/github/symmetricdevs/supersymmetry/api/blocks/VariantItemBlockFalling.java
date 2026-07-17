package io.github.symmetricdevs.supersymmetry.api.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Standard {@link BlockItem} for falling blocks. Retained as a distinct class to preserve the
 * API hierarchy from the 1.12.2 codebase; no additional behaviour beyond the default {@code BlockItem}.
 */
public class VariantItemBlockFalling extends BlockItem {

    public VariantItemBlockFalling(Block block, Item.Properties properties) {
        super(block, properties);
    }
}
