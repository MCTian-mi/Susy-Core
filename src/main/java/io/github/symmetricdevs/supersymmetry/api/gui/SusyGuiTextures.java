package io.github.symmetricdevs.supersymmetry.api.gui;

// MUI2 removed

import org.jetbrains.annotations.ApiStatus;

// MUI2 removed: import com.cleanroommc.modularui.drawable.UITexture;

import com.gregtechceu.gtceu.api.GTValues;
// SteamTexture removed in Modern
// TextureArea removed in Modern

public class SusyGuiTextures {

    // TODO SteamTexture port: =
            .fullImage("textures/gui/progress_bar/progress_bar_mixer_%s.png");
    // TODO SteamTexture port: =
            .fullImage("textures/gui/progress_bar/progress_bar_extraction_%s.png");
    // TODO SteamTexture port: =.fullImage("textures/gui/base/fluid_slot_%s.png");
    // TODO SteamTexture port: =
            .fullImage("textures/gui/overlay/mold_overlay_%s.png");
    // TODO SteamTexture port: =
            .fullImage("textures/gui/progress_bar/int_circuit_overlay_%s.png");
    // TODO SteamTexture port: =
            .fullImage("textures/gui/widget/button_circuit_plus_%s.png");
    // TODO SteamTexture port: =
            .fullImage("textures/gui/widget/button_circuit_minus_%s.png");

    // TODO TextureArea port: =
            .fullImage("textures/gui/progress_bar/progress_bar_extraction.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/overlay/catalyst_bed_overlay.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/overlay/catalyst_pellet_overlay.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/overlay/electrode_overlay.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/overlay/electromagnetic_separator_fluid_overlay.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/overlay/electromagnetic_separator_item_overlay.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/overlay/large_reactor_fluid_overlay.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/overlay/large_reactor_item_overlay.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/overlay/sifter_fluid_overlay.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/overlay/sifter_item_input_overlay.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/overlay/sifter_item_output_overlay.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/overlay/cubic_lattice_overlay.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/overlay/ore_chunk_overlay.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/widget/button_circuit_plus_primitive.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/widget/button_circuit_minus_primitive.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/base/fluid_slot_primitive.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/widget/button_quarry_modes.png");
    // TODO TextureArea port: =.fullImage("textures/gui/widget/icon_indicator_arrow.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/widget/button_energy_voiding.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/widget/slider_background_dark.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/widget/slider_dark.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/widget/button_left_dark.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/widget/button_right_dark.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/widget/circle_green.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/widget/hazard.png");
    // TODO TextureArea port: =
            .fullImage("textures/gui/widget/suspicious_button.png");

    // TODO TextureArea port: =.fullImage("textures/gui/widget/green.png");
    // TODO TextureArea port: =.fullImage("textures/gui/widget/red.png");
    // TODO TextureArea port: =.fullImage("textures/gui/widget/x.png");

    public static final /* TODO MUI2 port: UITexture */ BACKGROUND_POPUP = UITexture.builder()
            .location(GTValues.MODID, "textures/gui/base/background_popup.png")
            .imageSize(195, 136)
            .adaptable(4)
            .name("gregtech_cover_bg")
            .canApplyTheme()
            .build();

    public static final /* TODO MUI2 port: UITexture */ BUTTON_POWER = UITexture.builder()
            .location(GTValues.MODID, "textures/gui/widget/button_power_2.png")
            .imageSize(18, 36)
            .build();

    public static final /* TODO MUI2 port: UITexture */ GREGTECH_LOGO = fullImage(GTValues.MODID, "textures/gui/icon/gregtech_logo.png");
    public static final /* TODO MUI2 port: UITexture */ GREGTECH_LOGO_XMAS = fullImage(GTValues.MODID,
            "textures/gui/icon/gregtech_logo_xmas.png");
    public static final /* TODO MUI2 port: UITexture */ OREDICT_ERROR = fullImage(GTValues.MODID, "textures/gui/widget/ore_filter/error.png");
    public static final /* TODO MUI2 port: UITexture */ OREDICT_INFO = fullImage(GTValues.MODID, "textures/gui/widget/ore_filter/info.png");
    public static final /* TODO MUI2 port: UITexture */ OREDICT_SUCCESS = fullImage(GTValues.MODID,
            "textures/gui/widget/ore_filter/success.png");
    public static final /* TODO MUI2 port: UITexture */ OREDICT_WAITING = fullImage(GTValues.MODID,
            "textures/gui/widget/ore_filter/waiting.png");
    public static final /* TODO MUI2 port: UITexture */ BUTTON_RENDER_AREA = fullImage(GTValues.MODID,
            "textures/gui/widget/button_render_area.png");
    public static final /* TODO MUI2 port: UITexture */ BUTTON_SETTINGS = fullImage(GTValues.MODID,
            "textures/gui/widget/button_settings.png");
    public static final /* TODO MUI2 port: UITexture */ BUTTON_STOCK_FILTER = fullImage(GTValues.MODID,
            "textures/gui/widget/button_stock_filter.png");
    public static final /* TODO MUI2 port: UITexture */ BRAKE_ACTIVE = fullImage(GTValues.MODID, "textures/gui/widget/icon_brake_active.png");
    public static final /* TODO MUI2 port: UITexture */ BRAKE_INACTIVE = fullImage(GTValues.MODID,
            "textures/gui/widget/icon_brake_inactive.png");
    public static final /* TODO MUI2 port: UITexture */ THROTTLE_ACTIVE = fullImage(GTValues.MODID,
            "textures/gui/widget/icon_throttle_active.png");
    public static final /* TODO MUI2 port: UITexture */ THROTTLE_INACTIVE = fullImage(GTValues.MODID,
            "textures/gui/widget/icon_throttle_inactive.png");

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "GTCEu 2.9")
    public static final /* TODO MUI2 port: UITexture */ SLOT = new UITexture.Builder()
            .location(GTValues.MODID, "textures/gui/base/slot.png")
            .imageSize(18, 18)
            .adaptable(1)
            .name("standard_slot")
            .canApplyTheme()
            .build();

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "GTCEu 2.9")
    public static final /* TODO MUI2 port: UITexture */ ICON_RIGHT = UITexture.builder()
            .location(GTValues.MODID, "textures/gui/terminal/icon/right_hover.png")
            .name("right_button")
            .canApplyTheme()
            .build();

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "GTCEu 2.9")
    public static final /* TODO MUI2 port: UITexture */ ICON_LEFT = UITexture.builder()
            .location(GTValues.MODID, "textures/gui/terminal/icon/left_hover.png")
            .name("left_button")
            .canApplyTheme()
            .build();
}
