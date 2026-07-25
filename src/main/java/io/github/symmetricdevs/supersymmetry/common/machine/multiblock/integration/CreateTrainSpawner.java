package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.integration;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainPacket;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;

import com.gregtechceu.gtceu.GTCEu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.PacketDistributor;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Helper that spawns a minimal Create train on an existing Create track.
 *
 * <p>Mirrors the assembly flow from {@code StationBlockEntity.assemble(UUID)}
 * without requiring a Create station block. The train is assembled from a
 * single small bogey placed temporarily in-world; the contraption is removed
 * from the world and attached to a new {@link Train} that is registered with
 * Create's railway manager.
 */
public final class CreateTrainSpawner {

    private CreateTrainSpawner() {}

    public static TrainSpawnResult trySpawnTrain(Level level, BlockPos trackPos, Direction assemblyDirection) {
        if (level.isClientSide) {
            return TrainSpawnResult.failed("client side");
        }

        BlockState trackState = level.getBlockState(trackPos);
        ITrackBlock track;
        if (trackState.getBlock() instanceof ITrackBlock t) {
            track = t;
        } else {
            return TrainSpawnResult.failed("no track at " + trackPos);
        }

        Vec3 centre = Vec3.atBottomCenterOf(trackPos)
                .add(0, track.getElevationAtCenter(level, trackPos, trackState), 0);
        Vec3 targetOffset = Vec3.atLowerCornerOf(assemblyDirection.getNormal());
        Collection<TrackNodeLocation.DiscoveredLocation> ends =
                track.getConnected(level, trackPos, trackState, true, null);
        TrackNodeLocation location = null;
        GTCEu.LOGGER.debug("[SuSy] Railroad spawn: track at {} has {} connected ends, assembly direction {}",
                trackPos, ends.size(), assemblyDirection);
        for (TrackNodeLocation.DiscoveredLocation end : ends) {
            Vec3 toEnd = end.getLocation().subtract(centre).normalize();
            if (Mth.equal(0, targetOffset.distanceToSqr(toEnd))) {
                location = end;
                break;
            }
        }
        if (location == null) {
            return TrainSpawnResult.failed("no connected node in direction " + assemblyDirection);
        }

        double bogeySize = AllBlocks.SMALL_BOGEY.get().getWheelPointSpacing();
        List<Double> pointOffsets = List.of(0.5 - bogeySize / 2, 0.5 + bogeySize / 2);

        List<TravellingPoint> points = new ArrayList<>();
        Vec3 directionVec = targetOffset;
        TrackGraph graph = null;
        TrackNode secondNode = null;

        for (int j = 0; j < 80; j++) {
            double i = j / 2d;
            if (points.size() == pointOffsets.size()) {
                break;
            }

            TrackNodeLocation currentLocation = location;
            location = new TrackNodeLocation(location.getLocation().add(directionVec.scale(.5)))
                    .in(location.dimension);

            if (graph == null) {
                graph = Create.RAILWAYS.getGraph(level, currentLocation);
            }
            if (graph == null) {
                continue;
            }
            TrackNode node = graph.locateNode(currentLocation);
            if (node == null) {
                continue;
            }

            for (int pointIndex = points.size(); pointIndex < pointOffsets.size(); pointIndex++) {
                double offset = pointOffsets.get(pointIndex);
                if (offset > i) {
                    break;
                }
                double positionOnEdge = i - offset;

                Map<TrackNode, TrackEdge> connectionsFromNode = graph.getConnectionsFrom(node);
                if (secondNode == null) {
                    for (Map.Entry<TrackNode, TrackEdge> entry : connectionsFromNode.entrySet()) {
                        TrackEdge edge = entry.getValue();
                        TrackNode otherNode = entry.getKey();
                        if (edge.isTurn()) {
                            continue;
                        }
                        Vec3 edgeDirection = edge.getDirection(true);
                        if (Mth.equal(edgeDirection.normalize().dot(directionVec), -1d)) {
                            secondNode = otherNode;
                            break;
                        }
                    }
                }
                if (secondNode == null) {
                    return TrainSpawnResult.failed("no valid starting node found");
                }

                TrackEdge edge = connectionsFromNode.get(secondNode);
                if (edge == null) {
                    return TrainSpawnResult.failed("missing graph edge");
                }

                points.add(new TravellingPoint(node, secondNode, edge, positionOnEdge, false));
            }
            secondNode = node;
        }

        if (points.size() != pointOffsets.size()) {
            return TrainSpawnResult.failed("not all travelling points created (found " + points.size() + ")");
        }

        return assembleSingleBogeyTrain(level, trackPos, trackState, track, graph, points, assemblyDirection);
    }

