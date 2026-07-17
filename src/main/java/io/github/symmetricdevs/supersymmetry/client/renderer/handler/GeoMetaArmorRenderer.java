package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

/**
 * Stub — GeckoLib 3 armor renderer removed.
 * <p>
 * In 1.12.2 this was a GeckoLib {@code IGeoRenderer<IGeoMetaArmor>}
 * extending {@code ModelBiped}. GeckoLib 3 is not available on 1.20.1;
 * animated armor rendering will be re-added with GeckoLib 4.
 */
@SuppressWarnings("DuplicatedCode")
public class GeoMetaArmorRenderer extends HumanoidModel<LivingEntity> {

    public static final GeoMetaArmorRenderer INSTANCE = new GeoMetaArmorRenderer();

    public GeoMetaArmorRenderer() {
        super(0.0F);
    }

    public GeoMetaArmorRenderer setCurrentItem(LivingEntity mob, ItemStack itemStack,
                                               EquipmentSlot armorSlot) {
        return this;
    }

    public final GeoMetaArmorRenderer applyEntityStats(HumanoidModel<LivingEntity> defaultArmor) {
        return this;
    }

    public GeoMetaArmorRenderer applySlot(EquipmentSlot slot) {
        return this;
    }
}
