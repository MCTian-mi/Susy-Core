package io.github.symmetricdevs.supersymmetry.common.metatileentities.multiblockpart;

import java.util.*;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.common.capabilities.Capability;

import org.jetbrains.annotations.Nullable;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.gregtechceu.gtceu.api.capability.*;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.ModularUI;
import com.gregtechceu.gtceu.api.gui.Widget;
import com.gregtechceu.gtceu.api.gui.widgets.*;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.ICleanroomProvider;
import com.gregtechceu.gtceu.api.machine.multiblock.ICleanroomReceiver;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerBase;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.util.TextComponentUtil;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MetaTileEntityMultiblockPart;
import io.github.symmetricdevs.supersymmetry.api.capability.impl.ScannerLogic;
import io.github.symmetricdevs.supersymmetry.api.rocketry.components.AbstractComponent;
import io.github.symmetricdevs.supersymmetry.api.util.DataStorageLoader;
import io.github.symmetricdevs.supersymmetry.api.util.StructAnalysis;
import io.github.symmetricdevs.supersymmetry.api.util.StructAnalysis.BuildStat;
import io.github.symmetricdevs.supersymmetry.common.item.SuSyMetaItems;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.rocket.MetaTileEntityBuildingCleanroom;

public class MetaTileEntityComponentScanner extends MetaTileEntityMultiblockPart
                                            implements ICleanroomReceiver, IWorkable {

    private final ScannerLogic scannerLogic;
    private float scanDuration = 0;
    private MetaTileEntityBuildingCleanroom linkedCleanroom;
    private BuildStat shownStatus;
    private BlockPos errorPos = null;

    public StructAnalysis struct;

    public MetaTileEntityComponentScanner(ResourceLocation mteId) {
        super(mteId, 0); // it kind of is and isn't
        shownStatus = BuildStat.UNSCANNED;

        struct = new StructAnalysis(getWorld());
        importItems = new DataStorageLoader(
                this,
                is -> {
                    int metaV = SuSyMetaItems.isMetaItem(is);
                    return metaV == SuSyMetaItems.DATA_CARD.metaValue ||
                            metaV == SuSyMetaItems.DATA_CARD_ACTIVE.metaValue;
                });
        if (importItems.getStackInSlot(0).isItemEqual(ItemStack.EMPTY)) {
            shownStatus = BuildStat.NO_CARD;
        }
        scannerLogic = new ScannerLogic(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityComponentScanner(this.metaTileEntityId);
    }

    public DataStorageLoader getInventory() {
        return (DataStorageLoader) importItems;
    }

    @Override
    public boolean canPartShare() {
        return false;
    }

    public void scanPart() {
        getInventory().clearNBT();
        struct = new StructAnalysis(getWorld());
        if (linkedCleanroom == null || !linkedCleanroom.isClean()) {
            struct.status = BuildStat.UNCLEAN;
            return;
        }
        AABB interior = linkedCleanroom.getInteriorBB();
        int solidBlocks = 0;
        ArrayList<BlockPos> blockList = struct.getBlocks(getWorld(), interior, true);

        if (blockList == null) { // error propagated
            return;
        } else if (blockList.isEmpty()) {
            this.struct.status = BuildStat.EMPTY;
            return;
        }

        scanDuration = (int) (blockList.size() / (Math.pow(2, linkedCleanroom.getEnergyTier() - 3))) + 4; // 5 being the
        // minimum
        // value
        scannerLogic.setGoalTime(scanDuration);

        Set<BlockPos> blocksConnected = struct.getBlockConn(interior, blockList.get(0));

        if (blocksConnected.size() != blockList.size()) {
            this.struct.status = BuildStat.DISCONNECTED;
            return;
        }
        struct.status = BuildStat.SCANNING;

        detectComponents(blockList);

        if (struct.status == BuildStat.SCANNING) {
            struct.status = BuildStat.UNRECOGNIZED;
            /* if it wasnt changed after scanning, nothing matched */ }

        /*
         * Plan from here on out:
         * 1. Gather block statistics
         * 2. Check for unallowed TileEntities (we can't have as many if it's all being modelized)
         * 3. Identify component purpose:
         * a. Payload fairing
         * - Distinguishable by material type
         * - Attachments along a fissure plane and circling the bottom
         * - Holds port
         * - Bottom opening is not filled
         * - No through-holes:
         * - All partial holes are counted (if two blocks have a midpoint not in a block, there is a partial hole)
         * - All air blocks in the hole are counted
         * - There is no more than one contiguous set of air blocks not inside the hole that has access to the hole!
         * b. Life compartment
         * - Contains interior space
         * - Contains life support TEs
         * - Allows for containers
         * c. Fuel tank
         * - Contains interior space
         * - Contains structural blocks
         * - Has a port
         * - Contains exterior blocks
         * d. Engine
         * - Specialized blocks for ignition containment
         * e. Hull cover
         * - Connection blocks (skirts)
         * - Particular surface blocks
         * - Support blocks
         * f. Control room
         * - Port
         * - Guidance computer (not a tile entity)
         * - Seat
         */
    }

    public void detectComponents(ArrayList<BlockPos> blockList) {
        for (AbstractComponent<?> component : AbstractComponent.getRegistry()) {
            if (component
                    .getDetectionPredicate()
                    .test(new Tuple<>(struct, blockList))) {
                Optional<CompoundTag> scanResult = component.analyzePattern(struct, linkedCleanroom.getInteriorBB());
                if (scanResult.isPresent()) {
                    if (scanResult.get().hasKey("errorPos")) {
                        errorPos = BlockPos.fromLong(scanResult.get().getLong("errorPos"));
                        continue;
                    }
                    getInventory()
                            .setNBT(
                                    t -> {
                                        CompoundTag tag = scanResult.get();
                                        component.writeToNBT(tag);
                                        return tag;
                                    });
                    errorPos = null;
                    break;
                }
            }
        }
    }

    public void handleScan(Widget.ClickData click) {
        if (linkedCleanroom == null || !linkedCleanroom.isClean()) {
            struct.status = BuildStat.UNCLEAN;
        }
        if (this.isWorkingEnabled()) {
            if (importItems.getStackInSlot(0).isEmpty()) {
                struct.status = BuildStat.NO_CARD;
                finishScan();
                return;
            }
            scannerLogic.setWorkingEnabled(true);
            scannerLogic.setActive(true);

            getInventory().setLocked(true);
            struct.status = BuildStat.SCANNING;
            scanPart();
            if (struct.status != BuildStat.SUCCESS) {
                getInventory().clearNBT();
            }
        }
    }

    @Override
    public @Nullable ICleanroomProvider getCleanroom() {
        return linkedCleanroom;
    }

    // @Override
    // public @Nullable MultiblockControllerBase getController() {
    // return linkedCleanroom;
    // }

    @Override
    public void setCleanroom(ICleanroomProvider iCleanroomProvider) {
        if (iCleanroomProvider instanceof MetaTileEntityBuildingCleanroom)
            linkedCleanroom = (MetaTileEntityBuildingCleanroom) iCleanroomProvider;
    }

    @Override
    public boolean isWorkingEnabled() {
        return scannerLogic.isWorkingEnabled() && linkedCleanroom != null && linkedCleanroom.isWorkingEnabled();
    }

    public void setWorkingEnabled(boolean isActivationAllowed) {
        this.scannerLogic.setWorkingEnabled(isActivationAllowed);
    }

    public boolean drainEnergy(boolean simulate) {
        if (linkedCleanroom == null) {
            return false;
        }
        IEnergyContainer energyContainer = linkedCleanroom.getEnergyContainer();
        long resultEnergy = energyContainer.getEnergyStored() - scannerLogic.getInfoProviderEUt();
        if (resultEnergy >= 0L && resultEnergy <= energyContainer.getEnergyCapacity()) {
            if (!simulate) energyContainer.changeEnergy(-scannerLogic.getInfoProviderEUt());
            return true;
        }
        return false;
    }

    public void update() {
        if (!getWorld().isRemote) {
            this.scannerLogic.updateLogic();
        }
    }

    @Override
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.LOCK_OBJECT_HOLDER) {
            getInventory().setLocked(buf.readBoolean());
        }
        if (dataId == GregtechDataCodes.WORKABLE_ACTIVE) {
            scannerLogic.setActive(buf.readBoolean());
        }
    }

    @Override
    public <T> T getCapability(Capability<T> capability, Direction side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_WORKABLE) {
            return GregtechTileCapabilities.CAPABILITY_WORKABLE.cast(this);
        } else {
            return capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE ?
                    GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this) : super.getCapability(capability, side);
        }
    }

    @Override
    public void renderMetaTileEntity(
                                     CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay()
                .renderOrientedState(
                        renderState,
                        translation,
                        pipeline,
                        this.getFrontFacing(),
                        this.isActive(),
                        this.isWorkingEnabled());
    }

    @Override
    public ICubeRenderer getBaseTexture() {
        MultiblockControllerBase controller = getController();
        if (controller != null) {
            return this.hatchTexture = controller.getBaseTexture(this);
        } else {
            return Textures.VOLTAGE_CASINGS[4];
        }
    }

    public ICubeRenderer getFrontOverlay() {
        return Textures.RESEARCH_STATION_OVERLAY;
    }

    public void finishScan() {
        if (struct.status == BuildStat.SUCCESS) {
            getInventory().setImageType(SuSyMetaItems.DATA_CARD_ACTIVE.metaValue); // is this cursed? yes
        }
        getInventory().setLocked(false);
        shownStatus = struct.status;
    }

    @Override
    public int getProgress() {
        return (int) scannerLogic.getProgress();
    }

    @Override
    public int getMaxProgress() {
        return (int) scannerLogic.getMaxProgress();
    }

    @Override
    public boolean isActive() {
        return scannerLogic.isActive();
    }

    protected void modifyItem(String key, String value) {
        getInventory().mutateItem(key, value);
    }

    @Override
    protected ModularUI createUI(Player Player) {
        return this.createGUITemplate(Player).build(this.getHolder(), Player);
    }

    protected void addDisplayText(List<Component> textList) {
        MultiblockDisplayText.builder(textList, this.getCleanroom() != null)
                .setWorkingStatus(this.isWorkingEnabled(), this.isActive())
                .addEnergyUsageLine(linkedCleanroom.getEnergyContainer())
                .addCustom(
                        (tl) -> {
                            if (linkedCleanroom != null) {
                                Component.translatable cleanState;
                                if (scannerLogic.isActive() || struct.status == BuildStat.SCANNING) {
                                    tl.add(
                                            TextComponentUtil.translationWithColor(
                                                    ChatFormatting.YELLOW, "susy.machine.component_scanner.scanning"));
                                } else if (shownStatus == BuildStat.SUCCESS) {
                                    tl.add(
                                            TextComponentUtil.translationWithColor(
                                                    ChatFormatting.GREEN, "susy.machine.component_scanner.success"));

                                } else if (shownStatus == BuildStat.UNSCANNED) {
                                    tl.add(
                                            TextComponentUtil.translationWithColor(
                                                    ChatFormatting.GRAY, "susy.machine.component_scanner.unscanned"));
                                } else {
                                    tl.add(
                                            TextComponentUtil.translationWithColor(
                                                    ChatFormatting.DARK_RED, shownStatus.getCode()));
                                }
                            }
                        })
                .addCustom((tl) -> {
                    if (errorPos != null &&
                            !(scannerLogic.isActive() || struct.status == BuildStat.SCANNING)) {
                        tl.add(TextComponentUtil.translationWithColor(ChatFormatting.GRAY,
                                "susy.machine.component_scanner.pos",
                                "(" + errorPos.getX() + ", " + errorPos.getY() + ", " + errorPos.getZ() + ")"));
                    }
                })
                .addProgressLine(this.scannerLogic.getProgressPercent());
    }

    private ModularUI.Builder createGUITemplate(Player Player) {
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 198, 208);
        // Spliced from MultiblockControllerMachine
        // Display
        builder.image(4, 4, 190, 109, GuiTextures.DISPLAY);

        // single bar
        ProgressWidget progressBar = new ProgressWidget(
                scannerLogic::getProgressPercent,
                4,
                115,
                190,
                7,
                GuiTextures.PROGRESS_BAR_MULTI_ENERGY_YELLOW,
                ProgressWidget.MoveType.HORIZONTAL)
                        .setHoverTextConsumer(list -> addBarHoverText(list, 0));
        builder.widget(progressBar);

        builder.widget(
                new IndicatorImageWidget(174, 93, 17, 17, GuiTextures.GREGTECH_LOGO_DARK)
                        .setWarningStatus(GuiTextures.GREGTECH_LOGO_BLINKING_YELLOW, this::addWarningText)
                        .setErrorStatus(GuiTextures.GREGTECH_LOGO_BLINKING_RED, this::addErrorText));

        builder.label(9, 9, getMetaFullName(), 0xFFFFFF);
        builder.widget(
                new AdvancedTextWidget(9, 20, this::addDisplayText, 0xFFFFFF)
                        .setMaxWidthLimit(181)
                        .setClickHandler(this::handleDisplayClick));

        // Power Button
        IControllable controllable = getCapability(GregtechTileCapabilities.CAPABILITY_CONTROLLABLE, null);
        if (controllable != null) {
            builder.widget(
                    new ImageCycleButtonWidget(
                            173,
                            183,
                            18,
                            18,
                            GuiTextures.BUTTON_POWER,
                            controllable::isWorkingEnabled,
                            controllable::setWorkingEnabled));
            builder.widget(new ImageWidget(173, 201, 18, 6, GuiTextures.BUTTON_POWER_DETAIL));
        }
        // Scan Button
        builder
                .widget(
                        new ClickButtonWidget(
                                68,
                                75,
                                54,
                                18,
                                Component.translatable("susy.machine.component_scanner.scan_button")
                                        .getUnformattedComponentText(),
                                this::handleScan))
                .slot(importItems, 0, 90, 95, GuiTextures.SLOT);

        builder.bindPlayerInventory(Player.inventory, 125);
        return builder;
    }

    private void handleDisplayClick(String s, Widget.ClickData clickData) {}

    private void addBarHoverText(List<Component> list, int i) {}

    private void addWarningText(List<Component> iTextComponents) {
        if (struct.status == BuildStat.UNSCANNED) {
            iTextComponents.add(Component.translatable(BuildStat.UNSCANNED.getCode()));
        }
    }

    private void addErrorText(List<Component> iTextComponents) {
        if (struct.status != BuildStat.SUCCESS && struct.status != BuildStat.SCANNING &&
                struct.status != BuildStat.UNSCANNED) {
            iTextComponents.add(Component.translatable(BuildStat.UNSCANNED.getCode()));
        }
    }
}
