package io.github.symmetricdevs.supersymmetry.common.metatileentities.multiblockpart;

import static io.github.symmetricdevs.supersymmetry.api.capability.SuSyDataCodes.UPDATE_REDSTONE_ACTIVATION;
import static io.github.symmetricdevs.supersymmetry.api.capability.SuSyDataCodes.UPDATE_REDSTONE_SIGNAL;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.ModularUI;
import com.gregtechceu.gtceu.api.gui.widgets.DynamicLabelWidget;
import com.gregtechceu.gtceu.api.gui.widgets.ImageWidget;
import com.gregtechceu.gtceu.api.gui.widgets.IncrementButtonWidget;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerBase;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.client.renderer.texture.cube.SimpleOverlayRenderer;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MetaTileEntityMultiblockPart;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.IRedstoneControllable;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityComponentRedstoneController extends MetaTileEntityMultiblockPart {

    public static TraceabilityPredicate controllerPredicate() {
        return (new TraceabilityPredicate(
                (blockWorldState -> {
                    BlockEntity tile = blockWorldState.getTileEntity();
                    if (tile instanceof MetaTileEntityHolder) {
                        MetaTileEntity metaTileEntity = ((MetaTileEntityHolder) tile).getMetaTileEntity();
                        if (metaTileEntity instanceof MetaTileEntityComponentRedstoneController) {
                            Set<IMultiblockPart> partsFound = blockWorldState.getMatchContext()
                                    .getOrCreate("MultiblockParts", HashSet::new);
                            partsFound.add((IMultiblockPart) MetaMachine);

                            return true;
                        }
                    }
                    return false;
                })));
    }

    public int signal = 0;

    boolean pulledUp = false;

    public MetaTileEntityComponentRedstoneController(ResourceLocation mteId) {
        super(mteId, GTValues.HV);
    }

    public void changeSignal(int delta) {
        if (this.getController() != null && this.getController() instanceof IRedstoneControllable controllable) {

            int newsig = Math.floorMod(this.signal + delta, controllable.getSignalCeiling() + 1);
            if (newsig != this.signal && newsig >= 0) {
                this.writeCustomData(
                        UPDATE_REDSTONE_SIGNAL,
                        (buf) -> {
                            buf.writeInt(newsig);
                            this.signal = newsig;
                        });
            }
        }
    }

    // ran after the value is reset
    public void pulse() {
        MultiblockControllerBase controller = this.getController();
        if (controller != null && controller instanceof IRedstoneControllable receiver) {
            if (receiver.redstoneControlEnabled() && receiver.getSignalCeiling() >= this.signal) {
                receiver.pulse(this.signal);
            }
        }
    }

    @Override
    public void renderMetaTileEntity(
                                     CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    private SimpleOverlayRenderer getOverlay() {
        return SusyTextures.REDSTONE_CONTROLLER_OVERLAY;
    }

    @Override
    public boolean canPlaceCoverOnSide(@NotNull Direction side) {
        return false;
    }

    @Override
    public boolean canPartShare() {
        return false;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityComponentRedstoneController(this.metaTileEntityId);
    }

    @Override
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == UPDATE_REDSTONE_SIGNAL) {
            this.signal = buf.readInt();
        }
        if (dataId == UPDATE_REDSTONE_ACTIVATION) {
            this.pulledUp = buf.readBoolean();
        }
    }

    @Override
    public void updateInputRedstoneSignals() {
        super.updateInputRedstoneSignals();
        int val = this.getInputRedstoneSignal(this.frontFacing, true);
        if (pulledUp ^ (val == 0)) { // Only one is true; they are not equal
            if (val != 0) {
                pulse();
            }
            pulledUp = val != 0;
            writeCustomData(UPDATE_REDSTONE_ACTIVATION, buf -> {
                buf.writeBoolean(pulledUp);
            });
        }
    }

    @Override
    protected ModularUI createUI(Player Player) {
        return this.createGUITemplate(Player).build(this.getHolder(), Player);
    }

    @Override
    protected boolean canMachineConnectRedstone(Direction side) {
        return this.frontFacing == side;
    }

    private ModularUI.Builder createGUITemplate(Player Player) {
        MultiblockControllerBase controller = this.getController();
        if (controller != null && controller instanceof IRedstoneControllable receiver) {
            int limit = receiver.getSignalCeiling();
            ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 190, 130);
            ImageWidget screen = new ImageWidget(5, 5, 180, 120, GuiTextures.DISPLAY);

            IncrementButtonWidget up = new IncrementButtonWidget(7, 7, 20, 20, 1, 4, 16, 64, this::changeSignal);
            IncrementButtonWidget down = new IncrementButtonWidget(163, 7, 20, 20, -1, -4, -16, -64,
                    this::changeSignal);
            DynamicLabelWidget sig = new DynamicLabelWidget(
                    27,
                    11,
                    () -> {
                        return I18n.format(
                                this.getMetaName() + ".signal_label",
                                Integer.toString(signal),
                                Integer.toString(limit));
                    },
                    0xffffff);

            DynamicLabelWidget name = new DynamicLabelWidget(
                    8,
                    27,
                    () -> {
                        int n = 34;
                        String s = I18n.format(
                                this.getMetaName() + ".signal_desc",
                                I18n.format(
                                        controller.getMetaName() + ".signal." + receiver.getSignalName(this.signal)));

                        return IntStream.range(0, (s.length() + n - 1) / n)
                                .mapToObj(i -> s.substring(i * n, Math.min(s.length(), (i + 1) * n)))
                                .collect(Collectors.joining("\n"));
                    },
                    0xffffff);
            DynamicLabelWidget status = new DynamicLabelWidget(
                    8,
                    110,
                    () -> {
                        return I18n.format(
                                this.getMetaName() + ".pulled." + Boolean.toString(this.pulledUp));
                    });
            builder.widget(screen);
            builder.widget(sig);
            builder.widget(up);
            builder.widget(down);
            builder.widget(name);
            builder.widget(status);
            return builder;
        } else {
            ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 165, 35);
            builder.widget(new ImageWidget(4, 4, 157, 27, GuiTextures.DISPLAY));
            builder.label(9, 12, this.getMetaName() + ".not_connected", 0xAE5421);
            return builder;
        }
    }

    @Override
    protected boolean shouldSerializeInventories() {
        return false;
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        this.signal = data.getInteger("signal");
        this.pulledUp = data.getBoolean("state");
        super.readFromNBT(data);
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag data) {
        data = super.writeToNBT(data);
        data.setInteger("signal", this.signal);
        data.setBoolean("state", this.pulledUp);
        return super.writeToNBT(data);
    }
}
