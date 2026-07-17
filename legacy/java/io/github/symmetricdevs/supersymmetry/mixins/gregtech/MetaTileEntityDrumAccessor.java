package io.github.symmetricdevs.supersymmetry.mixins.gregtech;

import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.gregtechceu.gtceu.api.capability.IPropertyFluidFilter;
import com.gregtechceu.gtceu.common.metatileentities.storage.MetaTileEntityDrum;

@Deprecated
@ScheduledForRemoval(inVersion = "Next CEu update")
@Mixin(value = MetaTileEntityDrum.class, remap = false)
public interface MetaTileEntityDrumAccessor {

    @Accessor("fluidFilter")
    IPropertyFluidFilter getFluidFilter();

    @Accessor("color")
    int getColor();

    @Accessor("tankSize")
    int getTankSize();

    @Accessor("isAutoOutput")
    boolean isAutoOutput();
}
