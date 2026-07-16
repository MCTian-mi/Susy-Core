package io.github.symmetricdevs.supersymmetry.api.particle;

/**
 * Modern port of the 1.12.2 {@code IParticleBeamProvider}.
 */
public interface IParticleBeamProvider {

    ParticleBeam getParticleBeam();

    ParticleBeam insertBeam(ParticleBeam beam);
}
