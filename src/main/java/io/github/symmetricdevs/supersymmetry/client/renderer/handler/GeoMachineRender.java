package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraft.core.Vec3i;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import org.jspecify.annotations.NonNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import io.github.symmetricdevs.supersymmetry.SuSyValues;

import java.util.HashMap;
import java.util.Map;

/**
 * GTCEu {@link DynamicRender} that delegates a formed multiblock's animated model to GeckoLib 4.
 *
 * <p>The model, animation, and texture are selected from the controller definition, while the
 * animatable remains keyed to the controller position so separate machines have separate timelines.
 */
public class GeoMachineRender extends DynamicRender<MultiblockControllerMachine, GeoMachineRender> {

    // spotless:off
    public static final Codec<GeoMachineRender> CODEC = Codec.unit(GeoMachineRender::new);
    public static final DynamicRenderType<MultiblockControllerMachine, GeoMachineRender> TYPE =
            new DynamicRenderType<>(CODEC);
    // spotless:on

    private static final Map<String, GeoMachineAssets> ASSETS = Map.of(
            "ball_mill", GeoMachineAssets.of("ball_mill", "default_loop"),
            "rotary_kiln_v2", GeoMachineAssets.of("rotary_kiln_v2", "rotary_kiln.animation"));
    private static final Map<Long, MachineAnimatable> ANIMATABLES = new HashMap<>();

    private final GeoObjectRenderer<MachineAnimatable> geoRenderer = new GeoObjectRenderer<>(
            new GeoMachineModel()) {

        @Override
        public long getInstanceId(MachineAnimatable animatable) {
            return animatable.instanceId;
        }
    };

    public GeoMachineRender() {}

    @Override
    public @NonNull DynamicRenderType<MultiblockControllerMachine, GeoMachineRender> getType() {
        return TYPE;
    }

    @Override
    public void render(MultiblockControllerMachine machine, float partialTick, @NonNull PoseStack poseStack,
                       @NonNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!machine.isFormed()) return;

        GeoMachineAssets assets = ASSETS.get(machine.getDefinition().getName());
        if (assets == null) return;

        BlockPos pos = machine.getPos();
        MachineAnimatable animatable = ANIMATABLES.compute(pos.asLong(), (id, existing) -> {
            if (existing == null || existing.machine != machine || existing.assets != assets) {
                return new MachineAnimatable(machine, assets, id);
            }
            return existing;
        });

        poseStack.pushPose();
        // Mirror the legacy GeoMTERenderer transform order: center on the controller block, apply the
        // machine's world-space model offset, then mirror (if flipped) and rotate to the facing. The
        // rotation must pivot about the block center, so centering happens BEFORE rotateToFace.
        poseStack.translate(0.5, 0.5, 0.5);

        Direction front = machine.getFrontFacing();
        Direction up = machine.getUpwardsFacing();
        boolean flipped = machine.isFlipped();

        Vec3i offset = machineOffset(machine.getDefinition().getName(), front, up, flipped);
        poseStack.translate(offset.getX(), offset.getY(), offset.getZ());

        if (flipped) {
            Direction left = RelativeDirection.LEFT.getRelative(front, up, true);
            flip(poseStack, left);
        }
        rotateToFace(poseStack, front, up);

        // GeoObjectRenderer#preRender re-centers the model by (0.5, 0.51, 0.5) AFTER this transform.
        // Applied in the rotated frame, that centering would drag the model one block backward for
        // 180deg/270deg facings, so cancel it here to keep our own block-center pivot authoritative.
        poseStack.translate(-0.5, -0.51, -0.5);

        // The controller sits embedded inside the casing, where sky/block light is occluded and the
        // BER-supplied packedLight is near-zero (model renders black). Legacy sampled combined light
        // at an elevated open-air offset instead; reproduce that per machine.
        int light = sampleLight(machine, pos, front, up, flipped, packedLight);

