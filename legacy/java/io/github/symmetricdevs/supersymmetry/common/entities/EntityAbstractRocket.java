package io.github.symmetricdevs.supersymmetry.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import io.github.symmetricdevs.supersymmetry.api.items.CargoItemStackHandler;

/**
 * Stub — GeckoLib 3 animation and rocketry fuel system removed.
 * <p>
 * Base class for rocket entities. In 1.20.1 this extends {@link Mob} directly
 * rather than {@link net.minecraft.world.entity.LivingEntity}, matching the
 * {@link EntityDrone} pattern. The rocket fuel and cargo mass abstract methods
 * have been removed pending the rocketry fuel system port.
 */
public abstract class EntityAbstractRocket extends Mob {

    public static final String ROCKET_CONFIG_KEY = "config";

    protected static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(
            EntityAbstractRocket.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> COUNTDOWN_STARTED = SynchedEntityData.defineId(
            EntityAbstractRocket.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(
            EntityAbstractRocket.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> LAUNCH_TIME = SynchedEntityData.defineId(
            EntityAbstractRocket.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> FLIGHT_TIME = SynchedEntityData.defineId(
            EntityAbstractRocket.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> START_POS = SynchedEntityData.defineId(
            EntityAbstractRocket.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Boolean> ACTED = SynchedEntityData.defineId(
            EntityAbstractRocket.class, EntityDataSerializers.BOOLEAN);

    protected CargoItemStackHandler cargo;

    public EntityAbstractRocket(EntityType<? extends EntityAbstractRocket> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LAUNCHED, false);
        this.entityData.define(COUNTDOWN_STARTED, false);
        this.entityData.define(AGE, 0);
        this.entityData.define(LAUNCH_TIME, 0);
        this.entityData.define(FLIGHT_TIME, 0);
        this.entityData.define(START_POS, 0.F);
        this.entityData.define(ACTED, false);
    }

    public boolean isLaunched() {
        return this.entityData.get(LAUNCHED);
    }

    public void setLaunched(boolean launched) {
        this.entityData.set(LAUNCHED, launched);
    }

    public boolean isCountDownStarted() {
        return this.entityData.get(COUNTDOWN_STARTED);
    }

    public void setCountdownStarted(boolean countdownStarted) {
        this.entityData.set(COUNTDOWN_STARTED, countdownStarted);
    }

    public int getAge() {
        return this.entityData.get(AGE);
    }

    public void setAge(Integer age) {
        this.entityData.set(AGE, age);
    }

    public boolean hasActed() {
        return this.entityData.get(ACTED);
    }

    public void setActed(boolean acted) {
        this.entityData.set(ACTED, acted);
    }

    public int getFlightTime() {
        return this.entityData.get(FLIGHT_TIME);
    }

    public void setFlightTime(Integer flightTime) {
        this.entityData.set(FLIGHT_TIME, flightTime);
    }

    public int getLaunchTime() {
        return this.entityData.get(LAUNCH_TIME);
    }

    public void setLaunchTime(Integer launchTime) {
        this.entityData.set(LAUNCH_TIME, launchTime);
    }

    public float getStartPos() {
        return this.entityData.get(START_POS);
    }

    public void setStartPos(Float startPos) {
        this.entityData.set(START_POS, startPos);
    }

    public void startCountdown(int length) {
        this.setCountdownStarted(true);
        this.setLaunchTime(this.getAge() + length);
        this.setStartPos((float) this.getY());
    }

    public void launchRocket() {
        this.setLaunched(true);
        this.setActed(false);
        this.hasImpulse = true;
    }

    public void explode() {
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 4.0F, Level.ExplosionInteraction.MOB);
        }
        this.discard();
    }

    protected abstract float getExplosionStrength();

    @Override
    public void tick() {
        super.tick();

        if (this.getY() > 600 && this.isLaunched()) {
            if (this.hasActed() && this.getPassengers().isEmpty()) {
                this.discard();
            } else {
                this.setActed(true);
            }
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (amount < 30.0F) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.cargo == null) {
            this.cargo = new CargoItemStackHandler(1);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    public CargoItemStackHandler getInventory() {
        return this.cargo;
    }
}
