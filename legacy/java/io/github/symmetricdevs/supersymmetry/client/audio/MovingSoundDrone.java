package io.github.symmetricdevs.supersymmetry.client.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import io.github.symmetricdevs.supersymmetry.common.entities.EntityDrone;

/**
 * Ported from 1.12.2 {@code MovingSound} → Modern {@link AbstractTickableSoundInstance}.
 * <p>
 * Manages the drone takeoff/flyby sound while the drone entity is alive.
 */
public class MovingSoundDrone extends AbstractTickableSoundInstance {

    private final EntityDrone drone;
    private float distance = 0.0F;

    public MovingSoundDrone(EntityDrone drone, SoundEvent soundEvent) {
        super(soundEvent, SoundSource.NEUTRAL, drone.getRandom());
        this.drone = drone;
        this.looping = false;
        this.volume = 0.5F;
    }

    public void startPlaying() {
        this.volume = 0.5F;
    }

    public void stopPlaying() {
        this.volume = 0.0F;
    }

    @Override
    public void tick() {
        if (this.drone.isRemoved()) {
            this.stop();
        } else {
            this.x = (float) this.drone.getX();
            this.y = (float) this.drone.getY();
            this.z = (float) this.drone.getZ();

            this.distance = Math.min(this.distance + 0.0025F, 1.0F);
        }
    }
}
