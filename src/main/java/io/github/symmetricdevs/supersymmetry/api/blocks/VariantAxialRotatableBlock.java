package io.github.symmetricdevs.supersymmetry.api.blocks;

import net.minecraft.world.level.block.RotatedPillarBlock;

/**
 * An axially-rotatable block extending {@link RotatedPillarBlock}.
 * <p>
 * Inherits the {@code AXIS} property ({@link net.minecraft.world.level.block.state.properties.EnumProperty}{@code <Direction.Axis>})
 * from {@code RotatedPillarBlock}. On placement the axis is set to the clicked face's
 * axis via {@link RotatedPillarBlock#getStateForPlacement}.
 */
public class VariantAxialRotatableBlock extends RotatedPillarBlock {

    public VariantAxialRotatableBlock(Properties properties) {
        super(properties);
    }
}
