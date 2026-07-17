package io.github.symmetricdevs.supersymmetry.client.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

import io.github.symmetricdevs.supersymmetry.api.sound.SusySounds;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityLander;

public class MovingSoundLander extends AbstractTickableSoundInstance {

    private final EntityLander lander;
    private float distance = 0.0F;

    public MovingSoundLander(EntityLander lander) {
        super(SusySounds.ROCKET_LOOP, SoundSource.NEUTRAL, lander.getRandom());
        this.lander = lander;
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
        if (this.lander.isRemoved()) {
            this.stop();
        } else {
            this.x = (float) this.lander.getX();
            this.y = (float) this.lander.getY();
            this.z = (float) this.lander.getZ();

            this.distance = Mth.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
        }
    }
}
