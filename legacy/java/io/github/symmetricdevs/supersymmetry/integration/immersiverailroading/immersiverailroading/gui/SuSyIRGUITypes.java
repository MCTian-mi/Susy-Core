package io.github.symmetricdevs.supersymmetry.integration.immersiverailroading.gui;

import cam72cam.mod.gui.GuiRegistry;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityTunnelBore;

public class SuSyIRGUITypes {

    public static final GuiRegistry.EntityGUI<EntityTunnelBore> TUNNEL_BORE = GuiRegistry
            .registerEntityContainer(EntityTunnelBore.class, TunnelBoreContainer::new);
}
