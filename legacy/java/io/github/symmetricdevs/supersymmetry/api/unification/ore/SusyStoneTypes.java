package io.github.symmetricdevs.supersymmetry.api.unification.ore;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;
import io.github.symmetricdevs.supersymmetry.common.blocks.SusyStoneVariantBlock;
import io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials;

/**
 * Porting stub for custom stone types.
 * <p>
 * Modern GTCEu does not have a general-purpose {@code StoneType} class that
 * third parties can instantiate — it uses its own {@code StoneTypes} enum and
 * {@code StoneBlockType} for ore-generation integration. The custom stone
 * blocks (Gabbro, Gneiss, etc.) are registered as normal blocks in Phase 5;
 * worldgen ore-vein integration is deferred to Phase 8.
 * <p>
 * This holder will be re-architected when worldgen is ported.
 */
public class SusyStoneTypes {

    public SusyStoneTypes() {}

    public static void init() {
        // Stone type registration deferred to Phase 8 (worldgen).
        // The stone blocks themselves (SusyStoneVariantBlock) are registered
        // via SuSyBlocks and work as decorative/construction blocks.
    }

    private static BlockState gtStoneState(SusyStoneVariantBlock.StoneType stoneType) {
        return SuSyBlocks.SUSY_STONE_BLOCKS.get(SusyStoneVariantBlock.StoneVariant.SMOOTH).getState(stoneType);
    }

    private static boolean gtStonePredicate(BlockState state, SusyStoneVariantBlock.StoneType stoneType) {
        SusyStoneVariantBlock block = SuSyBlocks.SUSY_STONE_BLOCKS.get(SusyStoneVariantBlock.StoneVariant.SMOOTH);
        return state.getBlock() == block && block.getState(state) == stoneType;
    }
}
