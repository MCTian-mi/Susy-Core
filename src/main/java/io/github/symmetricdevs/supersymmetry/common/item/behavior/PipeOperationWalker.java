package io.github.symmetricdevs.supersymmetry.common.item.behavior;

import com.gregtechceu.gtceu.api.pipenet.IPipeNode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Pipe operation walker.
 * Walks a pipe network and performs an operation on each pipe via an {@link ITraverseOption}.
 */
@ParametersAreNonnullByDefault
public class PipeOperationWalker<T extends IPipeNode<?, ?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger("Supersymmetry");

    private final Level world;
    private final List<Direction> nextPipeFacings = new ArrayList<>();
    private final List<T> nextPipes = new ArrayList<>();
    private final BlockPos.MutableBlockPos currentPos;
    private final Class<T> basePipeClass;
    private boolean reverse = false;
    private PipeOperationWalker<T> root;
    private Set<T> walked;
    private List<PipeOperationWalker<T>> walkers;
    private T currentPipe;
    private Direction from = null;
    private int walkedBlocks;
    private boolean invalid;
    private boolean running;
    private boolean failed = false;
    private ITraverseOption option;
    @Nullable
    private Direction direction;

    private PipeOperationWalker(Level world, BlockPos sourcePipe, int walkedBlocks, Class<T> basePipeClass) {
        this.world = world;
        this.walkedBlocks = walkedBlocks;
        this.currentPos = new BlockPos.MutableBlockPos(sourcePipe.getX(), sourcePipe.getY(), sourcePipe.getZ());
        this.basePipeClass = basePipeClass;
        this.root = this;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IPipeNode<?, ?>> int collectPipeNet(Level world, BlockPos sourcePipe, T pipe,
                                                                  Direction direction, ITraverseOption option,
                                                                  int maxWalks) {
        var walker = new PipeOperationWalker<>(world, sourcePipe, 0, (Class<T>) pipe.getClass());
        walker.currentPipe = pipe;
        walker.direction = direction;
        walker.option = option;
        walker.traverse(maxWalks);
        return walker.failed ? 0 : walker.walkedBlocks;
    }

    private void traverse(int maxWalks) {
        if (invalid) throw new IllegalStateException("This walker already walked!");
        this.root = this;
        this.walked = new ObjectOpenHashSet<>();
        this.running = true;

        int i = 0;
        while (running && !step() && i++ < maxWalks) {
            /* Do nothing */
        }
        this.walkedBlocks = i;
        this.running = false;
        this.walked = null;

        if (walkedBlocks >= maxWalks) {
            LOGGER.warn("The walker reached the maximum amount of walks {}", walkedBlocks);
        }
        invalid = true;
    }

    private boolean step() {
        if (walkers == null) {
            if (!checkCurrent()) {
                this.root.failed = true;
                return true;
            }

            if (nextPipeFacings.isEmpty()) return true;
            if (nextPipeFacings.size() == 1) {

                var next = nextPipes.get(0);
                var into = nextPipeFacings.get(0);

                this.root.option.operate(into, currentPipe, next, reverse);

                this.currentPos.set(next.getPipePos());
                this.currentPipe = next;
                this.from = into.getOpposite();
                this.walkedBlocks++;

                return !root.running;
            }

            walkers = new ArrayList<>();
            for (int i = 0; i < nextPipeFacings.size(); i++) {
                var into = nextPipeFacings.get(i);
                var walker = createSubWalker(world, into, currentPos.offset(into), walkedBlocks + 1);
                var nextPipe = nextPipes.get(i);

                root.option.operate(into, currentPipe, nextPipe, walker.reverse);

                walker.root = this.root;
                walker.currentPipe = nextPipe;
                walker.from = into.getOpposite();
                this.walkers.add(walker);
            }
        }

        walkers.removeIf(PipeOperationWalker::step);

        return !root.running || walkers.isEmpty();
    }

    @SuppressWarnings("unchecked")
    private boolean checkCurrent() {
        this.nextPipeFacings.clear();
        this.nextPipes.clear();
        if (currentPipe == null) {
            BlockEntity thisPipe = world.getBlockEntity(currentPos);
            if (!(thisPipe instanceof IPipeNode)) {
                LOGGER.error("PipeWalker expected a pipe, but found {} at {}", thisPipe, currentPos);
                return false;
            }
            if (!basePipeClass.isAssignableFrom(thisPipe.getClass())) {
                return false;
            }
            currentPipe = (T) thisPipe;
        }
        T pipeTile = currentPipe;

        this.root.walked.add(pipeTile);

        List<Direction> facings = root.option.findNext(from != null ? from : direction, pipeTile);

        if (walkedBlocks == 0) {
            facings.add(direction);
        }

        for (Direction side : facings) {
            BlockEntity tile = pipeTile.getNeighbor(side);
            if (tile != null && basePipeClass.isAssignableFrom(tile.getClass())) {
                T otherPipe = (T) tile;
                if (!isWalked(otherPipe)) {
                    nextPipeFacings.add(side);
                    nextPipes.add(otherPipe);
                }
            }
        }
        return true;
    }

    private boolean isWalked(T pipe) {
        return root.walked.contains(pipe);
    }

    private PipeOperationWalker<T> createSubWalker(Level world, Direction facingToNextPos, BlockPos nextPos,
                                                   int walkedBlocks) {
        boolean reverse = this.direction != null ? facingToNextPos != direction : this.reverse;
        var subWalker = new PipeOperationWalker<>(world, nextPos, walkedBlocks, this.basePipeClass);
        subWalker.reverse = reverse;
        return subWalker;
    }
}
