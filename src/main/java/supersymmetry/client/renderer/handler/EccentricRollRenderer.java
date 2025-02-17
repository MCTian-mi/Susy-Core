package supersymmetry.client.renderer.handler;

import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;
import supersymmetry.common.tileentities.TileEntityEccentricRoll;

public class EccentricRollRenderer extends GeoBlockRenderer<TileEntityEccentricRoll> {

    public EccentricRollRenderer() {
        super(new EccentricRollModel());
    }
}
