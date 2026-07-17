package io.github.symmetricdevs.supersymmetry.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

/**
 * Stub — the 1.12.2 custom explosion entity used world.isRemote,
 * onUpdate(), entityInit(), posX/Y/Z direct fields, and client-side
 * particle rendering APIs removed in 1.20.1.
 * <p>
 * Deferred until the particle/explosion system is ported.
 * The basic entity registration is kept; actual explosion logic is removed.
 */
public class EntityExplosion extends Entity {

    public EntityExplosion(EntityType<? extends EntityExplosion> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {}
}
