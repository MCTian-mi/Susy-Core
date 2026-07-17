package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static com.gregtechceu.gtceu.api.util.GTUtility.getMetaTileEntity;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.PatternMatchContext;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.block.CasingBlock;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.SusyLog;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockSuSyMultiblockCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityDrone;
import io.github.symmetricdevs.supersymmetry.common.item.SuSyMetaItems;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.storage.MetaTileEntityDroneDepositBasket;

public class MetaTileEntityCargoDronePad extends WorkableElectricMultiblockMachine {

    public static final String TAG_ROOT = "susy";
    public static final String TAG_X = "xcoord";
    public static final String TAG_Y = "ycoord";
    public static final String TAG_Z = "zcoord";
    private AABB landingAreaBB;
    private EntityDrone drone = null;
    public boolean droneReachedSky;
    MetaTileEntity targetBasket = null;
    BlockPos targetPos = null;
    public static final int basicDroneTier = 0;
    public static final int basicDroneRange = 1000;
    public static final double basicDroneSpeed = 0.125; // blocks per tick
    public static final int basicDroneCharge = 51200;
    public static final int advancedDroneTier = 1;
    public static final int advancedDroneRange = 2000;
    public static final double advancedDroneSpeed = 0.375;
    public static final int advancedDroneCharge = 204800;
    public static final int eliteDroneTier = 2;
    public static final int eliteDroneRange = 10000;
    public static final double eliteDroneSpeed = 1.25;
    public static final int eliteDroneFuel = 1000;
    private int flightTime = -1;
    int totalFlightTime = -1;
    private ItemStack currentItem = null;
    private int currentDroneTier = -1;
    boolean initiated = false;
    boolean deposited = false;
    private final int droneTimeout = 72000; // 1 hour
    private int dist = 0;

