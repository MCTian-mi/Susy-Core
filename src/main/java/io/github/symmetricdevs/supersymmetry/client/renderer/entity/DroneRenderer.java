package io.github.symmetricdevs.supersymmetry.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import io.github.symmetricdevs.supersymmetry.common.entities.EntityDrone;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** GeckoLib 4 renderer for the drone flight entity. */
public class DroneRenderer extends GeoEntityRenderer<EntityDrone> {

    public DroneRenderer(EntityRendererProvider.Context context) {
        super(context, new DroneModel());
    }
}
