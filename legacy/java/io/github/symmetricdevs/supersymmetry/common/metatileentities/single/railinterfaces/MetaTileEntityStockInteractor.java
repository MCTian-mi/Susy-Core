package io.github.symmetricdevs.supersymmetry.common.metatileentities.single.railinterfaces;

import java.io.IOException;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.platform.GlStateManager;

// MUI2 removed: import com.cleanroommc.modularui.api.drawable.IKey;
// MUI2 removed: import com.cleanroommc.modularui.drawable.GuiTextures;
// MUI2 removed: import com.cleanroommc.modularui.factory.PosGuiData;
// MUI2 removed: import com.cleanroommc.modularui.screen.ModularPanel;
// MUI2 removed: import com.cleanroommc.modularui.screen.UISettings;
// MUI2 removed: import com.cleanroommc.modularui.utils.Alignment;
// MUI2 removed: import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
// MUI2 removed: import com.cleanroommc.modularui.value.sync.PanelSyncHandler;
// MUI2 removed: import com.cleanroommc.modularui.value.sync.PanelSyncManager;
// MUI2 removed: import com.cleanroommc.modularui.widgets.ButtonWidget;
// MUI2 removed: import com.cleanroommc.modularui.widgets.CycleButtonWidget;
// MUI2 removed: import com.cleanroommc.modularui.widgets.SlotGroupWidget;
// MUI2 removed: import com.cleanroommc.modularui.widgets.ToggleButton;
// MUI2 removed: import com.cleanroommc.modularui.widgets.layout.Flow;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.entity.boundingbox.IBoundingBox;
import cam72cam.mod.math.Vec3;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import dev.tianmi.sussypatches.api.MetaMachine.IMui2Holder;
import com.gregtechceu.gtceu.api.capability.GregtechDataCodes;
import com.gregtechceu.gtceu.api.capability.GregtechTileCapabilities;
import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.gui.ModularUI;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.utils.RenderBufferHelper;
import com.gregtechceu.gtceu.client.utils.RenderUtil;
import io.github.symmetricdevs.supersymmetry.api.SusyLog;
import io.github.symmetricdevs.supersymmetry.api.gui.SusyGuiTextures;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.Mui2Utils;
import io.github.symmetricdevs.supersymmetry.api.stockinteraction.IStockInteractor;
import io.github.symmetricdevs.supersymmetry.api.stockinteraction.StockFilter;
import io.github.symmetricdevs.supersymmetry.api.stockinteraction.StockHelperFunctions;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

