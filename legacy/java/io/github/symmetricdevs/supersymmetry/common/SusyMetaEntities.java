package io.github.symmetricdevs.supersymmetry.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.common.entities.*;

@Mod.EventBusSubscriber(modid = Supersymmetry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SusyMetaEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Supersymmetry.MODID);

    public static final EntityType<EntityDropPod> DROP_POD = ENTITY_TYPES.register("drop_pod",
            () -> EntityType.Builder.<EntityDropPod>of(EntityDropPod::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F).clientTrackingRange(64).updateInterval(3).build("drop_pod"));

    public static final EntityType<EntityDrone> DRONE = ENTITY_TYPES.register("drone",
            () -> EntityType.Builder.<EntityDrone>of(EntityDrone::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.6F).clientTrackingRange(64).updateInterval(3).build("drone"));

    public static final EntityType<EntityRocket> ROCKET = ENTITY_TYPES.register("rocket_basic",
            () -> EntityType.Builder.<EntityRocket>of(EntityRocket::new, MobCategory.MISC)
                    .sized(1.0F, 4.0F).clientTrackingRange(64).updateInterval(3).fireImmune().build("rocket_basic"));

    public static final EntityType<EntityLander> LANDER = ENTITY_TYPES.register("lander",
            () -> EntityType.Builder.<EntityLander>of(EntityLander::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F).clientTrackingRange(64).updateInterval(3).build("lander"));

    // EntityExplosion removed — no longer needed

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(DROP_POD, EntityDropPod.createAttributes().build());
        event.put(DRONE, EntityDrone.createAttributes().build());
        event.put(ROCKET, EntityRocket.createAttributes().build());
        event.put(LANDER, EntityLander.createAttributes().build());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Renderers will be re-added when GeckoLib 4 animation is ported
    }
}
