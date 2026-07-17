package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi;

import static com.gregtechceu.gtceu.api.capability.GregtechDataCodes.WORKING_ENABLED;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.gregtechceu.gtceu.api.capability.GregtechDataCodes;
import com.gregtechceu.gtceu.api.capability.GregtechTileCapabilities;
import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.fluids.FluidState;
import com.gregtechceu.gtceu.api.fluids.GTFluid;
import com.gregtechceu.gtceu.api.fluids.attribute.AttributedFluid;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.unification.material.Material;
import com.gregtechceu.gtceu.api.unification.material.info.MaterialFlags;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;

public abstract class VoidingMultiblockBase extends MultiblockControllerMachine implements IControllable {

    // Update this value based on your needs
    // For instance, if you want your glorified trashcan to be tiered
    public int rateBonus = 1;
    // Amount of ticks between voiding
    public final int voidingFrequency = 10;
    public boolean active = false;
    protected boolean workingEnabled = true;

    public Object2BooleanOpenHashMap<Fluid> fluidCache = new Object2BooleanOpenHashMap<>();

    public VoidingMultiblockBase(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {
        if (this.getWorld().isRemote) return;
        if (getOffsetTimer() % voidingFrequency == 0) {
            this.active = false;
            if (!this.workingEnabled) return;
            for (IFluidTank tank : getAbilities(MultiblockAbility.IMPORT_FLUIDS)) {
                FluidStack fs = tank.getFluid();
                if (fs != null) {
                    Fluid fluid = fs.getFluid();
                    boolean voidable = fluidCache.computeIfAbsent(fluid, this::canVoid);
                    // TODO: Cache this?
                    if (voidable) {
                        tank.drain(this.getActualVoidingRate(), true);
                        this.active = true;
                    }
                }
            }
        }
    }

    public boolean canVoid(Fluid fluid) {
        if (fluid instanceof AttributedFluid attributedFluid) {
            FluidState state = attributedFluid.getState();
            if (fluid instanceof GTFluid.GTMaterialFluid gtFluid) {
                Material mat = gtFluid.getMaterial();
                return canVoidState(state) && (!incinerate() ^ mat.hasFlag(MaterialFlags.FLAMMABLE));
            }
            return canVoidState(state);
        }
        return false;
    }

    public boolean canVoidState(FluidState state) {
        return false;
    }

    private int getActualVoidingRate() {
        return rateBonus * getBaseVoidingRate();
    }

    public int getBaseVoidingRate() {
        return 1000;
    }

    // Whether to void flammable fluids or not
    public boolean incinerate() {
        return false;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                this.isActive(), true);
    }

    @Override
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.IS_WORKING) {
            this.active = this.lastActive;
        } else if (dataId == WORKING_ENABLED) {
            this.workingEnabled = buf.readBoolean();
            scheduleRenderUpdate();
        }
    }

    @Override
    public void writeInitialSyncData(FriendlyByteBuf buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(active);
        buf.writeBoolean(workingEnabled);
    }

    @Override
    public void receiveInitialSyncData(FriendlyByteBuf buf) {
        super.receiveInitialSyncData(buf);
        this.active = buf.readBoolean();
        this.workingEnabled = buf.readBoolean();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.active = false;
        this.writeCustomData(GregtechDataCodes.IS_WORKING, (buf -> buf.writeBoolean(false)));
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag data) {
        data.setBoolean("workingEnabled", this.workingEnabled);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.workingEnabled = data.getBoolean("workingEnabled");
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean getIsWeatherOrTerrainResistant() {
        return true;
    }

    @Override
    public boolean isWorkingEnabled() {
        return workingEnabled;
    }

    @Override
    public void setWorkingEnabled(boolean enabled) {
        this.workingEnabled = enabled;
        this.writeCustomData(WORKING_ENABLED, buf -> buf.writeBoolean(enabled));
    }

    @Override
    public <T> T getCapability(Capability<T> capability, Direction side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE)
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        return super.getCapability(capability, side);
    }
}
