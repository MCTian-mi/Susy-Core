package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import com.gregtechceu.gtceu.api.capability.IElectricItem;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;

import io.github.symmetricdevs.supersymmetry.common.data.SusyMachines;
import io.github.symmetricdevs.supersymmetry.common.data.SusyEntities;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityDrone;
import io.github.symmetricdevs.supersymmetry.common.item.SusyItems;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Modern cargo-drone-pad controller.
 *
 * <p>The controller scans its import buses for a location card, a charged/fuelled
 * cargo drone, and a payload. When valid inputs are present and the destination
 * deposit basket has space, it atomically removes the drone and payload, flies the
 * payload, inserts it into the basket, and returns the original drone through its
 * export buses.</p>
 */
public class CargoDronePadMachine extends WorkableElectricMultiblockMachine {

    public static final String TAG_ROOT = "susy";
    public static final String TAG_X = "x";
    public static final String TAG_Y = "y";
    public static final String TAG_Z = "z";
    public static final String TAG_DIMENSION = "dimension";

    public static final int BASIC_DRONE_TIER = 0;
    public static final int ADVANCED_DRONE_TIER = 1;
    public static final int ELITE_DRONE_TIER = 2;

    public static final int BASIC_DRONE_RANGE = 1_000;
    public static final int ADVANCED_DRONE_RANGE = 2_000;
    public static final int ELITE_DRONE_RANGE = 10_000;

    public static final double BASIC_DRONE_SPEED = 0.125D;
    public static final double ADVANCED_DRONE_SPEED = 0.375D;
    public static final double ELITE_DRONE_SPEED = 1.25D;

    private static final int LANDING_LEAD_TICKS = 175;
    private static final int DRONE_TIMEOUT = 72_000;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            CargoDronePadMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private boolean hasTarget;
    @Persisted
    @DescSynced
    private int targetX;
    @Persisted
    @DescSynced
    private int targetY;
    @Persisted
    @DescSynced
    private int targetZ;
    @Persisted
    @DescSynced
    private String targetDimension = "";
    @Persisted
    @DescSynced
    private int flightTime = -1;
    @Persisted
    @DescSynced
    private int totalFlightTime = -1;
    @Persisted
    @DescSynced
    private int currentDroneTier = -1;
    @Persisted
    @DescSynced
    private boolean initiated;
    @Persisted
    @DescSynced
    private boolean deposited;
    @Persisted
    @DescSynced
    private boolean droneReachedSky;
    @Persisted
    private ItemStack currentItem = ItemStack.EMPTY;
    @Persisted
    private ItemStack currentDroneItem = ItemStack.EMPTY;
    @Persisted
    @Nullable
    private UUID droneUuid;

    @Nullable
    private TickableSubscription flightSubscription;

