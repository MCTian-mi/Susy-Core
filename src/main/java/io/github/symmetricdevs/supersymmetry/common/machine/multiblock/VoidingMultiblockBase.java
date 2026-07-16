package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.fluids.FluidState;
import com.gregtechceu.gtceu.api.fluids.attribute.IAttributedFluid;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.transfer.fluid.IFluidHandlerModifiable;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MufflerPartMachine;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modern port of the 1.12.2 {@code VoidingMultiblockBase}: a non-workable multiblock
 * controller that drains voidable fluids from its import fluid hatches every 10 ticks.
 * <p>
 * Key re-derivation for GTCEu-Modern:
 * <ul>
 * <li>Extends {@link MultiblockControllerMachine} directly because no recipe logic or
 * energy is required.</li>
 * <li>{@code workingEnabled} and {@code active} are synced with
 * {@code @Persisted @DescSynced @RequireRerender} instead of custom packets.</li>
 * <li>The workable-casing overlay is driven by manually setting
 * {@link GTMachineModelProperties#RECIPE_LOGIC_STATUS} to {@code WORKING}/{@code IDLE}.</li>
 * <li>Fluid hatches are found by scanning part recipe handlers for
 * {@link FluidRecipeCapability#CAP} entries that implement {@link IFluidHandlerModifiable}.</li>
 * </ul>
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class VoidingMultiblockBase extends MultiblockControllerMachine
        implements IControllable, IDisplayUIMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            VoidingMultiblockBase.class, MultiblockControllerMachine.MANAGED_FIELD_HOLDER);

    public static final int VOIDING_FREQUENCY = 10;

    /** Height-derived multiplier for flare/smoke stacks; always {@code 1} for the dumper. */
    protected int rateBonus = 1;

    @Persisted
    @DescSynced
    @RequireRerender
    protected boolean active = false;

    @Persisted
    @DescSynced
    protected boolean workingEnabled = true;

    protected final Object2BooleanOpenHashMap<Fluid> fluidCache = new Object2BooleanOpenHashMap<>();

    @Nullable
    protected TickableSubscription voidingSubscription;

    public VoidingMultiblockBase(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.voidingSubscription = subscribeServerTick(this::voidFluids);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        unsubscribe(voidingSubscription);
        setActive(false);
    }

    @Override
    public void onPartUnload() {
        super.onPartUnload();
        unsubscribe(voidingSubscription);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        unsubscribe(voidingSubscription);
    }

    /**
     * Drains voidable fluids from every import fluid hatch. Runs on the server tick.
     */
    protected void voidFluids() {
        Level level = getLevel();
        if (level == null || level.isClientSide || !isFormed() || getMultiblockState().hasError()) {
            return;
        }
        if (getOffsetTimer() % VOIDING_FREQUENCY != 0) {
            return;
        }

        if (!workingEnabled) {
            setActive(false);
            return;
        }

        boolean anyVoided = false;
        int rate = getActualVoidingRate();

        for (IMultiPart part : getParts()) {
            for (RecipeHandlerList handlerList : part.getRecipeHandlers()) {
                if (!handlerList.isValid(IO.IN)) {
                    continue;
                }
                for (IRecipeHandler<?> handler : handlerList.getCapability(FluidRecipeCapability.CAP)) {
                    if (handler instanceof IFluidHandlerModifiable fluidHandler) {
                        anyVoided |= voidFromHandler(fluidHandler, rate);
                    }
                }
            }
        }

        setActive(anyVoided);
    }

    private boolean voidFromHandler(IFluidHandlerModifiable handler, int rate) {
        boolean any = false;
        for (int i = 0; i < handler.getTanks(); i++) {
            FluidStack stack = handler.getFluidInTank(i);
            if (stack.isEmpty()) {
                continue;
            }
            Fluid fluid = stack.getFluid();
            boolean voidable = fluidCache.computeIfAbsent(fluid, this::canVoid);
            if (voidable) {
                int toDrain = Math.min(rate, stack.getAmount());
                int drained = handler.drain(toDrain, FluidAction.EXECUTE).getAmount();
                if (drained > 0) {
                    any = true;
                }
            }
        }
        return any;
    }

    /**
     * Whether the given fluid can be voided by this machine. Caches the result per fluid.
     */
    public boolean canVoid(Fluid fluid) {
        if (fluid instanceof IAttributedFluid attributed) {
            FluidState state = attributed.getState();
            Material material = ChemicalHelper.getMaterial(fluid);
            if (material != null && material != GTMaterials.NULL) {
                return canVoidState(state) && (!incinerate() ^ material.hasFlag(MaterialFlags.FLAMMABLE));
            }
            return canVoidState(state);
        }
        return false;
    }

    /** Which {@link FluidState}(s) this machine can void. */
    public abstract boolean canVoidState(FluidState state);

    private int getActualVoidingRate() {
        return rateBonus * getBaseVoidingRate();
    }

    /** Base rate in millibuckets per voiding tick (every {@value #VOIDING_FREQUENCY} ticks). */
    public int getBaseVoidingRate() {
        return 1000;
    }

    /** If true, voids flammable fluids; otherwise voids non-flammable fluids. */
    public boolean incinerate() {
        return false;
    }

    public boolean isActive() {
        return active;
    }

    protected void setActive(boolean active) {
        if (this.active == active) {
            return;
        }
        this.active = active;
        var renderState = getRenderState();
        if (renderState.hasProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS)) {
            setRenderState(renderState.setValue(GTMachineModelProperties.RECIPE_LOGIC_STATUS,
                    active ? RecipeLogic.Status.WORKING : RecipeLogic.Status.IDLE));
        }
    }

    @Override
    public boolean isWorkingEnabled() {
        return workingEnabled;
    }

    @Override
    public void setWorkingEnabled(boolean enabled) {
        this.workingEnabled = enabled;
        if (!enabled) {
            setActive(false);
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        if (isFormed()) {
            textList.add(Component.translatable("susy.machine.voiding.rate",
                    Component.literal(getBaseVoidingRate() + " L/10t").withStyle(ChatFormatting.DARK_PURPLE)));
        }
    }

    /**
     * Shared helper for flare/smoke stacks: scan upward from the controller until the
     * muffler hatch is found, returning the number of blocks from controller to muffler
     * inclusive. JEI previews return the minimum height.
     */
    protected int scanHeightAboveController(int minHeight, int maxHeight) {
        Level level = getLevel();
        if (level == null) {
            return minHeight;
        }
        Direction relativeUp = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        BlockPos.MutableBlockPos pos = getPos().mutable();
        // The legacy code started scanning one block below the minimum structure height.
        pos.move(relativeUp, minHeight - 2);
        int height = minHeight;
        for (; height < maxHeight; height++) {
            pos.move(relativeUp);
            if (isBlockMuffler(level, pos)) {
                break;
            }
        }
        return height;
    }

    protected boolean isBlockMuffler(Level level, BlockPos pos) {
        if (level == null) {
            return true;
        }
        return MetaMachine.getMachine(level, pos) instanceof MufflerPartMachine;
    }
}
