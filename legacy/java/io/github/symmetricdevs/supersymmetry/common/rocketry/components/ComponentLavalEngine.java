package io.github.symmetricdevs.supersymmetry.common.rocketry.components;

import static io.github.symmetricdevs.supersymmetry.api.blocks.VariantDirectionalRotatableBlock.FACING;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Constants;

import com.gregtechceu.gtceu.api.block.VariantBlock;
import io.github.symmetricdevs.supersymmetry.api.SusyLog;
import io.github.symmetricdevs.supersymmetry.api.rocketry.components.AbstractComponent;
import io.github.symmetricdevs.supersymmetry.api.rocketry.components.MaterialCost;
import io.github.symmetricdevs.supersymmetry.api.rocketry.components.RocketEngine;
import io.github.symmetricdevs.supersymmetry.api.util.StructAnalysis;
import io.github.symmetricdevs.supersymmetry.api.util.StructAnalysis.BuildStat;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;
import io.github.symmetricdevs.supersymmetry.common.blocks.rocketry.BlockCombustionChamber;

public class ComponentLavalEngine extends AbstractComponent<ComponentLavalEngine> implements RocketEngine {

    public double areaRatio;
    public double fuelThroughput;

    public ComponentLavalEngine() {
        super(
                "laval_engine",
                "engine",
                candidate -> candidate.getSecond().stream()
                        .anyMatch(
                                pos -> {
                                    boolean a = candidate
                                            .getFirst().world
                                                    .getBlockState(pos)
                                                    .getBlock()
                                                    .equals(SuSyBlocks.COMBUSTION_CHAMBER);
                                    boolean b = candidate
                                            .getFirst().world
                                                    .getBlockState(pos)
                                                    .equals(SuSyBlocks.COMBUSTION_CHAMBER.getState(
                                                            BlockCombustionChamber.CombustionType.MONOPROPELLANT));
                                    return a && !b;
                                }));
        this.setComponentSlotValidator(
                x -> x.equals(this.getName()) || x.equals(this.getType()) ||
                        (x.equals(this.getType() + "_small") && this.radius < 2) ||
                        (x.equals(this.getName() + "_small") && this.radius < 2));
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setDouble("radius", this.radius);
        tag.setDouble("area_ratio", this.areaRatio);
        tag.setDouble("throughput", this.fuelThroughput);
    }

    @Override
    public Optional<ComponentLavalEngine> readFromNBT(CompoundTag compound) {
        if (compound.getString("type").isEmpty() || compound.getString("name").isEmpty()) {
            return Optional.empty();
        }
        ComponentLavalEngine engine = new ComponentLavalEngine();
        if (!compound.hasKey("mass", Constants.NBT.TAG_DOUBLE)) return Optional.empty();
        if (!compound.hasKey("radius", Constants.NBT.TAG_DOUBLE)) return Optional.empty();
        if (!compound.hasKey("area_ratio", Constants.NBT.TAG_DOUBLE)) return Optional.empty();
        if (!compound.hasKey("materials", Constants.NBT.TAG_LIST)) return Optional.empty();
        if (!compound.hasKey("throughput", Constants.NBT.TAG_DOUBLE)) return Optional.empty();
        compound
                .getTagList("materials", Constants.NBT.TAG_COMPOUND)
                .forEach(x -> engine.materials.add(MaterialCost.fromNBT((CompoundTag) x)));

        engine.areaRatio = compound.getDouble("area_ratio");
        engine.radius = compound.getDouble("radius");
        engine.mass = compound.getDouble("mass");
        engine.fuelThroughput = compound.getDouble("throughput");

        if (engine.materials.isEmpty()) {
            SusyLog.logger.warn("No materials were found in {}!", compound);
        }
        return Optional.of(engine);
    }

