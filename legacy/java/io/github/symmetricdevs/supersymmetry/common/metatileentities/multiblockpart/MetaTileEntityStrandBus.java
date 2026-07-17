package io.github.symmetricdevs.supersymmetry.common.metatileentities.multiblockpart;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.gregtechceu.gtceu.api.gui.ModularUI;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;

import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.client.renderer.texture.cube.SimpleOverlayRenderer;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MetaTileEntityMultiblockPart;
import io.github.symmetricdevs.supersymmetry.api.capability.IStrandProvider;
import io.github.symmetricdevs.supersymmetry.api.capability.Strand;
import io.github.symmetricdevs.supersymmetry.api.capability.SuSyCapabilities;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyMultiblockAbilities;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityStrandBus extends MetaTileEntityMultiblockPart
                                     implements IStrandProvider, IMultiblockAbilityPart<IStrandProvider> {

    private Strand strand;
    private boolean isExport;

    public MetaTileEntityStrandBus(ResourceLocation metaTileEntityId, boolean isExport) {
        super(metaTileEntityId, 4);
        this.isExport = isExport;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityStrandBus(metaTileEntityId, isExport);
    }

    @Override
    protected ModularUI createUI(Player Player) {
        return null;
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, Direction side) {
        if (capability == SuSyCapabilities.STRAND_PROVIDER) {
            return SuSyCapabilities.STRAND_PROVIDER.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public Strand getStrand() {
        return strand;
    }

    @Override
    public Strand take() {
        Strand transfer = this.strand; // Need to keep the reference
        this.strand = null;
        return transfer;
    }

    @Override
    public Strand insertStrand(Strand strand) {
        if (this.strand != null) {
            return strand;
        }
        this.strand = strand;
        return null;
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote && getOffsetTimer() % 5 == 0) {
            if (isExport) {
                pushItemsIntoNearbyHandlers(getFrontFacing());
            } else {
                pullItemsFromNearbyHandlers(getFrontFacing());
            }
        }
    }

    public void pushItemsIntoNearbyHandlers(Direction... allowedFaces) {
        this.transferToNearby(SuSyCapabilities.STRAND_PROVIDER, this::transferStrand, allowedFaces);
    }

    public void pullItemsFromNearbyHandlers(Direction... allowedFaces) {
        this.transferToNearby(SuSyCapabilities.STRAND_PROVIDER,
                (thisCap, otherCap) -> this.transferStrand(otherCap, thisCap), allowedFaces);
    }

    public void transferStrand(IStrandProvider sender, IStrandProvider receiver) {
        if (sender.getStrand() == null || receiver.getStrand() != null) {
            return;
        }
        if (receiver.insertStrand(sender.getStrand()) == null) {
            sender.take();
        }
    }

    private <T> void transferToNearby(Capability<T> capability, BiConsumer<T, T> transfer, Direction... allowedFaces) {
        for (Direction nearbyFacing : allowedFaces) {
            BlockEntity BlockEntity = this.getNeighbor(nearbyFacing);
            if (BlockEntity != null) {
                T otherCap = BlockEntity.getCapability(capability, nearbyFacing.getOpposite());
                T thisCap = this.getCoverCapability(capability, nearbyFacing);
                if (otherCap != null && thisCap != null) {
                    transfer.accept(thisCap, otherCap);
                }
            }
        }
    }

    @Override
    public MultiblockAbility<IStrandProvider> getAbility() {
        return isExport ? SuSyMultiblockAbilities.STRAND_EXPORT : SuSyMultiblockAbilities.STRAND_IMPORT;
    }

    @Override
    public void registerAbilities(List<IStrandProvider> abilityList) {
        abilityList.add(this);
    }

    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (this.shouldRenderOverlay()) {
            SusyTextures.STRAND_BUS_OVERLAY.renderSided(this.getFrontFacing(), renderState, translation, pipeline);
            SimpleOverlayRenderer overlay = this.isExport ? Textures.ITEM_HATCH_OUTPUT_OVERLAY :
                    Textures.ITEM_HATCH_INPUT_OVERLAY;
            overlay.renderSided(this.getFrontFacing(), renderState, translation, pipeline);
        }
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag data) {
        data.setTag("Strand", Strand.serialize(new CompoundTag(), this.strand));
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.strand = Strand.deserialize(data.getCompoundTag("Strand"));
    }

    @Override
    public void receiveInitialSyncData(FriendlyByteBuf buf) {
        super.receiveInitialSyncData(buf);
        try {
            this.strand = Strand.deserialize(buf.readCompoundTag());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeInitialSyncData(FriendlyByteBuf buf) {
        super.writeInitialSyncData(buf);
        buf.writeCompoundTag(Strand.serialize(new CompoundTag(), strand));
    }
}
