package io.github.symmetricdevs.supersymmetry.common.event;

import java.util.*;

import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.jetbrains.annotations.NotNull;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.common.item.SuSyArmorItem;

@Mod.EventBusSubscriber(modid = Supersymmetry.MODID)
public class EventHandlers {

    public static final String FIRST_SPAWN = Supersymmetry.MODID + ".first_spawn";
    public static List<DimensionRidingSwapData> travellingPassengers = new ArrayList<>();

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        CompoundTag playerData = event.getEntity().getPersistentData();
        CompoundTag data = playerData.contains(Player.PERSISTED_NBT_TAG) ?
                playerData.getCompound(Player.PERSISTED_NBT_TAG) : new CompoundTag();

        if (!event.getEntity().level().isClientSide && !data.getBoolean(FIRST_SPAWN)) {
            // TODO: Port first-spawn logic (drop pod, lander, etc.)
            data.putBoolean(FIRST_SPAWN, true);
            playerData.put(Player.PERSISTED_NBT_TAG, data);
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        Level level = (Level) event.getLevel();
        GameRules gameRules = level.getGameRules();

        // TODO: Set up custom game rules when SystemProp is available
        // GameRules.addGameRule("doInvasions", "true", GameRules.ValueType.BOOLEAN_VALUE);
        // GameRules.addGameRule("factionViolence", "true", GameRules.ValueType.BOOLEAN_VALUE);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide) return;

        if (!travellingPassengers.isEmpty()) {
            handleEntityTransfer(event.level);
        }

        if (event.phase != Phase.END) {
            return;
        }

        // TODO: Port lander spawn queue processing
        // processLanderSpawnQueue((ServerLevel) event.level);

        // TODO: Port mob horde tick processing once MobHordeWorldData/PlayerData are ported
        // ServerLevel server = (ServerLevel) event.level;
        // if (server.dimension() != Level.OVERWORLD) return;
        // ...
    }

    private static void handleEntityTransfer(Level level) {
        List<DimensionRidingSwapData> toRemove = new ArrayList<>();
        for (DimensionRidingSwapData data : travellingPassengers) {
            Entity mount = data.mount;
            Entity passenger = data.passenger;
            if (mount.level().dimension() != passenger.level().dimension() && passenger.getServer() != null &&
                    mount.level().getGameTime() - data.time > 2) {
                ServerLevel newWorld = passenger.getServer().getLevel(mount.level().dimension());

                passenger.teleportTo(newWorld,
                        mount.getX(), mount.getY(), mount.getZ(),
                        mount.getYRot(), mount.getXRot());

                Entity realMount = newWorld.getEntity(mount.getUUID());
                if (realMount != null) {
                    passenger.startRiding(realMount);
                }
                toRemove.add(data);
            }
        }
        for (DimensionRidingSwapData data : toRemove) {
            travellingPassengers.remove(data);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().getGameTime() % 20 == 0 && event.phase == TickEvent.Phase.START) {
            DimensionBreathabilityHandler.tickPlayer(event.player);
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onEntityLivingFallEventStart(LivingFallEvent event) {
        Entity armor = event.getEntity();
        if (armor instanceof Player player) {
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            if (!boots.isEmpty() && boots.getItem() instanceof SuSyArmorItem) {
                if (player.fallDistance > 3.2F) {
                    player.fallDistance = 0;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityLivingFallEvent(LivingFallEvent event) {
        // TODO: Port planet-specific gravity fall adjustments once WorldProviderPlanet is available
        // if (event.getEntity().level().dimensionType() ... )

        Entity armor = event.getEntity();
        if (armor instanceof Player player) {
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            if (!boots.isEmpty() && boots.getItem() instanceof SuSyArmorItem) {
                player.fallDistance = event.getDistance();
            }
        }
    }

    // TODO: Port torch block place prevention on fireless planets
}
