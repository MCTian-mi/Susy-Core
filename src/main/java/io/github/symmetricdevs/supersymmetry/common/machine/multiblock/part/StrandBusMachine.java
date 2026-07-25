package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import io.github.symmetricdevs.supersymmetry.api.capability.IStrandProvider;
import io.github.symmetricdevs.supersymmetry.api.capability.Strand;
import io.github.symmetricdevs.supersymmetry.api.machine.multiblock.SuSyMultiblockAbilities;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityStrandBus}.
 *
 * <p>A multiblock part that holds a single {@link Strand}. Import buses receive strands
 * from upstream machines; export buses pass strands downstream. When not connected to a
 * controller the bus still attempts to exchange strands with adjacent
 * {@link IStrandProvider} machines every 5 ticks.</p>
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StrandBusMachine extends MultiblockPartMachine implements IStrandProvider {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StrandBusMachine.class, MultiblockPartMachine.MANAGED_FIELD_HOLDER);

    private final boolean isExport;

    @Persisted
    @DescSynced
    @Nullable
    private CompoundTag strandTag;

    public StrandBusMachine(IMachineBlockEntity holder, boolean isExport) {
        super(holder);
        this.isExport = isExport;
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            subscribeServerTick(this::tickTransfer);
        }
    }

    private void tickTransfer() {
        if (getOffsetTimer() % 5 != 0) {
            return;
        }
        if (isExport) {
            pushToNeighbors();
        } else {
            pullFromNeighbors();
        }
    }

    private void pushToNeighbors() {
        Strand strand = getStrand();
        if (strand == null) return;
        IStrandProvider target = getNeighborProvider(getFrontFacing());
        if (target != null && target.getStrand() == null && target.insertStrand(strand) == null) {
            take();
        }
    }

    private void pullFromNeighbors() {
        if (getStrand() != null) return;
        IStrandProvider source = getNeighborProvider(getFrontFacing());
        if (source != null && source.getStrand() != null) {
            Strand pulled = source.take();
            if (pulled != null) {
                Strand leftover = insertStrand(pulled);
                if (leftover != null) {
                    source.insertStrand(leftover);
                }
            }
        }
    }

    private @Nullable IStrandProvider getNeighborProvider(Direction dir) {
        if (getLevel() == null) return null;
        BlockPos neighborPos = getPos().relative(dir);
        if (getLevel().getBlockEntity(neighborPos) == null) return null;
        MetaMachine machine = MetaMachine.getMachine(getLevel(), neighborPos);
        return machine instanceof IStrandProvider provider ? provider : null;
    }

    @Override
    public Strand getStrand() {
        return Strand.deserialize(strandTag);
    }

    @Override
    public Strand take() {
        Strand transfer = Strand.deserialize(strandTag);
        this.strandTag = null;
        return transfer;
    }

    @Override
    public Strand insertStrand(Strand strand) {
        Strand current = getStrand();
        if (current != null) {
            return strand;
        }
        this.strandTag = strand != null ? Strand.serialize(new CompoundTag(), strand) : null;
        return null;
    }

    public boolean isExport() {
        return isExport;
    }

    public static PartAbility getAbility(boolean isExport) {
        return isExport ? SuSyMultiblockAbilities.STRAND_EXPORT : SuSyMultiblockAbilities.STRAND_IMPORT;
    }
}
