package io.github.symmetricdevs.supersymmetry.mixins.fluidlogged_api;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import git.jbredwards.fluidlogged_api.api.util.FluidState;
import git.jbredwards.fluidlogged_api.api.util.FluidloggedUtils;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import io.github.symmetricdevs.supersymmetry.common.network.SPacketRemoveFluidState;

@Mixin(value = FluidloggedUtils.class, remap = false)
public class FluidloggedUtilsMixin {

    @Inject(method = "setFluidState(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;Lgit/jbredwards/fluidlogged_api/api/util/FluidState;ZI)Z",
            at = @At("HEAD"),
            cancellable = true)
    private static void setFluidStateWithFlag(World world, BlockPos pos, BlockState here, FluidState fluidState,
                                              boolean checkVaporize, int blockFlags,
                                              CallbackInfoReturnable<Boolean> cir) {
        if ((blockFlags & 64) != 0) {
            // Makes sure the server knows there is no fluid
            cir.setReturnValue(FluidloggedUtils.setFluidState(world, pos, here, FluidState.EMPTY, checkVaporize,
                    blockFlags - 64));
            // Makes sure the client knows there is no fluid
            GregTechAPI.networkHandler.sendToDimension(new SPacketRemoveFluidState(pos), world.provider.getDimension());
        }
    }
}
