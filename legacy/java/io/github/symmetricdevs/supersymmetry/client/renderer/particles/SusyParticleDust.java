package io.github.symmetricdevs.supersymmetry.client.renderer.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Stub — the 1.12.2 version extended ParticleSmokeNormal which was a
 * private class removed in 1.20.1. Re-implement as a standard
 * SimpleAnimatedParticle when particle system is re-enabled.
 */
@OnlyIn(Dist.CLIENT)
public class SusyParticleDust extends SimpleAnimatedParticle {

    protected SusyParticleDust(ClientLevel level, double x, double y, double z, SpriteSet sprites, float scale) {
        super(level, x, y, z, sprites, scale);
        this.gravity = 0.5F;
    }

    @Override
    public void tick() {
        super.tick();
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
            SusyParticleDust particle = new SusyParticleDust(level, x, y, z, this.spriteSet, 1.0F);
            particle.setSpriteFromAge(this.spriteSet);
            return particle;
        }
    }
}
