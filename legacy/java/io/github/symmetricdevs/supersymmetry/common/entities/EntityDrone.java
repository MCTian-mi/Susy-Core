package io.github.symmetricdevs.supersymmetry.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

/**
 * Stub — GeckoLib 3 animation has been removed.
 * <p>
 * The drone entity is a 1.12.2 GeckoLib-3-animated mob used by the
 * {@code MetaTileEntityDronePad} machine. GeckoLib 3 is not available on 1.20.1;
 * a GeckoLib 4 migration is needed before this entity can function.
 * <p>
 * The entity registration, basic data tracking, and NBT serialization remain;
 * animation and rendering will be added when GeckoLib 4 rendering is set up.
 */
public class EntityDrone extends Mob {

    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(EntityDrone.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PAD_ALTITUDE = SynchedEntityData.defineId(EntityDrone.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DESCENDING_MODE = SynchedEntityData.defineId(EntityDrone.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_LANDED = SynchedEntityData.defineId(EntityDrone.class,
            EntityDataSerializers.BOOLEAN);

    public EntityDrone(EntityType<? extends EntityDrone> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AGE, 0);
        this.entityData.define(PAD_ALTITUDE, 0);
        this.entityData.define(DESCENDING_MODE, false);
        this.entityData.define(HAS_LANDED, false);
    }

    // --- Sound playback (GeckoLib 4 deferred) ---

    @Override
    public void tick() {
        super.tick();
        // Custom flight/movement logic removed; will be re-added with GeckoLib 4.
    }

    @Override
    protected boolean isMovementNoise() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean canBeLeashed(@NotNull Player player) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean canBeAffectedByPotions() {
        return false;
    }

    @Override
    public void knockback(double strength, double x, double z) {}

    public boolean reachedSky() {
        return this.getY() > 256 && !this.isRemoved();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Age", this.entityData.get(AGE));
        compound.putInt("PadAltitude", this.entityData.get(PAD_ALTITUDE));
        compound.putBoolean("DescendingMode", this.entityData.get(DESCENDING_MODE));
        compound.putBoolean("HasLanded", this.entityData.get(HAS_LANDED));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(AGE, compound.getInt("Age"));
        this.entityData.set(PAD_ALTITUDE, compound.getInt("PadAltitude"));
        this.entityData.set(DESCENDING_MODE, compound.getBoolean("DescendingMode"));
        this.entityData.set(HAS_LANDED, compound.getBoolean("HasLanded"));
    }
}
