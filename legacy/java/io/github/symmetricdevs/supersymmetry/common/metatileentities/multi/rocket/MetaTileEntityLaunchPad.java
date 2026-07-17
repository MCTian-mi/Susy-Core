package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.rocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import cam72cam.mod.entity.ModdedEntity;


import com.gregtechceu.gtceu.api.capability.ItemHandlerList;
import com.gregtechceu.gtceu.api.items.itemhandlers.GTItemStackHandler;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.PatternMatchContext;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.utils.GTTransferUtils;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import io.github.symmetricdevs.supersymmetry.api.capability.SuSyDataCodes;
import io.github.symmetricdevs.supersymmetry.api.metatileentity.IAnimatableMTE;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.api.rocketry.fuels.RocketFuelEntry;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockRocketAssemblerCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityRocket;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityTransporterErector;

public class MetaTileEntityLaunchPad extends MultiblockControllerMachine implements IAnimatableMTE {

    private AABB trainAABB;
    private EntityTransporterErector selectedErector;
    private EntityRocket selectedRocket;
    private LaunchPadState state = LaunchPadState.EMPTY;
    protected IItemHandlerModifiable inputInventory;
    protected IMultipleTankHandler inputFluidInventory;

    // Animation helpers
    private double supportAngle = Math.PI / 4;
    private int reinitializationTimer = 0;
    private boolean needsReinitialization = false;

    @SideOnly(Side.CLIENT)
    private BlockPos lightPos;
    @SideOnly(Side.CLIENT)
    private Vec3i transformation;

    @SideOnly(Side.CLIENT)
    private BlockPos lightPos;
    private AABB renderBounding;
    @Nullable
    private Collection<BlockPos> hiddenBlocks;
    private int fuelingProgress;

    public MetaTileEntityLaunchPad(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityLaunchPad(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("DDDDDDDDDDDDD", "     CCC     ", "     CCC     ", "     CCC     ", "     CCC     ",
                        "     CCC     ", "     CCC     ", "     CCC     ", "     RRR     ")
                .aisle("DDDDDDDDDDDDD", "     CCC     ", "     CCC     ", "     CCC     ", "     CCC     ",
                        "     CCC     ", "     CCC     ", "     CCC     ", "     RRR     ")
                .aisle("DDDDDDDDDDDDD", "     CCC     ", "     CCC     ", "     CCC     ", "     CCC     ",
                        "     CCC     ", "     CCC     ", "     CCC     ", "     RRR     ")
                .aisle("DDDDDDDDDDDDD", "     CCC     ", "     CCC     ", "     CCC     ", "     CCC     ",
                        "     CCC     ", "     CCC     ", "     CCC     ", "     RRR     ")
                .aisle("DDDDDDDDDDDDD", "     CCC     ", "     CCC     ", "     CCC     ", "     CCC     ",
                        "     CCC     ", "     CCC     ", "     CCC     ", "     RRR     ")
                .aisle("DDDDDDDDDDDDD", "     CCC     ", "     CCC     ", "     CCC     ", "     CCC     ",
                        "     CCC     ", "     CCC     ", "     CCC     ", "     RRR     ")
                .aisle("DDDDDDDDDDDDD", "     FFF     ", "     FFF     ", "     FFF     ", "     FFF     ",
                        "     FFF     ", "     FFF     ", "     FFF     ", "     FFF     ")
                .aisle("DDDCCCCCCCDDD", "             ", "             ", "             ", "             ",
                        "             ", "             ", "             ", "             ")
                .aisle("DDDCCCCCCCDDD", "             ", "             ", "             ", "             ",
                        "             ", "             ", "             ", "             ")
                .aisle("DDDCCCCCCCDDD", "             ", "             ", "             ", "             ",
                        "             ", "             ", "             ", "             ")
                .aisle("DDDCCCCCCCDDD", " L         L ", " L         L ", " L         L ", " L         L ",
                        " L         L ", " L         L ", " L         L ", " L         L ")
                .aisle("DDDCCCCCCCDDD", "             ", "             ", "             ", "             ",
                        "             ", "             ", "             ", "             ")
                .aisle("DDDCCCCCCCDDD", "             ", "             ", "             ", "             ",
                        "             ", "             ", "             ", "             ")
                .aisle("DDDCCCCCCCDDD", "             ", "             ", "             ", "             ",
                        "             ", "             ", "             ", "             ")
                .aisle("DDDDDDDDDDDDD", "             ", "             ", "             ", "             ",
                        "             ", "             ", "             ", "             ")
                .aisle("DDDDDDDDDDDDD", "     FFF     ", "     FFF     ", "     FFF     ", "     FFF     ",
                        "     FFF     ", "     FFF     ", "     FFF     ", "     FFF     ")
                .aisle("DDDDDDSDDDDDD", "             ", "             ", "             ", "             ",
                        "             ", "             ", "             ", "             ")
                .where(' ', any())
                .where('A', air())
                .where('S', selfPredicate())
                .where('D', states(getFoundationState()).or(autoAbilities()))
                .where('C', states(getReinforcedFoundationState()))
                .where('F', frames(Materials.Steel))
                .where('R', SuSyPredicates.rails())
                .where('L', SuSyPredicates.hiddenStates(Blocks.AIR.getDefaultState(),
                        SuSyBlocks.SUPPORT.getDefaultState()))
                .build();
    }

    public TraceabilityPredicate autoAbilities() {
        return autoAbilities(true, true)
                .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(4));
    }

