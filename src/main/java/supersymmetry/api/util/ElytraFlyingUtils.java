package supersymmetry.api.util;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import supersymmetry.api.capability.SuSyCapabilities;

public class ElytraFlyingUtils {

    public static boolean isElytraFlying(@NotNull EntityLivingBase entity) {
        ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (itemstack.hasCapability(SuSyCapabilities.ELYTRA_FLYING_PROVIDER, null)) {
            return (itemstack.getCapability(SuSyCapabilities.ELYTRA_FLYING_PROVIDER, null).isElytraFlying(
                    entity, itemstack,
                    entity.onGround ||
                            ((entity instanceof EntityPlayer) && ((EntityPlayer) entity).capabilities.isFlying) ||
                            entity.isRiding() || entity.isInWater() || isInLavaSafe(entity)));
        }
        return false;
    }

    public static boolean canTakeOff(EntityPlayerMP player) {
        return !player.onGround && player.motionY < 0.0D && !player.isElytraFlying() && !player.isInWater() && !isInLavaSafe(player);
    }

    // non-chunkloading copy of Entity.isInLava()
    private static boolean isInLavaSafe(@NotNull Entity entity) {
        return isMaterialInBBSafe(entity.world,
                entity.getEntityBoundingBox().expand(-0.10000000149011612D, -0.4000000059604645D,
                        -0.10000000149011612D),
                Material.LAVA);
    }

    // non-chunkloading copy of World.isMaterialInBB()
    private static boolean isMaterialInBBSafe(@NotNull World world, @NotNull AxisAlignedBB bb,
                                      @NotNull Material materialIn) {
        int i = MathHelper.floor(bb.minX);
        int j = MathHelper.ceil(bb.maxX);
        int k = MathHelper.floor(bb.minY);
        int l = MathHelper.ceil(bb.maxY);
        int i1 = MathHelper.floor(bb.minZ);
        int j1 = MathHelper.ceil(bb.maxZ);
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
                    if (world.isBlockLoaded(blockpos$pooledmutableblockpos, false) &&
                            world.getBlockState(blockpos$pooledmutableblockpos).getMaterial() == materialIn) {
                        blockpos$pooledmutableblockpos.release();
                        return true;
                    }
                }
            }
        }

        blockpos$pooledmutableblockpos.release();
        return false;
    }
}
