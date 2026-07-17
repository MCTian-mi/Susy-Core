package io.github.symmetricdevs.supersymmetry.common.event;

import java.util.*;
import java.util.stream.Collectors;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import io.github.symmetricdevs.supersymmetry.api.event.MobHordeEvent;

public class MobHordePlayerData implements INBTSerializable<CompoundTag> {

    public static int DEFAULT_GRACE_PERIOD = 72000; // setting grace period as 1 hour (also made it static)

    // Player cooldown for all events.
    public int ticksUntilCanSpawn;
    public int gracePeriod;
    public int ticksActive;
    public int timeoutPeriod;
    public int[] invasionTimers;
    public boolean hasActiveInvasion = false;
    public List<UUID> invasionEntitiesUUIDs = new ArrayList<>();
    public String currentInvasion = "";
    public Set<String> completedScriptedEvents = new HashSet<>();

    public MobHordePlayerData() {
        this.gracePeriod = DEFAULT_GRACE_PERIOD;
        this.ticksUntilCanSpawn = DEFAULT_GRACE_PERIOD;
        this.invasionTimers = new int[MobHordeEvent.EVENTS.size()];
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag result = new CompoundTag();
        result.putInt("ticksUntilCanSpawn", ticksUntilCanSpawn);
        result.putIntArray("invasionTimers", invasionTimers);
        result.putBoolean("hasActiveInvasion", hasActiveInvasion);
        if (this.hasActiveInvasion && !this.invasionEntitiesUUIDs.isEmpty()) {
            result.putString("currentInvasion", currentInvasion);
            result.putInt("timeoutPeriod", this.timeoutPeriod);
            result.putInt("ticksActive", this.ticksActive);
            ListTag tagList = new ListTag();
            invasionEntitiesUUIDs.stream()
                    .forEach(uuid -> tagList.add(NbtUtils.createUUID(uuid)));
            result.put("invasionEntitiesUUIDs", tagList);
        }
        ListTag scriptedList = new ListTag();
        for (String key : completedScriptedEvents) {
            CompoundTag entry = new CompoundTag();
            entry.putString("key", key);
            scriptedList.add(entry);
        }
        result.put("completedScriptedEvents", scriptedList);
        return result;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ticksUntilCanSpawn = nbt.getInt("ticksUntilCanSpawn");
        invasionTimers = Arrays.copyOf(nbt.getIntArray("invasionTimers"), MobHordeEvent.EVENTS.size());
        hasActiveInvasion = nbt.getBoolean("hasActiveInvasion");
        if (hasActiveInvasion) {
            invasionEntitiesUUIDs.clear();
            this.currentInvasion = nbt.getString("currentInvasion");
            this.timeoutPeriod = nbt.getInt("timeoutPeriod");
            this.ticksActive = nbt.getInt("ticksActive");
            ListTag tagList = nbt.getList("invasionEntitiesUUIDs", 10); // TAG_COMPOUND
            for (int i = 0; i < tagList.size(); i++) {
                invasionEntitiesUUIDs.add(NbtUtils.loadUUID(tagList.getCompound(i)));
            }
        }
        completedScriptedEvents.clear();
        ListTag scriptedList = nbt.getList("completedScriptedEvents", 10); // TAG_COMPOUND
        for (int i = 0; i < scriptedList.size(); i++) {
            CompoundTag tag = scriptedList.getCompound(i);
            completedScriptedEvents.add(tag.getString("key"));
        }
    }

    public boolean hasCompleted(String key) {
        return completedScriptedEvents.contains(key);
    }

    public void markCompleted(String key) {
        completedScriptedEvents.add(key);
    }

    public void update(ServerPlayer player) {
        if (hasActiveInvasion) {
            ++ticksActive;
            if (this.ticksActive > this.timeoutPeriod) {
                this.stopInvasion(player);
            } else return;
        }
        ticksUntilCanSpawn--;
        for (int i = 0; i < invasionTimers.length; i++) {
            invasionTimers[i]--;
        }
        if (ticksUntilCanSpawn <= 0 && Math.random() < 0.001) {
            List<Integer> doableEvents = new ArrayList<>();
            List<MobHordeEvent> events = MobHordeEvent.EVENTS.values().stream()
                    .collect(Collectors.toList());
            MobHordeEvent event;
            for (int i = 0; i < MobHordeEvent.EVENTS.values().size(); i++) {
                event = events.get(i);
                if (event.canRun(player) && invasionTimers[i] <= 0) {
                    doableEvents.add(i);
                }
            }
            if (!doableEvents.isEmpty()) {
                ticksUntilCanSpawn = gracePeriod;
                int index = doableEvents.get((int) (Math.random() * doableEvents.size()));
                event = events.get(index);
                if (event.run(player, this::addEntity)) {
                    invasionTimers[index] = event.getNextDelay();

                    this.setCurrentInvasion(event);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        LivingEntity deadEntity = event.getEntity();
        UUID deadEntityUUID = deadEntity.getUUID();

        if (invasionEntitiesUUIDs.contains(deadEntityUUID)) {
            removeDeadEntity(deadEntityUUID);

            // Check if all spawned entities are dead
            if (invasionEntitiesUUIDs.isEmpty()) {
                this.finishInvasion();
            }
        }
    }

    public void setCurrentInvasion(MobHordeEvent event) {
        this.currentInvasion = event.KEY;
        this.timeoutPeriod = event.timeoutPeriod;
        this.hasActiveInvasion = true;
        this.ticksActive = 0;
    }

    public void addEntity(UUID uuid) {
        this.invasionEntitiesUUIDs.add(uuid);
    }

    private void removeDeadEntity(UUID deadEntityUUID) {
        this.invasionEntitiesUUIDs.remove(deadEntityUUID);
    }

    public void finishInvasion() {
        this.hasActiveInvasion = false;
        this.currentInvasion = "";
        this.ticksActive = 0;
    }

    public void stopInvasion(ServerPlayer player) {
        if (!this.hasActiveInvasion) return;

        ServerLevel world = player.serverLevel();

        for (UUID uuid : invasionEntitiesUUIDs) {
            Entity entity = world.getEntity(uuid);

            if (entity == null) continue;

            CompoundTag entityTag = entity.getPersistentData();
            if (!entityTag.contains("susy")) continue;

            CompoundTag susy = entityTag.getCompound("susy");

            String faction = susy.getString("faction");
            int hate = susy.getInt("hate");
            hate = hate * -1;

            if (!faction.isEmpty()) {
                // surviving mob inverts hate and adds to player
                // TODO: Port FactionHateManager
                // FactionHateManager.addHate(player, faction, hate);
            }

            // despawn / escape
            entity.discard();
        }

        this.invasionEntitiesUUIDs.clear();
        this.finishInvasion();
    }

    // moved over loxos code
    public void killInvasion(ServerPlayer player) {
        if (this.hasActiveInvasion) {
            ServerLevel world = player.serverLevel();
            this.invasionEntitiesUUIDs.stream()
                    .map(uuid -> world.getEntity(uuid))
                    .filter(Objects::nonNull)
                    .forEach(entity -> entity.discard());
            // Will get called implicitly from onEntityDeath, but I am doing it again just to be sure
            this.finishInvasion();
        }
    }
}
