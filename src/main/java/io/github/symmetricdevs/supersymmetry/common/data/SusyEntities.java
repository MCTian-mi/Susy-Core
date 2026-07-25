package io.github.symmetricdevs.supersymmetry.common.data;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityDrone;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Entity types registered by Supersymmetry. */
public final class SusyEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Supersymmetry.MOD_ID);

    public static final RegistryObject<EntityType<EntityDrone>> DRONE = ENTITY_TYPES.register("drone",
            () -> EntityType.Builder.of(EntityDrone::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build(Supersymmetry.MOD_ID + ":drone"));

    private SusyEntities() {}
}