    @Override
    public Optional<CompoundTag> analyzePattern(StructAnalysis analysis, AABB aabb) {
        Set<BlockPos> blocks = analysis.getBlockConn(aabb, analysis.getBlocks(analysis.world, aabb, true).get(0));
        Set<BlockPos> nozzle = analysis.getOfBlockType(blocks, SuSyBlocks.ROCKET_NOZZLE).collect(Collectors.toSet());
        if (nozzle.isEmpty()) {
            analysis.status = BuildStat.NO_NOZZLE;
            return Optional.empty();
        }
        ArrayList<Integer> areas = new ArrayList<>();
        AABB nozzleBB = analysis.getBB(nozzle);
        for (int i = (int) nozzleBB.maxY - 1; i >= (int) nozzleBB.minY; i--) {
            Set<BlockPos> airLayer = analysis.getLayerAir(nozzleBB, i);
            if (airLayer == null) { // there should be an error here
                analysis.status = BuildStat.NOZZLE_MALFORMED;
                return Optional.empty();
            }
            Set<BlockPos> airPerimeter = analysis.getPerimeter(airLayer, StructAnalysis.layerVecs);
            if ((double) airPerimeter.size() < 3 * Math.sqrt((double) airLayer.size())) { // Establishes a roughly
                // circular pattern
                analysis.status = BuildStat.NOZZLE_MALFORMED;
                return Optional.empty();
            }
            areas.add(airLayer.size() + airPerimeter.size() / 2);
        }

        // For all rocket nozzles, the air layer list should be increasing. 3 blocks should be a minimum
        // length under that assumption.
        if (areas.size() < 3 || areas.get(0) > 5) {
            analysis.status = BuildStat.NOZZLE_MALFORMED;
            return Optional.empty();
        }

        int initial = areas.get(0);
        int fin = initial;

        for (int a : areas) {
            if (fin <= a) {
                fin = a;
            } else {
                analysis.status = BuildStat.NOT_LAVAL;
                return Optional.empty();
            }
        }
        float computedAreaRatio = ((float) fin) / initial;
        if (computedAreaRatio < 1.5) {
            analysis.status = BuildStat.NOT_LAVAL;
            return Optional.empty();
        }

        // One combustion chamber is, I think, reasonable
        List<BlockPos> cChambers = analysis.getOfBlockType(blocks, SuSyBlocks.COMBUSTION_CHAMBER)
                .collect(Collectors.toList());
        if (cChambers.size() != 1) {
            analysis.status = BuildStat.WRONG_NUM_C_CHAMBERS;
            return Optional.empty();
        }
        // Below the chamber: Open space
        BlockPos cChamber = cChambers.get(0);
        Set<BlockPos> pumps = analysis
                .getOfBlockType(
                        analysis.getBlockNeighbors(cChamber, StructAnalysis.orthVecs), SuSyBlocks.TURBOPUMP)
                .collect(Collectors.toSet());
        if (nozzleBB.contains(new Vec3(cChamber))) {
            analysis.status = BuildStat.C_CHAMBER_INSIDE;
            return Optional.empty();
        }
        if (!analysis.world.isAirBlock(cChamber.add(0, -1, 0))) {
            analysis.status = BuildStat.NOZZLE_MALFORMED;
            return analysis.errorPos(cChamber.add(0, -1, 0));
        }
        // Analyze turbopumps
        BlockState chamberState = analysis.world.getBlockState(cChamber);
        int pumpNum = ((BlockCombustionChamber.CombustionType) (((VariantBlock<?>) chamberState.getBlock())
                .getState(chamberState)))
                        .getMinPumps();
        if (pumps.size() < pumpNum) {
            analysis.status = BuildStat.WRONG_NUM_PUMPS;
            return Optional.empty();
        }
        for (BlockPos pumpPos : pumps) {
            Direction dir = analysis.world.getBlockState(pumpPos).getValue(FACING);
            if (!dir.equals(Direction.DOWN) && !pumpPos.add(dir.getOpposite().getDirectionVec()).equals(cChamber)) {
                analysis.status = BuildStat.WEIRD_PUMP;
                return analysis.errorPos(pumpPos);
            }
        }
        // Creates engine
        Set<BlockPos> engineBlocks = new HashSet<>(nozzle);
        engineBlocks.addAll(pumps);
        engineBlocks.add(cChamber);
        engineBlocks.addAll(
                analysis.getOfBlockType(blocks, SuSyBlocks.INTERSTAGE).collect(Collectors.toSet()));
        if (engineBlocks.size() < blocks.size()) {
            analysis.status = BuildStat.EXTRANEOUS_BLOCKS;
            return Optional.empty();
        }
        analysis.status = BuildStat.SUCCESS;
        // currently a double
        CompoundTag tag = new CompoundTag();
        tag.setDouble("area_ratio", computedAreaRatio);
        this.areaRatio = computedAreaRatio;
        // Not the default; more of an inner radius
        this.radius = analysis.getRadius(
                blocks.stream().filter(bp -> bp.getY() == nozzleBB.maxY).collect(Collectors.toSet()));
        tag.setDouble("radius", radius);

        collectInfo(analysis, blocks, tag);

        double throughput = 0;

        for (BlockPos pumpPos : pumps) {
            BlockState pump = analysis.world.getBlockState(pumpPos);
            throughput += (SuSyBlocks.TURBOPUMP.getState(pump)).getThroughput();
        }

        this.fuelThroughput = throughput;
        tag.setDouble("throughput", fuelThroughput);

        writeBlocksToNBT(blocks, analysis.world);
        return Optional.of(tag);
    }

    @Override
    public double getFuelThroughput() {
        return fuelThroughput;
    }
}
