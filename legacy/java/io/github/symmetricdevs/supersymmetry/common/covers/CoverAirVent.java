package io.github.symmetricdevs.supersymmetry.common.covers;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import com.gregtechceu.gtceu.api.cover.CoverBase;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.cover.CoverableView;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.api.recipe.GasCollectorDimensionProperty;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import it.unimi.dsi.fastutil.ints.IntList;

public class CoverAirVent extends CoverBase implements ITickable {

    private final int airPerSecond;

    private @Nullable FluidStack cachedAirType;

    public CoverAirVent(@NotNull CoverDefinition definition, @NotNull CoverableView coverableView,
                        @NotNull Direction attachedSide, int airPerSecond) {
        super(definition, coverableView, attachedSide);
        this.airPerSecond = airPerSecond;
    }

    @Override
    public boolean canAttach(@NotNull CoverableView coverable, @NotNull Direction side) {
        return coverable.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
    }

    @Override
    public void renderCover(@NotNull CCRenderState renderState, @NotNull Matrix4 translation,
                            @NotNull IVertexOperation[] pipeline, @NotNull Cuboid6 plateBox,
                            @NotNull BlockRenderLayer layer) {
        Textures.AIR_VENT_OVERLAY.renderSided(getAttachedSide(), plateBox, renderState, pipeline, translation);
    }

    @Override
    public void update() {
        World world = getWorld();
        if (world.isRemote || getOffsetTimer() % 20 != 0) return;
        if (world.getBlockState(getPos().offset(getAttachedSide())).isFullBlock()) {
            return;
        }

        BlockEntity BlockEntity = getTileEntityHere();
        if (BlockEntity == null) return;

        IFluidHandler fluidHandler = BlockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
                getAttachedSide());
        if (fluidHandler == null) return;

        if (cachedAirType == null) {
            tryAcquireFluid();
        }

        if (cachedAirType != null) {
            fluidHandler.fill(cachedAirType.copy(), true);
        }
    }

    private void tryAcquireFluid() {
        final int dimension = getWorld().provider.getDimension();
        RecipeMaps.GAS_COLLECTOR_RECIPES.getRecipeList().stream()
                .filter(r -> {
                    IntList list = r.getProperty(GasCollectorDimensionProperty.getInstance(), null);
                    if (list == null) return false;

                    for (int dim : list) {
                        if (dimension == dim) {
                            return true;
                        }
                    }
                    return false;
                })
                .findFirst()
                .ifPresent(recipe -> {
                    FluidStack stack = recipe.getFluidOutputs().get(0);
                    if (stack != null) {
                        this.cachedAirType = new FluidStack(stack.getFluid(), airPerSecond);
                    }
                });
    }
}
