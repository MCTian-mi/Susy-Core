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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Modern port of the 1.12.2 Railroad Engineering Station controller
 * ({@code MetaTileEntityRailroadEngineeringStation}).
 *
 * <p>
 * <b>Deferred-scope:</b> every train behaviour — rolling-stock
 * detect/select/spawn/progressively-build/complete/kill, the train ghost item
 * slot, and the real IR rail blocks of the {@code 'R'} structure predicate — is
 * Immersive Railroading ({@code cam72cam.immersiverailroading.*}), which has no
 * 1.20.1 port and is excluded from this build (deferred with the
 * rocketry/transport scope). What IS ported now:
 * <ul>
 * <li>the structure AABB (used for the potion aura now, for entity scans later);</li>
 * <li>the mining-fatigue aura on players inside the structure while working;</li>
 * <li>the persisted spawned-stock UUID plumbing (1.12.2 {@code RollingStockEntityID};
 * the entity re-find itself is IR-bound);</li>
 * <li>{@link #beforeWorking}/{@link #afterWorking} hook points with {@code // TODO))}
 * gates at the exact call sites the legacy RecipeLogic used;</li>
 * <li>{@link #rails()}, an interim structure predicate matching vanilla rails.</li>
 * </ul>
 * Recipe item IO uses ordinary IMPORT/EXPORT_ITEMS buses; the 1.12.2 train
 * input/output slots (ghost display + spawn target) are dropped — see the
 * {@code // TODO))} gates below. The real recipes are also IR-bound (their item
 * outputs carry IR defID/gauge NBT and the loader is commented out in
 * {@code SuSyRecipeLoader}), so only dev/test recipes can run until an IR port lands.
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
     * Interim rail predicate for the {@code 'R'} structure slots: matches vanilla
     * rail blocks so the structure can form today.
     * // TODO)) ImmersiveRailroading (deferred — no 1.20.1 IR port; rocketry/transport
     * scope): match the IR {@code BLOCK_RAIL}/{@code BLOCK_RAIL_GAG} track blocks
     * instead (1.12.2 {@code SuSyPredicates.rails()}), and move this back into
     * {@code SuSyPredicates} at that point.
     */
    public static TraceabilityPredicate rails() {
        return Predicates.blocks(Blocks.RAIL, Blocks.POWERED_RAIL, Blocks.DETECTOR_RAIL,
                Blocks.ACTIVATOR_RAIL);
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
        // TODO)) ImmersiveRailroading: re-find the in-progress stock entity inside
        // structureAABB by spawnedStockUuid and rebuild its sorted component list
        // (1.12.2 readFromNBT + updateFormedValid isFirstTick re-find).
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        structureAABB = null;
        // TODO)) ImmersiveRailroading: kill the spawned rolling stock, clear the
        // selected stock and its ghost item (1.12.2 invalidateStructure / onRemoval /
        // invalidate — a broken multi must never leak a ghost train).
    }

    @Override
    public void onUnload() {
        unsubscribe(stationSubscription);
        stationSubscription = null;
        structureAABB = null;
        super.onUnload();
    }

    /** Per-tick station logic (1.12.2 updateFormedValid / update). */
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
            // TODO)) ImmersiveRailroading: scan structureAABB for EntityRollingStock,
            // (re)select a train and fill the train ghost item (1.12.2 canFindTrain
            // branch); while working, updateSpawnedStock(progress) so the buildable
            // stock visibly assembles as the recipe progresses.
        }
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        // TODO)) ImmersiveRailroading: on recipe start the 1.12.2 logic
        // (setupAndConsumeRecipeInputs) killed the selected stock and spawned the
        // recipe's first item output as an Entity(Buildable)RollingStock at the rail
        // position, persisting its UUID into spawnedStockUuid.
        return super.beforeWorking(recipe);
    }

    @Override
    public void afterWorking() {
        // TODO)) ImmersiveRailroading: the 1.12.2 completeRecipe() voided the item
        // output and finalized the in-world stock (completeSpawnedStock). Interim:
        // the item output ejects to the EXPORT_ITEMS bus normally — acceptable
        // because real recipes cannot be registered until the IR-bound recipe
        // loader is ported anyway.
        super.afterWorking();
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
