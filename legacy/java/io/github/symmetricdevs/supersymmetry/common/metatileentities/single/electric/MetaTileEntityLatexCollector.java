package io.github.symmetricdevs.supersymmetry.common.metatileentities.single.electric;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.PseudoMultiMachineMetaTileEntity;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.api.util.SuSyUtility;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityLatexCollector extends PseudoMultiMachineMetaTileEntity {

    private final int tankSize = 16000;

    public MetaTileEntityLatexCollector(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, SuSyRecipeMaps.LATEX_COLLECTOR_RECIPES, SusyTextures.LATEX_COLLECTOR_OVERLAY, tier,
                true, SuSyUtility.collectorTankSizeFunction);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityLatexCollector(this.metaTileEntityId, this.getTier());
    }

    @Override
    protected FluidTankList createExportFluidHandler() {
        return new FluidTankList(false, new FluidTank(this.tankSize));
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        return new ItemStackHandler(1);
    }

    @Override
    protected IItemHandlerModifiable createExportItemHandler() {
        return new ItemStackHandler(1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        SusyTextures.LATEX_COLLECTOR_OVERLAY.renderOrientedState(renderState, translation, pipeline,
                this.getFrontFacing(), this.isActive(), true);
    }

    @Override
    public <T> void addNotifiedInput(T input) {
        super.addNotifiedInput(input);
        this.onNeighborChanged();
    }

    @Override
    public boolean isValidFrontFacing(Direction facing) {
        return super.isValidFrontFacing(facing) && facing != getOutputFacingFluids().getOpposite() &&
                facing != getOutputFacingItems().getOpposite();
    }

    @Override
    public void setFrontFacing(Direction frontFacing) {
        super.setFrontFacing(frontFacing);
        if (this.getOutputFacingFluids() == frontFacing.getOpposite() ||
                this.getOutputFacingItems() == frontFacing.getOpposite()) {
            this.setOutputFacing(frontFacing.rotateY());
        }
    }

    @Override
    public boolean onWrenchClick(Player playerIn, InteractionHand hand, Direction facing,
                                 CuboidRayTraceResult hitResult) {
        if (!playerIn.isSneaking()) {
            if (this.getOutputFacing() == facing) {
                return false;
            } else if (this.hasFrontFacing() && facing == this.getFrontFacing() ||
                    facing == this.getFrontFacing().getOpposite()) {
                        return false;
                    } else {
                        if (!this.getWorld().isRemote) {
                            this.setOutputFacing(facing);
                        }
                        return true;
                    }
        } else {
            return super.onWrenchClick(playerIn, hand, facing, hitResult);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.set(1, I18n.format("susy.machine.latex_collector.tooltip"));
    }

    @Override
    public boolean getIsWeatherOrTerrainResistant() {
        return true;
    }
}
