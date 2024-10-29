package supersymmetry.mixins.early;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import supersymmetry.api.util.ElytraFlyingUtils;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase {

    public EntityPlayerMixin(World worldIn) {
        super(worldIn);
    }

    @Override
    public boolean isElytraFlying() {
        return super.isElytraFlying() || ElytraFlyingUtils.isElytraFlying(this);
    }

    @Shadow
    @NotNull
    public abstract ItemStack getItemStackFromSlot(@NotNull EntityEquipmentSlot slotIn);
}
