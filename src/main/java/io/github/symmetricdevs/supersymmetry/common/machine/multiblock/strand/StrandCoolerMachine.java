package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.strand;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraftforge.items.IItemHandlerModifiable;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import io.github.symmetricdevs.supersymmetry.api.capability.Strand;
import io.github.symmetricdevs.supersymmetry.api.capability.StrandConversion;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityStrandCooler}: the terminal machine of
 * a strand-casting line. It converts a cut strand into items using
 * {@link StrandConversion}.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StrandCoolerMachine extends StrandShaperMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StrandCoolerMachine.class, StrandShaperMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private ItemStack current = ItemStack.EMPTY;

    public StrandCoolerMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected boolean consumeInputsAndSetupRecipe() {
        if (input == null || input.getStrand() == null) {
            return false;
        }
        Strand strandIn = input.getStrand();
        if (!strandIn.isCut) {
            return false;
        }
        StrandConversion conversion = StrandConversion.getConversion(strandIn);
        if (conversion == null) {
            return false;
        }
        long materialAmount = conversion.prefix.getMaterialAmount(strandIn.material);
        int amount = (int) (conversion.amount * materialAmount / GTValues.M);
        FluidStack water = GTMaterials.Water.getFluid(amount);
        if (inputFluidInventory == null) return false;
        FluidStack drained = inputFluidInventory.drain(water, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
        if (drained == null || drained.getAmount() != amount) {
            return false;
        }
        current = ChemicalHelper.get(conversion.prefix, strandIn.material, conversion.amount);
        inputFluidInventory.drain(water, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        input.take();
        maxProgress = amount * 4;
        return true;
    }

    @Override
    protected boolean outputsStrand() {
        return false;
    }

    @Override
    protected boolean hasRoom() {
        if (input == null || input.getStrand() == null) {
            return false;
        }
        StrandConversion conversion = StrandConversion.getConversion(input.getStrand());
        if (conversion == null) {
            return false;
        }
        if (outputInventory == null) return false;
        ItemStack expected = ChemicalHelper.get(conversion.prefix, input.getStrand().material, conversion.amount);
        return canInsertAll(outputInventory, expected);
    }

    private boolean canInsertAll(IItemHandlerModifiable handler, ItemStack stack) {
        if (stack.isEmpty()) return true;
        ItemStack remaining = stack.copy();
        for (int i = 0; i < handler.getSlots(); i++) {
            remaining = handler.insertItem(i, remaining, true);
            if (remaining.isEmpty()) return true;
        }
        return false;
    }

    @Override
    protected boolean output() {
        if (current.isEmpty() || outputInventory == null) {
            return false;
        }
        ItemStack remaining = current.copy();
        for (int i = 0; i < outputInventory.getSlots() && !remaining.isEmpty(); i++) {
            remaining = outputInventory.insertItem(i, remaining, false);
        }
        current = remaining;
        return current.isEmpty();
    }

    @Override
    protected @Nullable Strand resultingStrand() {
        return null;
    }
}
