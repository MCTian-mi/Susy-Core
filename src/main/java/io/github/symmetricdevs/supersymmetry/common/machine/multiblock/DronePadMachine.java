package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import io.github.symmetricdevs.supersymmetry.common.data.SusyEntities;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityDrone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Modern drone-pad controller.
 *
 * <p>A recipe begins only after its outbound drone has spawned. The controller holds
 * the final recipe tick until the return drone has landed, so recipe outputs never
 * appear before the visible round-trip has completed.</p>
 */
public class DronePadMachine extends WorkableElectricMultiblockMachine {

    private static final int RETURN_FLIGHT_LEAD_TICKS = 240;
    private static final int RETURN_WAIT_TIMEOUT = 1_200;
    private static final double RETURN_ALTITUDE = 296.0D;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            DronePadMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private boolean droneReachedSky;

    @Persisted
    @DescSynced
    private boolean returnFlightScheduled;

    @Persisted
    @Nullable
    private UUID droneUuid;

    @Persisted
    private int returnWaitTicks;

    public DronePadMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected @NotNull RecipeLogic createRecipeLogic(Object... args) {
        return new DronePadRecipeLogic(this);
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (!super.beforeWorking(recipe)) {
            return false;
        }
        return recipe == null || beginOutboundFlight();
    }

    @Override
    public boolean onWorking() {
        if (!super.onWorking()) {
            return false;
        }
        updateOutboundFlight();
        if (droneReachedSky && !returnFlightScheduled &&
                getRecipeLogic().getDuration() - getRecipeLogic().getProgress() <= RETURN_FLIGHT_LEAD_TICKS) {
            beginReturnFlight();
        }
        return true;
    }

    @Override
    public void onStructureInvalid() {
        clearFlightState();
        super.onStructureInvalid();
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        clearFlightState();
    }

    public boolean hasDroneReachedSky() {
        return droneReachedSky;
    }

    /** Called by a descending {@link EntityDrone} once it reaches this controller. */
    public void onDroneLanded(@NotNull UUID landedDroneUuid) {
        if (returnFlightScheduled && landedDroneUuid.equals(droneUuid)) {
            droneUuid = null;
            returnWaitTicks = 0;
        }
    }

    private boolean beginOutboundFlight() {
        droneReachedSky = false;
        returnFlightScheduled = false;
        droneUuid = spawnDrone(false, getPos());
        returnWaitTicks = 0;
        return droneUuid != null;
    }

    private void updateOutboundFlight() {
        if (droneReachedSky) {
            return;
        }
        EntityDrone drone = getTrackedDrone();
        if (drone == null) {
            // The owning chunk is loaded while this controller is working. If a saved
            // outbound entity is missing, allow recipe recovery rather than stranding
            // already-consumed inputs indefinitely.
            droneReachedSky = true;
            droneUuid = null;
            return;
        }
        if (drone.reachedSky()) {
            drone.discard();
            droneUuid = null;
            droneReachedSky = true;
        }
    }

    private void beginReturnFlight() {
        returnFlightScheduled = true;
        droneUuid = spawnDrone(true, getPos());
        returnWaitTicks = 0;
    }

    private boolean isAwaitingReturnDrone() {
        return returnFlightScheduled && droneUuid != null;
    }

    private void tickReturnWait() {
        if (++returnWaitTicks >= RETURN_WAIT_TIMEOUT) {
            EntityDrone drone = getTrackedDrone();
            if (drone != null) {
                drone.discard();
            }
            droneUuid = null;
            returnWaitTicks = 0;
        }
    }

    @Nullable
    private UUID spawnDrone(boolean descending, @NotNull BlockPos landingPos) {
        Level level = getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }
        Vec3 spawn = getDroneSpawnPosition(descending);
        EntityDrone drone = new EntityDrone(SusyEntities.DRONE.get(), serverLevel);
        drone.setPos(spawn.x, spawn.y, spawn.z);
        drone.withLandingPos(landingPos).withControllerPos(getPos());
        drone.setRotationFromFacing(getFrontFacing());
        if (descending) {
            drone.setDescendingMode();
            drone.setPadAltitude(landingPos.getY());
        }
        serverLevel.addFreshEntity(drone);
        return drone.getUUID();
    }

    private @NotNull Vec3 getDroneSpawnPosition(boolean descending) {
        Direction launchDirection = getFrontFacing().getOpposite();
        BlockPos launchPos = getPos().relative(launchDirection, 2);
        return new Vec3(launchPos.getX() + 0.5D,
                descending ? RETURN_ALTITUDE : getPos().getY() + 1.0D,
                launchPos.getZ() + 0.5D);
    }

    @Nullable
    private EntityDrone getTrackedDrone() {
        if (droneUuid == null || !(getLevel() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getEntity(droneUuid) instanceof EntityDrone drone ? drone : null;
    }

    private void clearFlightState() {
        EntityDrone drone = getTrackedDrone();
        if (drone != null) {
            drone.discard();
        }
        droneReachedSky = false;
        returnFlightScheduled = false;
        droneUuid = null;
        returnWaitTicks = 0;
    }

    private static final class DronePadRecipeLogic extends RecipeLogic {

        private final DronePadMachine dronePad;

        private DronePadRecipeLogic(DronePadMachine dronePad) {
            super(dronePad);
            this.dronePad = dronePad;
        }

        @Override
        public void handleRecipeWorking() {
            if (getProgress() == getDuration() - 1 && dronePad.isAwaitingReturnDrone()) {
                dronePad.tickReturnWait();
                return;
            }
            super.handleRecipeWorking();
        }
    }
}
