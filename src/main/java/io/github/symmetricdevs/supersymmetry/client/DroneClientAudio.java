package io.github.symmetricdevs.supersymmetry.client;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.client.audio.MovingSoundDrone;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityDrone;

/** Starts and stops the drone's client-only takeoff sound with its entity lifecycle. */
@Mod.EventBusSubscriber(modid = Supersymmetry.MOD_ID, value = Dist.CLIENT)
public final class DroneClientAudio {

    private static final Map<EntityDrone, MovingSoundDrone> SOUNDS = new IdentityHashMap<>();

    private DroneClientAudio() {}

    @SubscribeEvent
    public static void onDroneJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof EntityDrone drone) || !event.getLevel().isClientSide) {
            return;
        }

        MovingSoundDrone sound = new MovingSoundDrone(drone);
        SOUNDS.put(drone, sound);
        Minecraft.getInstance().getSoundManager().play(sound);
        sound.startPlaying();
    }

    @SubscribeEvent
    public static void onDroneLeave(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof EntityDrone drone) {
            MovingSoundDrone sound = SOUNDS.remove(drone);
            if (sound != null) {
                sound.stopPlaying();
            }
        }
    }
}
