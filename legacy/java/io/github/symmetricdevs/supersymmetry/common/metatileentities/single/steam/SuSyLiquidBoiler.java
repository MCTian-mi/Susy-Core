package io.github.symmetricdevs.supersymmetry.common.metatileentities.single.steam;

import java.util.Collections;

import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.GTValues;

import com.gregtechceu.gtceu.api.capability.FilteredFluidHandler;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.ModularUI;
import com.gregtechceu.gtceu.api.gui.widgets.TankWidget;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.client.particle.VanillaParticleEffects;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;

import com.gregtechceu.gtceu.common.metatileentities.steam.boiler.SteamBoiler;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import io.github.symmetricdevs.supersymmetry.api.capability.impl.SuSyBoilerLogic;

public class SuSyLiquidBoiler extends SteamBoiler {

    private static final Object2IntMap<Fluid> BOILER_FUEL_TO_CONSUMPTION = new Object2IntOpenHashMap<>();
    private static boolean initialized;

    private static final IFilter<FluidStack> FUEL_FILTER = new IFilter<>() {

        @Override
        public boolean test(@NotNull FluidStack fluidStack) {
            return !fluidStack.getFluid().isGaseous() && SuSyBoilerLogic.BOILER_FUEL.test(fluidStack);
        }

        @Override
        public int getPriority() {
            return IFilter.whitelistPriority(RecipeMaps.SEMI_FLUID_GENERATOR_FUELS.getRecipeList().size());
        }
    };

    private FluidTank fuelFluidTank;

    public SuSyLiquidBoiler(ResourceLocation metaTileEntityId, boolean isHighPressure) {
        super(metaTileEntityId, isHighPressure, Textures.LAVA_BOILER_OVERLAY);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new SuSyLiquidBoiler(metaTileEntityId, isHighPressure);
    }

    @Override
    protected FluidTankList createImportFluidHandler() {
        FluidTankList superHandler = super.createImportFluidHandler();
        this.fuelFluidTank = new FilteredFluidHandler(16000).setFilter(FUEL_FILTER);
        return new FluidTankList(false, superHandler, fuelFluidTank);
    }

    @Override
    protected void tryConsumeNewFuel() {
        FluidStack fluid = fuelFluidTank.getFluid();
        if (fluid == null || fluid.tag != null) { // fluid with nbt tag cannot match normal fluids
            return;
        }
        Recipe fluidFuelRecipe = RecipeMaps.SEMI_FLUID_GENERATOR_FUELS.findRecipe(
                GTValues.V[GTValues.MAX], NonNullList.create(), Collections.singletonList(fluid));
        if (fluidFuelRecipe == null) {
            return;
        }
        int consumption = fluidFuelRecipe.getFluidInputs().get(0).getInputFluidStack().amount;
        if (consumption > 0 && fuelFluidTank.getFluidAmount() >= consumption) {
            fuelFluidTank.drain(consumption, true);
            // 1920 = 96L/t (1A LV, default for recipes) * 20t/s
            int burnTime = fluidFuelRecipe.getDuration() * 1920 / this.getBaseSteamOutput();
            burnTime = SuSyCoalBoiler.modifyBurnTime(burnTime, isHighPressure);
            setFuelMaxBurnTime(burnTime);
        }
    }

    @Override
    protected int getCooldownInterval() {
        return isHighPressure ? 40 : 45;
    }

    @Override
    protected int getCoolDownRate() {
        return 1;
    }

    @Override
    protected ModularUI createUI(Player Player) {
        return createUITemplate(Player)
                .widget(new TankWidget(fuelFluidTank, 119, 26, 10, 54)
                        .setBackgroundTexture(GuiTextures.PROGRESS_BAR_BOILER_EMPTY.get(isHighPressure)))
                .build(getHolder(), Player);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick() {
        if (this.isActive()) {
            VanillaParticleEffects.RANDOM_LAVA_SMOKE.runEffect(this);
            if (ConfigHolder.machines.machineSounds && GTValues.RNG.nextDouble() < 0.1) {
                BlockPos pos = getPos();
                getWorld().playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                        SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
        }
    }

    @Override
    protected int getBaseSteamOutput() {
        // 48/96 L per tick
        return isHighPressure ? 1920 : 960;
    }
}
