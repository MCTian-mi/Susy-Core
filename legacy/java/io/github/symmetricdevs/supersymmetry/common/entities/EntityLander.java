package io.github.symmetricdevs.supersymmetry.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

/**
 * Stub — GeckoLib 3 animation and inventory container removed.
 * <p>
 * The lander is a 1.12.2 GeckoLib-3-animated entity used by the
 * landing pad. Animation and inventory logic have been stripped
 * pending GeckoLib 4 and container system port.
 */
public class EntityLander extends EntityAbstractRocket {

    private static final EntityDataAccessor<Boolean> HAS_LANDED = SynchedEntityData.defineId(EntityLander.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TIME_SINCE_LANDING = SynchedEntityData.defineId(EntityLander.class,
            EntityDataSerializers.INT);

    public EntityLander(EntityType<? extends EntityLander> type, Level level) {
        super(type, level);
    }

    public boolean canPlayerDismount() {
        return this.isRemoved() || this.getTimeSinceLanding() >= 30;
    }

    public boolean hasLanded() {
        return this.entityData.get(HAS_LANDED);
    }

    public void setLanded(boolean landed) {
        this.entityData.set(HAS_LANDED, landed);
    }

    public int getTimeSinceLanding() {
        return this.entityData.get(TIME_SINCE_LANDING);
    }

    public void setTimeSinceLanding(int timeSinceLanding) {
        this.entityData.set(TIME_SINCE_LANDING, timeSinceLanding);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_LANDED, false);
        this.entityData.define(TIME_SINCE_LANDING, 0);
    }

    @Override
    protected float getExplosionStrength() {
        return 1;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.canPlayerDismount()) {
            for (Entity rider : this.getPassengers()) {
                rider.stopRiding();
            }
        }

        if (!this.level().isClientSide) {
            if (!this.hasLanded()) {
                this.getPassengers().forEach(e -> e.fallDistance = 0);
            }

            this.setLanded(this.hasLanded() || this.onGround());

            if (this.hasLanded()) {
                this.setTimeSinceLanding(this.getTimeSinceLanding() + 1);
            }

            if (this.isLaunched()) {
                this.discard();
            }
        }
    }

    @Override
    protected void removePassenger(@NotNull Entity passenger) {
        if (this.canPlayerDismount()) {
            super.removePassenger(passenger);
            if (passenger instanceof Mob living) {
                living.setNoAi(false);
            }
        }
    }

    @Override
    public void positionRider(@NotNull Entity passenger) {
        super.positionRider(passenger);
        if (passenger instanceof Mob living) {
            living.setNoAi(false);
        }
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("landed", this.hasLanded());
        compound.putInt("time_since_landing", this.getTimeSinceLanding());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setLanded(compound.getBoolean("landed"));
        this.setTimeSinceLanding(compound.getInt("time_since_landing"));
    }
}
