package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.common.blockentity.EccentricRollBlockEntity;

import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * GeckoLib 4 block-entity renderer for the eccentric crusher roll. It delegates to GeoBlockRenderer
 * only while the enclosing crusher marks the component {@code ACTIVE}; idle rolls use their baked
 * block model instead.
 */
public class EccentricRollRenderer implements BlockEntityRenderer<EccentricRollBlockEntity> {

    private final GeoBlockRenderer<EccentricRollBlockEntity> geoRenderer =
            new GeoBlockRenderer<>(new EccentricRollModel());

    @Override
    public void render(EccentricRollBlockEntity roll, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        BlockState state = roll.getBlockState();
        if (!state.hasProperty(GTBlockStateProperties.ACTIVE) || !state.getValue(GTBlockStateProperties.ACTIVE)) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.5D, 0.0D);
        // TODO)) Phase 6: correct the active roll's longitudinal-axis orientation against the
        // formed crusher after an in-game visual reference is available.
        geoRenderer.render(roll, partialTick, poseStack, buffers, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private static final class EccentricRollModel extends GeoModel<EccentricRollBlockEntity> {

        private static final ResourceLocation MODEL = SuSyValues.susyId("geo/eccentric_roll.geo.json");
        private static final ResourceLocation TEXTURE = SuSyValues.susyId("textures/block/eccentric_roll/all.png");
        private static final ResourceLocation ANIMATION = SuSyValues.susyId("animations/eccentric_roll.animation.json");

        @Override
        public ResourceLocation getModelResource(EccentricRollBlockEntity animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(EccentricRollBlockEntity animatable) {
            return TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(EccentricRollBlockEntity animatable) {
            return ANIMATION;
        }
    }
}
