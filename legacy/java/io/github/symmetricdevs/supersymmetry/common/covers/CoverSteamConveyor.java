package io.github.symmetricdevs.supersymmetry.common.covers;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.cover.CoverableView;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.ModularUI;
import com.gregtechceu.gtceu.api.gui.widgets.*;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import com.gregtechceu.gtceu.common.covers.CoverConveyor;
import com.gregtechceu.gtceu.common.covers.DistributionMode;
import com.gregtechceu.gtceu.common.covers.ManualImportExportMode;
import com.gregtechceu.gtceu.common.pipelike.itempipe.tile.TileEntityItemPipe;

public class CoverSteamConveyor extends CoverConveyor {

    public CoverSteamConveyor(@NotNull CoverDefinition definition, @NotNull CoverableView coverableView,
                              @NotNull Direction attachedSide, int itemsPerSecond) {
        super(definition, coverableView, attachedSide, 0, itemsPerSecond);
    }

    @Override
    protected @NotNull TextureAtlasSprite getPlateSprite() {
        return Textures.STEAM_CASING_BRONZE.getSpriteOnSide(SimpleSidedCubeRenderer.RenderSide.SIDE);
    }

    @Override
    public ModularUI createUI(Player player) {
        WidgetGroup primaryGroup = new WidgetGroup();
        // TODO remove this override and do the title natively in 2.9
        primaryGroup.addWidget(new LabelWidget(10, 5, getUITitle()));

        primaryGroup.addWidget(new IncrementButtonWidget(136, 20, 30, 20, 1, 8, 64, 512, this::adjustTransferRate)
                .setDefaultTooltip()
                .setShouldClientCallback(false));
        primaryGroup.addWidget(new IncrementButtonWidget(10, 20, 30, 20, -1, -8, -64, -512, this::adjustTransferRate)
                .setDefaultTooltip()
                .setShouldClientCallback(false));

        primaryGroup.addWidget(new ImageWidget(40, 20, 96, 20, GuiTextures.DISPLAY));
        primaryGroup.addWidget(new TextFieldWidget2(42, 26, 92, 20, () -> String.valueOf(getTransferRate()), val -> {
            if (val != null && !val.isEmpty())
                setTransferRate(MathHelper.clamp(Integer.parseInt(val), 1, maxItemTransferRate));
        })
                .setNumbersOnly(1, maxItemTransferRate)
                .setMaxLength(4)
                .setPostFix("cover.conveyor.transfer_rate"));

        primaryGroup.addWidget(new CycleButtonWidget(10, 45, 75, 20,
                ConveyorMode.class, this::getConveyorMode, this::setConveyorMode));
        primaryGroup.addWidget(new CycleButtonWidget(7, 166, 116, 20,
                ManualImportExportMode.class, this::getManualImportExportMode, this::setManualImportExportMode)
                        .setTooltipHoverString("cover.universal.manual_import_export.mode.description"));

        if (getTileEntityHere() instanceof TileEntityItemPipe ||
                getNeighbor(getAttachedSide()) instanceof TileEntityItemPipe) {
            final ImageCycleButtonWidget distributionModeButton = new ImageCycleButtonWidget(149, 166, 20, 20,
                    GuiTextures.DISTRIBUTION_MODE, 3,
                    () -> distributionMode.ordinal(),
                    val -> setDistributionMode(DistributionMode.values()[val]))
                            .setTooltipHoverString(val -> DistributionMode.values()[val].getName());
            primaryGroup.addWidget(distributionModeButton);
        }

        this.itemFilterContainer.initUI(70, primaryGroup::addWidget);

        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 176, 190 + 82)
                .widget(primaryGroup)
                .bindPlayerInventory(player.inventory, GuiTextures.SLOT, 7, 190);
        return buildUI(builder, player);
    }

    @Override
    protected String getUITitle() {
        return "cover.conveyor.steam.title";
    }
}
