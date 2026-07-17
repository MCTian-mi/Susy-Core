package io.github.symmetricdevs.supersymmetry.client.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import io.github.symmetricdevs.supersymmetry.api.sound.SusySounds;

@OnlyIn(Dist.CLIENT)
public class MovingSoundJetEngine extends AbstractTickableSoundInstance {

    private static final float MAX_VOLUME = 1.0F;
    private boolean isThrottled = false;
    private final Player player;
    private float baseVolume = 0.0F;

    public MovingSoundJetEngine(Player player) {
        super(SusySounds.JET_ENGINE_LOOP, SoundSource.PLAYERS, player.getRandom());
        this.player = player;
        this.looping = true;
        this.delay = 0;
        this.volume = MAX_VOLUME;
    }

    public void startPlaying() {
        this.baseVolume = MAX_VOLUME;
    }

    public void stopPlaying() {
        this.baseVolume = 0;
    }

    public boolean isThrottled() {
        return this.isThrottled;
    }

    public void setThrottled(boolean isThrottled) {
        this.isThrottled = isThrottled;
    }

    public boolean isPlaying() {
        return volume > 0.0F;
    }

    @Override
    public void tick() {
        if (this.player.isRemoved()) {
            this.stop();
        } else {
            this.x = (float) (player.getX() + player.getDeltaMovement().x);
            this.y = (float) (player.getY() + player.getDeltaMovement().y);
            this.z = (float) (player.getZ() + player.getDeltaMovement().z);
        }
        float throttleMultiplier = isThrottled ? 0.2F : 1;
        if (volume < baseVolume * throttleMultiplier) {
            volume += 0.05F;
        } else {
            volume -= 0.02F;
        }
    }
}
