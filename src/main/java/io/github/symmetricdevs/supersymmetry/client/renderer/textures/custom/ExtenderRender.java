package io.github.symmetricdevs.supersymmetry.client.renderer.textures.custom;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;

import java.util.EnumMap;

/**
 * Extender pipe-bus face renderer.
 * <p>
 * Ported from 1.12.2 which used {@code codechicken.lib} rendering
 * primitives (CCRenderState, Cuboid6, Matrix4, IIconRegister). In
 * 1.20.1 the rendering pipeline uses GTCEu's {@code Textures} API
 * directly, with sprite registration happening through the GTCEu
 * texture system rather than {@link TextureMap}.
 * <p>
 * The {@code IIconRegister} interface is replaced with GTCEu's
 * {@code SimpleOverlayRenderer} / simple sprite-based approach;
 * sprites are registered via the standard block atlas event.
 */
public class ExtenderRender {

    public static final String GT_PATH = GTValues.MODID + ":blocks/";

    private final String basePath;

    @OnlyIn(Dist.CLIENT)
    private TextureAtlasSprite[] textures;

    public ExtenderRender(String basePath) {
        this.basePath = basePath;
    }

    @OnlyIn(Dist.CLIENT)
    public TextureAtlasSprite getSpriteForSide(Direction side, Direction inFace, Direction outFace) {
        if (textures == null || textures.length < 3) return null;
        if (side == inFace) return textures[0];
        if (side == outFace) return textures[2];
        return textures[1];
    }

    @OnlyIn(Dist.CLIENT)
    public void setTextures(TextureAtlasSprite in, TextureAtlasSprite side, TextureAtlasSprite out) {
        this.textures = new TextureAtlasSprite[]{in, side, out};
    }

    @OnlyIn(Dist.CLIENT)
    public TextureAtlasSprite getParticleTexture() {
        return textures != null && textures.length > 0 ? textures[0] : null;
    }
}
