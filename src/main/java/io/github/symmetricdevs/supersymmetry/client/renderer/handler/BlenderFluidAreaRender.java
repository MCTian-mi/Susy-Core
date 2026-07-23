package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IFluidRenderMulti;
import com.gregtechceu.gtceu.client.renderer.block.FluidBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.RenderUtil;
import com.gregtechceu.gtceu.config.ConfigHolder;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.RenderTypeHelper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * GTCEu dynamic renderer for the Blender's active recipe fluid.
 *
 * <p>The native {@link IFluidRenderMulti} interface synchronizes the formed 3x3 region. Unlike
 * GTCEu's generic renderer, this variant deliberately takes only the first fluid output, matching
 * the legacy Blender rather than falling back to a recipe input.
 */
public final class BlenderFluidAreaRender extends DynamicRender<IFluidRenderMulti, BlenderFluidAreaRender> {

    // The legacy renderer filled the bottom 3/16ths of each interior cell. An UP plane starts at
    // y=1, so moving it down 13/16 preserves that visual level without a custom fluid mesh.
    private static final FluidBlockRenderer FLUID_RENDERER = FluidBlockRenderer.Builder.create()
            .setFaceOffset(-13.0F / 16.0F)
            .getRenderer();

    // spotless:off
    public static final Codec<BlenderFluidAreaRender> CODEC = Codec.unit(BlenderFluidAreaRender::new);
    public static final DynamicRenderType<IFluidRenderMulti, BlenderFluidAreaRender> TYPE =
            new DynamicRenderType<>(CODEC);
    // spotless:on

    public BlenderFluidAreaRender() {}

    @Override
    public DynamicRenderType<IFluidRenderMulti, BlenderFluidAreaRender> getType() {
        return TYPE;
    }

    @Override
    public int getViewDistance() {
        return 32;
    }

    @Override
    public void render(IFluidRenderMulti machine, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        if (!ConfigHolder.INSTANCE.client.renderer.renderFluids || !machine.isFormed()) return;

        var offsets = machine.getFluidOffsets();
        if (offsets == null || offsets.isEmpty()) return;

        Fluid fluid = getOutputFluid(machine);
        if (fluid == null) return;

        var fluidRenderType = ItemBlockRenderTypes.getRenderLayer(fluid.defaultFluidState());
        var consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(fluidRenderType, false));

        poseStack.pushPose();
        FLUID_RENDERER.drawPlane(Direction.UP, offsets, poseStack.last().pose(), consumer, fluid,
                RenderUtil.FluidTextureType.STILL, packedOverlay, machine.self().getPos());
        poseStack.popPose();
    }

    private static @Nullable Fluid getOutputFluid(IFluidRenderMulti machine) {
        if (!machine.isActive()) return null;

        var recipe = machine.getRecipeLogic().getLastRecipe();
        if (recipe == null) return null;

        for (var content : recipe.getOutputContents(FluidRecipeCapability.CAP)) {
            var ingredient = FluidRecipeCapability.CAP.of(content.content);
            for (var stack : ingredient.getStacks()) {
                if (!stack.isEmpty()) return stack.getFluid();
            }
        }
        return null;
    }

    @Override
    public boolean shouldRenderOffScreen(IFluidRenderMulti machine) {
        return true;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(IFluidRenderMulti machine) {
        AABB box = super.getRenderBoundingBox(machine);
        for (var offset : machine.getFluidOffsets()) {
            box = box.minmax(new AABB(offset));
        }
        return box.inflate(getViewDistance());
    }
}