    public BlockState getFoundationState() {
        return SuSyBlocks.ROCKET_ASSEMBLER_CASING
                .getState(BlockRocketAssemblerCasing.RocketAssemblerCasingType.FOUNDATION);
    }

    public BlockState getReinforcedFoundationState() {
        return SuSyBlocks.ROCKET_ASSEMBLER_CASING
                .getState(BlockRocketAssemblerCasing.RocketAssemblerCasingType.REINFORCED_FOUNDATION);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        setTrainAABB();

        this.hiddenBlocks = context.getOrDefault("Hidden", new ArrayList<>());
        World world = getWorld();

        // This will only be called on a server side world
        // so actually no need to check !world.isRemote
        if (world != null) {
            disableBlockRendering(true);
            this.fillHiddenBlocksWith(SuSyBlocks.SUPPORT.getDefaultState());
        }
        if (this.needsReinitialization) {
            this.setLaunchPadState(LaunchPadState.INITIALIZING);
        } else {
            findRocket();
            if (this.selectedRocket != null) {
                this.setLaunchPadState(LaunchPadState.LOADED);
            }
        }

        this.inputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.inputFluidInventory = new FluidTankList(false,
                getAbilities(MultiblockAbility.IMPORT_FLUIDS));
    }

    @Override
    public void onPlacement() {
        super.onPlacement();
        this.needsReinitialization = true;
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.fillHiddenBlocksWith(Blocks.AIR.getDefaultState());
        this.trainAABB = null;
        this.needsReinitialization = true;
        this.inputInventory = new GTItemStackHandler(this, 0);
        this.inputFluidInventory = new FluidTankList(true);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    public void setTrainAABB() {
        // Had to make it overshoot a little :(
        BlockPos offsetBottomLeft = new BlockPos(6, 5, 9);
        BlockPos offsetTopRight = new BlockPos(-6, 20, 17);

        switch (this.getFrontFacing()) {
            case EAST:
                offsetBottomLeft = offsetBottomLeft.rotate(Rotation.CLOCKWISE_90);
                offsetTopRight = offsetTopRight.rotate(Rotation.CLOCKWISE_90);
                break;
            case SOUTH:
                offsetBottomLeft = offsetBottomLeft.rotate(Rotation.CLOCKWISE_180);
                offsetTopRight = offsetTopRight.rotate(Rotation.CLOCKWISE_180);
                break;
            case WEST:
                offsetBottomLeft = offsetBottomLeft.rotate(Rotation.COUNTERCLOCKWISE_90);
                offsetTopRight = offsetTopRight.rotate(Rotation.COUNTERCLOCKWISE_90);
                break;
            default:
                break;
        }

        this.trainAABB = new AABB(getPos().add(offsetBottomLeft), getPos().add(offsetTopRight));
    }

    public Vec3 getLaunchPosition() {
        Vec3 offset = new Vec3(0, 1, 6);
        switch (this.getFrontFacing()) {
            case EAST:
                offset = new Vec3(-6, 1, 0);
                break;
            case SOUTH:
                offset = new Vec3(0, 1, -6);
                break;
            case WEST:
                offset = new Vec3(6, 1, 0);
                break;
            default:
                break;
        }
        return new Vec3(this.getPos()).add(offset);
    }

    public AABB getRocketAABB() {
        Vec3 launchPosition = getLaunchPosition();
        return new AABB(launchPosition, launchPosition).expand(2, 2, 2);
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    protected void updateFormedValid() {
        switch (this.state) {
            case INITIALIZING:
                reinitializationTimer++;
                if (reinitializationTimer >= 20) {
                    this.needsReinitialization = false;
                    this.setLaunchPadState(LaunchPadState.EMPTY);
                }
                break;
            case EMPTY:
                if (this.getOffsetTimer() % 20 == 0) {
                    updateSelectedErector();
                    if (this.selectedErector != null) {
                        this.setLaunchPadState(LaunchPadState.LOADING);
                        this.selectedErector.setLiftingMode(EntityTransporterErector.LiftingMode.UP);
                    }
                } else {
                    break;
                }
            case LOADING:
                if (this.selectedErector == null || this.selectedErector.isDead()) {
                    this.setLaunchPadState(LaunchPadState.EMPTY);
                    break;
                }
                this.supportAngle = this.selectedErector.getLifterAngle();
                if (this.selectedErector.getLifterAngle() >= Math.PI / 2) {
                    this.selectedErector.setRocketLoaded(false);
                    spawnRocket();
                    this.setLaunchPadState(LaunchPadState.LOADED);
                } else {
                    break;
                }
            case LOADED:
                if (this.selectedRocket == null || this.selectedRocket.isDead) {
                    findRocket();
                    if (this.selectedRocket == null || this.selectedRocket.isDead) {
                        this.setLaunchPadState(LaunchPadState.EMPTY);
                        break;
                    }
                }
                loadCargo();
                if (this.getInputRedstoneSignal(this.getFrontFacing(), false) == 0) {
                    break;
                }
                this.setLaunchPadState(LaunchPadState.LAUNCHING);
            case LAUNCHING:
                if (this.selectedErector != null) {
                    this.selectedErector.setLiftingMode(EntityTransporterErector.LiftingMode.DOWN);
                }
                if (this.selectedRocket == null || this.selectedRocket.isDead) {
                    findRocket();
                    if (this.selectedRocket == null || this.selectedRocket.isDead) {
                        this.setLaunchPadState(LaunchPadState.EMPTY);
                        break;
                    }
                }
                this.supportAngle = Math.max(Math.PI / 4, this.supportAngle - (0.087 / 20));
                if (this.supportAngle <= Math.PI / 4 && !this.selectedRocket.isCountDownStarted()) {
                    this.selectedRocket.startCountdown(200);
                }
                if (this.selectedRocket.posY > this.getLaunchPosition().y + 40) {
                    this.setLaunchPadState(LaunchPadState.EMPTY);
                }
                break;
        }
    }

    // In liters per second
    private static final int MAX_FUELING_SPEED = 100;

    private void loadCargo() {
        GTTransferUtils.moveInventoryItems(this.inputInventory, selectedRocket.cargo);

        RocketFuelEntry fuelEntry = selectedRocket.getFuel();
        var composition = fuelEntry.getComposition();
        int unitsDrained = MAX_FUELING_SPEED;
        for (var comp : composition) {
            FluidStack drained = inputFluidInventory.drain(comp.getFirst().getFluid(MAX_FUELING_SPEED), false);
            int amount = drained == null ? 0 : drained.amount;
            // Intentional integer division moment
            unitsDrained = Math.min(amount, unitsDrained / comp.getSecond());
        }
        for (var comp : composition) {
            FluidStack drained = inputFluidInventory.drain(comp.getFirst()
                    .getFluid(comp.getSecond() * unitsDrained), true);
        }
    }

    private void setFuelingProgress(int fuelingProgress) {
        this.fuelingProgress = fuelingProgress;
        writeCustomData(SuSyDataCodes.UPDATE_FUEL_PROGRESS, (buf) -> buf.writeInt(fuelingProgress));
    }

    @Override
    protected void initializeInventory() {
        super.initializeInventory();
    }

    @Override
    protected boolean canMachineConnectRedstone(Direction side) {
        return side == this.getFrontFacing();
    }

    private void updateSelectedErector() {
        if (this.selectedErector == null) {
            List<ModdedEntity> trains = getWorld().getEntitiesWithinAABB(ModdedEntity.class, this.trainAABB);

            if (!trains.isEmpty()) {
                for (ModdedEntity forgeTrainEntity : trains) {
                    if (forgeTrainEntity.getSelf() instanceof EntityTransporterErector rollingStock &&
                            rollingStock.isRocketLoaded()) {
                        // Dot product check to make sure it's facing the right way
                        Vec3 toController = new Vec3(getPos()).subtract(rollingStock.internal.getPositionVector());
                        double dot = toController.dotProduct(rollingStock.internal.getLookVec());
                        if (dot > 0) {
                            this.selectedErector = rollingStock;
                        }
                    }
                }
            }
        } else {
            if (!this.selectedErector.internal.getEntityBoundingBox().intersects(this.trainAABB)) {
                this.selectedErector = null;
            }
        }
    }

    private void findRocket() {
        List<EntityRocket> rockets = getWorld().getEntitiesWithinAABB(EntityRocket.class, getRocketAABB());
        if (!rockets.isEmpty()) {
            this.selectedRocket = rockets.get(0);
        }
    }

    public void spawnRocket() {
        Vec3 position = this.getLaunchPosition();
        this.selectedRocket = new EntityRocket(this.getWorld(), position, this.getFrontFacing().getHorizontalAngle());
        if (this.selectedErector.getRocketNBT() != null) {
            // Copy in all tags
            for (Map.Entry<String, NBTBase> tag : selectedErector.getRocketNBT().tagMap.entrySet()) {
                this.selectedRocket.getEntityData().setTag(tag.getKey(), tag.getValue());
            }
            this.selectedRocket.initializeCargo();
        }
        this.getWorld().spawnEntity(this.selectedRocket);
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.state = LaunchPadState.valueOf(data.getString("state"));
        this.fuelingProgress = data.getInteger("fuelingProgress");
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag data) {
        data.setString("state", this.state.name());
        data.setInteger("fuelProgress", this.fuelingProgress);
        return super.writeToNBT(data);
    }

    @Override
    public void writeInitialSyncData(FriendlyByteBuf buf) {
        super.writeInitialSyncData(buf);
        if (this.isStructureFormed()) {
            findRocket();
            updateSelectedErector();
        }
        buf.writeEnumValue(this.state);
        buf.writeInt(this.fuelingProgress);
    }

    @Override
    public void receiveInitialSyncData(FriendlyByteBuf buf) {
        super.receiveInitialSyncData(buf);
        this.state = buf.readEnumValue(LaunchPadState.class);
        this.fuelingProgress = buf.readInt();
    }

    @Override
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        if (dataId == SuSyDataCodes.RESET_RENDER_FIELDS) {
            this.lightPos = null;
            this.renderBounding = null;
            this.transformation = null;
        } else if (dataId == SuSyDataCodes.UPDATE_RENDER_STATE) {
            this.state = buf.readEnumValue(LaunchPadState.class);
        } else if (dataId == SuSyDataCodes.UPDATE_FUEL_PROGRESS) {
            this.fuelingProgress = buf.readInt();
        } else {
            super.receiveCustomData(dataId, buf);
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        if (this.renderBounding == null) {
            Direction front = getFrontFacing();
            // The left side of the controller, not from the player's perspective
            Direction left = RelativeDirection.LEFT.getRelativeFacing(front, getUpwardsFacing(), isFlipped());
            Direction up = RelativeDirection.UP.getRelativeFacing(front, getUpwardsFacing(), isFlipped());

            BlockPos pos = getPos();

            var v1 = pos.offset(left.getOpposite(), 10).offset(up.getOpposite());
            var v2 = pos.offset(left, 10).offset(up, 31).offset(front.getOpposite(), 17);
            this.renderBounding = new AABB(v1, v2);
        }
        return renderBounding;
    }

    @Override
    @Nullable
    public Collection<BlockPos> getHiddenBlocks() {
        return hiddenBlocks;
    }

    protected void fillHiddenBlocksWith(BlockState state) {
        if (this.hiddenBlocks == null) {
            return;
        }
        for (BlockPos pos : this.hiddenBlocks) {
            getWorld().setBlockState(pos, state);
        }
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0.0F, this::predicate));
    }

    public void setLaunchPadState(LaunchPadState state) {
        if (this.state != state) {
            this.state = state;
            this.writeCustomData(SuSyDataCodes.UPDATE_RENDER_STATE, buf -> buf.writeEnumValue(state));
        }
    }

    @Override
    protected void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        textList.add(Component.translatable("susy.launch_pad." + this.state.name().toLowerCase()));
    }

    public enum LaunchPadState {
        INITIALIZING, // The launch pad is going through its initial animation of the supports coming out of the ground.
        EMPTY, // No rocket transporter has been selected, nor is there any rocket in the launch pad.
        LOADING, // A rocket transporter has been selected, causing it to begin the erecting process.
        LOADED, // A rocket has been loaded into the launch pad. Players should be able to enter through physical rocket
                // supports and remotely launch the rocket.
        LAUNCHING // The rocket supports retract and the engines are turned on.
    }
}
