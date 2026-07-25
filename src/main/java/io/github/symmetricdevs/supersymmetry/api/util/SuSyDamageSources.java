package io.github.symmetricdevs.supersymmetry.api.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

/**
 * Ported from the 1.12.2 {@code supersymmetry.api.util.SuSyDamageSources}.
 * <p>
 * In 1.20.1, {@link DamageSource} is created via {@link DamageSources} helpers
 * or via custom {@link DamageType} resource keys registered in datapack JSON.
 * This class provides static helpers for SuSy-specific damage sources.
 * <p>
 * TODO: Register these as actual {@link DamageType} entries if they need
 * death-message formatting or scaling. For now they use raw string names.
 */
public class SuSyDamageSources {

    // Resource keys for custom damage types (register in datapack if needed)
    private static final ResourceKey<DamageType> SUFFOCATION = key("suffocation");
    private static final ResourceKey<DamageType> TOXIC_ATMO = key("toxic_atmo");
    private static final ResourceKey<DamageType> CRUSHER = key("crusher");
    private static final ResourceKey<DamageType> PRESSURE = key("pressure");
    private static final ResourceKey<DamageType> DEPRESSURIZATION = key("depressurization");
    private static final ResourceKey<DamageType> IMPACT = key("impact");
    private static final ResourceKey<DamageType> VAPORIZATION = key("vaporization");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE,
                ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, name));
    }

    public static DamageSource getSuffocationDamage(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(SUFFOCATION));
    }

    public static DamageSource getToxicAtmoDamage(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(TOXIC_ATMO));
    }

    public static DamageSource getCrusherDamage(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(CRUSHER));
    }

    public static DamageSource pressure(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(PRESSURE));
    }

    public static DamageSource depressurization(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DEPRESSURIZATION));
    }

    public static DamageSource impact(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(IMPACT));
    }

    public static DamageSource vaporization(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(VAPORIZATION));
    }

    private SuSyDamageSources() {}
}