    public MetaTileEntityCargoDronePad(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.CARGO_DRONE_PAD); // dummy GTRecipeType, do not use
    }

    protected static BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.ALUMINIUM_FROSTPROOF);
    }

    protected static BlockState getPadState() {
        return SuSyBlocks.MULTIBLOCK_CASING.getState(BlockSuSyMultiblockCasing.CasingType.DRONE_PAD);
    }

    @Override
    public @Nullable BlockEntity getNeighbor(@NotNull Direction facing) {
        return super.getNeighbor(facing);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle(" CCC ")
                .aisle("CPPPC")
                .aisle("CPPPC")
                .aisle("CPPPC")
                .aisle(" CSC ")
                .where(' ', any())
                .where('S', selfPredicate())
                .where('C', states(getCasingState()).setMinGlobalLimited(6)
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMinGlobalLimited(1)))
                .where('P', states(getPadState()))
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.FROST_PROOF_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityCargoDronePad(metaTileEntityId);
    }

    @Nullable
    public EntityDrone getDrone() {
        return this.drone;
    }

    public void setDrone(@Nullable EntityDrone drone) {
        this.drone = drone;
    }

    public void setDroneDead(boolean setReachedSky) {
        if (this.drone != null) {
            this.drone.setDead();
            this.drone = null;
        }
        this.droneReachedSky = setReachedSky;
    }

    public void spawnDroneEntityOnPad(boolean descending) {
        CompoundTag CompoundTag = new CompoundTag();
        CompoundTag.setString("id", "susy:drone");

        Vec3 pos = this.getDroneSpawnPositionOnPad(descending);

        EntityDrone drone = ((EntityDrone) AnvilChunkLoader.readWorldEntityPos(CompoundTag, this.getWorld(), pos.x,
                pos.y, pos.z, true));

        if (drone != null) {
            setDrone(drone.withLandingPos(getPos()));
        }

        if (getDrone() != null) {
            getDrone().setRotationFromFacing(this.getFrontFacing());
            getDrone().setCargoPad(this);
            if (descending) {
                getDrone().setDescendingMode();
                getDrone().setPadAltitude(this.getPos().getY());
            }
        } else {
            SusyLog.logger.error("Failed to spawn drone entity at: {}", pos);
        }
    }

    public Vec3 getDroneSpawnPositionOnPad(boolean descending) {
        double altitude = descending ? 296.D : this.getPos().getY() + 1.;

        switch (this.getFrontFacing()) {
            case EAST -> {
                return new Vec3(this.getPos().getX() - 1.5, altitude, this.getPos().getZ() + 0.5);
            }
            case SOUTH -> {
                return new Vec3(this.getPos().getX() + 0.5, altitude, this.getPos().getZ() - 1.5);
            }
            case WEST -> {
                return new Vec3(this.getPos().getX() + 2.5, altitude, this.getPos().getZ() + 0.5);
            }
            default -> {
                return new Vec3(this.getPos().getX() + 0.5, altitude, this.getPos().getZ() + 2.5);
            }
        }
    }

    public Vec3 getDroneSpawnPositionOnBasket(BlockPos pos, boolean descending) {
        double altitude = descending ? 296.D : pos.getY() + 1.;
        return new Vec3(pos.getX() + 0.5, altitude, pos.getZ() + 0.5);
    }

    public void spawnDroneEntityOnBasket(BlockPos basketPos, boolean descending) {
        CompoundTag CompoundTag = new CompoundTag();
        CompoundTag.setString("id", "susy:drone");

        Vec3 pos = this.getDroneSpawnPositionOnBasket(basketPos, descending);

        EntityDrone drone = ((EntityDrone) AnvilChunkLoader.readWorldEntityPos(CompoundTag, this.getWorld(), pos.x,
                pos.y, pos.z, true));

        if (drone != null) {
            setDrone(drone.withLandingPos(basketPos));
        }

        if (getDrone() != null) {
            getDrone().setRotationFromFacing(Direction.NORTH);
            getDrone().setCargoPad(this);
            if (descending) {
                getDrone().setDescendingMode();
                getDrone().setPadAltitude(basketPos.getY());
            }
        } else {
            SusyLog.logger.error("Failed to spawn drone entity at: {}", pos);
        }
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.setStructureAABB();
    }

    public void setStructureAABB() {
        double x = this.getPos().getX();
        double y = this.getPos().getY();
        double z = this.getPos().getZ();

        switch (this.getFrontFacing()) {

            case EAST -> {
                this.landingAreaBB = new AABB(x - 1, y + 1, z + 1, x - 3, y + 2, z - 1);
            }
            case SOUTH -> {
                this.landingAreaBB = new AABB(x - 1, y + 1, z - 1, x + 1, y + 2, z - 3);
            }
            case WEST -> {
                this.landingAreaBB = new AABB(x + 1, y + 1, z - 1, x + 3, y + 2, z + 1);
            }
            default -> {
                this.landingAreaBB = new AABB(x + 1, y + 1, z + 1, x - 1, y + 2, z + 3);
            }
        }
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        setDroneDead(false);
    }

    @Override
    public CompoundTag writeToNBT(@NotNull CompoundTag data) {
        super.writeToNBT(data);
        data.setBoolean("droneReachedSky", this.droneReachedSky);
        if (targetPos != null) {
            data.setInteger(TAG_X, targetPos.getX());
            data.setInteger(TAG_Y, targetPos.getY());
            data.setInteger(TAG_Z, targetPos.getZ());
        }
        data.setInteger("flightTime", flightTime);
        data.setInteger("totalFlightTime", totalFlightTime);
        data.setBoolean("initiated", initiated);
        data.setBoolean("deposited", deposited);
        if (currentItem != null) {
            currentItem.writeToNBT(data);
        }
        if (currentDroneTier > -1) {
            if (currentDroneTier == 0) {
                data.setInteger("currentDroneTier", 0);
            } else if (currentDroneTier == 1) {
                data.setInteger("currentDroneTier", 1);
            }
        }
        return data;
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.droneReachedSky = data.getBoolean("droneReachedSky");
        if (data.hasKey(TAG_X) && data.hasKey(TAG_Y) && data.hasKey(TAG_Z) && targetPos == null) {
            targetPos = new BlockPos(data.getInteger(TAG_X), data.getInteger(TAG_Y), data.getInteger(TAG_Z));
        }
        if (data.hasKey("flightTime")) {
            flightTime = data.getInteger("flightTime");
        }
        if (data.hasKey("totalFlightTime")) {
            totalFlightTime = data.getInteger("totalFlightTime");
        }
        if (data.hasKey("initiated")) {
            initiated = data.getBoolean("initiated");
        }
        if (data.hasKey("deposited")) {
            deposited = data.getBoolean("deposited");
        }
        if (currentItem == null) {
            currentItem = new ItemStack(data);
        }
        if (data.hasKey("currentDroneTier")) {
            if (data.getInteger("currentDroneTier") == basicDroneTier) {
                currentDroneTier = basicDroneTier;
            } else if (data.getInteger("currentDroneTier") == advancedDroneTier) {
                currentDroneTier = advancedDroneTier;
            } else if (data.getInteger("currentDroneTier") == eliteDroneTier) {
                currentDroneTier = eliteDroneTier;
            }
        }
    }

    @Override
    public void writeInitialSyncData(FriendlyByteBuf buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(this.droneReachedSky);
    }

    @Override
    public void receiveInitialSyncData(FriendlyByteBuf buf) {
        super.receiveInitialSyncData(buf);
        this.droneReachedSky = buf.readBoolean();
    }

    @Override
    public boolean isMultiblockPartWeatherResistant(@Nonnull IMultiblockPart part) {
        return true;
    }

    @Override
    public boolean getIsWeatherOrTerrainResistant() {
        return true;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    public void update() {
        super.update();
        if (flightTime > -1) {
            flightTime++;
        }

        if (getOffsetTimer() % 10 == 0 && getWorld().provider.getDimension() == 0) {
            if (targetPos != null) {
                setTargetBasket(targetPos);
            }
            if (getFirstSlotWithValidCard() > -1 && !deposited && !initiated) {
                setTargetPos(getFirstSlotWithValidCard());

            }
            if (getFirstSlotWithItem() > -1 && getFirstSlotWithDrone() > -1 && !deposited && !initiated &&
                    recipeMapWorkable.isWorkingEnabled()) {
                initiated = initiateTransfer();
            }
        }

        if (initiated) {
            if (totalFlightTime - flightTime == 175) {
                spawnDroneEntityOnBasket(targetPos, true);
            }
            if (totalFlightTime - flightTime <= 0 || flightTime > droneTimeout) {
                deposited = depositItem();
                this.setDroneDead(true);
                spawnDroneEntityOnBasket(targetPos, false);
                if (deposited) {
                    flightTime = 0;
                } else {
                    SusyLog.logger.debug("Failed to deposit item into basket");
                    reset();
                }
                initiated = false;
                currentItem = null;
            }
        }

        if (deposited) {
            if (totalFlightTime - flightTime == 175) {
                spawnDroneEntityOnPad(true);
            }
            if (totalFlightTime - flightTime <= 0 || flightTime > droneTimeout) {
                this.setDroneDead(false);
                outputDrone();
                deposited = false;
                initiated = false;
                currentItem = null;
                flightTime = -1;
                totalFlightTime = -1;
            }
        }
    }

    public int getFirstSlotWithDrone() {
        if (getInputInventory().getSlots() > 0) {
            for (int i = 0; i < getInputInventory().getSlots() - 1; i++) {
                ItemStack item = getInputInventory().getStackInSlot(i);
                if (item.isItemEqual(SuSyMetaItems.BASIC_CARGO_DRONE.getStackForm()) ||
                        item.isItemEqual(SuSyMetaItems.ADVANCED_CARGO_DRONE.getStackForm()) ||
                        item.isItemEqual(SuSyMetaItems.ELITE_CARGO_DRONE.getStackForm())) {
                    CompoundTag tag = getInputInventory().getStackInSlot(i).getTagCompound();
                    if (tag != null && tag.hasKey("Charge")) {
                        if ((tag.getLong("Charge") == basicDroneCharge &&
                                item.isItemEqual(SuSyMetaItems.BASIC_CARGO_DRONE.getStackForm())) ||
                                (tag.getLong("Charge") == advancedDroneCharge &&
                                        item.isItemEqual(SuSyMetaItems.ADVANCED_CARGO_DRONE.getStackForm()))) {
                            return i;
                        }
                    }
                    if (item.getSubCompound("Fluid") != null) {
                        CompoundTag subTag = item.getSubCompound("Fluid");
                        if (subTag.getInteger("Amount") >= eliteDroneFuel) {
                            return i;
                        }
                    }
                }

            }
        }
        return -1;
    }

    public int getFirstFreeSlot(IItemHandlerModifiable outputInventory) {
        for (int i = 0; i < getInputInventory().getSlots() - 1; i++) {
            if (outputInventory.getStackInSlot(i) == ItemStack.EMPTY) {
                return i;
            }
        }
        return -1;
    }

    public int getFirstSlotWithValidCard() {
        if (getInputInventory().getSlots() > 0) {
            for (int i = 0; i < getInputInventory().getSlots(); i++) {
                if (getInputInventory().getStackInSlot(i).isItemEqual(SuSyMetaItems.LOCATION_CARD.getStackForm()) &&
                        getInputInventory().getStackInSlot(i).getSubCompound(TAG_ROOT) != null) {
                    CompoundTag tag = getInputInventory().getStackInSlot(i).getSubCompound(TAG_ROOT);
                    if (tag.hasKey(TAG_X) && tag.hasKey(TAG_Y) && tag.hasKey(TAG_Z)) {
                        return i;
                    }
                }

            }
        }
        return -1;
    }

    public void setTargetPos(int cardSlot) {
        ItemStack card = getInputInventory().getStackInSlot(cardSlot);
        CompoundTag tag = card.getSubCompound(TAG_ROOT);
        if (getFirstFreeSlot(getOutputInventory()) > -1) {
            int x = tag.getInteger(TAG_X);
            int y = tag.getInteger(TAG_Y);
            int z = tag.getInteger(TAG_Z);
            this.targetPos = new BlockPos(x, y, z);
            getInputInventory().setStackInSlot(cardSlot, ItemStack.EMPTY);
            getOutputInventory().setStackInSlot(getFirstFreeSlot(getOutputInventory()), card);
        }
    }

    public int getFirstSlotWithItem() {
        if (getInputInventory().getSlots() > 0) {
            for (int i = 0; i < getInputInventory().getSlots() - 1; i++) {
                if (!getInputInventory().getStackInSlot(i).isItemEqual(SuSyMetaItems.LOCATION_CARD.getStackForm()) &&
                        !getInputInventory().getStackInSlot(i)
                                .isItemEqual(SuSyMetaItems.BASIC_CARGO_DRONE.getStackForm()) &&
                        !getInputInventory().getStackInSlot(i)
                                .isItemEqual(SuSyMetaItems.ADVANCED_CARGO_DRONE.getStackForm()) &&
                        !getInputInventory().getStackInSlot(i)
                                .isItemEqual(SuSyMetaItems.ELITE_CARGO_DRONE.getStackForm()) &&
                        getInputInventory().getStackInSlot(i) != ItemStack.EMPTY) {
                    return i;
                }

            }
        }
        return -1;
    }

    public boolean setTargetBasket(BlockPos pos) {
        if (getBasketAtCoords(pos) != null) {
            targetBasket = getBasketAtCoords(pos);
            dist = getFlightDistance(pos);
            return true;
        } else {
            targetBasket = null;
            return false;
        }
    }

    @Nullable
    public MetaTileEntity getBasketAtCoords(BlockPos pos) {
        MetaTileEntity BlockEntity = getMetaTileEntity(this.getWorld(), pos);
        if (BlockEntity instanceof MetaTileEntityDroneDepositBasket) {
            return BlockEntity;
        } else {
            return null;
        }
    }

    public boolean consumeItem(int slot) {
        if (inputInventory.getStackInSlot(slot) != ItemStack.EMPTY) {
            inputInventory.setStackInSlot(slot, ItemStack.EMPTY);
            return true;
        }
        return false;
    }

    public boolean hasSpaceInBasket(MetaTileEntity basket) {
        for (int i = 0; i < basket.getItemInventory().getSlots(); i++) {
            if (basket.getItemInventory().getStackInSlot(i) == ItemStack.EMPTY) {
                return true;
            }
        }
        return false;
    }

    public int getFlightDistance(BlockPos pos) {
        int xdist = Math.abs(pos.getX() - this.getPos().getX());
        int zdist = Math.abs(pos.getZ() - this.getPos().getZ());
        dist = Math.toIntExact(Math.round(Math.hypot(xdist, zdist)));
        return dist;
    }

    public int getTotalFlightTime(int droneTier) {
        long flightTime = -1; // one-way
        dist = getFlightDistance(targetPos);
        if (droneTier == basicDroneTier) {
            if (dist <= basicDroneRange) {
                flightTime = 350 + Math.round(dist / basicDroneSpeed);
            } else {
                return -1;
            }
        } else if (droneTier == advancedDroneTier) {
            if (dist <= advancedDroneRange) {
                flightTime = 350 + Math.round(dist / advancedDroneSpeed);
            } else {
                return -1;
            }
        } else if (droneTier == eliteDroneTier) {
            if (dist <= eliteDroneRange) {
                flightTime = 350 + Math.round(dist / eliteDroneSpeed);
            } else {
                return -1;
            }
        }
        return Math.toIntExact(flightTime);
    }

    public boolean initiateTransfer() {
        if (targetPos != null) {
            setTargetBasket(targetPos);
            currentItem = inputInventory.getStackInSlot(getFirstSlotWithItem());
            ItemStack droneStack = inputInventory.getStackInSlot(getFirstSlotWithDrone());
            if (droneStack.isItemEqual(SuSyMetaItems.ADVANCED_CARGO_DRONE.getStackForm())) {
                currentDroneTier = advancedDroneTier;
            } else if (droneStack.isItemEqual(SuSyMetaItems.BASIC_CARGO_DRONE.getStackForm())) {
                currentDroneTier = basicDroneTier;
            } else if (droneStack.isItemEqual(SuSyMetaItems.ELITE_CARGO_DRONE.getStackForm())) {
                currentDroneTier = eliteDroneTier;
            }
            if (targetBasket != null && hasSpaceInBasket(targetBasket) &&
                    getTotalFlightTime(currentDroneTier) > -1) {
                flightTime = 0;
                totalFlightTime = getTotalFlightTime(currentDroneTier);
                consumeItem(getFirstSlotWithItem());
                consumeItem(getFirstSlotWithDrone());
                spawnDroneEntityOnPad(false);
                return true;
            }
        }
        return false;
    }

    public boolean depositItem() {
        if (targetBasket != null && hasSpaceInBasket(targetBasket) && currentItem != null) {
            targetBasket.getItemInventory().insertItem(0, currentItem, false);
            return true;
        }
        return false;
    }

    public boolean outputDrone() {
        if (getFirstFreeSlot(outputInventory) > -1) {
            ItemStack droneStack = ItemStack.EMPTY;
            if (currentDroneTier == basicDroneTier) {
                droneStack = SuSyMetaItems.BASIC_CARGO_DRONE.getStackForm();
            } else if (currentDroneTier == advancedDroneTier) {
                droneStack = SuSyMetaItems.ADVANCED_CARGO_DRONE.getStackForm();
            } else if (currentDroneTier == eliteDroneTier) {
                droneStack = SuSyMetaItems.ELITE_CARGO_DRONE.getStackForm();
            }
            outputInventory.setStackInSlot(getFirstFreeSlot(outputInventory), droneStack);
            currentDroneTier = -1;
            return true;
        }
        return false;
    }

    public void reset() {
        setDroneDead(false);
        flightTime = -1;
        totalFlightTime = -1;
        currentItem = null;
        currentDroneTier = -1;
        initiated = false;
        deposited = false;
        droneReachedSky = false;
        drone = null;
    }

    public void droneExploded() {
        reset();
    }

    @Override
    protected void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (targetBasket == null) {
            textList.add(Component.translatable("susy.cargo_drone_pad.no_basket"));
        } else if (!hasSpaceInBasket(targetBasket)) {
            textList.add(Component.translatable("susy.cargo_drone_pad.no_space_in_basket"));
        }
        if (targetBasket != null && targetPos != null) {
            textList.add(Component.translatable("susy.cargo_drone_pad.basket_pos",
                    "(" + targetPos.getX() + ", " + targetPos.getY() + ", " + targetPos.getZ() + ")"));
            textList.add(Component.translatable("susy.cargo_drone_pad.distance",
                    dist));
        }
        if (initiated) {
            textList.add(Component.translatable("susy.cargo_drone_pad.initiated"));
        } else if (deposited) {
            textList.add(Component.translatable("susy.cargo_drone_pad.deposited"));
        }
        if (initiated || deposited) {
            textList.add(Component.translatable("susy.cargo_drone_pad.progress",
                    Math.round((double) flightTime * 100 / totalFlightTime)));
        }
    }

    @Override
    public boolean isActive() {
        return deposited || initiated;
    }
}
