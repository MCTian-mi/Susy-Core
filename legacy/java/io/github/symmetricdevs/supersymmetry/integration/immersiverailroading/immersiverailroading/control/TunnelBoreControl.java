package io.github.symmetricdevs.supersymmetry.integration.immersiverailroading.control;

public class TunnelBoreControl {

    private int distanceLeft;

    public TunnelBoreControl(int distance) {
        this.distanceLeft = distance;
    }

    public TunnelBoreControl() {
        this(-1);
    }

    public void onTick() {}
}
