package io.github.symmetricdevs.supersymmetry.client.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

import io.github.symmetricdevs.supersymmetry.api.sound.SusySounds;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityRocket;

public class MovingSoundRocket extends AbstractTickableSoundInstance {

    private final EntityRocket rocket;
    private float distance = 0.0F;

    public MovingSoundRocket(EntityRocket rocket) {
        super(SusySounds.ROCKET_LAUNCH, SoundSource.NEUTRAL, rocket.getRandom());
        this.attenuation = Attenuation.NONE;
        this.rocket = rocket;
        this.looping = false;
        this.delay = 0;
        this.volume = 1.F;
    }

    public void startPlaying() {
        this.volume = 1.F;
    }

    public void stopPlaying() {
        this.volume = 0.0F;
    }

    @Override
    public void tick() {
        if (this.rocket.isRemoved()) {
            this.stop();
        } else {
            this.x = (float) this.rocket.getX();
            this.y = (float) this.rocket.getY();
            this.z = (float) this.rocket.getZ();

            this.distance = Mth.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
        }
    }
}
