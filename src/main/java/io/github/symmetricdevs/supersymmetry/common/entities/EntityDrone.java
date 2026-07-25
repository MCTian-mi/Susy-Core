package io.github.symmetricdevs.supersymmetry.common.entities;

import io.github.symmetricdevs.supersymmetry.common.data.SusyMachines;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.CargoDronePadMachine;
import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.DronePadMachine;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Visible drone for drone-pad flight sequences.
 *
 * <p>Controllers retain all transfer state. This entity persists only enough route
 * information to notify its owning controller after a descending drone reaches its
 * landing pad, including when the entity is saved and loaded independently.</p>
 */
public class EntityDrone extends Entity implements GeoEntity {

    private static final double ASCENT_ACCELERATION = 0.125D;
    private static final double MAX_ASCENT_SPEED = 2.0D;
    private static final double DESCENT_SPEED = 2.0D;
    private static final double SKY_ALTITUDE = 256.0D;
    private static final int TAKEOFF_TICKS = 55;
    private static final int UNSET_ALTITUDE = Integer.MIN_VALUE;
    private static final EntityDataAccessor<Boolean> DESCENDING_MODE = SynchedEntityData.defineId(
            EntityDrone.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation TAKEOFF_ANIM = RawAnimation.begin().thenPlay("animation.drone.takeoff");
    private static final RawAnimation FLYING_ANIM = RawAnimation.begin().thenLoop("animation.drone.flying");
    private static final RawAnimation LANDING_ANIM = RawAnimation.begin().thenPlay("animation.drone.landing");

    @Nullable
    private BlockPos landingPos;
    @Nullable
    private BlockPos controllerPos;
    private int padAltitude = UNSET_ALTITUDE;
    private boolean landingReported;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public EntityDrone(EntityType<? extends EntityDrone> entityType, Level level) {
        super(entityType, level);
        noPhysics = true;
        setNoGravity(true);
    }

    /** Configures the pad position to which this drone descends. */
    public @NotNull EntityDrone withLandingPos(BlockPos pos) {
        landingPos = pos.immutable();
        return this;
    }

    /** Configures the controller that owns this drone's flight state. */
    public @NotNull EntityDrone withControllerPos(BlockPos pos) {
        controllerPos = pos.immutable();
        return this;
    }

    /** Aligns the drone model with the facing of its source pad. */
    public void setRotationFromFacing(Direction facing) {
        float yaw = switch (facing) {
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> 270.0F;
            default -> 0.0F;
        };
        setYRot(yaw);
        yRotO = yaw;
    }

    /** Changes this drone from its outbound ascent to a landing flight. */
    public void setDescendingMode() {
        entityData.set(DESCENDING_MODE, true);
    }

    /** Sets the Y coordinate at which the drone should reach its destination pad. */
    public void setPadAltitude(int padAltitude) {
        this.padAltitude = padAltitude;
    }

    /** Returns whether this drone has reached the sky hand-off altitude. */
    public boolean reachedSky() {
        return getY() > SKY_ALTITUDE && !isRemoved();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || isRemoved()) {
            return;
        }

        if (isDroneDescending()) {
            descend();
        } else if (!reachedSky()) {
            ascend();
        } else {
            setDeltaMovement(Vec3.ZERO);
        }
        fallDistance = 0.0F;
    }

    private void ascend() {
        if (tickCount < TAKEOFF_TICKS) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }
        double speed = Math.min(MAX_ASCENT_SPEED, ASCENT_ACCELERATION * (tickCount - TAKEOFF_TICKS + 1));
        Vec3 motion = new Vec3(0.0D, speed, 0.0D);
        setDeltaMovement(motion);
        move(MoverType.SELF, motion);
    }

    private void descend() {
        if (landingPos == null) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        double targetY = padAltitude == UNSET_ALTITUDE ? landingPos.getY() + 0.5D : padAltitude + 0.5D;
        Vec3 target = new Vec3(landingPos.getX() + 0.5D, targetY, landingPos.getZ() + 0.5D);
        Vec3 offset = target.subtract(position());
        double distance = offset.length();
        if (distance <= DESCENT_SPEED) {
            setPos(target);
            setDeltaMovement(Vec3.ZERO);
            reportLanding();
            discard();
            return;
        }

        Vec3 motion = offset.scale(DESCENT_SPEED / distance);
        setDeltaMovement(motion);
        move(MoverType.SELF, motion);
    }

    private void reportLanding() {
        if (landingReported || controllerPos == null) {
            return;
        }
        landingReported = true;
        MetaMachine machine = MetaMachine.getMachine(level(), controllerPos);
        if (machine instanceof DronePadMachine dronePad) {
            dronePad.onDroneLanded(getUUID());
        } else if (machine instanceof CargoDronePadMachine cargoDronePad) {
            cargoDronePad.onDroneLanded(getUUID());
        }
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DESCENDING_MODE, false);
    }

    private boolean isDroneDescending() {
        return entityData.get(DESCENDING_MODE);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(DESCENDING_MODE, tag.getBoolean("DescendingMode"));
        padAltitude = tag.contains("PadAltitude") ? tag.getInt("PadAltitude") : UNSET_ALTITUDE;
        landingPos = tag.contains("LandingPos") ? BlockPos.of(tag.getLong("LandingPos")) : null;
        controllerPos = tag.contains("ControllerPos") ? BlockPos.of(tag.getLong("ControllerPos")) : null;
        landingReported = tag.getBoolean("LandingReported");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("DescendingMode", isDroneDescending());
        if (padAltitude != UNSET_ALTITUDE) {
            tag.putInt("PadAltitude", padAltitude);
        }
        if (landingPos != null) {
            tag.putLong("LandingPos", landingPos.asLong());
        }
        if (controllerPos != null) {
            tag.putLong("ControllerPos", controllerPos.asLong());
        }
        tag.putBoolean("LandingReported", landingReported);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "drone_controller", state -> {
            if (isDroneDescending()) {
                state.setAnimation(LANDING_ANIM);
            } else if (tickCount < TAKEOFF_TICKS) {
                state.setAnimation(TAKEOFF_ANIM);
            } else {
                state.setAnimation(FLYING_ANIM);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object entity) {
        return tickCount;
    }
}
