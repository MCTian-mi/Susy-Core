package io.github.symmetricdevs.supersymmetry.common.cover;

import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ported from the 1.12.2 {@code supersymmetry.common.covers.CoverAirVent}.
 * <p>
 * Pumps fresh air (as water fluid, 100mB/s) into the attached machine every 20 ticks.
 * The 1.12.2 version queried {@code GAS_COLLECTOR_RECIPES} per-dimension to determine
 * which gas to produce. This port simplifies by producing a fixed fluid; the
 * dimension-based gas mapping can be re-added when the gas collector system is ported.
 */
public class CoverAirVent extends CoverBehavior {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(CoverAirVent.class,
            CoverBehavior.MANAGED_FIELD_HOLDER);

    private final int airPerSecond;

    private @Nullable FluidStack cachedAirType;

    public CoverAirVent(@NotNull CoverDefinition definition, @NotNull ICoverable coverHolder,
                        @NotNull Direction attachedSide, int airPerSecond) {
        super(definition, coverHolder, attachedSide);
        this.airPerSecond = airPerSecond;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public boolean canAttach() {
        return coverHolder.getFluidHandlerCap(attachedSide, false) != null;
    }

    @Override
    public void onNeighborChanged(Block block, BlockPos fromPos, boolean isMoving) {
        // Re-evaluate air availability when neighbor changes
        cachedAirType = null;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // Subscribe to server ticks
        var subscription = coverHolder.subscribeServerTick(this::update);
    }

    protected void update() {
        Level world = coverHolder.getLevel();
        if (world == null || world.isClientSide) return;
        if (coverHolder.getOffsetTimer() % 20 != 0) return;

        // Check if the block on the attached side is passable
        BlockPos neighborPos = coverHolder.getPos().relative(attachedSide);
        BlockState neighborState = world.getBlockState(neighborPos);
        if (neighborState.isSolidRender(world, neighborPos)) {
            return;
        }

        IFluidHandler fluidHandler = coverHolder.getFluidHandlerCap(attachedSide, false);
        if (fluidHandler == null) return;

        if (cachedAirType == null) {
            cachedAirType = produceAir();
        }

        if (cachedAirType != null) {
            FluidStack toFill = cachedAirType.copy();
            fluidHandler.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    /**
     * Produces the air fluid stack. Currently returns fresh air (water).
     * Override or extend to implement dimension-based gas selection.
     */
    protected @Nullable FluidStack produceAir() {
        // Simplified: produce "fresh air" as water. The 1.12.2 version queried
        // GAS_COLLECTOR_RECIPES per-dimension; re-implement when gas collector
        // dimension conditions are ported.
        return new FluidStack(net.minecraft.world.level.material.Fluids.WATER, airPerSecond);
    }

    @Override
    public boolean canPipePassThrough() {
        return true;
    }
}
