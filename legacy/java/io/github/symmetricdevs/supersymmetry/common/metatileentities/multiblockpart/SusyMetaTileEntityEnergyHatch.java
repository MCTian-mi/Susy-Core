package io.github.symmetricdevs.supersymmetry.common.metatileentities.multiblockpart;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;

import com.gregtechceu.gtceu.common.machine.multiblock.part.MetaTileEntityEnergyHatch;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.SuSyMetaTileEntities;

public class SusyMetaTileEntityEnergyHatch extends MetaTileEntityEnergyHatch
                                           implements IMultiblockAbilityPart<IEnergyContainer> {

    public SusyMetaTileEntityEnergyHatch(ResourceLocation metaTileEntityId, int tier, int amperage,
                                         boolean isExportHatch) {
        super(metaTileEntityId, tier, amperage, isExportHatch);
    }

    @Override
    public void getSubItems(CreativeTabs creativeTab, NonNullList<ItemStack> subItems) {
        for (MetaTileEntityEnergyHatch hatch : SuSyMetaTileEntities.NEW_ENERGY_OUTPUT_HATCH_4A) {
            if (hatch != null) subItems.add(hatch.getStackForm());
        }
        for (MetaTileEntityEnergyHatch hatch : SuSyMetaTileEntities.NEW_ENERGY_OUTPUT_HATCH_16A) {
            if (hatch != null) subItems.add(hatch.getStackForm());
        }
    }
}
