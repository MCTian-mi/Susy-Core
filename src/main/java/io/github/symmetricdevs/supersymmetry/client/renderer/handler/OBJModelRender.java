package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Stub — the 1.12.2 version was a ModelRenderer extension using OBJLoader,
 * IBakedModel, Tessellator, display lists, GlStateManager, and codechicken.lib.
 * None of these APIs exist in 1.20.1. This will be replaced with GeckoLib 4
 * or JSON model rendering when armor visualization is re-added.
 */
@OnlyIn(Dist.CLIENT)
public class OBJModelRender {

    public float scale;

    public OBJModelRender(Object baseModel, ResourceLocation customModel) {
    }

    public OBJModelRender(Object baseModel, ResourceLocation customModel, float scaleMultiplier) {
    }
}
