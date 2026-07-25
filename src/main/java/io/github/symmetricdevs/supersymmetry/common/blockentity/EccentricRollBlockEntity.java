package io.github.symmetricdevs.supersymmetry.common.blockentity;

import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import io.github.symmetricdevs.supersymmetry.common.data.SusyBlockEntities;

import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Animated block entity for the eccentric crusher roll. The roll block is a GTCEu
 * {@link com.gregtechceu.gtceu.api.block.ActiveBlock}; the parent {@code WorkableMultiblockMachine}
 * toggles its {@code ACTIVE} state while the crusher is working (via the native {@code vaBlocks}
 * mechanism), and the animation loop only advances while {@code ACTIVE} is set — otherwise it
 * freezes on the current frame. Ported from the 1.12.2 {@code AnimatablePartTileEntity}.
 */
public class EccentricRollBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final RawAnimation LOOP = RawAnimation.begin().thenLoop("default_loop");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public EccentricRollBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public EccentricRollBlockEntity(BlockPos pos, BlockState state) {
        this(SusyBlockEntities.ECCENTRIC_ROLL.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // TODO)) Phase 6: phase-lock the independently cached GeckoLib block animations without
        // bypassing the normal controller lifecycle; the default controller is reliable but lets
        // rolls first rendered at different times start on separate phases.
        controllers.add(new AnimationController<>(this, "roll_loop", state -> {
            state.setAnimation(LOOP);
            return isActive() ? PlayState.CONTINUE : PlayState.STOP;
        }));
    }

    private boolean isActive() {
        BlockState state = getBlockState();
        return state.hasProperty(GTBlockStateProperties.ACTIVE) && state.getValue(GTBlockStateProperties.ACTIVE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object entity) {
        return getLevel() == null ? 0 : getLevel().getGameTime();
    }
}
