package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.strand;

import com.gregtechceu.gtceu.api.transfer.fluid.IFluidHandlerModifiable;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Simple combined fluid handler for strand-casting controllers that need to treat all
 * import (or export) fluid hatches as one tank list.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FluidTankList implements IFluidHandlerModifiable {

    private final List<? extends IFluidHandlerModifiable> handlers;

    public FluidTankList(List<? extends IFluidHandlerModifiable> handlers) {
        this.handlers = handlers;
    }

    @Override
    public int getTanks() {
        int count = 0;
        for (var handler : handlers) {
            count += handler.getTanks();
        }
        return count;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        int[] ref = globalToLocal(tank);
        return handlers.get(ref[0]).getFluidInTank(ref[1]);
    }

    @Override
    public int getTankCapacity(int tank) {
        int[] ref = globalToLocal(tank);
        return handlers.get(ref[0]).getTankCapacity(ref[1]);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        int[] ref = globalToLocal(tank);
        return handlers.get(ref[0]).isFluidValid(ref[1], stack);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        int total = 0;
        FluidStack remaining = resource.copy();
        for (var handler : handlers) {
            int filled = handler.fill(remaining, action);
            total += filled;
            remaining.shrink(filled);
            if (remaining.isEmpty()) break;
        }
        return total;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        FluidStack total = FluidStack.EMPTY;
        FluidStack toDrain = resource.copy();
        for (var handler : handlers) {
            FluidStack drained = handler.drain(toDrain, action);
            if (!drained.isEmpty()) {
                if (total.isEmpty()) {
                    total = drained.copy();
                } else {
                    total.grow(drained.getAmount());
                }
                toDrain.shrink(drained.getAmount());
                if (toDrain.isEmpty()) break;
            }
        }
        return total;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        FluidStack total = FluidStack.EMPTY;
        int remaining = maxDrain;
        for (var handler : handlers) {
            FluidStack drained = handler.drain(remaining, action);
            if (!drained.isEmpty()) {
                if (total.isEmpty()) {
                    total = drained.copy();
                } else {
                    total.grow(drained.getAmount());
                }
                remaining -= drained.getAmount();
                if (remaining <= 0) break;
            }
        }
        return total;
    }

    @Override
    public void setFluidInTank(int tank, FluidStack fluid) {
        int[] ref = globalToLocal(tank);
        handlers.get(ref[0]).setFluidInTank(ref[1], fluid);
    }

    private int[] globalToLocal(int globalTank) {
        int remaining = globalTank;
        for (int i = 0; i < handlers.size(); i++) {
            int tanks = handlers.get(i).getTanks();
            if (remaining < tanks) {
                return new int[] { i, remaining };
            }
            remaining -= tanks;
        }
        throw new IndexOutOfBoundsException(globalTank);
    }
}
