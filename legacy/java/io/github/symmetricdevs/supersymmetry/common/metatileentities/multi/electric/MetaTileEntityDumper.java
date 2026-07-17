package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
// FluidRegistry removed in 1.20.1
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.gregtechceu.gtceu.api.fluids.FluidState;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.api.util.TextComponentUtil;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.block.CasingBlock.MetalCasingType;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.VoidingMultiblockBase;

public class MetaTileEntityDumper extends VoidingMultiblockBase {

    public MetaTileEntityDumper(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        // Hardcode these annoyances for now
        fluidCache.put(FluidRegistry.WATER, true);
        fluidCache.put(FluidRegistry.LAVA, true);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityDumper(this.metaTileEntityId);
    }

    @Override
    public boolean canVoidState(FluidState state) {
        return state == FluidState.LIQUID;
    }

    @Override
    public int getBaseVoidingRate() {
        return 16000;
    }

    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("A  A", "BBBB", "A  A")
                .aisle("BBBB", "C##A", "BBBB")
                .aisle("A  A", "BSBB", "A  A")
                .where('S', selfPredicate())
                .where('A', frames(Materials.Steel))
                .where('B', states(MetaBlocks.METAL_CASING.getState(MetalCasingType.STEEL_SOLID)))
                .where('C', abilities(MultiblockAbility.IMPORT_FLUIDS).setExactLimit(1))
                .where(' ', any())
                .where('#', air())
                .build();
    }

    @Override
    public void update() {
        super.update();

        if (this.isActive()) {
            if (getWorld().isRemote) {
                dumpingParticles();
            }
        }
    }

    @Override
    protected void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            Component componentRate = TextComponentUtil.stringWithColor(ChatFormatting.DARK_PURPLE,
                    this.getBaseVoidingRate() + " L/10t");

            textList.add(TextComponentUtil.translationWithColor(
                    ChatFormatting.GRAY,
                    "susy.machine.dumper.rate",
                    componentRate));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        tooltip.add(I18n.format("susy.machine.dumper.tooltip.1", getBaseVoidingRate()));
        super.addInformation(stack, world, tooltip, advanced);
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

    @SideOnly(Side.CLIENT)
    private void dumpingParticles() {
        Direction facing = this.getFrontFacing().getOpposite();
        Vec3 pos = new Vec3(this.getPos()).add(0.5, 0.5, 0.5);

        float xPos = (float) (pos.x + (1F * facing.getXOffset()) + (2.5F * -facing.getZOffset()));
        float yPos = (float) pos.y;

        float zPos = (float) (pos.z + (2.5F * facing.getXOffset()) + (1F * facing.getZOffset()));
        float ySpd = 0F;
        float xSpd = facing.getZOffset() * 1F;
        float zSpd = -facing.getXOffset() * 1F;

        getWorld().spawnParticle(EnumParticleTypes.WATER_DROP, xPos, yPos, zPos, xSpd, ySpd, zSpd);
    }

    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return SusyTextures.DUMPER_OVERLAY;
    }
}