        RenderType renderType = RenderType.entityCutoutNoCull(assets.texture());
        geoRenderer.render(poseStack, animatable, buffer, renderType, buffer.getBuffer(renderType), light);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(MultiblockControllerMachine machine, @NonNull Vec3 cameraPos) {
        return machine.isFormed() && super.shouldRender(machine, cameraPos);
    }

    @Override
    public boolean shouldRenderOffScreen(MultiblockControllerMachine machine) {
        return machine.isFormed();
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(MultiblockControllerMachine machine) {
        BlockPos pos = machine.getPos();
        Direction front = machine.getFrontFacing();
        Direction up = machine.getUpwardsFacing();
        boolean flipped = machine.isFlipped();
        Direction left = RelativeDirection.LEFT.getRelative(front, up, flipped);
        Direction upDir = RelativeDirection.UP.getRelative(front, up, flipped);
        Direction back = front.getOpposite();

        return switch (machine.getDefinition().getName()) {
            // Ported from the legacy getRenderBoundingBox() of each machine.
            case "ball_mill" -> new AABB(
                    pos.relative(left.getOpposite(), 4).relative(upDir.getOpposite()),
                    pos.relative(left, 9).relative(upDir, 8).relative(back, 6));
            case "rotary_kiln_v2" -> new AABB(
                    pos.relative(left.getOpposite(), 7).relative(upDir.getOpposite(), 3),
                    pos.relative(left, 7).relative(upDir, 4).relative(back, 4));
            default -> new AABB(pos).inflate(8.0);
        };
    }

    /**
     * Controller-relative model displacement, ported from the legacy {@code getTransformation()} of each machine.
     * These anchor the Geo model origin against the formed structure.
     */
    private static Vec3i machineOffset(String name, Direction front, Direction up, boolean flipped) {
        Direction back = front.getOpposite();
        Direction left = RelativeDirection.LEFT.getRelative(front, up, flipped);
        Direction upDir = RelativeDirection.UP.getRelative(front, up, flipped);
        return switch (name) {
            case "ball_mill" -> new Vec3i(
                    back.getStepX() * 3 + left.getStepX() * 4 + upDir.getStepX() * 3,
                    back.getStepY() * 3 + left.getStepY() * 4 + upDir.getStepY() * 3,
                    back.getStepZ() * 3 + left.getStepZ() * 4 + upDir.getStepZ() * 3);
            case "rotary_kiln_v2" -> new Vec3i(
                    back.getStepX() - upDir.getStepX(),
                    back.getStepY() - upDir.getStepY(),
                    back.getStepZ() - upDir.getStepZ());
            default -> Vec3i.ZERO;
        };
    }

    /**
     * Recompute combined light at the legacy {@code getLightPos()} sample point of each machine. The
     * controller is buried inside the structure, so sampling at an open-air offset avoids a black model.
     * Falls back to the BER-supplied value for machines without a legacy light offset.
     */
    private static int sampleLight(MultiblockControllerMachine machine, BlockPos pos, Direction front,
                                   Direction up, boolean flipped, int fallback) {
        Level level = machine.getLevel();
        if (level == null) return fallback;

        Direction back = front.getOpposite();
        Direction left = RelativeDirection.LEFT.getRelative(front, up, flipped);
        Direction upDir = RelativeDirection.UP.getRelative(front, up, flipped);
        BlockPos samplePos = switch (machine.getDefinition().getName()) {
            case "ball_mill" -> pos.relative(upDir, 6).relative(back, 3).relative(left, 3);
            case "rotary_kiln_v2" -> pos.relative(upDir, 2).relative(back, 1);
            default -> null;
        };
        if (samplePos == null) return fallback;

        return LevelRenderer.getLightColor(level, samplePos);
    }

    /** Legacy {@code GeoMTERenderer.flip}: mirror the model across the axis of {@code facing}. */
    private static void flip(PoseStack poseStack, Direction facing) {
        float fX = facing.getStepX() == 0 ? 1 : -1;
        float fY = facing.getStepY() == 0 ? 1 : -1;
        float fZ = facing.getStepZ() == 0 ? 1 : -1;
        poseStack.scale(fX, fY, fZ);
    }

    /** Legacy {@code GeoMTERenderer.rotateToFace}: orient the model for any front facing and spin (up) direction. */
    private static void rotateToFace(PoseStack poseStack, Direction face, Direction spin) {
        int angle = spin == Direction.EAST ? 90 : spin == Direction.SOUTH ? 180 : spin == Direction.WEST ? 270 : 0;
        switch (face) {
            case UP -> {
                poseStack.scale(-1, 1, 1);
                rotateDeg(poseStack, 90, 1, 0, 0);
                rotateDeg(poseStack, -angle, 0, 0, 1);
            }
            case DOWN -> {
                rotateDeg(poseStack, 270, 1, 0, 0);
                rotateDeg(poseStack, spin == Direction.EAST || spin == Direction.WEST ? -angle : angle, 0, 0, 1);
            }
            case EAST -> {
                rotateDeg(poseStack, 270, 0, 1, 0);
                rotateDeg(poseStack, angle, 0, 0, 1);
            }
            case WEST -> {
                rotateDeg(poseStack, 90, 0, 1, 0);
                rotateDeg(poseStack, angle, 0, 0, 1);
            }
            case NORTH -> rotateDeg(poseStack, angle, 0, 0, 1);
            case SOUTH -> {
                rotateDeg(poseStack, 180, 0, 1, 0);
                rotateDeg(poseStack, angle, 0, 0, 1);
            }
        }
    }

    private static void rotateDeg(PoseStack poseStack, float deg, float x, float y, float z) {
        if (deg == 0) return;
        poseStack.mulPose(new Quaternionf().rotateAxis((float) Math.toRadians(deg), x, y, z));
    }

    private record GeoMachineAssets(ResourceLocation model, ResourceLocation texture, ResourceLocation animation,
                                    RawAnimation loop) {

        private static GeoMachineAssets of(String name, String animationName) {
            return new GeoMachineAssets(
                    SuSyValues.susyId("geo/" + name + ".geo.json"),
                    SuSyValues.susyId("textures/geo/" + name + "/all.png"),
                    SuSyValues.susyId("animations/" + name + ".animation.json"),
                    RawAnimation.begin().thenLoop(animationName));
        }
    }

    private static final class GeoMachineModel extends GeoModel<MachineAnimatable> {

        @Override
        public ResourceLocation getModelResource(MachineAnimatable animatable) {
            return animatable.assets.model();
        }

        @Override
        public ResourceLocation getTextureResource(MachineAnimatable animatable) {
            return animatable.assets.texture();
        }

        @Override
        public ResourceLocation getAnimationResource(MachineAnimatable animatable) {
            return animatable.assets.animation();
        }
    }

    /**
     * Lightweight {@link GeoAnimatable} wrapper around one controller render call.
     */
    private static final class MachineAnimatable implements GeoAnimatable {

        private final MultiblockControllerMachine machine;
        private final GeoMachineAssets assets;
        private final long instanceId;
        private final AnimatableInstanceCache cache;

        MachineAnimatable(MultiblockControllerMachine machine, GeoMachineAssets assets, long instanceId) {
            this.machine = machine;
            this.assets = assets;
            this.instanceId = instanceId;
            this.cache = GeckoLibUtil.createInstanceCache(this);
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            // Legacy parity: keep the loop bound, but only advance it while the machine is running
            // (isActive covers WORKING/WAITING). Idle machines freeze on the current frame.
            controllers.add(new AnimationController<>(this, "machine_loop", state -> {
                state.setAnimation(assets.loop());
                return isActive() ? PlayState.CONTINUE : PlayState.STOP;
            }));
        }

        private boolean isActive() {
            return machine instanceof IRecipeLogicMachine logic && logic.isActive();
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }

        @Override
        public double getTick(Object animatable) {
            return machine.getOffsetTimer();
        }
    }
}
