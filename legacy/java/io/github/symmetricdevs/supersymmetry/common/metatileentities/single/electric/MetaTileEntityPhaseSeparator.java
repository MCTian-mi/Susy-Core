package io.github.symmetricdevs.supersymmetry.common.metatileentities.single.electric;

import static com.gregtechceu.gtceu.api.recipe.logic.OverclockingLogic.standardOverclockingLogic;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.EnergyContainerHandler;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import com.gregtechceu.gtceu.utils.GTUtil;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityPhaseSeparator extends SimpleTieredMachine {

    public MetaTileEntityPhaseSeparator(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.PHASE_SEPARATOR, SusyTextures.PHASE_SEPARATOR_OVERLAY, 1, true,
                GTUtility.largeTankSizeFunction);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityPhaseSeparator(metaTileEntityId);
    }

    @Override
    protected RecipeLogicEnergy createWorkable(GTRecipeType<?> GTRecipeType) {
        return new PhaseSeparatorRecipeLogic(this, GTRecipeType, () -> energyContainer);
    }

    @Override
    protected void reinitializeEnergyContainer() {
        this.energyContainer = new EnergyContainerHandler(this, 0, 0, 0, 0, 0) {

            @Override
            public boolean isOneProbeHidden() {
                return true;
            }
        };
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        if (workable.getRecipeMap() != null && workable.getRecipeMap().getMaxFluidInputs() != 0) {
            tooltip.add(I18n.format("gregtech.universal.tooltip.fluid_storage_capacity",
                    this.getTankScalingFunction().apply(getTier())));
        }
    }

    // TODO make this extend PrimitiveRecipeLogic in GT 2.9
    private static class PhaseSeparatorRecipeLogic extends RecipeLogicEnergy {

        public PhaseSeparatorRecipeLogic(MetaTileEntity MetaMachine, GTRecipeType<?> GTRecipeType,
                                         Supplier<IEnergyContainer> energyContainer) {
            super(MetaMachine, GTRecipeType, energyContainer);
        }

        @NotNull
        @Override
        public MetaTileEntityPhaseSeparator getMetaTileEntity() {
            return (MetaTileEntityPhaseSeparator) super.getMetaTileEntity();
        }

        @Override
        protected long getEnergyInputPerSecond() {
            return Integer.MAX_VALUE;
        }

        @Override
        protected long getEnergyStored() {
            return Integer.MAX_VALUE;
        }

        @Override
        protected long getEnergyCapacity() {
            return Integer.MAX_VALUE;
        }

        @Override
        protected boolean drawEnergy(int recipeEUt, boolean simulate) {
            return true; // spoof energy being drawn
        }

        @Override
        public long getMaxVoltage() {
            return GTValues.LV;
        }

        @Override
        protected int @NotNull [] runOverclockingLogic(@NotNull IRecipePropertyStorage propertyStorage, int recipeEUt,
                                                       long maxVoltage, int recipeDuration, int amountOC) {
            return standardOverclockingLogic(
                    1,
                    getMaxVoltage(),
                    recipeDuration,
                    amountOC,
                    getOverclockingDurationDivisor(),
                    getOverclockingVoltageMultiplier());
        }

        @Override
        public long getMaximumOverclockVoltage() {
            return GTValues.V[GTValues.LV];
        }
    }
}
