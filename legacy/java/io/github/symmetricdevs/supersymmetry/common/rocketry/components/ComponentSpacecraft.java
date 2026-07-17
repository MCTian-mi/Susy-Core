package io.github.symmetricdevs.supersymmetry.common.rocketry.components;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

import com.gregtechceu.gtceu.api.block.VariantBlock;
import io.github.symmetricdevs.supersymmetry.api.rocketry.components.AbstractComponent;
import io.github.symmetricdevs.supersymmetry.api.rocketry.components.MaterialCost;
import io.github.symmetricdevs.supersymmetry.api.util.StructAnalysis;
import io.github.symmetricdevs.supersymmetry.api.util.StructAnalysis.BuildStat;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;
import io.github.symmetricdevs.supersymmetry.common.tileentities.TileEntityCoverable;

public class ComponentSpacecraft extends AbstractComponent<ComponentSpacecraft> {

    public Map<String, Integer> parts = new HashMap<>();
    public Map<String, Integer> instruments = new HashMap<>();
    public boolean hasAir;
    public double volume;

    public ComponentSpacecraft() {
        super(
                "spacecraft_hull",
                "spacecraft_hull",
                tuple -> tuple.getSecond().stream()
                        .anyMatch(
                                pos -> tuple
                                        .getFirst().world
                                                .getBlockState(pos)
                                                .getBlock()
                                                .equals(SuSyBlocks.SPACECRAFT_HULL)));
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setDouble("radius", this.radius);
        tag.setDouble("volume", this.volume);
        tag.setBoolean("hasAir", this.hasAir);
        CompoundTag instrumentsTag = new CompoundTag();
        CompoundTag partsTag = new CompoundTag();
        for (Entry<String, Integer> part : this.parts.entrySet()) {
            partsTag.setInteger(part.getKey(), part.getValue());
        }
        for (Entry<String, Integer> instrument : this.instruments.entrySet()) {
            instrumentsTag.setInteger(instrument.getKey(), instrument.getValue());
        }
        tag.setTag(INSTRUMENTS_KEY, instrumentsTag);
        tag.setTag(PARTS_KEY, partsTag);
    }

    @Override
    public Optional<ComponentSpacecraft> readFromNBT(CompoundTag compound) {
        ComponentSpacecraft spacecraft = new ComponentSpacecraft();

        if (!compound.getString("name").equals(spacecraft.name)) return Optional.empty();
        if (!compound.getString("type").equals(spacecraft.type)) return Optional.empty();
        if (!compound.hasKey("radius", NBT.TAG_DOUBLE)) return Optional.empty();
        if (!compound.hasKey("mass", NBT.TAG_DOUBLE)) return Optional.empty();
        if (!compound.hasKey("hasAir")) return Optional.empty();
        if (!compound.hasKey("volume", NBT.TAG_DOUBLE)) return Optional.empty();
        if (!compound.hasKey(AbstractComponent.PARTS_KEY, NBT.TAG_COMPOUND)) return Optional.empty();
        if (!compound.hasKey(AbstractComponent.INSTRUMENTS_KEY, NBT.TAG_COMPOUND)) return Optional.empty();
        if (!compound.hasKey("materials", NBT.TAG_LIST)) return Optional.empty();
        compound
                .getTagList("materials", NBT.TAG_COMPOUND)
                .forEach(x -> spacecraft.materials.add(MaterialCost.fromNBT((CompoundTag) x)));

        spacecraft.radius = compound.getDouble("radius");
        spacecraft.mass = compound.getDouble("mass");
        spacecraft.volume = compound.getDouble("volume");
        spacecraft.hasAir = compound.getBoolean("hasAir");

        CompoundTag instrumentsList = compound.getCompoundTag(AbstractComponent.INSTRUMENTS_KEY);
        for (String key : instrumentsList.getKeySet()) {
            spacecraft.instruments.put(key, compound.getInteger(key));
        }

        CompoundTag partsList = compound.getCompoundTag(AbstractComponent.PARTS_KEY);
        for (String key : partsList.getKeySet()) {
            spacecraft.parts.put(key, partsList.getInteger(key));
        }

        return Optional.of(spacecraft);
    }

    @Override
    public Optional<CompoundTag> analyzePattern(StructAnalysis analysis, AABB aabb) {
        Set<BlockPos> blocksConnected = analysis.getBlockConn(aabb,
                analysis.getBlocks(analysis.world, aabb, true).get(0));
        StructAnalysis.HullData hullCheck = analysis.checkHull(aabb, blocksConnected, false);
        Set<BlockPos> exterior = hullCheck.exterior();
        Set<BlockPos> interior = hullCheck.interior();
        return spacecraftPattern(
                blocksConnected,
                exterior,
                interior,
                analysis);
    }

