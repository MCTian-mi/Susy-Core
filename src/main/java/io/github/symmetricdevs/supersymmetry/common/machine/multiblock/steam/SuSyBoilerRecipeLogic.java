package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.steam;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.transfer.fluid.IFluidHandlerModifiable;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import io.github.symmetricdevs.supersymmetry.common.data.SuSyRecipeTypes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Custom {@link RecipeLogic} for the SuSy large boiler.
 *
 * <p>
 * It burns solid fuel from {@link SuSyRecipeTypes#BOILER_RECIPES} or fluid fuel
 * from {@link GTRecipeTypes#LARGE_BOILER_RECIPES}. While fuel is burning the boiler
 * heats up; steam output scales with current heat. Water is consumed every tick
 * and converted into steam; running dry causes an explosion. Throttle clamps
 * between 25% and 100% and affects both burn time and steam output.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SuSyBoilerRecipeLogic extends RecipeLogic {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SuSyBoilerRecipeLogic.class, RecipeLogic.MANAGED_FIELD_HOLDER);

    private final SuSyBoilerType boilerType;

    @Persisted
    @DescSynced
    private int currentHeat;
    @Persisted
    @DescSynced
    private int lastTickSteamOutput;
    @Persisted
    private int excessWater;
    @Persisted
    private int excessProjectedEU;
    @Persisted
    @DescSynced
    private int currentThrottle = 100;
    @Persisted
    private int baseFuelBurnTime;

    public SuSyBoilerRecipeLogic(IRecipeLogicMachine machine, SuSyBoilerType boilerType) {
        super(machine);
        this.boilerType = boilerType;
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void updateTickSubscription() {
        if (isSuspend() || !machine.isRecipeLogicAvailable()) {
            if (subscription != null) {
                subscription.unsubscribe();
                subscription = null;
            }
        } else {
            subscription = getMachine().subscribeServerTick(subscription, this::serverTick);
        }
    }

    @Override
    public void serverTick() {
        if (isSuspend() || !machine.isRecipeLogicAvailable()) return;
        if (getMachine().isInValid()) {
            resetRecipeLogic();
            return;
        }

        boolean canProgress = canProgressRecipe();
        if (currentHeat > 0 && (!isActive() || !canProgress)) {
            setHeat(currentHeat - 1);
            setLastTickSteam(0);
        }

        if (isWorking() && duration > 0) {
            updateRecipeProgress(canProgress);
        } else {
            trySearchNewRecipe();
        }
    }

    private boolean canProgressRecipe() {
        return machine.isRecipeLogicAvailable() && !getMachine().isInValid();
    }

    private void updateRecipeProgress(boolean canProgress) {
        if (!canProgress) return;

        int generatedSteam = getCurrentSteamOutput() * getEffectiveMaxHeat() / getMaxHeat();
        if (generatedSteam > 0) {
            int steamPerWater = ConfigHolder.INSTANCE.machines.largeBoilers.steamPerWater;
            long amount = (generatedSteam + steamPerWater) / steamPerWater;
            excessWater += (int) (amount * steamPerWater - generatedSteam);
            amount -= excessWater / steamPerWater;
            excessWater %= steamPerWater;

            int drained = drainBoilerFluid((int) amount);
            if (amount != 0 && drained < amount) {
                explode();
            } else {
                fillSteam(generatedSteam);
                setLastTickSteam(generatedSteam);
            }
        }

        if (currentHeat < getMaxHeat()) {
            setHeat(currentHeat + 1);
        }

        progress++;
        if (progress >= duration) {
            onRecipeFinish();
        }
    }

    @Override
    public void setupRecipe(GTRecipe recipe) {
        if (!machine.beforeWorking(recipe)) {
            setStatus(Status.IDLE);
            progress = 0;
            duration = 0;
            isActive = false;
            return;
        }
        var result = handleRecipeIO(recipe, IO.IN);
        if (result.isSuccess()) {
            failureReasonMap.clear();
            recipeDirty = false;
            lastRecipe = recipe;
            setStatus(Status.WORKING);
            progress = 0;
            isActive = true;
            baseFuelBurnTime = boilerType.runtimeBoost(recipe.duration * 96 / boilerType.steamPerTick());
            currentThrottle = getController().getThrottle();
            duration = adjustBurnTimeForThrottle(baseFuelBurnTime);
        }
    }

    @Override
    public void onRecipeFinish() {
        machine.afterWorking();
        runAttempt = 0;
        runDelay = 0;
        progress = 0;
        duration = 0;
        baseFuelBurnTime = 0;
        isActive = false;
        lastRecipe = null;
        if (getStatus() != Status.SUSPEND) {
            setStatus(Status.IDLE);
        }
    }

    @Override
    public void resetRecipeLogic() {
        super.resetRecipeLogic();
        currentHeat = 0;
        lastTickSteamOutput = 0;
        excessWater = 0;
        excessProjectedEU = 0;
        baseFuelBurnTime = 0;
        currentThrottle = 100;
    }

    public void onThrottleChanged(int newThrottle) {
        if (baseFuelBurnTime > 0) {
            double multiplier = (double) currentThrottle / newThrottle;
            duration = (int) Math.round(baseFuelBurnTime / (newThrottle / 100.0));
            progress = (int) Math.round(multiplier * progress);
        }
        currentThrottle = Mth.clamp(newThrottle, 25, 100);
    }

    private void trySearchNewRecipe() {
        if (hasTooManyMaintenanceProblems()) return;

        Iterator<GTRecipe> fluidMatches = GTRecipeTypes.LARGE_BOILER_RECIPES.searchRecipe(machine, r -> true);
        while (fluidMatches.hasNext()) {
            GTRecipe match = fluidMatches.next();
            if (RecipeHelper.matchRecipe(machine, match).isSuccess()) {
                setupRecipe(match);
                return;
            }
        }

        Iterator<GTRecipe> solidMatches = SuSyRecipeTypes.BOILER_RECIPES.searchRecipe(machine, r -> true);
        while (solidMatches.hasNext()) {
            GTRecipe match = solidMatches.next();
            if (RecipeHelper.matchRecipe(machine, match).isSuccess()) {
                setupRecipe(match);
                return;
            }
        }
    }

    private boolean hasTooManyMaintenanceProblems() {
        if (!ConfigHolder.INSTANCE.machines.enableMaintenance) return false;
        int problems = 0;
        for (IMultiPart part : getController().getParts()) {
            if (part instanceof IMaintenanceMachine maintenance) {
                problems = Math.max(problems, maintenance.getNumMaintenanceProblems());
            }
        }
        return problems > 5;
    }

    private int getCurrentSteamOutput() {
        return Math.max(25, (int) (boilerType.steamPerTick() * (currentThrottle / 100.0)));
    }

    private int getEffectiveMaxHeat() {
        int maxHeat = getMaxHeat();
        if (!ConfigHolder.INSTANCE.machines.enableMaintenance) return Math.min(currentHeat, maxHeat);
        int problems = 0;
        for (IMultiPart part : getController().getParts()) {
            if (part instanceof IMaintenanceMachine maintenance) {
                problems = Math.max(problems, maintenance.getNumMaintenanceProblems());
            }
        }
        return (int) Math.min(currentHeat, (1 - 0.1 * problems) * maxHeat);
    }

    private int getMaxHeat() {
        return boilerType.getTicksToBoiling();
    }

    public int getHeatScaled() {
        return (int) Math.round(currentHeat / (1.0 * getMaxHeat()) * 100);
    }

    public int getLastTickSteamOutput() {
        return lastTickSteamOutput;
    }

    private void setHeat(int heat) {
        this.currentHeat = Mth.clamp(heat, 0, getMaxHeat());
    }

    private void setLastTickSteam(int output) {
        this.lastTickSteamOutput = output;
    }

    private int adjustBurnTimeForThrottle(int rawBurnTime) {
        int rawEUt = boilerType.steamPerTick();
        int adjustedEUt = getCurrentSteamOutput();
        if (adjustedEUt <= 0) return rawBurnTime;
        int adjustedBurnTime = rawBurnTime * rawEUt / adjustedEUt;
        excessProjectedEU += rawEUt * rawBurnTime - adjustedEUt * adjustedBurnTime;
        adjustedBurnTime += excessProjectedEU / adjustedEUt;
        excessProjectedEU %= adjustedEUt;
        return adjustedBurnTime;
    }

    private int drainBoilerFluid(int amount) {
        if (amount <= 0) return 0;
        int drained = 0;
        for (IRecipeHandler<?> handler : getInputHandlers(IO.IN, FluidRecipeCapability.CAP)) {
            if (handler instanceof IFluidHandlerModifiable tank) {
                FluidStack water = tank.drain(new FluidStack(Fluids.WATER, amount - drained), IFluidHandler.FluidAction.EXECUTE);
                if (water != null && !water.isEmpty()) {
                    drained += water.getAmount();
                }
                if (drained < amount) {
                    FluidStack distilled = tank.drain(new FluidStack(GTMaterials.DistilledWater.getFluid(), amount - drained), IFluidHandler.FluidAction.EXECUTE);
                    if (distilled != null && !distilled.isEmpty()) {
                        drained += distilled.getAmount();
                    }
                }
                if (drained >= amount) break;
            }
        }
        return drained;
    }

    private void fillSteam(int amount) {
        if (amount <= 0) return;
        FluidStack steam = GTMaterials.Steam.getFluid(amount);
        for (IRecipeHandler<?> handler : getInputHandlers(IO.OUT, FluidRecipeCapability.CAP)) {
            if (handler instanceof IFluidHandlerModifiable tank) {
                int filled = tank.fill(steam, IFluidHandler.FluidAction.EXECUTE);
                steam.shrink(filled);
                if (steam.isEmpty()) break;
            }
        }
    }

    private List<IRecipeHandler<?>> getInputHandlers(IO io, FluidRecipeCapability cap) {
        List<IRecipeHandler<?>> handlers = new ArrayList<>();
        handlers.addAll(getController().getCapabilitiesFlat(io, cap));
        if (io != IO.BOTH) {
            handlers.addAll(getController().getCapabilitiesFlat(IO.BOTH, cap));
        }
        return handlers;
    }

    private void explode() {
        float strength = (1.0f * currentHeat / getMaxHeat()) * 8.0f;
        getController().doExplosion(strength);
    }

    private SuSyLargeBoilerMachine getController() {
        return (SuSyLargeBoilerMachine) machine.self();
    }
}
