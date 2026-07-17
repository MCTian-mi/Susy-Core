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
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.ICryogenicProvider;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.ICryogenicReceiver;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.CryogenicEnvironmentProperty;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityBathCondenser extends SimpleTieredMachine implements ICryogenicReceiver {

    private @Nullable ICryogenicProvider provider;

    public MetaTileEntityBathCondenser(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.BATH_CONDENSER, SusyTextures.BATH_CONDENSER_OVERLAY, 1, true);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityBathCondenser(metaTileEntityId);
    }

    @Override
    protected RecipeLogicEnergy createWorkable(GTRecipeType<?> GTRecipeType) {
        return new BathCondenserRecipeLogic(this, GTRecipeType, () -> energyContainer);
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
    public @Nullable ICryogenicProvider getCryogenicProvider() {
        return provider;
    }

    @Override
    public void setCryogenicProvider(@Nullable ICryogenicProvider cryogenicProvider) {
        this.provider = cryogenicProvider;
        if (this.provider == null && this.workable instanceof BathCondenserRecipeLogic logic) {
            logic.invalidate();
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        if (workable.getRecipeMap() != null && workable.getRecipeMap().getMaxFluidInputs() != 0) {
            tooltip.add(I18n.format("gregtech.universal.tooltip.fluid_storage_capacity",
                    this.getTankScalingFunction().apply(getTier())));
        }
    }

    // TODO make this extend PrimitiveRecipeLogic in GT 2.9
    private static class BathCondenserRecipeLogic extends RecipeLogicEnergy {

        public BathCondenserRecipeLogic(MetaTileEntity BlockEntity, GTRecipeType<?> GTRecipeType,
                                        Supplier<IEnergyContainer> energyContainer) {
            super(BlockEntity, GTRecipeType, energyContainer);
        }

        @NotNull
        @Override
        public MetaTileEntityBathCondenser getMetaTileEntity() {
            return (MetaTileEntityBathCondenser) super.getMetaTileEntity();
        }

        @Override
        public boolean checkRecipe(@NotNull Recipe recipe) {
            if (super.checkRecipe(recipe)) {
                boolean cryo = CryogenicEnvironmentProperty.getCryogenicEnvironment(recipe, false);
                return !cryo || (getMetaTileEntity().getCryogenicProvider() != null &&
                        getMetaTileEntity().getCryogenicProvider().isStructureFormed());
            }

            return false;
        }

        @Override
        protected boolean canProgressRecipe() {
            if (super.canProgressRecipe()) {
                if (previousRecipe == null) return true;
                Boolean value = previousRecipe.getProperty(CryogenicEnvironmentProperty.getInstance(), null);
                return value == null || !value || (getMetaTileEntity().getCryogenicProvider() != null &&
                        getMetaTileEntity().getCryogenicProvider().isStructureFormed());
            }
            return false;
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
                    getOverclockingVoltageMultiplier()

            );
        }

        @Override
        public long getMaximumOverclockVoltage() {
            return GTValues.V[GTValues.LV];
        }

        /**
         * Used to reset cached values in the Recipe Logic on structure deform
         */
        public void invalidate() {
            previousRecipe = null;
            progressTime = 0;
            maxProgressTime = 0;
            recipeEUt = 0;
            fluidOutputs = null;
            itemOutputs = null;
            setActive(false); // this marks dirty for us
        }
    }
}
