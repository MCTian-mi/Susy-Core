package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static com.gregtechceu.gtceu.api.util.RelativeDirection.*;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.gregtechceu.gtceu.api.capability.GregtechDataCodes;
import com.gregtechceu.gtceu.api.fluids.FluidState;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.PatternMatchContext;
import com.gregtechceu.gtceu.api.util.TextComponentUtil;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.blocks.BlockFireboxCasing;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MetaTileEntityMufflerHatch;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.VoidingMultiblockBase;

public class MetaTileEntityFlareStack extends VoidingMultiblockBase {

    // Storing this, just in case it is ever needed
    private int height = 5;

    public MetaTileEntityFlareStack(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityFlareStack(this.metaTileEntityId);
    }

    @Override
    public boolean canVoidState(FluidState state) {
        return switch (state) {
            case GAS, LIQUID -> true;
            default -> false;
        };
    }

    @Override
    public boolean incinerate() {
        return true;
    }

    protected BlockPattern createStructurePattern() {
        // May want to force the input to be underneath the pipe casings
        return FactoryBlockPattern.start(FRONT, RIGHT, UP)
                .aisle("S")
                .aisle("P").setRepeatable(3, 7)
                .aisle("F")
                .where('S', this.selfPredicate())
                .where('P', states(this.getFireboxCasingState())
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setExactLimit(1)))
                .where('F', abilities(MultiblockAbility.MUFFLER_HATCH).setExactLimit(1))
                .build();
    }

    // Updates the height and rate of the multiblock
    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.updateHeight();
    }

    // Update the height when rotating the multiblock
    @Override
    public void setUpwardsFacing(Direction upwardsFacing) {
        super.setUpwardsFacing(upwardsFacing);
        this.updateHeight();
    }

    public void updateHeight() {
        World world = getWorld();
        if (world == null) { // JEI previews
            return;
        }
        // Minimum height
        int height = 5;
        // One block below the minimum height

        Direction relativeUp = UP.getRelativeFacing(this.getFrontFacing(), this.getUpwardsFacing(), this.isFlipped());

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(getPos().offset(relativeUp, height - 2));
        for (; height < 10; height++) {
            if (isBlockMuffler(world, pos.move(relativeUp))) break;
        }

        this.height = height;
        // Arbitrary base, minimum structure height is five blocks
        this.rateBonus = (int) Math.pow(2, height - 5);

        writeCustomData(GregtechDataCodes.UPDATE_STRUCTURE_SIZE, buf -> {
            buf.writeInt(this.height);
            buf.writeInt(this.rateBonus);
        });
    }

    // For determining the multiblocks height
    public boolean isBlockMuffler(World world, @NotNull BlockPos pos) {
        if (world == null) { // JEI previews
            return true;
        }
        if (world.getTileEntity(pos) instanceof IGregTechTileEntity gtTe) {
            MetaTileEntity mte = gtTe.getMetaTileEntity();
            return mte instanceof MetaTileEntityMufflerHatch;
        }
        return false;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                this.isActive(), true);
    }

    @Override
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.UPDATE_STRUCTURE_SIZE) {
            this.height = buf.readInt();
            this.rateBonus = buf.readInt();
        }
    }

    @Override
    public CompoundTag writeToNBT(@NotNull CompoundTag data) {
        super.writeToNBT(data);
        data.setInteger("height", this.height);
        data.setInteger("rateBonus", this.rateBonus);
        return data;
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.height = data.hasKey("height") ? data.getInteger("height") : this.height;
        this.rateBonus = data.hasKey("rateBonus") ? data.getInteger("rateBonus") : this.rateBonus;
    }

    @Override
    public void writeInitialSyncData(FriendlyByteBuf buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(height);
        buf.writeInt(rateBonus);
    }

    @Override
    public void receiveInitialSyncData(FriendlyByteBuf buf) {
        super.receiveInitialSyncData(buf);
        this.height = buf.readInt();
        this.rateBonus = buf.readInt();
    }

    @Override
    protected void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            Component componentHeight = TextComponentUtil.stringWithColor(ChatFormatting.BLUE,
                    String.valueOf(this.height));
            Component componentRateBonus = TextComponentUtil.stringWithColor(ChatFormatting.DARK_PURPLE,
                    this.rateBonus + "x");
            Component componentRateBase = TextComponentUtil.translationWithColor(ChatFormatting.GRAY,
                    "susy.machine.flare_stack.rate",
                    componentRateBonus);
            Component componentRateHover = TextComponentUtil.translationWithColor(ChatFormatting.GRAY,
                    "susy.machine.flare_stack.rate_hover");

            textList.add(TextComponentUtil.translationWithColor(
                    ChatFormatting.GRAY,
                    "susy.machine.flare_stack.height",
                    componentHeight));
            textList.add(TextComponentUtil.setHover(componentRateBase, componentRateHover));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        tooltip.add(I18n.format("susy.machine.flare_stack.tooltip.1", getBaseVoidingRate()));
        tooltip.add(I18n.format("susy.machine.flare_stack.tooltip.2"));
        super.addInformation(stack, world, tooltip, advanced);
    }

    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.SOLID_STEEL_CASING;
    }

    protected static BlockState getFireboxCasingState() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX);
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return SusyTextures.FLARE_STACK_OVERLAY;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return true;
    }
}
