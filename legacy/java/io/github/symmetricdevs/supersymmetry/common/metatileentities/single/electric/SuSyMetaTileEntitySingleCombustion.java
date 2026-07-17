package io.github.symmetricdevs.supersymmetry.common.metatileentities.single.electric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;

import com.gregtechceu.gtceu.api.capability.FuelRecipeLogic;
import com.gregtechceu.gtceu.api.capability.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.ModularUI;
import com.gregtechceu.gtceu.api.gui.widgets.ImageWidget;
import com.gregtechceu.gtceu.api.gui.widgets.LabelWidget;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.common.metatileentities.electric.MetaTileEntitySingleCombustion;
import io.github.symmetricdevs.supersymmetry.api.capability.impl.SuSyFluidFilters;
import io.github.symmetricdevs.supersymmetry.api.fluids.FilteredTankWidget;
import io.github.symmetricdevs.supersymmetry.api.fluids.SuSyFluidTankHandler;
import io.github.symmetricdevs.supersymmetry.api.util.SuSyUtility;

public class SuSyMetaTileEntitySingleCombustion extends MetaTileEntitySingleCombustion {

    private int workCounter;
    private boolean isFull;

    private SuSyUtility.Lubricant lubricant;
    private SuSyUtility.Coolant coolant;

    private boolean sufficientFluids;

    private SuSyFluidTankHandler lubricantTank;
    private SuSyFluidTankHandler coolantTank;

    private FluidTankList displayedTankList;

    public SuSyMetaTileEntitySingleCombustion(ResourceLocation metaTileEntityId, GTRecipeType<?> GTRecipeType,
                                              ICubeRenderer renderer, int tier,
                                              Function<Integer, Integer> tankScalingFunction) {
        super(metaTileEntityId, GTRecipeType, renderer, tier, tankScalingFunction);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new SuSyMetaTileEntitySingleCombustion(metaTileEntityId, GTRecipeType, renderer, this.getTier(),
                this.getTankScalingFunction());
    }

    @Override
    protected FluidTankList createImportFluidHandler() {
        if (workable == null) return new FluidTankList(false);

        FluidTank[] fluidImports = new FluidTank[workable.getRecipeMap().getMaxFluidInputs()];
        FluidTank[] displayedTanks = new FluidTank[workable.getRecipeMap().getMaxFluidInputs()];
        for (int i = 0; i < fluidImports.length; i++) {
            NotifiableFluidTank tank = new NotifiableFluidTank(
                    this.getTankScalingFunction().apply(this.getTier()), this, false);
            fluidImports[i] = tank;
            displayedTanks[i] = tank;
        }

        this.lubricantTank = (SuSyFluidTankHandler) new SuSyFluidTankHandler(1000, this, false)
                .setFilter(SuSyFluidFilters.LUBRICANT);
        this.coolantTank = (SuSyFluidTankHandler) new SuSyFluidTankHandler(1000, this, false)
                .setFilter(SuSyFluidFilters.COOLANT);

        this.displayedTankList = new FluidTankList(false, displayedTanks);

        List<FluidTank> allTanks = new ArrayList<>(Arrays.asList(fluidImports));
        allTanks.add(lubricantTank);
        allTanks.add(coolantTank);
        return new FluidTankList(false, allTanks.toArray(new FluidTank[0]));
    }

