package io.github.symmetricdevs.supersymmetry.common.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.Constants;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

public class MobHordeWorldData extends SavedData
                               implements Function<UUID, MobHordePlayerData> {

    private static final String DATA_NAME = Supersymmetry.MODID + "_InvasionData";
    private final Map<UUID, MobHordePlayerData> playerDataMap;

    public MobHordeWorldData() {
        this(DATA_NAME);
    }

    public MobHordeWorldData(String name) {
        super();
        this.playerDataMap = new HashMap<>();
    }

    public static MobHordeWorldData get(Level world) {
        return world.getDataStorage().computeIfAbsent(MobHordeWorldData::new, DATA_NAME);
    }

    @Override
    public MobHordePlayerData apply(UUID uuid) {
        return this.getPlayerData(uuid);
    }

    public MobHordePlayerData getPlayerData(UUID uuid) {
        MobHordePlayerData invasionPlayerData = this.playerDataMap.get(uuid);

        if (invasionPlayerData == null) {
            invasionPlayerData = new MobHordePlayerData();
            this.playerDataMap.put(uuid, invasionPlayerData);
            this.setDirty();
        }

        return invasionPlayerData;
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        ListTag tagList = tag.getList("PlayerData", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag tagEntry = tagList.getCompound(i);
            UUID uuid = UUID.fromString(tagEntry.getString("UUID"));
            MobHordePlayerData data = new MobHordePlayerData();
            data.deserializeNBT(tagEntry.getCompound("Data"));
            this.playerDataMap.put(uuid, data);
        }
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag tag) {
        ListTag tagList = new ListTag();

        for (Map.Entry<UUID, MobHordePlayerData> entry : this.playerDataMap.entrySet()) {
            CompoundTag tagEntry = new CompoundTag();
            tagEntry.putString("UUID", entry.getKey().toString());
            tagEntry.put("Data", entry.getValue().serializeNBT());
            tagList.add(tagEntry);
        }

        tag.put("PlayerData", tagList);

        return tag;
    }
}
