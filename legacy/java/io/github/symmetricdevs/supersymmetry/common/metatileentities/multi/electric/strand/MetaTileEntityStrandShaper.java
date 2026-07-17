package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric.strand;

import java.io.IOException;
import java.util.List;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import com.google.common.collect.Lists;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;

import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.capability.EnergyContainerList;

import com.gregtechceu.gtceu.api.capability.ItemHandlerList;
import com.gregtechceu.gtceu.api.items.itemhandlers.GTItemStackHandler;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.pattern.PatternMatchContext;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.unification.FluidUnifier;
import com.gregtechceu.gtceu.api.unification.material.Material;
import com.gregtechceu.gtceu.api.unification.material.properties.PropertyKey;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import io.github.symmetricdevs.supersymmetry.api.blocks.VariantAxialRotatableBlock;
import io.github.symmetricdevs.supersymmetry.api.capability.IStrandProvider;
import io.github.symmetricdevs.supersymmetry.api.capability.Strand;
import io.github.symmetricdevs.supersymmetry.api.capability.StrandConversion;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyMultiblockAbilities;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockMetallurgyRoll;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public abstract class MetaTileEntityStrandShaper extends MultiblockControllerMachine implements IWorkable {

    protected Strand strand;
    protected IStrandProvider input;
    protected IStrandProvider output;

    protected IItemHandlerModifiable inputInventory;
    protected IItemHandlerModifiable outputInventory;
    protected IMultipleTankHandler inputFluidInventory;
    protected IMultipleTankHandler outputFluidInventory;
    protected IEnergyContainer energyContainer;

    protected int progress;
    protected int maxProgress;
    protected boolean isActive;
    protected boolean hasNotEnoughEnergy;

    public MetaTileEntityStrandShaper(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {
        // Update progress if needed
        if (isActive) {
            if (progress <= maxProgress) {
                if (consumeEnergy()) {
                    progress++;
                    hasNotEnoughEnergy = false;
                } else {
                    if (progress > 0)
                        progress--;
                    hasNotEnoughEnergy = true;
                }
            } else if (!getWorld().isRemote) {
                // Output
                output();
                this.strand = null;
                this.progress = 0;
                this.isActive = false;
            }
            if (!getWorld().isRemote) {
                this.markDirty();
            }
        }

        // Check if there is a resulting strand
        // Consume input strand if it exists
        if (!getWorld().isRemote && !isActive) {
            if (!hasRoom()) {
                return;
            }
            Strand possibleStrand = resultingStrand();
            if (getVoltage() == 0 || (possibleStrand == null && outputsStrand()) || !consumeInputsAndSetupRecipe()) {
                return;
            }
            strand = possibleStrand;
            progress = 0;
            maxProgress /= (int) Math.pow(2, GTUtility.getTierByVoltage(getVoltage()) - 1);
            maxProgress = Math.max(1, maxProgress);
            isActive = true;
            this.markDirty();
        }
    }

    protected boolean outputsStrand() {
        return true;
    }

    protected boolean hasRoom() {
        return this.output.getStrand() == null;
    }

    protected boolean consumeEnergy() {
        return energyContainer.changeEnergy(-getVoltage()) == -getVoltage();
    }

    protected FluidStack getFirstMaterialFluid() {
        for (IMultipleTankHandler.MultiFluidTankEntry tank : this.inputFluidInventory.getFluidTanks()) {
            FluidStack stack = tank.getFluid();
            if (stack == null || stack.amount == 0) {
                continue;
            }
            Material mat = FluidUnifier.getMaterialFromFluid(stack.getFluid());
            if (mat != null && mat.hasProperty(PropertyKey.INGOT)) {
                return stack;
            }
        }
        return null;
    }

    public long getVoltage() {
        return this.energyContainer.getInputVoltage();
    }

    public IEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    protected abstract boolean consumeInputsAndSetupRecipe();

    protected abstract Strand resultingStrand();

    protected void output() {
        output.insertStrand(strand);
    }

    protected void initializeAbilities() {
        if (!this.getAbilities(SuSyMultiblockAbilities.STRAND_IMPORT).isEmpty())
            this.input = this.getAbilities(SuSyMultiblockAbilities.STRAND_IMPORT).get(0);
        if (!this.getAbilities(SuSyMultiblockAbilities.STRAND_EXPORT).isEmpty())
            this.output = this.getAbilities(SuSyMultiblockAbilities.STRAND_EXPORT).get(0);
        this.inputInventory = new ItemHandlerList(this.getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.inputFluidInventory = new FluidTankList(true, this.getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputFluidInventory = new FluidTankList(true, this.getAbilities(MultiblockAbility.EXPORT_FLUIDS));
        this.outputInventory = new ItemHandlerList(this.getAbilities(MultiblockAbility.EXPORT_ITEMS));
        this.energyContainer = new EnergyContainerList(this.getAbilities(MultiblockAbility.INPUT_ENERGY));
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
    }

    @Override
    public void invalidateStructure() {
        isActive = false;
        progress = 0;
        maxProgress = 0;
        super.invalidateStructure();
        resetTileAbilities();
    }

    private void resetTileAbilities() {
        this.inputInventory = new GTItemStackHandler(this, 0);
        this.inputFluidInventory = new FluidTankList(true);
        this.outputInventory = new GTItemStackHandler(this, 0);
        this.outputFluidInventory = new FluidTankList(true);
        this.energyContainer = new EnergyContainerList(Lists.newArrayList());

        this.input = null;
        this.output = null;
    }

    @Override
    public void writeInitialSyncData(FriendlyByteBuf buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.progress);
        buf.writeInt(this.maxProgress);
        buf.writeBoolean(this.isActive);
        buf.writeCompoundTag(Strand.serialize(new CompoundTag(), strand));
    }

    @Override
    public void receiveInitialSyncData(FriendlyByteBuf buf) {
        super.receiveInitialSyncData(buf);
        this.progress = buf.readInt();
        this.maxProgress = buf.readInt();
        this.isActive = buf.readBoolean();
        try {
            this.strand = Strand.deserialize(buf.readCompoundTag());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag data) {
        data.setInteger("Progress", this.progress);
        data.setInteger("MaxProgress", this.maxProgress);
        data.setBoolean("IsActive", this.isActive);
        data.setTag("Strand", Strand.serialize(new CompoundTag(), this.strand));
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.progress = data.getInteger("Progress");
        this.maxProgress = data.getInteger("MaxProgress");
        this.isActive = data.getBoolean("IsActive");
        this.strand = Strand.deserialize(data.getCompoundTag("Strand"));
    }

    @Override
    protected void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        MultiblockDisplayText.builder(textList, this.isStructureFormed()).setWorkingStatus(true, isActive)
                .addEnergyUsageLine(this.energyContainer)
                .addEnergyTierLine(GTUtility.getTierByVoltage(this.energyContainer.getInputVoltage()))
                .addWorkingStatusLine().addProgressLine(this.getProgressPercent())
                .addLowPowerLine(hasNotEnoughEnergy)
                .addCustom((comps) -> {
                    if (!this.isStructureFormed()) {
                        return;
                    }
                    Strand displayStrand = strand != null ? strand :
                            ((output == null || output.getStrand() == null) ? input.getStrand() : output.getStrand());
                    if (displayStrand == null) {
                        comps.add(Component.translatable("susy.multiblock.strand_casting.no_strand"));
                        return;
                    }
                    comps.add(Component.translatable("susy.multiblock.strand_casting.thickness",
                            String.format("%.2f", displayStrand.thickness)));
                    comps.add(Component.translatable("susy.multiblock.strand_casting.width",
                            String.format("%.2f", displayStrand.width)));

                    StrandConversion conversion = StrandConversion.getConversion(displayStrand);
                    if (conversion == null) {
                        comps.add(Component.translatable("susy.multiblock.strand_casting.no_conversion"));
                        return;
                    }
                    comps.add(Component.translatable("susy.multiblock.strand_casting.ore_prefix",
                            Component.translatable(
                                    "supersymmetry.prefix." + conversion.prefix.name.toLowerCase())));
                });
    }

    private double getProgressPercent() {
        return progress / (double) maxProgress;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    @Override
    public boolean isActive() {
        return isActive && this.isStructureFormed();
    }

    @Override
    public boolean isWorkingEnabled() {
        return true;
    }

    @Override
    public void setWorkingEnabled(boolean b) {
        // They cannot stop it.
    }

    protected Direction getRelativeFacing(RelativeDirection dir) {
        return dir.getRelativeFacing(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }

    public TraceabilityPredicate autoAbilities(boolean checkEnergyIn, boolean checkMaintenance, boolean checkMuffler) {
        TraceabilityPredicate predicate = super.autoAbilities(checkMaintenance, checkMuffler);
        if (checkEnergyIn) {
            predicate = predicate.or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1)
                    .setMaxGlobalLimited(2).setPreviewCount(1));
        }
        return predicate;
    }

    public TraceabilityPredicate autoAbilities() {
        return autoAbilities(true, true, false);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                isActive, true);
    }

    protected TraceabilityPredicate rollOrientation(RelativeDirection direction) {
        // makes sure rotor's front faces the left side (relative to the player) of controller front
        return SuSyPredicates.axisOrientation(this, rollState(), direction, VariantAxialRotatableBlock.AXIS);
    }

    private BlockState rollState() {
        return SuSyBlocks.METALLURGY_ROLL.getState(BlockMetallurgyRoll.BlockMetallurgyRollType.ROLL);
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }
}
