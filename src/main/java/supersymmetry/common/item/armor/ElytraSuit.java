package supersymmetry.common.item.armor;

import gregtech.api.items.armor.ArmorLogicSuite;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.metaitem.stats.IItemCapabilityProvider;
import gregtech.api.items.metaitem.stats.IItemComponent;
import gregtech.api.util.GTUtility;
import gregtech.api.util.input.KeyBind;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import supersymmetry.api.SusyLog;
import supersymmetry.api.capability.IElytraFlyingProvider;
import supersymmetry.api.capability.SuSyCapabilities;
import supersymmetry.api.util.ElytraFlyingUtils;
import supersymmetry.client.models.ModelElectricElytra;


public class ElytraSuit extends ArmorLogicSuite {

    protected ElytraSuit(int energyPerUse, long maxCapacity, int tier) {
        super(energyPerUse, maxCapacity, tier, EntityEquipmentSlot.CHEST);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        SusyLog.logger.info(KeyBind.VANILLA_JUMP.isPressed(player));
        NBTTagCompound data = GTUtility.getOrCreateNbtCompound(itemStack);
        boolean pressed = false;
        boolean elytraActive = false;
        if (data.hasKey("pressed")) pressed = data.getBoolean("pressed");
        if (data.hasKey("elytraActive")) elytraActive = data.getBoolean("elytraActive");

        if (!pressed && KeyBind.VANILLA_JUMP.isKeyDown(player)) {
            pressed = true;
            if (!world.isRemote) {
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                if (!elytraActive) {
                    if (ElytraFlyingUtils.canTakeOff(playerMP)) {
                        playerMP.setElytraFlying();
                        elytraActive = true;
                    }
                } else {
                    playerMP.clearElytraFlying();
                    elytraActive = false;
                }
            }
        }

        if (pressed && !KeyBind.VANILLA_JUMP.isKeyDown(player)) pressed = false;

        data.setBoolean("pressed", pressed);
        data.setBoolean("elytraActive", elytraActive);
        player.inventoryContainer.detectAndSendChanges();
    }

    @Override
    public void addToolComponents(ArmorMetaItem.ArmorMetaValueItem mvi) {
        super.addToolComponents(mvi);
        mvi.addComponents(new TestElytraFlyingProvider());
    }

    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot,
                                    ModelBiped defaultModel) {
        return ModelElectricElytra.INSTANCE;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "gregtech:textures/armor/elytra.png";
    }

    private static class TestElytraFlyingProvider implements IItemComponent, IElytraFlyingProvider, ICapabilityProvider, IItemCapabilityProvider {

        @Override
        public boolean isElytraFlying(@NotNull EntityLivingBase entity, @NotNull ItemStack itemStack, boolean shouldStop) {
            if (entity instanceof EntityPlayer) {
                NBTTagCompound data = GTUtility.getOrCreateNbtCompound(itemStack);
                if (shouldStop) {
                    data.setBoolean("elytraActive", false);
                }
                return data.hasKey("elytraActive") && data.getBoolean("elytraActive");
            }
            return false;
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
