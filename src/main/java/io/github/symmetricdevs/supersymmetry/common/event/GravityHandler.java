package io.github.symmetricdevs.supersymmetry.common.event;

import java.util.WeakHashMap;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;

// My endless gratitude to the AdvancedRocketry team for the gravity code

public class GravityHandler {

    public static final float LIVING_OFFSET = 0.0755f;
    public static final float FLUID_LIVING_OFFSET = 0.02f;
    public static final float THROWABLE_OFFSET = 0.03f;
    public static final float OTHER_OFFSET = 0.04f;
    public static final float ARROW_OFFSET = 0.05f;

    private static WeakHashMap<Entity, Double> entityMap = new WeakHashMap<>();

    public static void applyGravity(Entity entity) {
        if (entity.hasNoGravity()) return;
        // NOTE: Elytra gravity logic port deferred to tick handler
        if ((!(entity instanceof Player) && !(entity instanceof FlyingMob)) ||
                (!(entity instanceof FlyingMob) && !(((Player) entity).getAbilities().flying ||
                        ((LivingEntity) entity).isFallFlying()))) {
            Double d;
            if (entityMap.containsKey(entity) && (d = entityMap.get(entity)) != null) {

                double multiplier = (isOtherEntity(entity) || entity instanceof ItemEntity) ? OTHER_OFFSET * d :
                        (entity instanceof AbstractArrow) ? ARROW_OFFSET * d :
                                (entity instanceof ThrowableProjectile) ? THROWABLE_OFFSET * d : LIVING_OFFSET * d;

                entity.setDeltaMovement(entity.getDeltaMovement().add(0, multiplier, 0));

            } else if (entity.level().dimensionTypeId().hashCode() != 0) {
                // TODO: Re-enable planet-specific gravity once SuSyDimensions/WorldProviderPlanet are ported
                double gravMult = 1.0; // placeholder: default gravity

                if (entity instanceof ItemEntity)
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, -(gravMult * OTHER_OFFSET - OTHER_OFFSET), 0));
                else if (isOtherEntity(entity))
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, -(gravMult * OTHER_OFFSET - OTHER_OFFSET), 0));
                else if (entity instanceof ThrowableProjectile)
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, -(gravMult * THROWABLE_OFFSET - THROWABLE_OFFSET), 0));
                else if (entity instanceof AbstractArrow)
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, -(gravMult * ARROW_OFFSET - ARROW_OFFSET), 0));
                else if (entity instanceof LivingEntity && (entity.isInWater() || entity.isInLava())) {
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, -(gravMult * FLUID_LIVING_OFFSET - FLUID_LIVING_OFFSET), 0));
                } else if (entity instanceof LivingEntity) {
                    // Normally gravity works for living entities by accelerating motionY by 0.08, and then applying
                    // "drag"
                    // in the form of multiplying the resulting value by 0.98.
                    // This code we have here runs *before* all of that, and I'm not about to make a second mixin to
                    // change that.
                    // Let's say we've figured out that the *next* motionY should be X after the vanilla code runs.
                    // X = (motionY - 0.08) * 0.98
                    // motionY = X / 0.98 + 0.08

                    double drag = 0.98; // placeholder: default drag
                    double intended = (entity.getDeltaMovement().y - (gravMult * 0.08)) * drag;
                    entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0, 1).add(0, intended / 0.98 + 0.08, 0));
                }
            }
        }
    }

    public static boolean isOtherEntity(Entity entity) {
        return entity instanceof Boat || entity instanceof AbstractMinecart ||
                entity instanceof FallingBlockEntity || entity instanceof PrimedTnt;
    }
}
