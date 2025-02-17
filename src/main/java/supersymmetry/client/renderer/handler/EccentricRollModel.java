package supersymmetry.client.renderer.handler;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import supersymmetry.common.tileentities.TileEntityEccentricRoll;

import static supersymmetry.api.util.SuSyUtility.susyId;

public class EccentricRollModel extends AnimatedGeoModel<TileEntityEccentricRoll> {

    private static final ResourceLocation modelResource = susyId("geo/eccentric_roll.geo.json");
    private static final ResourceLocation textureResource = susyId("textures/tileentities/eccentric_roll.png");
    private static final ResourceLocation animationResource = susyId("animations/eccentric_roll.animation.json");

    @Override
    public ResourceLocation getModelLocation(TileEntityEccentricRoll entityDropPod) {
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureLocation(TileEntityEccentricRoll entityDropPod) {
        return textureResource;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(TileEntityEccentricRoll entityDropPod) {
        return animationResource;
    }
}
