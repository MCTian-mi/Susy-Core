package io.github.symmetricdevs.supersymmetry.common.metatileentities.multiblockpart;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;

import com.gregtechceu.gtceu.common.machine.multiblock.part.MetaTileEntityEnergyHatch;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MetaTileEntitySubstationEnergyHatch;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.SuSyMetaTileEntities;

public class SusyMetaTileEntitySubstationEnergyHatch extends MetaTileEntitySubstationEnergyHatch
                                                     implements IMultiblockAbilityPart<IEnergyContainer> {

    public SusyMetaTileEntitySubstationEnergyHatch(ResourceLocation metaTileEntityId, int tier, int amperage,
                                                   boolean isExportHatch) {
        super(metaTileEntityId, tier, amperage, isExportHatch);
    }

    @Override
    public void getSubItems(CreativeTabs creativeTab, NonNullList<ItemStack> subItems) {
        for (MetaTileEntityEnergyHatch hatch : SuSyMetaTileEntities.NEW_SUBSTATION_ENERGY_OUTPUT_HATCH_64A) {
            if (hatch != null) subItems.add(hatch.getStackForm());
        }
        for (MetaTileEntityEnergyHatch hatch : SuSyMetaTileEntities.NEW_SUBSTATION_ENERGY_OUTPUT_HATCH_256A) {
            if (hatch != null) subItems.add(hatch.getStackForm());
        }
        for (MetaTileEntityEnergyHatch hatch : SuSyMetaTileEntities.NEW_SUBSTATION_ENERGY_INPUT_HATCH_64A) {
            if (hatch != null) subItems.add(hatch.getStackForm());
        }
        for (MetaTileEntityEnergyHatch hatch : SuSyMetaTileEntities.NEW_SUBSTATION_ENERGY_INPUT_HATCH_256A) {
            if (hatch != null) subItems.add(hatch.getStackForm());
        }
    }
}
