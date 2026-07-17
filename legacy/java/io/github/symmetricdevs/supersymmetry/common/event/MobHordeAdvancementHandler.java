package io.github.symmetricdevs.supersymmetry.common.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.event.MobHordeEvent;

@Mod.EventBusSubscriber(modid = Supersymmetry.MODID)
public class MobHordeAdvancementHandler {

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ResourceLocation advancementID = event.getAdvancement().getId();

        MobHordeWorldData worldData = MobHordeWorldData.get(player.level());
        MobHordePlayerData playerData = worldData.getPlayerData(player.getUUID());

        for (MobHordeEvent mobEvent : MobHordeEvent.EVENTS.values()) {

            ResourceLocation required = mobEvent.getRequiredAdvancement();
            if (required == null) continue;

            if (!required.equals(advancementID)) continue;

            if (mobEvent.isRunOnce() && playerData.hasCompleted(mobEvent.KEY)) {
                continue;
            }

            try {
                if (mobEvent.run(player, playerData::addEntity)) {
                    playerData.setCurrentInvasion(mobEvent);

                    if (mobEvent.isRunOnce()) {
                        playerData.markCompleted(mobEvent.KEY);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
