package io.github.symmetricdevs.supersymmetry.common.faction;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

@Mod.EventBusSubscriber(modid = Supersymmetry.MODID)
public class FactionHate {

    private static final String TAG_ROOT = "susy";
    private static final String TAG_FACTION = "faction";
    private static final String TAG_HATE = "hate";

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().world.isRemote) return;

        LivingEntity dead = (LivingEntity) event.getEntity();

        // Get killer
        Entity source = event.getSource().getTrueSource();
        if (!(source instanceof Player)) return;

        Player player = (Player) source;

        CompoundTag entityTag = dead.getEntityData();
        if (!entityTag.hasKey(TAG_ROOT)) return;

        CompoundTag susy = entityTag.getCompoundTag(TAG_ROOT);

        String faction = susy.getString(TAG_FACTION);
        if (faction.isEmpty()) return;

        int hateValue = susy.getInteger(TAG_HATE);

        // Apply to player
        FactionHateManager.addHate(player, faction, hateValue);
    }

    // making sure the hate stays after you die
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity().world.isRemote) return;

        Player original = event.getOriginal();
        Player clone = (Player) event.getEntity();

        CompoundTag originalData = original.getEntityData();
        if (!originalData.hasKey(TAG_ROOT)) return;

        CompoundTag susyData = originalData.getCompoundTag(TAG_ROOT);
        if (!susyData.hasKey(TAG_HATE)) return;

        CompoundTag cloneData = clone.getEntityData();
        cloneData.setTag(TAG_ROOT, susyData.copy());
    }
}
