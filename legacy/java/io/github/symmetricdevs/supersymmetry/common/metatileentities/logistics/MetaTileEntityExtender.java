package io.github.symmetricdevs.supersymmetry.common.metatileentities.logistics;

import static com.gregtechceu.gtceu.api.capability.GregtechDataCodes.UPDATE_OUTPUT_FACING;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.unification.material.Material;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.logistics.MetaTileEntityDelegator;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.custom.ExtenderRender;

public class MetaTileEntityExtender extends MetaTileEntityDelegator {

    protected final ExtenderRender renderer;
    protected Direction inputFacing;

    public MetaTileEntityExtender(ResourceLocation metaTileEntityId, Predicate<Capability<?>> capFilter,
                                  ExtenderRender renderer, Material baseMaterial) {
        this(metaTileEntityId, capFilter, renderer, baseMaterial.getMaterialRGB());
    }

    public MetaTileEntityExtender(ResourceLocation metaTileEntityId, Predicate<Capability<?>> capFilter,
                                  ExtenderRender renderer, int baseColor) {
        super(metaTileEntityId, capFilter, baseColor);
        this.renderer = renderer;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityExtender(metaTileEntityId, capFilter, renderer, baseColor);
    }

    @Override
    @Nullable
    public Direction getDelegatingFacing(Direction facing) {
        return facing == getFrontFacing() ? inputFacing : getFrontFacing();
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.renderer.render(renderState, translation, pipeline, getFrontFacing(), inputFacing);
    }

    @Override
    public boolean onWrenchClick(Player playerIn, InteractionHand hand, Direction facing,
                                 CuboidRayTraceResult hitResult) {
        if (!playerIn.isSneaking()) {
            if (getInputFacing() == facing || facing == getFrontFacing()) return false;
            if (!getWorld().isRemote) {
                setInputFacing(facing);
            }
            return true;
        }
        return super.onWrenchClick(playerIn, hand, facing, hitResult);
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        data.setInteger("InputFacing", getInputFacing().getIndex());
        return data;
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.inputFacing = Direction.VALUES[data.getInteger("InputFacing")];
    }

    @Override
    public void writeInitialSyncData(@NotNull FriendlyByteBuf buf) {
        super.writeInitialSyncData(buf);
        buf.writeByte(getInputFacing().getIndex());
    }

    @Override
    public void receiveInitialSyncData(@NotNull FriendlyByteBuf buf) {
        super.receiveInitialSyncData(buf);
        this.inputFacing = Direction.VALUES[buf.readByte()];
    }

    @Override
    public void receiveCustomData(int dataId, @NotNull FriendlyByteBuf buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == UPDATE_OUTPUT_FACING) {
            this.inputFacing = Direction.VALUES[buf.readByte()];
            scheduleRenderUpdate();
        }
    }

    @Override
    public boolean isValidFrontFacing(Direction facing) {
        return facing != getFrontFacing() && facing != inputFacing;
    }

    @Override
    public void setFrontFacing(Direction frontFacing) {
        super.setFrontFacing(frontFacing);
        if (this.inputFacing == null) {
            // set initial input facing as opposite to output (front)
            setInputFacing(frontFacing.getOpposite());
        }
    }

    public Direction getInputFacing() {
        return inputFacing == null ? Direction.SOUTH : inputFacing;
    }

    public void setInputFacing(Direction inputFacing) {
        this.inputFacing = inputFacing;
        if (!getWorld().isRemote) {
            notifyBlockUpdate();
            writeCustomData(UPDATE_OUTPUT_FACING, buf -> buf.writeByte(inputFacing.getIndex()));
            markDirty();
        }
    }

    @Override
    public boolean needsSneakToRotate() {
        return true;
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }
}
