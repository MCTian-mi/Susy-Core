package io.github.symmetricdevs.supersymmetry.common.entities.teleporters;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.ITeleporter;

public class DropPodTeleporter implements ITeleporter {

    @Override
    public void placeEntity(World world, Entity entity, float yaw) {
        world.spawnEntity(entity);
    }
}
