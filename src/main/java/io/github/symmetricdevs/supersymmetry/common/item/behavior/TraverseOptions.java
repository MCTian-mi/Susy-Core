package io.github.symmetricdevs.supersymmetry.common.item.behavior;

import static io.github.symmetricdevs.supersymmetry.common.item.behavior.TraverseOptions.Lambdas.*;

import com.gregtechceu.gtceu.api.pipenet.IPipeNode;

import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;

import java.util.ArrayList;
import java.util.List;

public enum TraverseOptions implements ITraverseOption {

    CONNECTING(FIND_TO_CONNECT, CONNECTOR),
    DISCONNECTING(FIND_CONNECTED, DISCONNECTOR),
    BLOCKING(FIND_CONNECTED, BLOCKER),
    UNBLOCKING(FIND_CONNECTED, UNBLOCKER),
    ;

    public static final Int2ObjectArrayMap<ITraverseOption> COLORING = new Int2ObjectArrayMap<>(
            1 + DyeColor.values().length);

    static {
        for (int i = -1; i <= DyeColor.values().length; i++) {
            final int index = i;

            COLORING.put(i, new ITraverseOption() {
                @Override
                public List<Direction> findNext(Direction from, IPipeNode<?, ?> pipe) {
                    return FIND_ALL_CONNECTED.findNext(from, pipe);
                }

                @Override
                public void operate(Direction from, IPipeNode<?, ?> self, IPipeNode<?, ?> other, boolean reverse) {
                    int colorValue = index == -1 ? self.getDefaultPaintingColor() :
                            DyeColor.byId(index).getTextColor();
                    if (self.getPaintingColor() != colorValue) {
                        self.setPaintingColor(colorValue);
                    }
                    other.setPaintingColor(colorValue);
                }
            });
        }
    }

    private final PathFinder pathFinder;
    private final PipeOperator pipeOperator;

    TraverseOptions(PathFinder pathFinder, PipeOperator pipeOperator) {
        this.pathFinder = pathFinder;
        this.pipeOperator = pipeOperator;
    }

    @Override
    public List<Direction> findNext(Direction from, IPipeNode<?, ?> pipe) {
        return pathFinder.findNext(from, pipe);
    }

    @Override
    public void operate(Direction from, IPipeNode<?, ?> self, IPipeNode<?, ?> other, boolean reverse) {
        pipeOperator.operate(from, self, other, reverse);
    }

    @FunctionalInterface
    private interface PathFinder {
        List<Direction> findNext(Direction from, IPipeNode<?, ?> pipe);
    }

    @FunctionalInterface
    private interface PipeOperator {
        void operate(Direction facingToOther, IPipeNode<?, ?> self, IPipeNode<?, ?> other, boolean reverse);
    }

    static class Lambdas {

        static final PathFinder FIND_TO_CONNECT = (from, pipe) -> {
            List<Direction> ret = new ArrayList<>(1);

            for (Direction facing : Direction.values()) {
                if (facing == from) continue;
                BlockEntity other = pipe.getNeighbor(facing);
                if (other instanceof IPipeNode<?, ?> otherPipe &&
                        pipe.getClass().isAssignableFrom(other.getClass()) &&
                        otherPipe.getNumConnections() == 0) {
                    if (ret.isEmpty()) {
                        ret.add(facing);
                    } else {
                        ret.clear();
                        return ret;
                    }
                }
            }
            return ret;
        };

        static final PathFinder FIND_CONNECTED = (from, pipe) -> {
            List<Direction> ret = new ArrayList<>(1);

            for (Direction facing : Direction.values()) {
                if (facing == from) continue;
                if (pipe.isConnected(facing)) {
                    if (ret.isEmpty()) {
                        ret.add(facing);
                    } else {
                        ret.clear();
                        return ret;
                    }
                }
            }
            return ret;
        };

        static final PathFinder FIND_ALL_CONNECTED = (from, pipe) -> {
            List<Direction> ret = new ArrayList<>(5);
            for (Direction facing : Direction.values()) {
                if (facing == from) continue;
                if (pipe.isConnected(facing)) {
                    ret.add(facing);
                }
            }
            return ret;
        };

        static final PipeOperator CONNECTOR = (facingToOther, self, other, reverse) ->
                self.setConnection(facingToOther, true, false);

        static final PipeOperator DISCONNECTOR = (facingToOther, self, other, reverse) ->
                self.setConnection(facingToOther, false, false);

        static final PipeOperator BLOCKER = (facingToOther, self, other, reverse) -> {
            if (reverse) {
                other.setBlocked(facingToOther.getOpposite(), true);
            } else {
                self.setBlocked(facingToOther, true);
            }
        };

        static final PipeOperator UNBLOCKER = (facingToOther, self, other, reverse) -> {
            if (reverse) {
                other.setBlocked(facingToOther.getOpposite(), false);
            } else {
                self.setBlocked(facingToOther, false);
            }
        };
    }
}
