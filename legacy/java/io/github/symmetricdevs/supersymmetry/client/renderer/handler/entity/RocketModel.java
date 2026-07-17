package io.github.symmetricdevs.supersymmetry.client.renderer.handler.entity;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

/**
 * Stub — RocketModel was a BlockBench-generated entity model using ModelBase + ModelRenderer,
 * both removed in 1.20.1. The model data is preserved as a reference stub; actual rocket
 * rendering will be re-implemented when rocketry is ported (deferred).
 */
public class RocketModel extends Model {

    public RocketModel() {
        super(ModelPart -> {});
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(com.mojang.blaze3d.vertex.PoseStack poseStack,
                               com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    }
}
