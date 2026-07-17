package io.github.symmetricdevs.supersymmetry.client.renderer.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.VertexConsumer;

import org.jetbrains.annotations.NotNull;

/**
 * Froth bubble particle mostly copied from the standard minecraft bubble particle.
 * Allows for using custom colors and doesn't despawn outside of water allowing it to be used in the Froth Flotation
 * Tank
 *
 * @author h3tR
 */
@OnlyIn(Dist.CLIENT)
public class SusyParticleFrothBubble extends TextureSheetParticle {

    public SusyParticleFrothBubble(ClientLevel world, double x, double y, double z, double xSpeed,
                                   double ySpeed, double zSpeed, int color) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.rCol = ((color >> 16) & 0xFF) / 255f;
        this.gCol = ((color >> 8) & 0xFF) / 255f;
        this.bCol = (color & 0xFF) / 255f;

        this.setSize(0.02F, 0.02F);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
        this.xd = xSpeed * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
        this.yd = ySpeed * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
        this.zd = zSpeed * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
        this.setLifetime((int) (4.0D / (Math.random() * 0.8D + 0.2D)));
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.yd += 0.002D;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.8500000238418579D;
        this.yd *= 0.8500000238418579D;
        this.zd *= 0.8500000238418579D;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull net.minecraft.client.Camera camera, float partialTick) {
        // Ripped from standard vanilla particle render logic.
        // This particle uses a custom texture via ParticleRenderType.CUSTOM.
        // The render system will call this with the appropriate buffer.

        Vec3[] corners = this.getCorners(camera, partialTick);
        if (corners == null) return;

        float f5 = (float) (Mth.lerp(partialTick, this.xo, this.x) - camera.getPosition().x());
        float f6 = (float) (Mth.lerp(partialTick, this.yo, this.y) - camera.getPosition().y());
        float f7 = (float) (Mth.lerp(partialTick, this.zo, this.z) - camera.getPosition().z());

        int i = this.getLightColor(partialTick);
        int j = i >> 16 & 65535;
        int k = i & 65535;

        buffer.vertex(corners[0].x + f5, corners[0].y + f6, corners[0].z + f7)
                .uv(1, 1).color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(j | k << 16).endVertex();
        buffer.vertex(corners[1].x + f5, corners[1].y + f6, corners[1].z + f7)
                .uv(1, 0).color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(j | k << 16).endVertex();
        buffer.vertex(corners[2].x + f5, corners[2].y + f6, corners[2].z + f7)
                .uv(0, 0).color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(j | k << 16).endVertex();
        buffer.vertex(corners[3].x + f5, corners[3].y + f6, corners[3].z + f7)
                .uv(0, 1).color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(j | k << 16).endVertex();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    private Vec3[] getCorners(@NotNull net.minecraft.client.Camera camera, float partialTick) {
        float size = this.getQuadSize(partialTick);
        if (size <= 0) return null;

        float f4 = size * 0.1F;
        Vec3 viewDir = Vec3.directionFromRotation(0.0F, camera.getYRot());
        Vec3 right = new Vec3(-viewDir.z, 0, viewDir.x).normalize();
        Vec3 up = new Vec3(0, 1, 0);

        return new Vec3[]{
                new Vec3(-right.x * f4 - up.x * f4, -right.y * f4 - up.y * f4, -right.z * f4 - up.z * f4).multiply(-1, 1, 1),
                new Vec3(-right.x * f4 + up.x * f4, -right.y * f4 + up.y * f4, -right.z * f4 + up.z * f4),
                new Vec3(right.x * f4 + up.x * f4, right.y * f4 + up.y * f4, right.z * f4 + up.z * f4),
                new Vec3(right.x * f4 - up.x * f4, right.y * f4 - up.y * f4, right.z * f4 - up.z * f4)
        };
    }
}
