package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;


/**
 * Stub — Grid overlay rendering for VariantCoverableBlock.
 */
@Mod.EventBusSubscriber(modid = Supersymmetry.MODID)
public class GridOverlayRenderer {

    private static float rColour;
    private static float gColour;
    private static float bColour;

    @OnlyIn(Dist.CLIENT)
    public static boolean shouldRenderGridOverlays(@NotNull BlockState state, @Nullable BlockEntity tile,
                                                   ItemStack mainHand, ItemStack offHand, boolean isSneaking) {
        // Coverable block entities are deferred until their BlockEntity renderer is ported.
        return tile != null;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean renderGridOverlays(@NotNull Player player, BlockPos pos, BlockState state,
                                             Direction facing, BlockEntity tile, float partialTicks) {
        return false;
    }
}