    private static TrainSpawnResult assembleSingleBogeyTrain(Level level, BlockPos trackPos,
                                                             BlockState trackState, ITrackBlock track,
                                                             @Nullable TrackGraph graph,
                                                             List<TravellingPoint> points,
                                                             Direction assemblyDirection) {
        if (graph == null) {
            return TrainSpawnResult.failed("no track graph");
        }

        BlockState bogeyState = track.getBogeyAnchor(level, trackPos, trackState);
        if (!(bogeyState.getBlock() instanceof AbstractBogeyBlock<?> bogeyBlock)) {
            return TrainSpawnResult.failed("track bogey anchor is not a bogey block");
        }

        // Place the bogey one block along the assembly direction, matching
        // StationTileEntity.assemble's offset for the frontmost bogey.
        BlockPos bogeyPos = trackPos.above().relative(assemblyDirection, 1);
        BlockState existing = level.getBlockState(bogeyPos);
        if (!existing.canBeReplaced()) {
            return TrainSpawnResult.failed("bogey placement blocked at " + bogeyPos);
        }
        level.setBlock(bogeyPos, bogeyState, 3);
        BlockEntity be = level.getBlockEntity(bogeyPos);
        if (!(be instanceof AbstractBogeyBlockEntity bogeyTileEntity)) {
            level.setBlock(bogeyPos, existing, 3);
            return TrainSpawnResult.failed("failed to create bogey tile entity");
        }

        // CarriageContraption.assemble() requires more than one captured block,
        // so add a temporary body block in front of the bogey.
        BlockPos bodyPos = bogeyPos.relative(assemblyDirection);
        BlockState existingBody = level.getBlockState(bodyPos);
        if (!existingBody.canBeReplaced()) {
            level.setBlock(bogeyPos, existing, 3);
            return TrainSpawnResult.failed("temporary train body placement blocked at " + bodyPos);
        }
        level.setBlock(bodyPos, Blocks.OAK_PLANKS.defaultBlockState(), 3);

        CarriageContraption contraption = new CarriageContraption(assemblyDirection);
        boolean success;
        try {
            success = contraption.assemble(level, bogeyPos);
        } catch (com.simibubi.create.content.contraptions.AssemblyException e) {
            level.setBlock(bodyPos, existingBody, 3);
            level.setBlock(bogeyPos, existing, 3);
            return TrainSpawnResult.failed("contraption assembly exception: " + e.getMessage());
        }
        if (!success) {
            level.setBlock(bodyPos, existingBody, 3);
            level.setBlock(bogeyPos, existing, 3);
            return TrainSpawnResult.failed("contraption assembly failed");
        }
        if (!contraption.hasForwardControls()) {
            // A single-bogey test train has no controls. Log but allow it to
            // spawn so recipe completion still produces a visible train.
            GTCEu.LOGGER.info("[SuSy] Spawned train has no forward controls; it will not be drivable.");
        }

        CarriageBogey bogey = new CarriageBogey(
                bogeyBlock, false, bogeyTileEntity.getBogeyData(),
                points.get(0), points.get(1));
        Carriage carriage = new Carriage(bogey, null, 0);

        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
        contraption.expandBoundsAroundAxis(net.minecraft.core.Direction.Axis.Y);

        Train train = new Train(UUID.randomUUID(), UUID.randomUUID(), graph,
                List.of(carriage), List.of(), false);
        carriage.setContraption(level, contraption);

        train.collectInitiallyOccupiedSignalBlocks();
        Create.RAILWAYS.addTrain(train);
        AllPackets.getChannel().send(PacketDistributor.ALL.noArg(), new TrainPacket(train, true));

        GTCEu.LOGGER.info("[SuSy] Spawned Create train {} on graph {} at {}", train.id, graph.id, trackPos);
        return new TrainSpawnResult(true, train.id, null);
    }

    public record TrainSpawnResult(boolean success, @Nullable UUID trainId, @Nullable String failure) {
        public static TrainSpawnResult failed(String reason) {
            return new TrainSpawnResult(false, null, reason);
        }
    }
}
