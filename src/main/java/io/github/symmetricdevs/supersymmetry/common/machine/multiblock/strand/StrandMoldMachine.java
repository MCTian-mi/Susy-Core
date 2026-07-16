package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.strand;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import io.github.symmetricdevs.supersymmetry.api.capability.Strand;
import io.github.symmetricdevs.supersymmetry.api.unification.material.info.SuSyMaterialFlags;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityStrandMold}: consumes molten metal and
 * coolant to produce a strand of fixed dimensions.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class StrandMoldMachine extends StrandShaperMachine {

    private static final FluidStack COOLANT = GTMaterials.Water.getFluid(50);
    private static final FluidStack HOT_COOLANT = GTMaterials.Steam.getFluid(50 * 960);

    public StrandMoldMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    protected abstract int getRequiredMetal();

    protected abstract double getOutputThickness();

    protected abstract double getOutputWidth();

    @Override
    protected boolean consumeInputsAndSetupRecipe() {
        FluidStack stack = getFirstMaterialFluid();
        if (stack == null || stack.getAmount() < getRequiredMetal()) return false;
        FluidStack drain = stack.copy();
        drain.setAmount(getRequiredMetal());
        if (inputFluidInventory == null) return false;
        inputFluidInventory.drain(drain, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        inputFluidInventory.drain(COOLANT, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        this.maxProgress = 40;
        return true;
    }

    @Override
    protected @Nullable Strand resultingStrand() {
        FluidStack stack = getFirstMaterialFluid();
        if (stack == null || stack.getAmount() < getRequiredMetal()) {
            return null;
        }
        Material mat = getMaterialFromFluid(stack);
        if (mat == null || !mat.hasProperty(PropertyKey.INGOT) || !mat.hasFlag(SuSyMaterialFlags.CONTINUOUSLY_CAST)) {
            return null;
        }

        Fluid molten = mat.getFluid(FluidStorageKeys.MOLTEN);
        if (molten != null) {
            if (stack.getFluid() != molten) {
                return null;
            }
        } else {
            Fluid liquid = mat.getFluid();
            if (liquid == null || stack.getFluid() != liquid) {
                return null;
            }
        }

        if (inputFluidInventory == null) return null;
        FluidStack coolant = inputFluidInventory.drain(COOLANT, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
        if (coolant == null || coolant.getAmount() < 50) {
            return null;
        }
        return new Strand(getOutputThickness(), getOutputWidth(), false, mat, stack.getFluid().getFluidType().getTemperature());
    }

    @Override
    protected boolean hasRoom() {
        return super.hasRoom() && canOutputHotCoolant();
    }

    private boolean canOutputHotCoolant() {
        return outputFluidInventory != null && outputFluidInventory.fill(HOT_COOLANT,
                net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE) == HOT_COOLANT.getAmount();
    }

    @Override
    protected boolean output() {
        if (getStrand() == null || output == null || output.getStrand() != null || !canOutputHotCoolant()) {
            return false;
        }
        int filled = outputFluidInventory.fill(HOT_COOLANT,
                net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        return filled == HOT_COOLANT.getAmount() && super.output();
    }
}
