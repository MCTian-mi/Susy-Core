package supersymmetry.common.item.armor;

import gregtech.api.items.armor.ArmorLogicSuite;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.metaitem.ElectricStats;
import gregtech.api.items.metaitem.stats.IItemCapabilityProvider;
import gregtech.api.items.metaitem.stats.IItemComponent;
import gregtech.api.util.input.KeyBind;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import supersymmetry.api.capability.IElytraFlyingProvider;
import supersymmetry.api.capability.SuSyCapabilities;

import java.util.List;

public class ElytraSuit extends ArmorLogicSuite {

    protected ElytraSuit(int energyPerUse, long maxCapacity, int tier) {
        super(energyPerUse, maxCapacity, tier, EntityEquipmentSlot.CHEST);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        boolean flyKeyDown = KeyBind.VANILLA_JUMP.isKeyDown(player);
        if (flyKeyDown && !player.isElytraFlying() && !player.capabilities.isFlying) {
            if (!world.isRemote) {
                ((EntityPlayerMP) player).setElytraFlying();
            }
        }
    }

    @Override
    public void addToolComponents(ArmorMetaItem.ArmorMetaValueItem mvi) {
        super.addToolComponents(mvi);
        mvi.addComponents(new TestElytraFlyingProvider());
    }

    private static class TestElytraFlyingProvider implements IItemComponent, IElytraFlyingProvider, ICapabilityProvider, IItemCapabilityProvider {

        @Override
        public boolean isElytraFlying(@NotNull EntityLivingBase entity, @NotNull ItemStack itemstack, boolean shouldStop) {
            return entity instanceof EntityPlayer;
        }

        @Override
        public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == SuSyCapabilities.ELYTRA_FLYING_PROVIDER;
        }

        @Nullable
        @Override
        public <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == SuSyCapabilities.ELYTRA_FLYING_PROVIDER ? SuSyCapabilities.ELYTRA_FLYING_PROVIDER.cast(this) : null;
        }

        @Override
        public ICapabilityProvider createProvider(ItemStack itemStack) {
            return this;
        }
    }
}
