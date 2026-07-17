package io.github.symmetricdevs.supersymmetry.common.metatileentities.logistics;

import java.util.function.Predicate;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

import org.jetbrains.annotations.Nullable;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.unification.material.Material;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.logistics.MetaTileEntityDelegator;

public class MetaTileEntityBridge extends MetaTileEntityDelegator {

    protected final ICubeRenderer renderer;

    public MetaTileEntityBridge(ResourceLocation metaTileEntityId, Predicate<Capability<?>> capFilter,
                                ICubeRenderer renderer, Material baseMaterial) {
        this(metaTileEntityId, capFilter, renderer, baseMaterial.getMaterialRGB());
    }

    public MetaTileEntityBridge(ResourceLocation metaTileEntityId, Predicate<Capability<?>> capFilter,
                                ICubeRenderer renderer, int baseColor) {
        super(metaTileEntityId, capFilter, baseColor);
        this.renderer = renderer;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityBridge(metaTileEntityId, capFilter, renderer, baseColor);
    }

    @Override
    @Nullable
    public Direction getDelegatingFacing(Direction facing) {
        return facing == null ? null : facing.getOpposite();
    }

    @Override
    public boolean hasFrontFacing() {
        return false;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.renderer.render(renderState, translation, pipeline);
    }
}
