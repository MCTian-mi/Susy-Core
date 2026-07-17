package io.github.symmetricdevs.supersymmetry.api.stockinteraction;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

// MUI2 removed: import com.cleanroommc.modularui.utils.Vector3f;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.items.ItemRollingStock;
import cam72cam.mod.entity.ModdedEntity;
import cam72cam.mod.item.ItemStack;

public class StockHelperFunctions {

    // mostly unused since stockFilter stored as int
    public static List<EntityRollingStock> getStocksInArea(Level world, AABB box) {
        return getStocksInArea(world, box, stock -> true);
    }

    public static List<EntityRollingStock> getStocksInArea(Level world, AABB box,
                                                           Predicate<EntityRollingStock> filter) {
        // get entities in box and stockFilter for only wanted ones
        return world.getEntitiesOfClass(ModdedEntity.class, box)
                .stream()
                .map(ModdedEntity::getSelf)
                .filter(EntityRollingStock.class::isInstance)
                .map(EntityRollingStock.class::cast)
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Nullable
    public static EntityRollingStock getStockFrom(Level world, AABB box, Predicate<EntityRollingStock> filter,
                                                  BlockPos pos) {
        // get entities in box and stockFilter for only wanted ones
        return world.getEntitiesOfClass(ModdedEntity.class, box)
                .stream()
                .map(ModdedEntity::getSelf)
                .filter(EntityRollingStock.class::isInstance)
                .map(EntityRollingStock.class::cast)
                .filter(filter)
                .min(Comparator.comparing(entity -> pos.distSqr(entity.getBlockPosition().internal())))
                .orElse(null);
    }

    public static AABB getBox(Vec3i pos, Direction facing, double width, double depth) {
        return getBox(fromVec3i(pos), facing, width, depth);
    }

    public static AABB getBox(Vector3f pos, Direction facing, double width, double depth) {
        // get base position and facing directions, as well as both non facing directions
        Vector3f one = new Vector3f(1, 1, 1);
        Vector3f facingDir = fromVec3i(facing.getNormal());
        Vector3f nonFacingDirs = Vector3f.sub(one, facingDir, null);

        double halfwidth = 0.5 * width;

        // get the position of the front of the block
        Vector3f facingDirHalved = copyVec(facingDir);
        facingDirHalved.scale(0.5f);
        Vector3f blockFront = Vector3f.add(pos, facingDirHalved, null);

        // get the vectors to add for local left down, and local up right
        Vector3f localLeftDown = copyVec(nonFacingDirs);
        localLeftDown.scale((float) halfwidth * -1.0f);
        Vector3f localRightUp = copyVec(nonFacingDirs);
        localRightUp.scale((float) halfwidth);

        // calculate the vector to add for the far corner
        Vector3f farVec = copyVec(facingDir);
        farVec.scale((float) depth);

        // calculate both corners and use to make bounding box
        Vector3f cornerA = Vector3f.add(blockFront, localLeftDown, null);
        Vector3f cornerB = Vector3f.add(blockFront, localRightUp, null);
        cornerB = Vector3f.add(cornerB, farVec, null);
        AABB box = new AABB(cornerA.getX(), cornerA.getY(), cornerA.getZ(), cornerB.getX(),
                cornerB.getY(), cornerB.getZ());
        return box;
    }

    public static Vector3f copyVec(Vector3f vec) {
        return new Vector3f(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vector3f fromVec3i(Vec3i pos) {
        return new Vector3f(pos.getX(), pos.getY(), pos.getZ());
    }

    @Nullable
    public static String getDefinitionNameFromStack(net.minecraft.world.item.ItemStack stack) {
        var data = new ItemRollingStock.Data(new ItemStack(stack));
        if (data.def != null) {
            return data.def.name();
        }
        return null;
    }
}
