package io.github.symmetricdevs.supersymmetry.client.renderer.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Ported to 1.20.1: replaces ParticleFlame (removed) with FlameParticle
 * which is the modern equivalent.
 */
@OnlyIn(Dist.CLIENT)
public class SusyParticleFlame extends FlameParticle {

    protected float hugeFlameScale;

    public SusyParticleFlame(ClientLevel level, double x, double y, double z,
                             double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.quadSize *= 3.0F;
        this.hugeFlameScale = this.quadSize;
    }

    @Override
    public void tick() {
        super.tick();
        float f0 = (float) this.age / (float) this.lifetime;
        this.quadSize = this.hugeFlameScale * (1.0F - f0 * f0 * 0.5F);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements net.minecraft.client.particle.ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            SusyParticleFlame particle = new SusyParticleFlame(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
