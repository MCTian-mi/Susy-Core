package io.github.symmetricdevs.supersymmetry.client.renderer.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Ported to 1.20.1: replaces World with ClientLevel, updated Particle API.
 */
@OnlyIn(Dist.CLIENT)
public class SusyParticleFlareSmoke extends SingleQuadParticle {

    public SusyParticleFlareSmoke(ClientLevel level, double x, double y, double z,
                                  double xSpeed, double ySpeed, double zSpeed,
                                  float R, float G, float B) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.xd = (random.nextDouble() - 0.5) * 0.01;
        this.yd = 0.4;
        this.zd = (random.nextDouble() - 0.5) * 0.01;
        this.rCol = R;
        this.gCol = G;
        this.bCol = B;
        this.quadSize = 10f;
        this.lifetime = 100;
        this.gravity = 0.0F;
    }

    @Override
    public void tick() {
        super.tick();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.yd += 0.0005;
        this.move(this.xd, this.yd, this.zd);

        this.alpha = 1.0f - ((float) this.age / this.lifetime);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    protected float getU0() {
        return 0;
    }

    @Override
    protected float getU1() {
        return 1;
    }

    @Override
    protected float getV0() {
        return 0;
    }

    @Override
    protected float getV1() {
        return 1;
    }
}
