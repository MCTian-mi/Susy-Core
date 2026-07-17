package io.github.symmetricdevs.supersymmetry.common.rocketry.components;

import static io.github.symmetricdevs.supersymmetry.api.blocks.VariantDirectionalRotatableBlock.FACING;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.world.level.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraftforge.common.util.Constants;

import io.github.symmetricdevs.supersymmetry.api.rocketry.components.AbstractComponent;
import io.github.symmetricdevs.supersymmetry.api.rocketry.components.MaterialCost;
import io.github.symmetricdevs.supersymmetry.api.util.StructAnalysis;
import io.github.symmetricdevs.supersymmetry.api.util.StructAnalysis.BuildStat;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;
import io.github.symmetricdevs.supersymmetry.common.tileentities.TileEntityCoverable;

/**
 * componentLiquidFuelTank
 */
public class ComponentLiquidFuelTank extends AbstractComponent<ComponentLiquidFuelTank> {

    public int volume;

    public ComponentLiquidFuelTank() {
        super(
                "fluid_tank",
                "tank",
                candidate -> candidate.getSecond().stream()
                        .anyMatch(
                                pos -> candidate
                                        .getFirst().world
                                                .getBlockState(pos)
                                                .getBlock()
                                                .equals(SuSyBlocks.TANK_SHELL)));
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setDouble("radius", this.radius);
        tag.setDouble("mass", this.mass);
        tag.setInteger("volume", this.volume);
    }

    // but the Lord laughs at the wicked,
    // for he knows their day is coming.
    @Override
    public Optional<ComponentLiquidFuelTank> readFromNBT(CompoundTag compound) {
        if (compound.getString("type").isEmpty() || compound.getString("name").isEmpty()) {
            return Optional.empty();
        }
        if (!compound.hasKey("mass")) {
            return Optional.empty();
        }
        if (!compound.hasKey("radius")) {
            return Optional.empty();
        }
        if (!compound.hasKey("volume")) {
            return Optional.empty();
        }
        if (!compound.hasKey("materials")) {
            return Optional.empty();
        }

        ComponentLiquidFuelTank tank = new ComponentLiquidFuelTank();
        compound
                .getTagList("materials", Constants.NBT.TAG_COMPOUND)
                .forEach(tag -> tank.materials.add(MaterialCost.fromNBT((CompoundTag) tag)));

        tank.volume = compound.getInteger("volume");
        tank.radius = compound.getDouble("radius");
        tank.mass = compound.getDouble("mass");
        return Optional.of(tank);
    }

    @Override
    public Optional<CompoundTag> analyzePattern(StructAnalysis analysis, AABB aabb) {
        List<BlockPos> detectedBlocks = analysis.getBlocks(analysis.world, aabb, true);
        if (detectedBlocks.isEmpty()) {
            analysis.status = BuildStat.ERROR;
            return Optional.empty();
        }

        Set<BlockPos> blocks = analysis.getBlockConn(aabb, detectedBlocks.get(0));
        StructAnalysis.HullData hullData = analysis.checkHull(aabb, blocks, false);

        Set<BlockPos> hullBlocks = hullData.exterior();
        Set<BlockPos> interiorAir = hullData.interior();

        if (interiorAir.size() < 2) {
            analysis.status = BuildStat.HULL_FULL;
            return Optional.empty();
        }

        Predicate<BlockPos> shellPredicate = block -> {
            Block candidate = analysis.world.getBlockState(block).getBlock();
            return candidate.equals(SuSyBlocks.TANK_SHELL) || candidate.equals(SuSyBlocks.TANK_SHELL1);
        };
        for (BlockPos block : hullBlocks) {
            if (!shellPredicate.test(block)) {
                analysis.status = BuildStat.HULL_WEAK;
                return analysis.errorPos(block);
            }
            TileEntityCoverable blockTiles = (TileEntityCoverable) analysis.world.getTileEntity(block);
            if (blockTiles == null) {
                analysis.status = BuildStat.ERROR;
                return analysis.errorPos(block);
            }
            Direction facingFromBlock = analysis.world.getBlockState(block).getValue(FACING);
            for (Direction facing : Direction.values()) {
                BlockPos neighbor = block.add(facing.getDirectionVec());
                if (interiorAir.contains(neighbor)) {
                    Vec3i difference = analysis.diff(neighbor, block);
                    if (!difference.equals(facingFromBlock.getOpposite().getDirectionVec())) {
                        // honeycombs
                        analysis.status = BuildStat.HULL_WEAK;
                        return analysis.errorPos(block);
                    }
                } else if (!interiorAir.contains(neighbor) &&
                        (analysis.world.isAirBlock(neighbor) ||
                                !StructAnalysis.blockCont(aabb, neighbor))) { // this means it should be exterior air
                                    if (!blockTiles.isCovered(facing)) {
                                        analysis.status = BuildStat.MISSING_TILE;
                                        return analysis.errorPos(block);
                                    }
                                }

            }
        }

        double radius = analysis.getRadius(blocks);
        int calculatedHeight = (int) (analysis.getBB(blocks).maxZ - analysis.getBB(blocks).minZ);
        if (calculatedHeight > radius * 2) {
            analysis.status = BuildStat.TOO_SHORT;
        }
        CompoundTag tag = new CompoundTag();

        // The scan is successful by this point
        analysis.status = BuildStat.SUCCESS;
        this.volume = interiorAir.size();
        tag.setInteger("volume", this.volume);

        collectInfo(analysis, blocks, tag);
        writeBlocksToNBT(blocks, analysis.world);
        return Optional.of(tag);
    }
}
