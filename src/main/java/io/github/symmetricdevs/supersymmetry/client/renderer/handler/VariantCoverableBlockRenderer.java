package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.apache.commons.lang3.tuple.Pair;

import io.github.symmetricdevs.supersymmetry.api.util.SuSyUtility;
import io.github.symmetricdevs.supersymmetry.common.tileentities.TileEntityCoverable;

/**
 * Stub — Variant coverable block renderer.
 * The 1.12.2 implementation used codechicken.lib (ICCBlockRenderer, CCRenderState,
 * BlockRenderer, BlockRenderingRegistry, etc.) which is removed in 1.20.1.
 * This will be re-implemented as a standard BlockEntityRenderer or JSON model.
 */
@OnlyIn(Dist.CLIENT)
public class VariantCoverableBlockRenderer {

    public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(
            SuSyUtility.susyId("variant_coverable_block"), "normal");
    public static final VariantCoverableBlockRenderer INSTANCE = new VariantCoverableBlockRenderer();

    public static void preInit() {
        // Stub — codechicken.lib BlockRenderingRegistry removed in 1.20.1
    }

    public static TileEntityCoverable getTileCoverable(BlockGetter world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        return te instanceof TileEntityCoverable ? (TileEntityCoverable) te : null;
    }

    public static Pair<TextureAtlasSprite, Integer> getParticleTexture(BlockGetter world, BlockPos pos) {
        TileEntityCoverable tileECoverable = getTileCoverable(world, pos);
        if (tileECoverable == null) {
            return Pair.of(Minecraft.getInstance().getTextureAtlas().getShadowsSprite(), 0xFFFFFF);
        } else {
            return tileECoverable.getParticleTexture();
        }
    }
}