    @Override
    // Override recipe logic
    protected CombustionRecipeLogic createWorkable(GTRecipeType<?> GTRecipeType) {
        return new CombustionRecipeLogic(this, GTRecipeType, () -> this.energyContainer);
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote) {
            updateSufficientFluids();
            isFull = energyContainer.getEnergyStored() >= energyContainer.getEnergyCapacity();

            if (workable.isWorking() && !isFull) workCounter += 1;
            if (workCounter == 600) {
                workCounter = 0;

                lubricantTank.drain((int) (lubricant.amount_required * Math.pow(4, getTier() - 1)), true);
                coolantTank.drain((int) (coolant.amount_required * Math.pow(4, getTier() - 1)), true);
            }
        }
    }

    protected void updateSufficientFluids() {
        // Check coolant & lubricant levels, activity
        FluidStack lubricantStack = lubricantTank.drain(Integer.MAX_VALUE, false);
        FluidStack coolantStack = coolantTank.drain(Integer.MAX_VALUE, false);

        lubricant = lubricantStack == null ? null : SuSyUtility.lubricants.get(lubricantStack.getFluid().getName());
        coolant = coolantStack == null ? null : SuSyUtility.coolants.get(coolantStack.getFluid().getName());

        if (lubricant == null || coolant == null) {
            sufficientFluids = false;
            return;
        }

        sufficientFluids = lubricantStack.amount >= lubricant.amount_required &&
                coolantStack.amount >= coolant.amount_required;
    }

    @Override
    // Create GUI template for the combustion generator
    protected ModularUI.Builder createGuiTemplate(Player player) {
        GTRecipeType<?> workableRecipeMap = workable.getRecipeMap();
        int yOffset = 15;

        ModularUI.Builder builder;
        builder = workableRecipeMap.createUITemplateNoOutputs(workable::getProgressPercent, importItems,
                exportItems, displayedTankList, exportFluids, yOffset);
        builder.widget(new LabelWidget(6, 6, getMetaFullName()))
                .bindPlayerInventory(player.inventory, GuiTextures.SLOT, yOffset);

        builder.widget(new FilteredTankWidget(lubricantTank, 110, 21, 10, 54)
                .setBackgroundTexture(GuiTextures.PROGRESS_BAR_BOILER_EMPTY.get(true))
                .setAlwaysShowFull(false)
                .setContainerClicking(true, true));  // both directions, filter guards filling
        builder.widget(new FilteredTankWidget(coolantTank, 124, 21, 10, 54)
                .setBackgroundTexture(GuiTextures.PROGRESS_BAR_BOILER_EMPTY.get(true))
                .setAlwaysShowFull(false)
                .setContainerClicking(true, true));
        builder.widget(new ImageWidget(152, 63 + yOffset, 17, 17,
                GTValues.XMAS.get() ? GuiTextures.GREGTECH_LOGO_XMAS : GuiTextures.GREGTECH_LOGO)
                        .setIgnoreColor(true));

        return builder;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("susy.machine.combustion_generator.tooltip"));
    }

    private class CombustionRecipeLogic extends FuelRecipeLogic {

        public CombustionRecipeLogic(SuSyMetaTileEntitySingleCombustion MetaMachine, GTRecipeType<?> GTRecipeType,
                                     Supplier<IEnergyContainer> energyContainer) {
            super(MetaMachine, GTRecipeType, energyContainer);
        }

        @Override
        public boolean checkRecipe(@NotNull Recipe recipe) {
            return sufficientFluids && !isFull;
        }

        @Override
        public boolean isWorking() {
            return sufficientFluids && !isFull && super.isWorking();
        }

        @Override
        protected void updateRecipeProgress() {
            if (canRecipeProgress && drawEnergy(recipeEUt, true)) {
                drawEnergy(recipeEUt, false);
                // as recipe starts with progress on 1 this has to be > only not => to compensate for it
                if (++progressTime > getMaxProgress()) {
                    completeRecipe();
                }
                if (this.hasNotEnoughEnergy && getEnergyInputPerSecond() > 19L * recipeEUt) {
                    this.hasNotEnoughEnergy = false;
                }
            } else if (recipeEUt > 0) {
                // only set hasNotEnoughEnergy if this recipe is consuming recipe
                // generators always have enough energy
                this.hasNotEnoughEnergy = true;
                decreaseProgress();
            }
        }

        @Override
        public int getMaxProgress() {
            int baseDuration = super.getMaxProgress();

            if (lubricant != null) {
                return (int) (baseDuration * lubricant.boost);
            }

            return baseDuration;
        }
    }
}
