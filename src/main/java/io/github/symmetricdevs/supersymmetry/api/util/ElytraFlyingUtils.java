package io.github.symmetricdevs.supersymmetry.api.util;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.modules.ModuleManager;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.capability.SuSyCapabilities;
import io.github.symmetricdevs.supersymmetry.integration.baubles.BaublesModule;
import io.github.symmetricdevs.supersymmetry.modules.SuSyModules;

public class ElytraFlyingUtils {

    @SuppressWarnings("DataFlowIssue")
    public static boolean isElytraFlying(@NotNull LivingEntity entity) {
        ItemStack itemstack = entity.getItemStackFromSlot(EquipmentSlot.CHEST);
        if (!itemstack.isEmpty() && isFlying(entity, itemstack)) {
            return true;
        }
        if (ModuleManager.getInstance().isModuleEnabled(Supersymmetry.MODID, SuSyModules.MODULE_BAUBLES)) {
            itemstack = BaublesModule.getElytraBauble(entity);
            return !itemstack.isEmpty() && isFlying(entity, itemstack);
        }
        return false;
    }

    public static boolean isFlying(@NotNull LivingEntity entity, ItemStack itemstack) {
        if (itemstack.hasCapability(SuSyCapabilities.ELYTRA_FLYING_PROVIDER, null)) {
            return itemstack.getCapability(SuSyCapabilities.ELYTRA_FLYING_PROVIDER, null).isElytraFlying(
                    entity, itemstack,
                    entity.onGround ||
                            entity instanceof Player && ((Player) entity).capabilities.isFlying ||
                            entity.isRiding() || entity.isInWater() || isInLavaSafe(entity));
        }
        return false;
    }

    public static boolean canTakeOff(Player player, boolean ignoreOnGround) {
        return (ignoreOnGround || (!player.onGround && player.motionY < 0.0D)) && !player.isElytraFlying() &&
                !player.isInWater() && !isInLavaSafe(player);
    }

    // non-chunkloading copy of Entity.isInLava()
    private static boolean isInLavaSafe(@NotNull Entity entity) {
        return isMaterialInBBSafe(entity.world,
                entity.getEntityBoundingBox().expand(-0.1, -0.4, -0.1),
                Material.LAVA);
    }

    // non-chunkloading copy of World.isMaterialInBB()
    private static boolean isMaterialInBBSafe(@NotNull World world, @NotNull AABB bb,
                                              @NotNull Material materialIn) {
        int i = MathHelper.floor(bb.minX);
        int j = MathHelper.ceil(bb.maxX);
        int k = MathHelper.floor(bb.minY);
        int l = MathHelper.ceil(bb.maxY);
        int i1 = MathHelper.floor(bb.minZ);
        int j1 = MathHelper.ceil(bb.maxZ);
        BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    pos.setPos(k1, l1, i2);
                    if (world.isBlockLoaded(pos, false) &&
                            world.getBlockState(pos).getMaterial() == materialIn) {
                        pos.release();
                        return true;
                    }
                }
            }
        }

        pos.release();
        return false;
    }
}
