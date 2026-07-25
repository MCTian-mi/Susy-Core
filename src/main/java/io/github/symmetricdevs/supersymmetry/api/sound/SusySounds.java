package io.github.symmetricdevs.supersymmetry.api.sound;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Sound event registry for Supersymmetry.
 * <p>
 * Uses DeferredRegister to register SoundEvents on the mod event bus.
 * Register via {@code SusySounds.SOUNDS.register(modEventBus)} in the mod constructor.
 */
public class SusySounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, Supersymmetry.MOD_ID);

    private SusySounds() {}

    // Rocket/engine sounds
    public static final RegistryObject<SoundEvent> ROCKET_LOOP = SOUNDS.register("rocket_loop",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "rocket_loop")));

    public static final RegistryObject<SoundEvent> DRONE_TAKEOFF = SOUNDS.register("drone_takeoff",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "drone_takeoff")));

    public static final RegistryObject<SoundEvent> ROCKET_LAUNCH = SOUNDS.register("rocket_launch",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "rocket_launch")));

    public static final RegistryObject<SoundEvent> JET_ENGINE_LOOP = SOUNDS.register("jet_engine_active",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "jet_engine_active")));

    // Block sounds
    public static final RegistryObject<SoundEvent> LOCKED_CRATE = SOUNDS.register("locked_crate",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "locked_crate")));

    public static final RegistryObject<SoundEvent> COMPLEX_ALARM = SOUNDS.register("complex_alarm",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "complex_alarm")));

    public static final RegistryObject<SoundEvent> METAL_DOOR_CLOSE = SOUNDS.register("metal_door_close",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "metal_door_close")));

    public static final RegistryObject<SoundEvent> METAL_DRAWER_OPEN = SOUNDS.register("metal_drawer_open",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "metal_drawer_open")));
}