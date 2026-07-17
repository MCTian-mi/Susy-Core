package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ITextureRegistrar {

    @SideOnly(Side.CLIENT)
    List<ResourceLocation> getTextureLocations();
}
