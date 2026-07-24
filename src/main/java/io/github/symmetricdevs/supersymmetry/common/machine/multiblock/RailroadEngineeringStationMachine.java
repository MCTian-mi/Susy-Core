package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.track.ITrackBlock;

import io.github.symmetricdevs.supersymmetry.common.machine.multiblock.integration.CreateTrainSpawner;

/**
 * Modern port of the 1.12.2 Railroad Engineering Station controller.
 *
 * <p>Instead of the original Immersive Railroading integration (no 1.20.1 port),
 * this implementation uses Create tracks as the rail bed and spawns a Create
 * train when a recipe completes. The multiblock's front-facing direction
 * determines the train's assembly direction.</p>
 */
public class RailroadEngineeringStationMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            RailroadEngineeringStationMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    /**
     * UUID (as string) of the in-progress spawned rolling stock; empty when no
     * stock is mid-build. Replaces the 1.12.2 {@code "RollingStockEntityID"} NBT.
     * Written by the IR spawn logic — see the {@code // TODO))} in
     * {@link #beforeWorking}.
     */
    @Persisted
    private String spawnedStockUuid = "";

    /** Transient; recomputed in {@link #onStructureFormed()}. Null while unformed. */
    @Nullable
    private AABB structureAABB;

    @Nullable
    private TickableSubscription stationSubscription;

    public RailroadEngineeringStationMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    /**
     * Structure predicate for the {@code 'R'} rail slots: accepts Create track blocks.
     */
    public static TraceabilityPredicate rails() {
        return Predicates.blocks(AllBlocks.TRACK.get());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        stationSubscription = subscribeServerTick(stationSubscription, this::stationUpdate);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        computeStructureAABB();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        structureAABB = null;
    }

    @Override
    public void onUnload() {
        unsubscribe(stationSubscription);
        stationSubscription = null;
        structureAABB = null;
        super.onUnload();
    }

    /** Per-tick station logic (mining-fatigue aura while working). */
    private void stationUpdate() {
        if (!isFormed() || structureAABB == null) {
            return;
        }
        if (getOffsetTimer() % 20 == 1) {
            if (isActive()) {
                Level level = getLevel();
                if (level != null) {
                    // 1.12.2 mining-fatigue aura (Mining Fatigue II, 21 ticks).
                    for (Player player : level.getEntitiesOfClass(Player.class, structureAABB)) {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 21, 1));
                    }
                }
            }
        }
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        return super.beforeWorking(recipe);
    }

    @Override
    public void afterWorking() {
        spawnTrainOnCompletion();
        super.afterWorking();
    }

    /**
     * Spawns a Create train on the rail bed when the recipe finishes. The rail
     * runs left-to-right behind the controller, so the train is assembled facing
     * the controller's right along the rail.
     */
    private void spawnTrainOnCompletion() {
        Level level = getLevel();
        if (level == null || level.isClientSide) {
            return;
        }
        BlockPos trackPos = findCentralTrackPos();
        if (trackPos == null) {
            return;
        }
        Direction assemblyDirection = getFrontFacing().getClockWise();
        CreateTrainSpawner.TrainSpawnResult result = CreateTrainSpawner.trySpawnTrain(level, trackPos, assemblyDirection);
        if (result.success()) {
            this.spawnedStockUuid = result.trainId().toString();
        }
    }

    /**
     * Finds a Create track block along the single central rail aisle of the structure.
     * The legacy rail bed runs left-to-right behind the controller front; the
     * returned position is the left-most track in the central rail row.
     */
    @Nullable
    private BlockPos findCentralTrackPos() {
        Level level = getLevel();
        if (level == null) {
            return null;
        }
        // The rail aisle is 4 blocks behind the controller (slice 7 of 15, with
        // the controller at slice 11). Scan left-to-right so the train can be
        // assembled facing right along the rail.
        BlockPos.MutableBlockPos cursor = getPos().mutable();
        for (int i = 0; i < 4; i++) {
            cursor.move(getFrontFacing().getOpposite());
        }
        // right is positive toward the controller's left; start at the left edge.
        for (int right = 8; right >= -8; right--) {
            BlockPos probe = relativePos(cursor, right, 0, 0);
            BlockState state = level.getBlockState(probe);
            if (state.getBlock() instanceof ITrackBlock) {
                return probe;
            }
        }
        return null;
    }

    private BlockPos relativePos(BlockPos origin, int right, int up, int forward) {
        Direction front = getFrontFacing();
        Direction rightDir = front.getCounterClockWise();
        return origin.relative(rightDir, right).above(up).relative(front, forward);
    }

    /**
     * Port of the 1.12.2 setStructureAABB(): an AABB around the controller that
     * deliberately overshoots the rail bed, rotated by the front facing (NORTH basis).
     */
    private void computeStructureAABB() {
        BlockPos bottomLeft = new BlockPos(9, -1, 2);
        BlockPos topRight = new BlockPos(-9, 8, 7);
        Rotation rotation = switch (getFrontFacing()) {
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
        this.structureAABB = new AABB(getPos().offset(bottomLeft.rotate(rotation)),
                getPos().offset(topRight.rotate(rotation)));
    }
}
