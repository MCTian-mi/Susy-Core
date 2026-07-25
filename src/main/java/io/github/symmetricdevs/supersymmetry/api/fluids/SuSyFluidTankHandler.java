package io.github.symmetricdevs.supersymmetry.api.fluids;

import com.gregtechceu.gtceu.api.transfer.fluid.IFluidHandlerModifiable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * A simple fluid tank handler implementing {@link IFluidHandler}
 * with internal {@link FluidStack} storage and a fixed capacity.
 * Optionally supports a filter predicate and notifiable changes.
 */
public class SuSyFluidTankHandler implements IFluidHandlerModifiable {

    private FluidStack fluid;
    private final long capacity;
    private Predicate<FluidStack> filter;
    private Runnable onContentsChanged;

    public SuSyFluidTankHandler(long capacity) {
        this.fluid = FluidStack.EMPTY;
        this.capacity = capacity;
        this.filter = stack -> true;
        this.onContentsChanged = () -> {};
    }

    public SuSyFluidTankHandler setFilter(Predicate<FluidStack> filter) {
        this.filter = filter;
        return this;
    }

    public SuSyFluidTankHandler setOnContentsChanged(Runnable onContentsChanged) {
        this.onContentsChanged = onContentsChanged;
        return this;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    // --- IFluidHandler ---

    @Override
    public int getTanks() {
        return 1;
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return fluid.copy();
    }

    @Override
    public int getTankCapacity(int tank) {
        return (int) Math.min(capacity, Integer.MAX_VALUE);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return filter.test(stack);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource == null || resource.isEmpty() || !filter.test(resource)) return 0;
        if (!fluid.isEmpty() && !fluid.isFluidEqual(resource)) return 0;

        int amount = (int) Math.min(resource.getAmount(), capacity - fluid.getAmount());
        if (action.execute()) {
            if (fluid.isEmpty()) {
                fluid = resource.copy();
                fluid.setAmount(amount);
            } else {
                fluid.grow(amount);
            }
            onContentsChanged.run();
        }
        return amount;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource == null || resource.isEmpty() || !resource.isFluidEqual(fluid))
            return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (fluid.isEmpty()) return FluidStack.EMPTY;

        int drained = Math.min(maxDrain, fluid.getAmount());
        FluidStack result = fluid.copy();
        result.setAmount(drained);

        if (action.execute()) {
            fluid.shrink(drained);
            if (fluid.isEmpty()) {
                fluid = FluidStack.EMPTY;
            }
            onContentsChanged.run();
        }
        return result;
    }

    // --- IFluidHandlerModifiable ---

    @Override
    public void setFluidInTank(int tank, FluidStack stack) {
        this.fluid = stack.copy();
        onContentsChanged.run();
    }
}
