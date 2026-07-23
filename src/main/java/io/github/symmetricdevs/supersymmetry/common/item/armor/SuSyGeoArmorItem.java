package io.github.symmetricdevs.supersymmetry.common.item.armor;

import com.gregtechceu.gtceu.api.item.armor.ArmorComponentItem;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import org.jetbrains.annotations.NotNull;

import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * GTCEu {@link ArmorComponentItem} that renders its worn model through GeckoLib.
 * The renderer is supplied per item instance so different armor sets can point to
 * different {@code .geo.json} / texture assets while sharing this base class.
 */
public class SuSyGeoArmorItem extends ArmorComponentItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<GeoArmorRenderer<SuSyGeoArmorItem>> rendererSupplier;

    public SuSyGeoArmorItem(ArmorMaterial material, ArmorItem.Type type, Properties properties,
                            Supplier<GeoArmorRenderer<SuSyGeoArmorItem>> rendererSupplier) {
        super(material, type, properties);
        this.rendererSupplier = rendererSupplier;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Static armor: no continuous animation controller needed.
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {

            private GeoArmorRenderer<SuSyGeoArmorItem> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack,
                                                                   EquipmentSlot slot, HumanoidModel<?> original) {
                if (renderer == null) {
                    renderer = rendererSupplier.get();
                }
                renderer.prepForRender(entity, stack, slot, original);
                return renderer;
            }

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return null;
            }
        });
    }

    @Override
    public double getTick(Object itemStack) {
        return GeoItem.super.getTick(itemStack);
    }
}
