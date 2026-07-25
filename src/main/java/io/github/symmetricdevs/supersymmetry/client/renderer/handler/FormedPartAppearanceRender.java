package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.client.model.machine.IControllerModelRenderer;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Replaces a formed multiblock part's base model with the block state selected by the controller's
 * {@code partAppearance} callback. GTCEu keeps the original hatch or bus overlay quads intact.
 */
public final class FormedPartAppearanceRender
        extends DynamicRender<MultiblockControllerMachine, FormedPartAppearanceRender>
        implements IControllerModelRenderer {

    // spotless:off
    public static final Codec<FormedPartAppearanceRender> CODEC = Codec.unit(FormedPartAppearanceRender::new);
    public static final DynamicRenderType<MultiblockControllerMachine, FormedPartAppearanceRender> TYPE =
            new DynamicRenderType<>(CODEC);
    // spotless:on

    @Override
    public DynamicRenderType<MultiblockControllerMachine, FormedPartAppearanceRender> getType() {
        return TYPE;
    }

    @Override
    public void render(MultiblockControllerMachine machine, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        // This renderer only contributes baked quads while GTCEu replaces a formed part model.
    }

    @Override
    public boolean shouldRender(MultiblockControllerMachine machine, Vec3 cameraPos) {
        return false;
    }

    @Override
    public boolean isBlockEntityRenderer() {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderPartModel(List<BakedQuad> quads, IMultiController controller, IMultiPart part,
                                Direction frontFacing, @Nullable Direction side, RandomSource random,
                                @NotNull ModelData modelData, @Nullable RenderType renderType) {
        if (!(controller instanceof MultiblockControllerMachine machine)) {
            return;
        }

        BlockState appearance = controller.getPartAppearance(part, side, null, null);
        if (appearance == null || machine.getLevel() == null) {
            return;
        }

        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(appearance);
        if (model == null) {
            return;
        }

        emitQuads(quads, model, machine.getLevel(), part.self().getPos(), appearance, side, random,
                modelData, renderType);
    }

    private static void emitQuads(List<BakedQuad> quads, BakedModel model, BlockAndTintGetter level,
                                  BlockPos pos, BlockState state, @Nullable Direction side,
                                  RandomSource random, ModelData modelData, @Nullable RenderType renderType) {
        ModelData appearanceData = model.getModelData(level, pos, state, modelData);
        quads.addAll(model.getQuads(state, side, random, appearanceData, renderType));
    }
}
