package io.github.symmetricdevs.supersymmetry.client;

import io.github.symmetricdevs.supersymmetry.client.renderer.handler.BlenderFluidAreaRender;
import io.github.symmetricdevs.supersymmetry.client.renderer.handler.FormedPartAppearanceRender;
import io.github.symmetricdevs.supersymmetry.client.renderer.handler.GeoMachineRender;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;

import io.github.symmetricdevs.supersymmetry.SuSyValues;

/**
 * Client-only initialization for renderer registrations that must exist before machine models load.
 */
public class SuSyClientProxy {

    private SuSyClientProxy() {}

    public static void init() {
        DynamicRenderManager.register(SuSyValues.susyId("blender_fluid_area"), BlenderFluidAreaRender.TYPE);
        DynamicRenderManager.register(SuSyValues.susyId("formed_part_appearance"), FormedPartAppearanceRender.TYPE);
        DynamicRenderManager.register(SuSyValues.susyId("geo_machine"), GeoMachineRender.TYPE);
    }
}
