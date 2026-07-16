package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.strand;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.transfer.fluid.IFluidHandlerModifiable;
import net.minecraftforge.items.IItemHandlerModifiable;
import com.gregtechceu.gtceu.utils.GTUtil;

import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import io.github.symmetricdevs.supersymmetry.api.capability.IStrandProvider;
import io.github.symmetricdevs.supersymmetry.api.capability.Strand;
import io.github.symmetricdevs.supersymmetry.api.capability.StrandConversion;
import io.github.symmetricdevs.supersymmetry.api.machine.multiblock.SuSyMultiblockAbilities;
import io.github.symmetricdevs.supersymmetry.api.pattern.SuSyPredicates;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityStrandShaper}: the abstract base for
 * every strand-casting-line controller.
 *
 * <p>The shaper holds a single {@link Strand}, consumes EU/t, and progresses a recipe
 * that transforms the strand (or creates one from molten metal). It does not use
 * GTCEu-Modern recipe logic; instead it subclasses {@link MultiblockControllerMachine}
 * directly and runs its own server-tick loop.</p>
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class StrandShaperMachine extends MultiblockControllerMachine
        implements IDisplayUIMachine, IWorkable {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StrandShaperMachine.class, MultiblockControllerMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    @RequireRerender
    @Nullable
    private CompoundTag strandTag;

    /**
     * Get the current strand, deserialized from the backing NBT tag.
     * Returns {@code null} when no strand is held.
     */
    protected @Nullable Strand getStrand() {
        return Strand.deserialize(strandTag);
    }

    /**
     * Set the current strand, serializing it into the backing NBT tag.
     * Pass {@code null} to clear the strand.
     */
    protected void setStrand(@Nullable Strand strand) {
        this.strandTag = strand != null ? Strand.serialize(new CompoundTag(), strand) : null;
    }

    protected @Nullable IStrandProvider input;
    protected @Nullable IStrandProvider output;

    protected @Nullable IItemHandlerModifiable inputInventory;
    protected @Nullable IItemHandlerModifiable outputInventory;
    protected @Nullable IFluidHandlerModifiable inputFluidInventory;
    protected @Nullable IFluidHandlerModifiable outputFluidInventory;
    protected @Nullable EnergyContainerList energyContainer;

    @Persisted
    @DescSynced
    protected int progress;
    @Persisted
    @DescSynced
    protected int maxProgress;
    @Persisted
    @DescSynced
    @RequireRerender
    protected boolean isActive;
    @Persisted
    @DescSynced
    protected boolean hasNotEnoughEnergy;

    public StrandShaperMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            subscribeServerTick(this::serverTickMethod);
        }
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        initializeAbilities();
        applyOrientationFixups();
    }

    @Override
    public void onStructureInvalid() {
        setActive(false);
        progress = 0;
        maxProgress = 0;
        super.onStructureInvalid();
        resetTileAbilities();
    }

    @Override
    public void onPartUnload() {
        super.onPartUnload();
        resetTileAbilities();
    }

    protected void initializeAbilities() {
        this.input = findStrandProvider(SuSyMultiblockAbilities.STRAND_IMPORT);
        this.output = findStrandProvider(SuSyMultiblockAbilities.STRAND_EXPORT);
        this.inputInventory = collectItemHandler(IO.IN);
        this.outputInventory = collectItemHandler(IO.OUT);
        this.inputFluidInventory = collectFluidHandler(IO.IN);
        this.outputFluidInventory = collectFluidHandler(IO.OUT);
        this.energyContainer = collectEnergyContainer();
    }

    protected void resetTileAbilities() {
        this.input = null;
        this.output = null;
        this.inputInventory = null;
        this.outputInventory = null;
        this.inputFluidInventory = null;
        this.outputFluidInventory = null;
        this.energyContainer = null;
    }

    protected @Nullable IStrandProvider findStrandProvider(PartAbility ability) {
        for (IMultiPart part : getParts()) {
            if (ability.isApplicable(part.self().getBlockState().getBlock()) &&
                    part instanceof IStrandProvider provider) {
                return provider;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected @Nullable IItemHandlerModifiable collectItemHandler(IO io) {
        List<IItemHandlerModifiable> handlers = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            for (RecipeHandlerList handlerList : part.getRecipeHandlers()) {
                if (!handlerList.isValid(io)) continue;
                for (IRecipeHandler<?> handler : handlerList.getCapability(ItemRecipeCapability.CAP)) {
                    if (handler instanceof IItemHandlerModifiable itemHandler) {
                        handlers.add(itemHandler);
                    }
                }
            }
        }
        if (handlers.isEmpty()) return null;
        if (handlers.size() == 1) return handlers.get(0);
        return new io.github.symmetricdevs.supersymmetry.common.machine.multiblock.strand.ItemHandlerList(handlers);
    }

    @SuppressWarnings("unchecked")
    protected @Nullable IFluidHandlerModifiable collectFluidHandler(IO io) {
        List<IFluidHandlerModifiable> handlers = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            for (RecipeHandlerList handlerList : part.getRecipeHandlers()) {
                if (!handlerList.isValid(io)) continue;
                for (IRecipeHandler<?> handler : handlerList.getCapability(FluidRecipeCapability.CAP)) {
                    if (handler instanceof IFluidHandlerModifiable fluidHandler) {
                        handlers.add(fluidHandler);
                    }
                }
            }
        }
        if (handlers.isEmpty()) return null;
        if (handlers.size() == 1) return handlers.get(0);
        return new io.github.symmetricdevs.supersymmetry.common.machine.multiblock.strand.FluidTankList(handlers);
    }

    @SuppressWarnings("unchecked")
    protected @Nullable EnergyContainerList collectEnergyContainer() {
        List<IEnergyContainer> containers = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            for (RecipeHandlerList handlerList : part.getRecipeHandlers()) {
                if (!handlerList.isValid(IO.IN)) continue;
                for (IRecipeHandler<?> handler : handlerList.getCapability(EURecipeCapability.CAP)) {
                    if (handler instanceof IEnergyContainer container) {
                        containers.add(container);
                    }
                }
            }
        }
        if (containers.isEmpty()) return null;
        return new EnergyContainerList(containers);
    }

    protected void applyOrientationFixups() {
        Level level = getLevel();
        if (level == null || level.isClientSide) return;

        List<Pair<BlockPos, com.gregtechceu.gtceu.api.pattern.util.RelativeDirection>> fixups =
                SuSyPredicates.getOrientationFixups(this);
        for (var fixup : fixups) {
            BlockPos pos = fixup.getLeft();
            var rel = fixup.getRight();
            BlockState state = level.getBlockState(pos);
            BlockState oriented;
            if (state.hasProperty(RotatedPillarBlock.AXIS)) {
                Direction facing = SuSyPredicates.resolveAxialFacing(this, rel);
                oriented = state.setValue(RotatedPillarBlock.AXIS, facing.getAxis());
            } else if (state.hasProperty(BlockStateProperties.FACING)) {
                Direction facing = SuSyPredicates.resolveFacing(this, rel);
                oriented = SuSyPredicates.withFacing(state, facing);
            } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                Direction facing = SuSyPredicates.resolveFacing(this, rel);
                oriented = SuSyPredicates.withHorizontalFacing(state, facing);
            } else {
                continue;
            }
            if (oriented != state) {
                level.setBlockAndUpdate(pos, oriented);
            }
        }
    }

    protected void serverTickMethod() {
        if (!isFormed() || getMultiblockState().hasError()) {
            return;
        }

        if (isActive) {
            if (progress <= maxProgress) {
                if (consumeEnergy()) {
                    progress++;
                    hasNotEnoughEnergy = false;
                } else {
                    if (progress > 0) progress--;
                    hasNotEnoughEnergy = true;
                }
            } else if (output()) {
                setStrand(null);
                this.progress = 0;
                setActive(false);
            }
            return;
        }

        if (!hasRoom()) {
            return;
        }
        Strand possibleStrand = resultingStrand();
        if (getVoltage() == 0 || (possibleStrand == null && outputsStrand()) || !consumeInputsAndSetupRecipe()) {
            return;
        }
        setStrand(possibleStrand);
        this.progress = 0;
        if (this.maxProgress > 0) {
            int tier = GTUtil.getFloorTierByVoltage(getVoltage());
            this.maxProgress /= (int) Math.pow(2, Math.max(0, tier - 1));
            this.maxProgress = Math.max(1, this.maxProgress);
        }
        setActive(true);
        hasNotEnoughEnergy = false;
    }

    protected boolean outputsStrand() {
        return true;
    }

    protected boolean hasRoom() {
        return !outputsStrand() || output != null && output.getStrand() == null;
    }

    protected boolean consumeEnergy() {
        if (energyContainer == null) return false;
        return energyContainer.changeEnergy(-getVoltage()) == -getVoltage();
    }

    public long getVoltage() {
        if (energyContainer == null) return 0;
        return energyContainer.getInputVoltage();
    }

    public int getTier() {
        return GTUtil.getFloorTierByVoltage(getVoltage());
    }

    protected @Nullable FluidStack getFirstMaterialFluid() {
        if (inputFluidInventory == null) return null;
        for (int i = 0; i < inputFluidInventory.getTanks(); i++) {
            FluidStack stack = inputFluidHandlerGet(i);
            if (stack == null || stack.isEmpty()) continue;
            Material mat = getMaterialFromFluid(stack);
            if (mat != null && mat.hasProperty(PropertyKey.INGOT)) {
                return stack;
            }
        }
        return null;
    }

    protected FluidStack inputFluidHandlerGet(int tank) {
        return inputFluidInventory.getFluidInTank(tank);
    }

    protected @Nullable Material getMaterialFromFluid(FluidStack stack) {
        if (!stack.getFluid().is(CustomTags.MOLTEN_FLUIDS)) {
            return null;
        }
        Material material = ChemicalHelper.getMaterial(stack.getFluid());
        return material == GTMaterials.NULL ? null : material;
    }

    protected abstract boolean consumeInputsAndSetupRecipe();

    protected abstract @Nullable Strand resultingStrand();

    protected boolean output() {
        if (!outputsStrand()) {
            return true;
        }
        if (output == null || getStrand() == null) {
            return false;
        }
        return output.insertStrand(getStrand()) == null;
    }

    protected void setActive(boolean active) {
        if (this.isActive == active) {
            return;
        }
        this.isActive = active;
        var renderState = getRenderState();
        if (renderState.hasProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS)) {
            setRenderState(renderState.setValue(GTMachineModelProperties.RECIPE_LOGIC_STATUS,
                    active ? RecipeLogic.Status.WORKING : RecipeLogic.Status.IDLE));
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        if (!isFormed()) return;

        if (energyContainer != null) {
            textList.add(Component.translatable("gtceu.multiblock.energy_usage",
                    Component.literal(String.valueOf(getVoltage())).withStyle(net.minecraft.ChatFormatting.YELLOW)));
            textList.add(Component.translatable("gtceu.multiblock.tier",
                    Component.literal(GTValues.VN[getTier()]).withStyle(net.minecraft.ChatFormatting.YELLOW)));
        }

        textList.add(Component.translatable(isActive ? "gtceu.multiblock.running" : "gtceu.multiblock.idling"));
        if (maxProgress > 0) {
            textList.add(Component.translatable("gtceu.multiblock.progress",
                    Component.literal(String.format("%.1f", progress / (double) maxProgress * 100))));
        }
        if (hasNotEnoughEnergy) {
            textList.add(Component.translatable("gtceu.multiblock.not_enough_energy").withStyle(net.minecraft.ChatFormatting.RED));
        }

        Strand displayStrand = getStrand();
        if (displayStrand == null) {
            displayStrand = (output == null || output.getStrand() == null) ?
                    (input == null ? null : input.getStrand()) : output.getStrand();
        }
        if (displayStrand == null) {
            textList.add(Component.translatable("susy.multiblock.strand_casting.no_strand"));
            return;
        }
        textList.add(Component.translatable("susy.multiblock.strand_casting.thickness",
                String.format("%.2f", displayStrand.thickness)));
        textList.add(Component.translatable("susy.multiblock.strand_casting.width",
                String.format("%.2f", displayStrand.width)));

        StrandConversion conversion = StrandConversion.getConversion(displayStrand);
        if (conversion == null) {
            textList.add(Component.translatable("susy.multiblock.strand_casting.no_conversion"));
            return;
        }
        textList.add(Component.translatable("susy.multiblock.strand_casting.ore_prefix",
                Component.translatable("susy.prefix." + conversion.prefix.name)));
    }

    @Override
    public com.lowdragmc.lowdraglib.gui.modular.ModularUI createUI(net.minecraft.world.entity.player.Player entityPlayer) {
        var group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(new LabelWidget(4, 5, self().getBlockState().getBlock().getDescriptionId()));
        group.addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                .textSupplier(this.getLevel().isClientSide ? null : this::addDisplayText)
                .setMaxWidthLimit(200));
        group.setBackground(com.gregtechceu.gtceu.api.gui.GuiTextures.BACKGROUND_INVERSE);
        return new com.lowdragmc.lowdraglib.gui.modular.ModularUI(198, 208, this, entityPlayer).widget(group);
    }

    @Override
    public boolean isWorkingEnabled() {
        return true;
    }

    @Override
    public void setWorkingEnabled(boolean enabled) {
        // Strand lines cannot be paused while processing.
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public boolean isActive() {
        return isActive && isFormed();
    }

    protected static boolean canInsertItems(IItemHandlerModifiable handler, net.minecraft.world.item.ItemStack stack, boolean simulate) {
        if (handler == null || stack.isEmpty()) return false;
        return handler.insertItem(0, stack, simulate).getCount() != stack.getCount();
    }
}
