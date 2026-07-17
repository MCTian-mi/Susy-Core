package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Vec3i;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import io.github.symmetricdevs.supersymmetry.api.metatileentity.IAnimatableMTE;

/**
 * Stub — GeckoLib 3 renderer removed.
 * <p>
 * In 1.12.2 this was a GeckoLib {@code IGeoRenderer<IAnimatableMTE>} enum
 * that animated multiblock machines. GeckoLib 3 is not available on 1.20.1;
 * animated MTE rendering will be re-added with GeckoLib 4.
 */
public class GeoMTERenderer {

    public static final GeoMTERenderer INSTANCE = new GeoMTERenderer();

    public static void setupLight(int light) {
        int lx = light % 0x10000;
        int ly = light / 0x10000;
        com.mojang.blaze3d.platform.Lighting.setupLevel(com.mojang.math.Matrix4f::new);
    }

    public static void rotateToFace(Direction face, Direction spin) {
        int angle = spin == Direction.EAST ? 90 : spin == Direction.SOUTH ? 180 : spin == Direction.WEST ? 270 : 0;
        switch (face) {
            case UP -> {
                RenderSystem.scale(-1, 1, 1);
                RenderSystem.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                RenderSystem.rotate(-angle, 0, 0, 1);
            }
            case DOWN -> {
                RenderSystem.rotate(270.0F, 1.0F, 0.0F, 0.0F);
                RenderSystem.rotate(spin == Direction.EAST || spin == Direction.WEST ? -angle : angle, 0, 0, 1);
            }
            case EAST -> {
                RenderSystem.rotate(270.0F, 0.0F, 1.0F, 0.0F);
                RenderSystem.rotate(angle, 0, 0, 1);
            }
            case WEST -> {
                RenderSystem.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                RenderSystem.rotate(angle, 0, 0, 1);
            }
            case NORTH -> RenderSystem.rotate(angle, 0, 0, 1);
            case SOUTH -> {
                RenderSystem.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                RenderSystem.rotate(angle, 0, 0, 1);
            }
        }
    }

    public static void flip(Direction facing) {
        int fX = facing.getStepX() == 0 ? 1 : -1;
        int fY = facing.getStepY() == 0 ? 1 : -1;
        int fZ = facing.getStepZ() == 0 ? 1 : -1;
        RenderSystem.scale(fX, fY, fZ);
    }
}
