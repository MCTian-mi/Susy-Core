package io.github.symmetricdevs.supersymmetry.common.faction;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

@Mod.EventBusSubscriber(modid = Supersymmetry.MODID)
public class FactionViolence {

    private static final String TAG_ROOT = "susy";
    private static final String TAG_FACTION = "faction";
    private static final double radius = 32.0;

    // violence
    // checks every mob every tick, probably not the best way to do this
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().world.isRemote) return; // server only
        if (!FactionViolenceManager.isEnabled(event.getEntity().world)) return;
        if (!(event.getEntity() instanceof Mob)) return;

        Mob mob = (Mob) event.getEntity();
        CompoundTag tag = mob.getEntityData();
        if (!tag.hasKey(TAG_ROOT)) return;

        CompoundTag susyTag = tag.getCompoundTag(TAG_ROOT);
        if (!susyTag.hasKey(TAG_FACTION)) return;

        String mobFaction = susyTag.getString(TAG_FACTION);
        if (mobFaction.isEmpty()) return;

        // Clear attack target if dead or invalid
        // not sure if this is needed or not, but techguns will shoot at nothing if it isn't
        // they just built different like that
        if (mob.getAttackTarget() != null &&
                (mob.getAttackTarget().isDead ||
                        !mob.getAttackTarget().isEntityAlive() ||
                        !(mob.getAttackTarget() instanceof net.minecraft.entity.monster.IMob) &&
                                !(mob.getAttackTarget() instanceof net.minecraft.world.entity.player.Player))) {
            mob.setAttackTarget(null);
        }

        // Only assign a new target if none exists
        if (mob.getAttackTarget() != null) return;

        LivingEntity bestTarget = null;
        double bestDistanceSq = Double.MAX_VALUE;

        for (LivingEntity target : mob.world.getEntitiesWithinAABB(LivingEntity.class,
                mob.getEntityBoundingBox().grow(radius))) {

            if (target == mob) continue;
            if (!mob.canEntityBeSeen(target)) continue;

            CompoundTag targetTag = target.getEntityData();
            String targetFaction = "";

            if (targetTag.hasKey(TAG_ROOT)) {
                CompoundTag targetSusy = targetTag.getCompoundTag(TAG_ROOT);
                if (targetSusy.hasKey(TAG_FACTION)) {
                    targetFaction = targetSusy.getString(TAG_FACTION);
                }
            }

            boolean isUnaligned = targetFaction.isEmpty();
            boolean isOpposingFaction = !isUnaligned && !mobFaction.equals(targetFaction);
            boolean shouldAttack = false;

            if (isUnaligned) {
                // Fix the mobs not attacking the player
                if (target instanceof net.minecraft.entity.monster.IMob ||
                        (target instanceof net.minecraft.world.entity.player.Player &&
                                !((net.minecraft.world.entity.player.Player) target).isCreative()) &&
                                !((net.minecraft.world.entity.player.Player) target).isSpectator()) {
                    shouldAttack = true;
                }
            } else if (isOpposingFaction) {
                shouldAttack = true;
            }

            if (!shouldAttack) continue;

            double distSq = mob.getDistanceSq(target);
            if (distSq < bestDistanceSq) {
                bestDistanceSq = distSq;
                bestTarget = target; // smart targetting
            }
        }

        if (bestTarget != null) {
            mob.setAttackTarget(bestTarget);
        }
    }
}
