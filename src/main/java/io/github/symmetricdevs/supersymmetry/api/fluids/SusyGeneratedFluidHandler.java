package io.github.symmetricdevs.supersymmetry.api.fluids;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.transfer.fluid.IFluidHandlerModifiable;

import io.github.symmetricdevs.supersymmetry.api.unification.material.info.SuSyMaterialFlags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides an {@link IFluidHandlerModifiable} wrapper around a single
 * {@link FluidStack} with a fixed capacity.
 */
public class SusyGeneratedFluidHandler implements IFluidHandlerModifiable {

    public static final List<Material> CAST_MATERIALS = new ArrayList<>();

    private FluidStack fluid;
    private final long capacity;

    public SusyGeneratedFluidHandler(long capacity) {
        this.fluid = FluidStack.EMPTY;
        this.capacity = capacity;
    }

    public static void init() {
        // Previously iterated registered materials to enqueue molten fluids;
        // in modern GTCEu-Modern, fluid registration is handled by the material
        // property system directly. This holder remains for the CAST_MATERIALS list.
    }

    public static void createMoltenFluid(@NotNull Material material) {
        FluidProperty fluidProperty = material.getProperty(PropertyKey.FLUID);
        if (fluidProperty == null) return;

        if (material.hasFlag(SuSyMaterialFlags.CONTINUOUSLY_CAST)) {
            CAST_MATERIALS.add(material);
        }
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
        return true;
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource == null || resource.isEmpty()) return 0;
        if (!fluid.isEmpty() && !fluid.isFluidEqual(resource)) return 0;

        int amount = (int) Math.min(resource.getAmount(), capacity - fluid.getAmount());
        if (action.execute()) {
            if (fluid.isEmpty()) {
                fluid = resource.copy();
                fluid.setAmount(amount);
            } else {
                fluid.grow(amount);
            }
        }
        return amount;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource == null || resource.isEmpty()) return FluidStack.EMPTY;
        if (!resource.isFluidEqual(fluid)) return FluidStack.EMPTY;
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
        }
        return result;
    }

    // --- IFluidHandlerModifiable ---

    @Override
    public void setFluidInTank(int tank, FluidStack stack) {
        this.fluid = stack.copy();
    }
}
