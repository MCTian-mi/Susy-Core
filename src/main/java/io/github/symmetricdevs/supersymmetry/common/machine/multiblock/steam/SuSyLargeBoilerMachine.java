package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.steam;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IExplosionMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.transfer.fluid.IFluidHandlerModifiable;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import io.github.symmetricdevs.supersymmetry.api.machine.multiblock.IRedstoneControllable;
import io.github.symmetricdevs.supersymmetry.common.data.SuSyRecipeTypes;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntitySuSyLargeBoiler}.
 * <p>
 * It is a {@link WorkableMultiblockMachine} with a custom {@link SuSyBoilerRecipeLogic}
 * that burns solid fuel ({@link SuSyRecipeTypes#BOILER_RECIPES}) or fluid fuel
 * ({@link GTRecipeTypes#LARGE_BOILER_RECIPES}) to heat water into steam. Throttle can be
 * adjusted through the UI or by a redstone-controller multiblock part.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SuSyLargeBoilerMachine extends WorkableMultiblockMachine
        implements IExplosionMachine, IDisplayUIMachine, IRedstoneControllable {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SuSyLargeBoilerMachine.class, WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    private static final List<String> SIGNAL_NAMES;
    private static final List<Consumer<SuSyLargeBoilerMachine>> SIGNAL_OPS;

    static {
        List<Integer> steps = List.of(1, 5, 10, 25);
        SIGNAL_NAMES = new ArrayList<>();
        SIGNAL_OPS = new ArrayList<>();

        SIGNAL_NAMES.add("set25");
        SIGNAL_NAMES.add("set100");
        SIGNAL_OPS.add(self -> self.throttlePercentage = 25);
        SIGNAL_OPS.add(self -> self.throttlePercentage = 100);

        for (int i : steps) {
            SIGNAL_NAMES.add("incr" + i);
            SIGNAL_NAMES.add("dec" + i);
            int step = i;
            SIGNAL_OPS.add(self -> self.setThrottle(self.throttlePercentage + step));
            SIGNAL_OPS.add(self -> self.setThrottle(self.throttlePercentage - step));
        }
    }

    public final SuSyBoilerType boilerType;

    @Persisted
    @DescSynced
    private int throttlePercentage = 100;

    public SuSyLargeBoilerMachine(IMachineBlockEntity holder, SuSyBoilerType boilerType) {
        super(holder);
        this.boilerType = boilerType;
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new SuSyBoilerRecipeLogic(this, boilerType);
    }

    @Override
    public SuSyBoilerRecipeLogic getRecipeLogic() {
        return (SuSyBoilerRecipeLogic) super.getRecipeLogic();
    }

    public SuSyBoilerType getBoilerType() {
        return boilerType;
    }

    public int getThrottle() {
        return throttlePercentage;
    }

    public void setThrottle(int throttle) {
        this.throttlePercentage = Mth.clamp(throttle, 25, 100);
        getRecipeLogic().onThrottleChanged(throttlePercentage);
    }

    @Override
    public GTRecipeType[] getRecipeTypes() {
        return new GTRecipeType[] { SuSyRecipeTypes.BOILER_RECIPES, GTRecipeTypes.LARGE_BOILER_RECIPES };
    }

    /**
     * Dummy recipe modifier required by the registrate DSL; real burn-time scaling lives
     * in {@link SuSyBoilerRecipeLogic#onThrottleChanged(int)}.
     */
    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        return ModifierFunction.IDENTITY;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        IDisplayUIMachine.super.addDisplayText(textList);
        if (isFormed()) {
            var logic = getRecipeLogic();
            int steam = logic.getLastTickSteamOutput();
            int efficiency = logic.getHeatScaled();
            textList.add(Component.translatable("gtceu.multiblock.large_boiler.steam_output",
                    Component.literal(String.valueOf(steam)).withStyle(ChatFormatting.AQUA)));
            textList.add(Component.translatable("gtceu.multiblock.large_boiler.efficiency",
                    Component.literal(efficiency + "%").withStyle(getNumberColor(efficiency))));
            textList.add(Component.translatable("gtceu.multiblock.large_boiler.throttle",
                    Component.literal(throttlePercentage + "%").withStyle(getNumberColor(throttlePercentage))));

            int[] water = getWaterAmount();
            if (water[0] == 0) {
                textList.add(Component.translatable("gtceu.multiblock.large_boiler.no_water")
                        .withStyle(ChatFormatting.YELLOW));
                textList.add(Component.translatable("gtceu.multiblock.large_boiler.explosion_tooltip")
                        .withStyle(ChatFormatting.GRAY));
            } else {
                textList.add(Component.translatable("susy.multiblock.large_boiler.water_amount",
                        Component.literal(water[0] + " / " + water[1] + " L").withStyle(ChatFormatting.BLUE)));
            }
        }
    }

    private ChatFormatting getNumberColor(int number) {
        if (number == 0) return ChatFormatting.DARK_RED;
        if (number <= 40) return ChatFormatting.RED;
        if (number < 100) return ChatFormatting.YELLOW;
        return ChatFormatting.GREEN;
    }

    @Override
    public void handleDisplayClick(String componentData, ClickData clickData) {
        if (!clickData.isRemote) {
            int delta = "add".equals(componentData) ? 5 : -5;
            setThrottle(throttlePercentage + delta);
        }
    }

    @Override
    public IGuiTexture getScreenTexture() {
        return GuiTextures.DISPLAY;
    }

    @Override
    public List<String> getSignals() {
        return SIGNAL_NAMES;
    }

    @Override
    public String getSignalName(int sig) {
        if (sig >= 0 && sig < SIGNAL_NAMES.size()) {
            return SIGNAL_NAMES.get(sig);
        }
        return "";
    }

    @Override
    public void pulse(int sig) {
        if (sig >= 0 && sig < SIGNAL_OPS.size()) {
            SIGNAL_OPS.get(sig).accept(this);
        }
    }

    /**
     * Returns {@code {filled, capacity}} of all import fluid hatches that currently hold
     * a valid boiler fluid (water or distilled water).
     */
    public int[] getWaterAmount() {
        if (!isFormed()) return new int[] { 0, 0 };
        int filled = 0;
        int capacity = 0;
        for (IRecipeHandler<?> handler : getFluidInputHandlers()) {
            if (handler instanceof IFluidHandlerModifiable tank) {
                for (int i = 0; i < tank.getTanks(); i++) {
                    FluidStack fluid = tank.getFluidInTank(i);
                    if (isBoilerFluid(fluid)) {
                        filled += fluid.getAmount();
                        capacity += tank.getTankCapacity(i);
                    }
                }
            }
        }
        return new int[] { filled, capacity };
    }

    public static boolean isBoilerFluid(FluidStack fluid) {
        if (fluid == null || fluid.isEmpty()) return false;
        return fluid.getFluid().isSame(Fluids.WATER) || fluid.getFluid().isSame(GTMaterials.DistilledWater.getFluid());
    }

    public List<IRecipeHandler<?>> getFluidInputHandlers() {
        List<IRecipeHandler<?>> handlers = new ArrayList<>();
        handlers.addAll(getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP));
        handlers.addAll(getCapabilitiesFlat(IO.BOTH, FluidRecipeCapability.CAP));
        return handlers;
    }
}
