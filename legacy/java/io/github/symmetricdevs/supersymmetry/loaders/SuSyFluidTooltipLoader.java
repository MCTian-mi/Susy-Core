package io.github.symmetricdevs.supersymmetry.loaders;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.material.Fluid;
// FluidRegistry removed in 1.20.1

import com.google.common.collect.Lists;

import com.gregtechceu.gtceu.api.fluids.FluidState;
import com.gregtechceu.gtceu.api.fluids.GTFluid;
import com.gregtechceu.gtceu.api.fluids.attribute.AttributedFluid;
import com.gregtechceu.gtceu.api.unification.material.Material;
import com.gregtechceu.gtceu.api.unification.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.util.FluidTooltipUtil;

public class SuSyFluidTooltipLoader {

    public static void registerTooltips() {
        Supplier<List<String>> liquidVoidableTooltip = () -> Lists
                .newArrayList(ChatFormatting.YELLOW + I18n.format("susy.fluid.voiding.liquid"));
        Supplier<List<String>> gasVoidableTooltip = () -> Lists
                .newArrayList(ChatFormatting.YELLOW + I18n.format("susy.fluid.voiding.gas"));
        Supplier<List<String>> flammableVoidableTooltip = () -> Lists
                .newArrayList(ChatFormatting.YELLOW + I18n.format("susy.fluid.voiding.flammable"));

        FluidTooltipUtil.registerTooltip(FluidRegistry.WATER, liquidVoidableTooltip);
        FluidTooltipUtil.registerTooltip(FluidRegistry.LAVA, liquidVoidableTooltip);

        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
            if (fluid instanceof AttributedFluid aFluid) {
                FluidState state = aFluid.getState();
                if (fluid instanceof GTFluid.GTMaterialFluid gtFluid) {
                    Material mat = gtFluid.getMaterial();
                    if (mat.hasFlag(MaterialFlags.FLAMMABLE)) {
                        FluidTooltipUtil.registerTooltip(fluid, flammableVoidableTooltip);
                        continue;
                    }
                }
                if (state == FluidState.LIQUID) FluidTooltipUtil.registerTooltip(fluid, liquidVoidableTooltip);
                else if (state == FluidState.GAS) FluidTooltipUtil.registerTooltip(fluid, gasVoidableTooltip);
            }
        }
    }
}
