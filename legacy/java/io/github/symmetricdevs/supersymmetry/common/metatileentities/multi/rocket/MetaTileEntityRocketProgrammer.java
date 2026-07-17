package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.rocket;

import static io.github.symmetricdevs.supersymmetry.common.entities.EntityAbstractRocket.ROCKET_CONFIG_KEY;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import org.jetbrains.annotations.NotNull;

import cam72cam.mod.entity.ModdedEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.PatternMatchContext;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.block.CasingBlock;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.capability.SuSyDataCodes;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityTransporterErector;
import io.github.symmetricdevs.supersymmetry.common.rocketry.RocketConfiguration;

public class MetaTileEntityRocketProgrammer extends MultiblockControllerMachine {

    protected IItemHandlerModifiable circuitHolder = new ItemStackHandler(1);
    protected AABB structureAABB;
    protected boolean canHandleFullConfig = true;

    public MetaTileEntityRocketProgrammer(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityRocketProgrammer(metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.setStructureAABB();
    }

    @Override
    protected void updateFormedValid() {
        if (this.getOffsetTimer() % 10 == 0 && this.getConfig() != null) {
            EntityTransporterErector rocket = searchRocket();
            if (rocket != null) {
                RocketConfiguration config = new RocketConfiguration(this.getConfig());
                // Set budget to 2
                // TODO: Make the transporter erector hold rocket types for IV
                setLowTierWarning(config.setBudget(this.getWorld().provider.getDimension(), 2));
                rocket.getRocketNBT().setTag(ROCKET_CONFIG_KEY, config.serialize());
            }
        }
    }

    private EntityTransporterErector searchRocket() {
        List<ModdedEntity> trains = getWorld().getEntitiesWithinAABB(ModdedEntity.class, this.structureAABB);

        if (!trains.isEmpty()) {
            for (ModdedEntity forgeTrainEntity : trains) {
                if (forgeTrainEntity.getSelf() instanceof EntityTransporterErector rollingStock &&
                        rollingStock.isRocketLoaded()) {
                    return rollingStock;
                }
            }
        }
        return null;
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.DOWN, RelativeDirection.FRONT)
                .aisle("     CCECC     ",
                        "   CCC   CCC   ",
                        "  CC       CC  ",
                        " CC         CC ",
                        " C           C ",
                        "CC           CC",
                        "C             C",
                        "S             E",
                        "C             C",
                        "CCCCCCCCCCCCCCC")
                .where(' ', any())
                .where('C', states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID)))
                .where('S', selfPredicate())
                .where('E',
                        states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID))
                                .or(abilities(MultiblockAbility.INPUT_ENERGY)))
                .build();
    }

    @Override
    public void clearMachineInventory(NonNullList<ItemStack> itemBuffer) {
        super.clearMachineInventory(itemBuffer);
        itemBuffer.add(circuitHolder.getStackInSlot(0));
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.ASSEMBLER_OVERLAY;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    public void setStructureAABB() {
        BlockPos left = this.getPos().offset(getRelativeFacing(RelativeDirection.RIGHT));
        BlockPos right = this.getPos().offset(getRelativeFacing(RelativeDirection.RIGHT), 13).offset(Direction.UP);

        this.structureAABB = new AABB(left, right);
    }

    protected Direction getRelativeFacing(RelativeDirection dir) {
        return dir.getRelativeFacing(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }

    public CompoundTag getConfig() {
        if (!circuitHolder.getStackInSlot(0).isEmpty()) {
            return circuitHolder.getStackInSlot(0).getTagCompound();
        }
        return null;
    }

    public void writeConfigItemToNBT(CompoundTag tag) {
        if (!circuitHolder.getStackInSlot(0).isEmpty()) {
            CompoundTag item = new CompoundTag();
            circuitHolder.getStackInSlot(0).writeToNBT(item);
            tag.setTag("config", item);
        }
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag data) {
        writeConfigItemToNBT(data);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        if (data.hasKey("config")) {
            this.circuitHolder.setStackInSlot(0, new ItemStack(data.getCompoundTag("config")));
        }
        reinitializeStructurePattern();
    }

    @Override
    protected void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!this.canHandleFullConfig) {
            textList.add(Component.translatable("susy.rocket_programmer.not_enough_budget"));
        }
    }

    @Override
    public void writeInitialSyncData(FriendlyByteBuf buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(this.canHandleFullConfig);
    }

    @Override
    public void receiveInitialSyncData(FriendlyByteBuf buf) {
        super.receiveInitialSyncData(buf);
        this.canHandleFullConfig = buf.readBoolean();
    }

    @Override
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        if (dataId == SuSyDataCodes.UPDATE_CAN_HANDLE_FULL_CONFIG) {
            this.canHandleFullConfig = buf.readBoolean();
        } else {
            super.receiveCustomData(dataId, buf);
        }
    }

    public void setLowTierWarning(boolean setWarning) {
        if (this.canHandleFullConfig != setWarning) {
            this.canHandleFullConfig = setWarning;
            if (!getWorld().isRemote) {
                writeCustomData(SuSyDataCodes.UPDATE_CAN_HANDLE_FULL_CONFIG, buf -> buf.writeBoolean(setWarning));
            }
        }
    }
}
