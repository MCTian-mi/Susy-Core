package io.github.symmetricdevs.supersymmetry.common.item.behavior;

import com.gregtechceu.gtceu.api.pipenet.IPipeNode;

import net.minecraft.core.Direction;

import java.util.List;

public interface ITraverseOption {

    List<Direction> findNext(Direction from, IPipeNode<?, ?> pipe);

    void operate(Direction from, IPipeNode<?, ?> self, IPipeNode<?, ?> other, boolean reverse);
}