@SuppressWarnings("deprecation")
public abstract class MetaTileEntityStockInteractor extends MetaTileEntity
                                                    implements IMui2Holder, IStockInteractor, IFastRenderMetaTileEntity,
                                                    IControllable {

    AABB interactionBoundingBox;
    private double interactionWidth = 11.;
    private double interactionDepth = 5.;

    // This defines which stock classes can be interacted with
    protected StockFilter stockFilter;

    @Nullable
    protected EntityRollingStock stock;
    protected boolean workingEnabled = true;
    // Rendering
    protected final ICubeRenderer renderer;
    // If the current bounding box should be rendered
    protected boolean renderBoundingBox = false;
    protected boolean highlightSelectedStock = false;

    public static final int TICK_RATE = 3;
    private static final int SYNC_STOCK = 1377;
    private static final int SYNC_STOCK_LEAVE = 1378;

    public MetaTileEntityStockInteractor(ResourceLocation metaTileEntityId, ICubeRenderer renderer) {
        super(metaTileEntityId);
        this.renderer = renderer;
        this.stockFilter = new StockFilter(9);
    }

    @Override
    public void update() {
        super.update();
        updateStock();
    }

    public void updateStock() {
        if (!getWorld().isRemote && this.isWorkingEnabled() && this.getOffsetTimer() % TICK_RATE == 0) {
            EntityRollingStock newStock = StockHelperFunctions.getStockFrom(getWorld(), getInteractionBoundingBox(),
                    stockFilter, this.getPos());
            if (newStock != this.stock) {
                this.stock = newStock;
                if (newStock != null) {
                    this.writeCustomData(SYNC_STOCK, (buf) -> buf.writeInt(newStock.getId()));
                } else {
                    this.writeCustomData(SYNC_STOCK_LEAVE, (buf) -> buf.writeBoolean(true));
                }
            }
        }
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        this.workingEnabled = workingEnabled;
        World world = this.getWorld();
        if (world != null && !world.isRemote) {
            this.writeCustomData(GregtechDataCodes.WORKING_ENABLED, (buf) -> buf.writeBoolean(workingEnabled));
        }
    }

    @Override
    public boolean isWorkingEnabled() {
        return this.workingEnabled;
    }

    protected abstract <T> T getStockCapability(Capability<T> capability, Direction side);

    @Override
    public <T> T getCapability(Capability<T> capability, Direction side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE) {
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        }

        T stockCapability = getStockCapability(capability, side);
        if (stockCapability != null) {
            return stockCapability;
        }

        return super.getCapability(capability, side);
    }

    @Override
    public boolean needsSneakToRotate() {
        return true;
    }

    @Override
    protected ModularUI createUI(Player Player) {
        return null;
    }

    // UI
    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager syncManager, UISettings settings) {
        PanelSyncHandler panel = (PanelSyncHandler) syncManager.panel("filter_panel",
                (panelSyncManager, syncHandler) -> stockFilter.createPopupPanel(panelSyncManager),
                true);

        BooleanSyncValue workingStateValue = new BooleanSyncValue(() -> workingEnabled, this::setWorkingEnabled);
        BooleanSyncValue renderBoundingBoxValue = new BooleanSyncValue(() -> renderBoundingBox,
                val -> renderBoundingBox = val);
        BooleanSyncValue highlightSelectedStockValue = new BooleanSyncValue(() -> highlightSelectedStock,
                val -> highlightSelectedStock = val);

        return Mui2Utils.defaultPanel(this)
                .child(IKey.lang(getMetaFullName()).asWidget().pos(5, 5))
                .child(SlotGroupWidget.playerInventory(true).left(7).bottom(7))
                .child(Mui2Utils.getLogo().asWidget().size(17).right(7).bottom(88))
                .child(new CycleButtonWidget()
                        .left(7)
                        .bottom(90)
                        .background(GuiTextures.BUTTON_CLEAN)
                        .hoverBackground(GuiTextures.BUTTON_CLEAN)
                        .stateCount(2)
                        .stateOverlay(SusyGuiTextures.BUTTON_POWER)
                        .value(workingStateValue))
                .child(Flow.column()
                        .top(18)
                        .margin(7, 0)
                        .widthRel(1f)
                        .coverChildrenHeight()
                        .child(Flow.row()
                                .coverChildrenHeight()
                                .marginBottom(2)
                                .widthRel(1f)
                                .child(new ToggleButton()
                                        .overlay(SusyGuiTextures.BUTTON_RENDER_AREA
                                                .asIcon()
                                                .size(16))
                                        .addTooltipLine(IKey.lang(
                                                "susy.gui.stock_interactor.button.render_bounding_box.tooltip"))
                                        .value(renderBoundingBoxValue))
                                .child(IKey.lang("susy.gui.stock_interactor.title.render_bounding_box")
                                        .asWidget()
                                        .align(Alignment.CenterRight)
                                        .height(18)))
                        .child(Flow.row()
                                .coverChildrenHeight()
                                .marginBottom(2)
                                .widthRel(1f)
                                .child(new ToggleButton()
                                        .overlay(SusyGuiTextures.BUTTON_RENDER_AREA
                                                .asIcon()
                                                .size(16))
                                        .addTooltipLine(IKey.lang(
                                                "susy.gui.stock_interactor.button.highlight_selected_stock.tooltip"))
                                        .value(highlightSelectedStockValue))
                                .child(IKey.lang("susy.gui.stock_interactor.title.highlight_selected_stock")
                                        .asWidget()
                                        .align(Alignment.CenterRight)
                                        .height(18)))
                        .child(Flow.row()
                                .coverChildrenHeight()
                                .marginBottom(2)
                                .widthRel(1f)
                                .child(new ButtonWidget<>()
                                        .overlay(SusyGuiTextures.BUTTON_STOCK_FILTER
                                                .asIcon()
                                                .size(16))
                                        .addTooltipLine(
                                                IKey.lang("susy.gui.stock_interactor.button.stock_filter.tooltip"))
                                        .onMousePressed(mouseButton -> {
                                            if (!panel.isPanelOpen()) {
                                                panel.openPanel();
                                            } else {
                                                panel.closePanel();
                                            }
                                            return true;
                                        }))
                                .child(IKey.lang("susy.gui.stock_interactor.title.stock_filter")
                                        .asWidget()
                                        .align(Alignment.CenterRight)
                                        .height(18))));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderMetaTileEntity(double x, double y, double z, float partialTicks) {
        if (this.shouldRenderBoundingBox()) {
            GlStateManager.pushMatrix();

            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.glLineWidth(15);

            RenderUtil.moveToFace(x, y, z, getFrontFacing());
            GlStateManager.translate(0, -.5, 0);
            RenderUtil.rotateToFace(getFrontFacing(), null);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

            RenderBufferHelper.renderCubeFrame(buffer, -this.getInteractionWidth() / 2, 0, 0,
                    this.getInteractionWidth() / 2, (-this.getInteractionDepth() - this.getInteractionWidth()) / 2,
                    this.getInteractionDepth(), 1, 0, 0, 0.6F);
            tessellator.draw();

            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();

            GlStateManager.popMatrix();
        }
        if (this.shouldHighlightSelectedStock()) {
            GlStateManager.pushMatrix();

            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.glLineWidth(10);

            IBoundingBox bounds = this.stock.getBounds()
                    .offset(new Vec3(-this.getPos().getX(), -this.getPos().getY(), -this.getPos().getZ()));

            RenderUtil.moveToFace(x, y, z, Direction.DOWN);
            GlStateManager.translate(-.5, 0, -.5);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

            RenderBufferHelper.renderCubeFrame(buffer, bounds.min().x, bounds.min().y - .65, bounds.min().z,
                    bounds.max().x, bounds.max().y, bounds.max().z, 0, 1, 0, 0.6F);
            tessellator.draw();

            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();

            GlStateManager.popMatrix();
        }
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        SusyTextures.STOCK_MACHINE_CASING.render(renderState, translation, pipeline);
        this.renderer.renderOrientedState(renderState, translation, pipeline, this.getFrontFacing(), true,
                this.isWorkingEnabled());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Pair<TextureAtlasSprite, Integer> getParticleTexture() {
        return Pair.of(SusyTextures.STOCK_MACHINE_CASING.getParticleSprite(), getPaintingColorForRendering());
    }

    @Override
    public AABB getRenderBoundingBox() {
        return this.getInteractionBoundingBox();
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        data.setTag("stockFilter", this.stockFilter.serializeNBT());
        data.setDouble("interactionWidth", this.interactionWidth);
        data.setDouble("interactionDepth", this.interactionDepth);
        data.setBoolean("renderBoundingBox", this.renderBoundingBox);
        data.setBoolean("highlightSelectedStock", this.highlightSelectedStock);
        data.setBoolean("workingEnabled", this.workingEnabled);
        return data;
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.stockFilter.deserializeNBT(data.getCompoundTag("stockFilter"));
        this.setInteractionWidth(data.getDouble("interactionWidth"));
        this.setInteractionDepth(data.getDouble("interactionDepth"));
        this.renderBoundingBox = data.getBoolean("renderBoundingBox");
        this.highlightSelectedStock = data.getBoolean("highlightSelectedStock");
        this.workingEnabled = data.getBoolean("workingEnabled");
    }

    @Override
    public void writeInitialSyncData(@NotNull FriendlyByteBuf buf) {
        super.writeInitialSyncData(buf);
        buf.writeCompoundTag(this.stockFilter.serializeNBT());
        buf.writeDouble(this.getInteractionWidth());
        buf.writeDouble(this.getInteractionDepth());
        buf.writeBoolean(this.renderBoundingBox);
        buf.writeBoolean(this.highlightSelectedStock);
        buf.writeBoolean(this.workingEnabled);
    }

    @Override
    public void receiveInitialSyncData(@NotNull FriendlyByteBuf buf) {
        super.receiveInitialSyncData(buf);
        try {
            // noinspection DataFlowIssue
            this.stockFilter.deserializeNBT(buf.readCompoundTag());
        } catch (IOException e) {
            SusyLog.logger.info("Could not deserialize stock stockFilter in stock interactor at {}", getPos());
            SusyLog.logger.error(e);
        }
        this.setInteractionWidth(buf.readDouble());
        this.setInteractionDepth(buf.readDouble());
        this.renderBoundingBox = buf.readBoolean();
        this.highlightSelectedStock = buf.readBoolean();
        this.workingEnabled = buf.readBoolean();
    }

    @Override
    public void receiveCustomData(int dataId, @NotNull FriendlyByteBuf buf) {
        super.receiveCustomData(dataId, buf);

        if (dataId == 6500) {
            this.renderBoundingBox = buf.readBoolean();
        }

        // Front facing changed
        if (dataId == GregtechDataCodes.UPDATE_FRONT_FACING) {
            this.recalculateBoundingBox();
        }

        if (dataId == GregtechDataCodes.WORKING_ENABLED) {
            this.workingEnabled = buf.readBoolean();
        }

        if (dataId == SYNC_STOCK) {
            int entityId = buf.readInt();
            this.stock = cam72cam.mod.world.World.get(getWorld()).getEntity(entityId, EntityRollingStock.class);
        }

        if (dataId == SYNC_STOCK_LEAVE) {
            this.stock = null;
        }

        this.scheduleRenderUpdate();
    }

    @Override
    public AABB getInteractionBoundingBox() {
        return interactionBoundingBox == null ? interactionBoundingBox = StockHelperFunctions.getBox(this.getPos(),
                this.getFrontFacing(), this.getInteractionWidth(), this.getInteractionDepth()) : interactionBoundingBox;
    }

    public void recalculateBoundingBox() {
        this.interactionBoundingBox = StockHelperFunctions.getBox(this.getPos(), this.getFrontFacing(),
                this.getInteractionWidth(), this.getInteractionDepth());
    }

    public double getInteractionWidth() {
        return interactionWidth;
    }

    public void setInteractionWidth(double interactionWidth) {
        this.interactionWidth = interactionWidth;
        this.recalculateBoundingBox();
    }

    public double getInteractionDepth() {
        return interactionDepth;
    }

    public void setInteractionDepth(double interactionDepth) {
        this.interactionDepth = interactionDepth;
        this.recalculateBoundingBox();
    }

    public boolean shouldRenderBoundingBox() {
        return this.renderBoundingBox;
    }

    public boolean shouldHighlightSelectedStock() {
        return this.highlightSelectedStock && this.stock != null && !this.stock.isDead();
    }
}
