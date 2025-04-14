package supersymmetry.client.renderer.handler;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import supersymmetry.common.tileentities.TileEntityDrillHead;

import static gregtech.api.util.GTUtility.gregtechId;
import static supersymmetry.api.util.SuSyUtility.susyId;

public class DrillHeadModel extends AnimatedGeoModel<TileEntityDrillHead> {

    private static final ResourceLocation modelResource = susyId("geo/drill_head.geo.json");
    private static final ResourceLocation textureResource = gregtechId("textures/blocks/casings/drill_head/all.png");
    private static final ResourceLocation animationResource = susyId("animations/drill_head.animation.json");

    @Override
    public ResourceLocation getModelLocation(TileEntityDrillHead entityDropPod) {
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureLocation(TileEntityDrillHead entityDropPod) {
        return textureResource;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(TileEntityDrillHead entityDropPod) {
        return animationResource;
    }
}