    public Optional<CompoundTag> spacecraftPattern(
                                                      Set<BlockPos> blocksConnected,
                                                      Set<BlockPos> exterior,
                                                      Set<BlockPos> interior,
                                                      StructAnalysis analysis) {
        Predicate<BlockPos> lifeSupportCheck = bp -> analysis.world.getBlockState(bp).getBlock()
                .equals(SuSyBlocks.LIFE_SUPPORT);
        Predicate<BlockPos> guidanceComputerCheck = bp -> analysis.world.getBlockState(bp).getBlock()
                .equals(SuSyBlocks.GUIDANCE_SYSTEM);

        Set<BlockPos> lifeSupports = blocksConnected.stream().filter(lifeSupportCheck).collect(Collectors.toSet());
        List<BlockPos> guidanceComputers = blocksConnected.stream().filter(guidanceComputerCheck)
                .collect(Collectors.toList());
        CompoundTag tag = new CompoundTag();

        lifeSupports.forEach(
                bp -> includePart(analysis, bp, tag, PARTS_KEY, this.parts));

        for (BlockPos bp : exterior) {
            if (analysis.world.getBlockState(bp).getBlock().equals(SuSyBlocks.SPACECRAFT_HULL)) {
                TileEntityCoverable te = (TileEntityCoverable) analysis.world.getTileEntity(bp);
                for (Direction side : Direction.VALUES) {
                    // If it is both covered but facing another hull block
                    // or not covered but facing air, then fail.
                    if (!te.isCovered(side) &&
                            !exterior.contains(bp.add(side.getDirectionVec())) &&
                            !interior.contains(bp.add(side.getDirectionVec()))) {
                        analysis.status = BuildStat.HULL_WEAK;
                        return analysis.errorPos(bp);
                    }
                }
            } else if (analysis.world.getBlockState(bp).getBlock().equals(SuSyBlocks.SPACE_INSTRUMENT)) {
                includePart(analysis, bp, tag, INSTRUMENTS_KEY, this.instruments);
            } else {
                analysis.status = BuildStat.HULL_WEAK;
            }
        }

        if (guidanceComputers.isEmpty()) {
            analysis.status = BuildStat.NO_GUIDANCE;
            return Optional.empty();
        } else if (guidanceComputers.size() > 1) {
            analysis.status = BuildStat.TOO_MUCH_GUIDANCE;
            return Optional.empty();
        }
        BlockState guidanceBlock = analysis.world.getBlockState(guidanceComputers.get(0));
        tag.setString("guidance", SuSyBlocks.GUIDANCE_SYSTEM.getState(guidanceBlock).toString());

        if (lifeSupports.isEmpty()) {
            // no airspace necessary
            if (!interior.isEmpty()) {
                analysis.status = BuildStat.SPACECRAFT_HOLLOW;
                return Optional.empty();
            }
            tag.setBoolean("hasAir", false);
            this.hasAir = false; // goog..?
        } else {
            if (interior.size() < 2) {
                analysis.status = BuildStat.HULL_FULL;
                return Optional.empty();
            }
            int volume = interior.size();
            tag.setInteger("volume", volume);
            Set<BlockPos> container = analysis.getPerimeter(interior, StructAnalysis.orthVecs);
            for (BlockPos bp : container) {
                Block block = analysis.world.getBlockState(bp).getBlock();
                if (block.equals(SuSyBlocks.LIFE_SUPPORT)) {
                    continue;
                }
                if (analysis.world.getTileEntity(bp) == null ||
                        !(analysis.world.getTileEntity(bp) instanceof TileEntityCoverable)) {
                    continue;
                }
                TileEntityCoverable te = (TileEntityCoverable) analysis.world.getTileEntity(bp);
                if (block.equals(SuSyBlocks.ROOM_PADDING)) {
                    for (Direction side : Direction.VALUES) {
                        if (te.isCovered(side) == interior.contains(bp.add(side.getDirectionVec()))) {
                            analysis.status = BuildStat.WEIRD_PADDING;
                            return analysis.errorPos(bp);
                        }
                    }
                }
            }
            tag.setBoolean("hasAir", true);
            this.hasAir = true;
        }
        double radius = analysis.getRadius(blocksConnected);

        // The scan is successful by this point
        analysis.status = BuildStat.SUCCESS;
        tag.setString("type", type);
        tag.setString("name", name);
        tag.setDouble("radius", radius);
        this.radius = radius;
        double mass = blocksConnected.stream()
                .mapToDouble(block -> getMassOfBlock(analysis.world.getBlockState(block)))
                .sum();
        tag.setDouble("mass", mass);
        this.mass = mass;
        writeBlocksToNBT(blocksConnected, analysis.world);
        return Optional.of(tag);
    }

    private void includePart(StructAnalysis analysis, BlockPos bp, CompoundTag tag, String key,
                             Map<String, Integer> instruments) {
        Block block = analysis.world.getBlockState(bp).getBlock();
        CompoundTag subTag = tag.getCompoundTag(key);
        String part = ((VariantBlock<?>) block).getState(analysis.world.getBlockState(bp)).toString();
        int count = subTag.getInteger(part); // default behavior is 0
        subTag.setInteger(part, count + 1);
        instruments.put(part, count + 1);
        tag.setTag(key, subTag);
    }
}
