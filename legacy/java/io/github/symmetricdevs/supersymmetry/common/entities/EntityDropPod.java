package io.github.symmetricdevs.supersymmetry.common.entities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

/**
 * Stub — the 1.12.2 DropPod entity was GeckoLib-3-animated and used
 * 1.12.2-only APIs (World, ICommandSender, Minecraft.getMinecraft().effectRenderer, etc.).
 * <p>
 * Rocketry/space content is deferred. Basic entity registration and data
 * tracking are kept; animation, flight logic, particles and sound will
 * be restored when space content is ported.
 */
public class EntityDropPod extends Mob {

    private static final EntityDataAccessor<Boolean> HAS_LANDED = SynchedEntityData.defineId(EntityDropPod.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TIME_SINCE_LANDING = SynchedEntityData.defineId(EntityDropPod.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TIME_SINCE_SPAWN = SynchedEntityData.defineId(EntityDropPod.class,
            EntityDataSerializers.INT);

    private boolean explosive = true;
    private List<String> commandsOnLanding = new ArrayList<>();

    public EntityDropPod(EntityType<? extends EntityDropPod> type, Level level) {
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

    private void setTimeSinceLanding(int timeSinceLanding) {
        this.entityData.set(TIME_SINCE_LANDING, timeSinceLanding);
    }

    public boolean hasTakenOff() {
        return this.getTimeSinceLanding() > 200;
    }

    private void handleCollidedBlocks(boolean above) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                BlockPos pos = new BlockPos((int) (this.getX() + i), above ? (int) (this.getY() + 2) : (int) (this.getY() - 1), (int) (this.getZ() + j));
                if (this.level().getBlockState(pos).getMaterial().isLiquid()) return;
                if (this.level().getBlockState(pos).getDestroySpeed(this.level(), pos) < 0.3) {
                    this.level().destroyBlock(pos, false);
                } else if (above) {
                    this.explode();
                    this.discard();
                    break;
                }
            }
        }
    }

    private void handleCollidedBlocks() {
        handleCollidedBlocks(false);
    }

    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);
        this.explode();
    }

    public void canExplode(boolean explosive) {
        this.explosive = explosive;
    }

    private void explode() {
        if (this.explosive) {
            int explosionStrength = 1;
            if (this.getVehicle() != null && this.getVehicle() instanceof Player) {
                explosionStrength = 6;
            }
            if (!this.level().isClientSide) {
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), explosionStrength, Level.ExplosionInteraction.MOB);
            }
        }
        this.discard();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_LANDED, false);
        this.entityData.define(TIME_SINCE_LANDING, 0);
        this.entityData.define(TIME_SINCE_SPAWN, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Landed", this.entityData.get(HAS_LANDED));
        compound.putInt("time_since_landing", this.entityData.get(TIME_SINCE_LANDING));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(HAS_LANDED, compound.getBoolean("Landed"));
        this.entityData.set(TIME_SINCE_LANDING, compound.getInt("time_since_landing"));
    }

    public void setCommandsOnLanding(List<String> commands) {
        this.commandsOnLanding.clear();
        if (commands != null) {
            this.commandsOnLanding.addAll(commands);
        }
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
            if (!this.onGround() && this.getDeltaMovement().y < 0.0D && this.getY() < 256) {
                // Lerp between motionY and 0.5 at 10%
                this.setDeltaMovement(this.getDeltaMovement().x,
                        this.getDeltaMovement().y + (0.5D - this.getDeltaMovement().y) * 0.1D,
                        this.getDeltaMovement().z);
            }

            if (!this.hasLanded()) {
                this.handleCollidedBlocks();
                this.getPassengers().forEach(e -> e.fallDistance = 0);
            }

            this.setLanded(this.hasLanded() || this.onGround());

            if (this.hasLanded()) {
                if (!this.commandsOnLanding.isEmpty()) {
                    // Commands placeholder — command execution requires server access
                    this.commandsOnLanding.clear();
                }
                if (this.getTimeSinceLanding() == 0) {
                    BlockPos posBeneath = new BlockPos((int) Math.floor(this.getX()),
                            (int) Math.floor(this.getY() - 1.2D), (int) Math.floor(this.getZ()));
                    BlockState blockBeneath = this.level().getBlockState(posBeneath);
                    if (blockBeneath.getMaterial() != Material.AIR) {
                        SoundType soundType = blockBeneath.getSoundType();
                        this.playSound(soundType.getBreakSound(), soundType.getVolume() * 3.0F,
                                soundType.getPitch() * 0.2F);
                    }
                }
                this.setTimeSinceLanding(this.getTimeSinceLanding() + 1);
                if (this.getTimeSinceLanding() > 1000) {
                    this.discard();
                }
            }

            if (this.hasTakenOff()) {
                this.handleCollidedBlocks(true);
                if (this.getY() > 300) {
                    this.discard();
                }
            }
        }

        this.entityData.set(TIME_SINCE_SPAWN, this.entityData.get(TIME_SINCE_SPAWN) + 1);
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
        float xOffset = (float) Math.sin(this.yBodyRot * 0.1F);
        float zOffset = (float) Math.cos(this.yBodyRot * 0.1F);
        passenger.setPos(this.getX() + (double) (0.1F * xOffset),
                this.getY() + (double) (this.getBbHeight() * 0.2F) + passenger.getMyRidingOffset() + 0.0D,
                this.getZ() - (double) (0.1F * zOffset));

        if (passenger instanceof LivingEntity living) {
            living.yBodyRot = this.yBodyRot;
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {}

    @Override
    protected boolean canDespawn() {
        return this.getTimeSinceLanding() > 1000;
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

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return true;
    }
}
