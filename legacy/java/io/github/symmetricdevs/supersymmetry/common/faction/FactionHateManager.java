package io.github.symmetricdevs.supersymmetry.common.faction;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

public class FactionHateManager {

    private static final String TAG_ROOT = "susy";
    private static final String TAG_HATE = "hate";

    public static int getHate(Player player, String faction) {
        CompoundTag root = player.getEntityData().getCompoundTag(TAG_ROOT);
        CompoundTag hate = root.getCompoundTag(TAG_HATE);

        return hate.getInteger(faction);
    }

    public static void addHate(Player player, String faction, int amount) {
        CompoundTag root = player.getEntityData().getCompoundTag(TAG_ROOT);
        CompoundTag hate = root.getCompoundTag(TAG_HATE);

        int current = hate.getInteger(faction);

        int next = current + amount;

        // baseline hate, once you progress there is no going back bellow this point.
        int baseline = supersymmetry.common.faction.FactionBaselineRegistry
                .getBaseline((net.minecraft.server.level.ServerPlayer) player);
        if (next < baseline) {
            next = baseline;
        }
        next = Math.max(0, next);

        hate.setInteger(faction, next);
        root.setTag(TAG_HATE, hate);
        player.getEntityData().setTag(TAG_ROOT, root);
    }

    public static void setHate(Player player, String faction, int amount) {
        // debug only, use addHate
        CompoundTag root = player.getEntityData().getCompoundTag(TAG_ROOT);
        CompoundTag hate = root.getCompoundTag(TAG_HATE);
        hate.setInteger(faction, amount);
        root.setTag(TAG_HATE, hate);
        player.getEntityData().setTag(TAG_ROOT, root);
    }
}
