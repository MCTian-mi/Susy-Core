package io.github.symmetricdevs.supersymmetry.common.tileentities;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;

import io.github.symmetricdevs.supersymmetry.api.blocks.IAnimatablePartBlock;

/**
 * Stub — GeckoLib 3 animation removed.
 * <p>
 * In 1.12.2 this BE implemented GeckoLib {@code IAnimatable} and used
 * {@code AnimationFactory}, {@code AnimationController}, etc. GeckoLib 3
 * is not available on 1.20.1; animation will be re-added with GeckoLib 4.
 */
public class AnimatablePartTileEntity extends BlockEntity {

    public AnimatablePartTileEntity() {
        super(null);
    }

    public IAnimatablePartBlock getPartBlock() {
        if (getBlockState().getBlock() instanceof IAnimatablePartBlock) {
            return (IAnimatablePartBlock) getBlockState().getBlock();
        }
        throw new IllegalStateException("Block should implement IAnimatablePart!");
    }

    @Override
    public @NotNull AABB getRenderBoundingBox() {
        return AABB.INFINITE;
    }
}