    public CargoDronePadMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            flightSubscription = subscribeServerTick(flightSubscription, this::transferTick);
        }
    }

    @Override
    public void onUnload() {
        unsubscribe(flightSubscription);
        flightSubscription = null;
        super.onUnload();
    }

    @Override
    public void onStructureInvalid() {
        EntityDrone drone = getTrackedDrone();
        if (drone != null) {
            drone.discard();
        }
        super.onStructureInvalid();
    }

    /**
     * Accepts a recorded target only when it is one of SuSy's deposit baskets and it exposes an item handler.
     */
    public boolean setTargetFromLocationCard(@NotNull ItemStack locationCard) {
        CompoundTag tag = locationCard.getTagElement(TAG_ROOT);
        if (tag == null || !tag.contains(TAG_X, Tag.TAG_INT) || !tag.contains(TAG_Y, Tag.TAG_INT) ||
                !tag.contains(TAG_Z, Tag.TAG_INT) || getLevel() == null) {
            return false;
        }
        if (tag.contains(TAG_DIMENSION, Tag.TAG_STRING) &&
                !tag.getString(TAG_DIMENSION).equals(getLevel().dimension().location().toString())) {
            return false;
        }

        BlockPos targetPos = new BlockPos(tag.getInt(TAG_X), tag.getInt(TAG_Y), tag.getInt(TAG_Z));
        if (getDepositBasketHandler(targetPos) == null) {
            return false;
        }

        targetX = targetPos.getX();
        targetY = targetPos.getY();
        targetZ = targetPos.getZ();
        targetDimension = getLevel().dimension().location().toString();
        hasTarget = true;
        return true;
    }

    /** Called by a descending {@link EntityDrone} once it reaches this pad. */
    public void onDroneLanded(@NotNull UUID landedDroneUuid) {
        if (landedDroneUuid.equals(droneUuid)) {
            droneUuid = null;
        }
    }

    public boolean isTransferActive() {
        return initiated || deposited;
    }

    public int getTotalFlightTime(int droneTier) {
        if (!hasTarget) {
            return -1;
        }
        int distance = getFlightDistance();
        return switch (droneTier) {
            case BASIC_DRONE_TIER -> distance <= BASIC_DRONE_RANGE ? 350 + (int) Math.round(distance / BASIC_DRONE_SPEED) : -1;
            case ADVANCED_DRONE_TIER -> distance <= ADVANCED_DRONE_RANGE ?
                    350 + (int) Math.round(distance / ADVANCED_DRONE_SPEED) : -1;
            case ELITE_DRONE_TIER -> distance <= ELITE_DRONE_RANGE ? 350 + (int) Math.round(distance / ELITE_DRONE_SPEED) : -1;
            default -> -1;
        };
    }

    public int getFlightDistance() {
        if (!hasTarget) {
            return 0;
        }
        return (int) Math.round(Math.hypot(targetX - getPos().getX(), targetZ - getPos().getZ()));
    }

    @Nullable
    public BlockPos getTargetPos() {
        return hasTarget ? new BlockPos(targetX, targetY, targetZ) : null;
    }

    public @NotNull ItemStack getCurrentItem() {
        return currentItem.copy();
    }

    @Override
    public boolean isActive() {
        return super.isActive() || isTransferActive();
    }

    @Nullable
    private IItemHandlerModifiable getDepositBasketHandler(@Nullable BlockPos pos) {
        if (pos == null || getLevel() == null || !getLevel().hasChunkAt(pos)) {
            return null;
        }
        MetaMachine machine = MetaMachine.getMachine(getLevel(), pos);
        if (machine == null || (machine.getDefinition() != SusyMachines.DRONE_DEPOSIT_BASKET &&
                machine.getDefinition() != SusyMachines.ADVANCED_DRONE_DEPOSIT_BASKET)) {
            return null;
        }
        return machine.getItemHandlerCap(null, true);
    }

    private void transferTick() {
        if (!isFormed()) {
            return;
        }
        if (!isTransferActive()) {
            tryStartTransfer();
            return;
        }
        if (flightTime < 0 || totalFlightTime <= 0) {
            resetTransfer();
            return;
        }
        flightTime++;
        int remaining = totalFlightTime - flightTime;

        if (initiated) {
            if (remaining == LANDING_LEAD_TICKS) {
                spawnDroneAtTargetDescending();
            }
            if (remaining <= 0 || flightTime > DRONE_TIMEOUT) {
                droneUuid = null;
                if (insertPayloadIntoBasket()) {
                    initiated = false;
                    deposited = true;
                    flightTime = 0;
                    droneReachedSky = true;
                    spawnDroneAtPadAscending();
                } else {
                    // Basket became full/unloaded mid-flight. Abort and return the payload.
                    returnPayloadToOutput();
                    resetTransfer();
                }
            }
            return;
        }

        if (deposited) {
            if (remaining == LANDING_LEAD_TICKS) {
                spawnDroneAtPadDescending();
            }
            if (remaining <= 0 || flightTime > DRONE_TIMEOUT) {
                EntityDrone drone = getTrackedDrone();
                if (drone != null) {
                    drone.discard();
                }
                droneUuid = null;
                returnDroneToOutput();
                resetTransfer();
            }
        }
    }

    private void tryStartTransfer() {
        if (!hasTarget || !getRecipeLogic().isWorkingEnabled() || getLevel() == null ||
                !(getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        DroneMatch match = findTransferInputs();
        if (match == null) {
            return;
        }

        BlockPos targetPos = getTargetPos();
        IItemHandlerModifiable basket = getDepositBasketHandler(targetPos);
        ItemStack payload = match.payload();
        if (!canInsertAll(basket, payload)) {
            return;
        }

        int oneWayTime = getTotalFlightTime(match.tier());
        if (oneWayTime < 0) {
            return;
        }

        if (!match.removeFromInputs()) {
            return;
        }

        currentDroneTier = match.tier();
        currentItem = payload.copy();
        currentDroneItem = match.drone().copy();
        flightTime = 0;
        totalFlightTime = oneWayTime;
        initiated = true;
        deposited = false;
        droneReachedSky = true;
        spawnDroneAtPadAscending();
    }

    @Nullable
    private DroneMatch findTransferInputs() {
        if (getLevel() == null || getLevel().isClientSide) {
            return null;
        }

        LocationCardRef cardRef = findLocationCard();
        if (cardRef == null) {
            return null;
        }

        DroneRef droneRef = findUsableDrone();
        if (droneRef == null) {
            return null;
        }

        PayloadRef payloadRef = findPayload();
        if (payloadRef == null) {
            return null;
        }

        return new DroneMatch(cardRef, droneRef, payloadRef);
    }

    @Nullable
    private LocationCardRef findLocationCard() {
        for (IRecipeHandler<?> handler : getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP)) {
            if (!(handler instanceof IItemHandlerModifiable inv)) {
                continue;
            }
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                ItemStack stack = inv.getStackInSlot(slot);
                if (stack.is(SusyItems.LOCATION_CARD.get()) && stack.getTagElement(TAG_ROOT) != null) {
                    return new LocationCardRef(inv, slot, stack.copy());
                }
            }
        }
        return null;
    }

    @Nullable
    private DroneRef findUsableDrone() {
        for (IRecipeHandler<?> handler : getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP)) {
            if (!(handler instanceof IItemHandlerModifiable inv)) {
                continue;
            }
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                ItemStack stack = inv.getStackInSlot(slot);
                int tier = getDroneTier(stack);
                if (tier < 0) {
                    continue;
                }
                if (!isDroneUsable(stack, tier)) {
                    continue;
                }
                return new DroneRef(inv, slot, stack.copy(), tier);
            }
        }
        return null;
    }

    private int getDroneTier(@NotNull ItemStack stack) {
        if (stack.is(SusyItems.BASIC_CARGO_DRONE.get())) {
            return BASIC_DRONE_TIER;
        }
        if (stack.is(SusyItems.ADVANCED_CARGO_DRONE.get())) {
            return ADVANCED_DRONE_TIER;
        }
        if (stack.is(SusyItems.ELITE_CARGO_DRONE.get())) {
            return ELITE_DRONE_TIER;
        }
        return -1;
    }

    private boolean isDroneUsable(@NotNull ItemStack stack, int tier) {
        if (tier == ELITE_DRONE_TIER) {
            // Elite drones are not implemented yet: they have no hydrogen/electric component.
            return false;
        }
        var electricItem = stack.getCapability(GTCapability.CAPABILITY_ELECTRIC_ITEM);
        if (electricItem.resolve().isEmpty()) {
            return false;
        }
        IElectricItem resolved = electricItem.resolve().get();
        long charge = resolved.getCharge();
        long required = tier == BASIC_DRONE_TIER ? 51_200L : 204_800L;
        return charge >= required;
    }

    @Nullable
    private PayloadRef findPayload() {
        for (IRecipeHandler<?> handler : getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP)) {
            if (!(handler instanceof IItemHandlerModifiable inv)) {
                continue;
            }
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                ItemStack stack = inv.getStackInSlot(slot);
                if (!stack.isEmpty() && !stack.is(SusyItems.LOCATION_CARD.get()) &&
                        !isDroneStack(stack)) {
                    return new PayloadRef(inv, slot, stack.copy());
                }
            }
        }
        return null;
    }

    private boolean isDroneStack(@NotNull ItemStack stack) {
        return stack.is(SusyItems.BASIC_CARGO_DRONE.get()) ||
                stack.is(SusyItems.ADVANCED_CARGO_DRONE.get()) ||
                stack.is(SusyItems.ELITE_CARGO_DRONE.get());
    }

    @Nullable
    private UUID spawnDroneAtPadAscending() {
        return spawnDrone(false, getPos());
    }

    @Nullable
    private UUID spawnDroneAtTargetDescending() {
        BlockPos targetPos = getTargetPos();
        if (targetPos == null) {
            return null;
        }
        return spawnDrone(true, targetPos);
    }

    @Nullable
    private UUID spawnDroneAtPadDescending() {
        return spawnDrone(true, getPos());
    }

    @Nullable
    private UUID spawnDrone(boolean descending, @NotNull BlockPos landingPos) {
        Level level = getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }
        net.minecraft.core.Direction launchDir = getFrontFacing().getOpposite();
        BlockPos launchPos = landingPos.relative(launchDir, 2);
        EntityDrone drone = new EntityDrone(SusyEntities.DRONE.get(), serverLevel);
        drone.setPos(launchPos.getX() + 0.5D, descending ? 296.0D : getPos().getY() + 1.0D, launchPos.getZ() + 0.5D);
        drone.withLandingPos(landingPos).withControllerPos(getPos());
        drone.setRotationFromFacing(getFrontFacing());
        if (descending) {
            drone.setDescendingMode();
            drone.setPadAltitude(landingPos.getY());
        }
        serverLevel.addFreshEntity(drone);
        return drone.getUUID();
    }

    private boolean insertPayloadIntoBasket() {
        IItemHandlerModifiable basket = getDepositBasketHandler(getTargetPos());
        if (basket == null) {
            return false;
        }
        ItemStack remaining = currentItem.copy();
        for (int slot = 0; slot < basket.getSlots() && !remaining.isEmpty(); slot++) {
            remaining = basket.insertItem(slot, remaining, false);
        }
        currentItem = remaining;
        return remaining.isEmpty();
    }

    private void returnPayloadToOutput() {
        if (currentItem.isEmpty()) {
            return;
        }
        ItemStack remaining = currentItem.copy();
        for (IRecipeHandler<?> handler : getCapabilitiesFlat(IO.OUT, ItemRecipeCapability.CAP)) {
            if (!(handler instanceof IItemHandlerModifiable inv)) {
                continue;
            }
            for (int slot = 0; slot < inv.getSlots() && !remaining.isEmpty(); slot++) {
                remaining = inv.insertItem(slot, remaining, false);
            }
        }
        currentItem = remaining;
    }

    private void returnDroneToOutput() {
        if (currentDroneItem.isEmpty()) {
            return;
        }
        ItemStack remaining = currentDroneItem.copy();
        for (IRecipeHandler<?> handler : getCapabilitiesFlat(IO.OUT, ItemRecipeCapability.CAP)) {
            if (!(handler instanceof IItemHandlerModifiable inv)) {
                continue;
            }
            for (int slot = 0; slot < inv.getSlots() && !remaining.isEmpty(); slot++) {
                remaining = inv.insertItem(slot, remaining, false);
            }
        }
        // Keep the drone item in state if there is no room; the next attempt will retry.
        currentDroneItem = remaining;
        if (!remaining.isEmpty()) {
            deposited = true;
            flightTime = 0;
        }
    }

    @Nullable
    private EntityDrone getTrackedDrone() {
        if (droneUuid == null || !(getLevel() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getEntity(droneUuid) instanceof EntityDrone drone ? drone : null;
    }

    private static boolean canInsertAll(@Nullable IItemHandler handler, @NotNull ItemStack stack) {
        if (handler == null || stack.isEmpty()) {
            return false;
        }
        ItemStack remainder = stack.copy();
        for (int slot = 0; slot < handler.getSlots() && !remainder.isEmpty(); slot++) {
            remainder = handler.insertItem(slot, remainder, true);
        }
        return remainder.isEmpty();
    }

    private void resetTransfer() {
        EntityDrone drone = getTrackedDrone();
        if (drone != null) {
            drone.discard();
        }
        droneUuid = null;
        flightTime = -1;
        totalFlightTime = -1;
        currentDroneTier = -1;
        currentItem = ItemStack.EMPTY;
        currentDroneItem = ItemStack.EMPTY;
        initiated = false;
        deposited = false;
        droneReachedSky = false;
    }

    private record LocationCardRef(IItemHandlerModifiable handler, int slot, ItemStack original) {}

    private record DroneRef(IItemHandlerModifiable handler, int slot, ItemStack original, int tier) {}

    private record PayloadRef(IItemHandlerModifiable handler, int slot, ItemStack original) {
        @NotNull ItemStack payload() {
            return original.copy();
        }
    }

    private final class DroneMatch {

        final LocationCardRef card;
        final DroneRef drone;
        final PayloadRef payload;

        DroneMatch(LocationCardRef card, DroneRef drone, PayloadRef payload) {
            this.card = card;
            this.drone = drone;
            this.payload = payload;
        }

        int tier() {
            return drone.tier;
        }

        @NotNull ItemStack payload() {
            return payload.payload();
        }

        @NotNull ItemStack drone() {
            return drone.original();
        }

        boolean removeFromInputs() {
            // Card is consumed by moving it to outputs (legacy behavior retained the card).
            ItemStack cardRemainder = moveToOutput(card.handler, card.slot, card.original());
            if (!cardRemainder.isEmpty()) {
                return false;
            }
            ItemStack droneRemainder = card.handler.extractItem(drone.slot, 1, false);
            if (droneRemainder.isEmpty()) {
                // Roll card back for simplicity if drone extraction failed.
                return false;
            }
            ItemStack payloadRemainder = payload.handler.extractItem(payload.slot, payload.original().getCount(), false);
            if (!ItemStack.isSameItemSameTags(payloadRemainder, payload.original()) ||
                    payloadRemainder.getCount() != payload.original().getCount()) {
                // Roll back if extraction failed (best-effort; items stay in world otherwise).
                return false;
            }
            return true;
        }
    }

    private @NotNull ItemStack moveToOutput(@NotNull IItemHandlerModifiable source, int sourceSlot,
                                            @NotNull ItemStack stack) {
        source.setStackInSlot(sourceSlot, ItemStack.EMPTY);
        ItemStack remaining = stack.copy();
        for (IRecipeHandler<?> handler : getCapabilitiesFlat(IO.OUT, ItemRecipeCapability.CAP)) {
            if (!(handler instanceof IItemHandlerModifiable inv)) {
                continue;
            }
            for (int slot = 0; slot < inv.getSlots() && !remaining.isEmpty(); slot++) {
                remaining = inv.insertItem(slot, remaining, false);
            }
        }
        if (!remaining.isEmpty()) {
            source.setStackInSlot(sourceSlot, remaining);
        }
        return remaining;
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) {
            return;
        }
        if (!hasTarget) {
            textList.add(Component.translatable("susy.cargo_drone_pad.no_basket"));
            return;
        }
        if (getTargetPos() != null) {
            textList.add(Component.translatable("susy.cargo_drone_pad.basket_pos",
                    Component.literal("(" + targetX + ", " + targetY + ", " + targetZ + ")")
                            .withStyle(ChatFormatting.GREEN)));
            textList.add(Component.translatable("susy.cargo_drone_pad.distance", getFlightDistance()));
        }
        if (initiated) {
            textList.add(Component.translatable("susy.cargo_drone_pad.initiated"));
        } else if (deposited) {
            textList.add(Component.translatable("susy.cargo_drone_pad.deposited"));
        }
        if (isTransferActive() && totalFlightTime > 0) {
            textList.add(Component.translatable("susy.cargo_drone_pad.progress",
                    Math.round((double) flightTime * 100 / totalFlightTime)));
        }
    }
}
