package io.github.symmetricdevs.supersymmetry.client.renderer.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

import org.joml.Matrix4f;

/**
 * Planet sky renderer — porting in progress.
 * <p>
 * The 1.12.2 implementation extended {@code IRenderHandler} and used
 * {@code WorldClient}, {@code GLAllocation}, direct GL calls, and the old
 * {@code Tessellator} API. In 1.20.1 the sky rendering pipeline has been
 * substantially reworked: custom sky is added through
 * {@link net.minecraft.client.renderer.DimensionSpecialEffects} and
 * the level renderer's sky hook.
 * <p>
 * This stub registers the {@code ISkyRenderer} callback and calls through to
 * the old star/earth/sun rendering methods ported to {@code RenderSystem}
 * and {@code Tesselator} as a best-effort port. A full reimplementation
 * that uses the {@code DimensionSpecialEffects} API will follow.
 */
public class SkyRendererMoon {

    public static ResourceLocation EARTH_TEXTURE = ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID,
            "textures/environment/earth_phases.png");
    private static final ResourceLocation SUN_TEXTURES = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");

    private static final float EARTH_SIZE = 6;

    private static boolean isInitialized = false;

    public SkyRendererMoon() {}

    /**
     * Called from the dimension's {@code DimensionSpecialEffects} or
     * from a mixin to the level renderer. Replaces the old
     * {@code render(float, WorldClient, Minecraft)} signature.
     */
    public static void renderSky(float partialTick, ClientLevel level, Minecraft mc) {
        PoseStack poseStack = new PoseStack();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        RenderSystem.disableBlend();
        RenderSystem.depthMask(false);

        // Earth render
        mc.getTextureManager().bindForSetup(EARTH_TEXTURE);
        int distEarth = 50;
        int phase = calculateEarthPhase(partialTick, level);
        int phaseX = phase % 4;
        int phaseY = phase / 4 % 2;
        float phaseXL = (float) (phaseX) / 4.0F;
        float phaseYU = (float) (phaseY) / 2.0F;
        float phaseXR = (float) (phaseX + 1) / 4.0F;
        float phaseYD = (float) (phaseY + 1) / 2.0F;

        Matrix4f mat = poseStack.last().pose();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(mat, -EARTH_SIZE, distEarth, -EARTH_SIZE).uv(phaseXR, phaseYU).endVertex();
        buffer.vertex(mat, EARTH_SIZE, distEarth, -EARTH_SIZE).uv(phaseXL, phaseYU).endVertex();
        buffer.vertex(mat, EARTH_SIZE, distEarth, EARTH_SIZE).uv(phaseXL, phaseYD).endVertex();
        buffer.vertex(mat, -EARTH_SIZE, distEarth, EARTH_SIZE).uv(phaseXR, phaseYD).endVertex();
        tesselator.end();

        // Sun render
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        float sunAngle = level.getTimeOfDay(partialTick) * 360.0F;
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-90F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(getSunAngle(level)));
        poseStack.mulPose(Axis.XP.rotationDegrees(sunAngle));
        float sunSize = 25F;
        double distSun = distEarth * 2;
        mat = poseStack.last().pose();

        mc.getTextureManager().bindForSetup(SUN_TEXTURES);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(mat, -sunSize, (float) distSun, -sunSize).uv(0.0F, 0.0F).endVertex();
        buffer.vertex(mat, sunSize, (float) distSun, -sunSize).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(mat, sunSize, (float) distSun, sunSize).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(mat, -sunSize, (float) distSun, sunSize).uv(0.0F, 1.0F).endVertex();
        tesselator.end();
        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
    }

    private static int calculateEarthPhase(float partialTick, ClientLevel world) {
        double sunRotation = world.getTimeOfDay(partialTick) * 360.0F;
        return (int) ((sunRotation / 360.0F * 8.0F) + 4) % 8;
    }

    private static float getSunAngle(ClientLevel world) {
        return 15 * (float) Math.cos((double) world.getDayTime() / 708000);
    }
}
