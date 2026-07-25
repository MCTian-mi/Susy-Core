package io.github.symmetricdevs.supersymmetry.client.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import io.github.symmetricdevs.supersymmetry.api.sound.SusySounds;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityDrone;

/** Takeoff sound that follows the visible drone until it leaves the client world. */
public class MovingSoundDrone extends AbstractTickableSoundInstance {

    private final EntityDrone drone;
    private float volumeFade;

    public MovingSoundDrone(EntityDrone drone) {
        super(SusySounds.DRONE_TAKEOFF.get(), SoundSource.NEUTRAL, RandomSource.create());
        this.drone = drone;
        this.looping = false;
        this.volume = 0.0F;
        this.pitch = 1.0F;
        this.attenuation = SoundInstance.Attenuation.LINEAR;
        this.x = drone.getX();
        this.y = drone.getY();
        this.z = drone.getZ();
    }

    public void startPlaying() {
        volumeFade = 0.0F;
        volume = 0.0F;
    }

    public void stopPlaying() {
        stop();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (!drone.isAlive() || drone.isRemoved()) {
            stop();
            return;
        }

        x = drone.getX();
        y = drone.getY();
        z = drone.getZ();
        volumeFade = Math.min(1.0F, volumeFade + 0.025F);
        volume = 0.5F * volumeFade;
    }
}
