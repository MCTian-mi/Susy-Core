package io.github.symmetricdevs.supersymmetry.mixins.xnet;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import com.gregtechceu.gtceu.api.block.machines.BlockMachine;
import com.gregtechceu.gtceu.api.pipenet.block.BlockPipe;
import mcjty.xnet.blocks.facade.FacadeItemBlock;

@Mixin(value = FacadeItemBlock.class)
public class FacadeItemBlockMixin {

    @Inject(method = "onItemUse(Lnet/minecraft/entity/player/Player;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/InteractionHand;Lnet/minecraft/util/Direction;FFF)Lnet/minecraft/util/EnumActionResult;",
            at = @At(
                     value = "INVOKE",
                     target = "Lmcjty/xnet/blocks/facade/FacadeItemBlock;setMimicBlock(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/state/BlockState;)V",
                     remap = false,
                     ordinal = 0),
            cancellable = true)
    private void skipGTTiles(Player why, World the, BlockPos hell, InteractionHand are, Direction there, float so,
                             float many, float variants,
                             CallbackInfoReturnable<EnumActionResult> cir, @Local(ordinal = 0) Block block) {
        if (block instanceof BlockPipe<?, ?, ?> || block instanceof BlockMachine) {
            cir.setReturnValue(EnumActionResult.FAIL);
        }
    }
}
