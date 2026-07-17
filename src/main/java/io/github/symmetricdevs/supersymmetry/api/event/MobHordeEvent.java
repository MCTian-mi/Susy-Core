package io.github.symmetricdevs.supersymmetry.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Modern Forge event fired when a mob horde occurs.
 * <p>
 * Cancel to prevent the horde from spawning.
 */
@Cancelable
public class MobHordeEvent extends Event {

    private final Player player;
    private final List<LivingEntity> mobs;
    private final String hordeType;

    public MobHordeEvent(Player player, List<LivingEntity> mobs, String hordeType) {
        this.player = player;
        this.mobs = mobs != null ? Collections.unmodifiableList(mobs) : List.of();
        this.hordeType = hordeType;
    }

    public Player getPlayer() {
        return player;
    }

    public List<LivingEntity> getMobs() {
        return mobs;
    }

    @Nullable
    public String getHordeType() {
        return hordeType;
    }
}