package io.github.symmetricdevs.supersymmetry.client.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

import io.github.symmetricdevs.supersymmetry.api.sound.SusySounds;

public class MovingSoundDropPod extends AbstractTickableSoundInstance {

    private final net.minecraft.world.entity.Entity dropPod;
    private float distance = 0.0F;

    public MovingSoundDropPod(net.minecraft.world.entity.Entity dropPod) {
        super(SusySounds.ROCKET_LOOP, SoundSource.NEUTRAL, dropPod.getRandom());
        this.dropPod = dropPod;
        this.looping = true;
        this.delay = 0;
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
        if (this.dropPod.isRemoved()) {
            this.stop();
        } else {
            this.x = (float) this.dropPod.getX();
            this.y = (float) this.dropPod.getY();
            this.z = (float) this.dropPod.getZ();

            this.distance = Mth.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
        }
    }
}
