package io.github.symmetricdevs.supersymmetry.api.fluids;

import com.gregtechceu.gtceu.api.gui.widget.TankWidget;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * A {@link TankWidget} that only allows fluids matching a given
 * {@link Predicate} to be filled into its attached handler.
 * <p>
 * Delegates to the wrapped handler after applying the filter.
 */
public class FilteredTankWidget extends TankWidget {

    private final Predicate<FluidStack> filter;

    public FilteredTankWidget(Predicate<FluidStack> filter, IFluidHandler handler,
                              int x, int y, boolean allowClickDrain, boolean allowClickFill) {
        super(new FilteredHandler(handler, filter), x, y, 18, 18, allowClickFill, allowClickDrain);
        this.filter = filter;
    }

    private static class FilteredHandler implements IFluidHandler {

        private final IFluidHandler delegate;
        private final Predicate<FluidStack> filter;

        private FilteredHandler(IFluidHandler delegate, Predicate<FluidStack> filter) {
            this.delegate = delegate;
            this.filter = filter;
        }

        @Override
        public int getTanks() {
            return delegate.getTanks();
        }

        @NotNull
        @Override
        public FluidStack getFluidInTank(int tank) {
            return delegate.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return delegate.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return filter.test(stack) && delegate.isFluidValid(tank, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource == null || resource.isEmpty() || !filter.test(resource)) return 0;
            return delegate.fill(resource, action);
        }

        @NotNull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return delegate.drain(resource, action);
        }

        @NotNull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return delegate.drain(maxDrain, action);
        }
    }
}
