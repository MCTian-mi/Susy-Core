package io.github.symmetricdevs.supersymmetry.client.renderer.entity;

import net.minecraft.resources.ResourceLocation;

import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityDrone;

import software.bernie.geckolib.model.GeoModel;

/** Static GeckoLib resource locations for {@link EntityDrone}. */
public class DroneModel extends GeoModel<EntityDrone> {

    public static final ResourceLocation MODEL = SuSyValues.susyId("geo/gatherer_drone.geo.json");
    public static final ResourceLocation TEXTURE = SuSyValues.susyId("textures/entities/drone.png");
    public static final ResourceLocation ANIMATION = SuSyValues.susyId("animations/drone.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityDrone drone) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityDrone drone) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityDrone drone) {
        return ANIMATION;
    }
}
