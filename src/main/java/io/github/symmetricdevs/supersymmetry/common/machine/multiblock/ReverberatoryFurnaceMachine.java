package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Modern port of the 1.12.2 MetaTileEntityReverberatoryFurnace.
 *
 * NoEnergy multiblock: REVERBERATORY_FURNACE_RECIPES builds EUt(0) recipes with no EU IO and the
 * structure grants no INPUT_ENERGY ability, so the stock workable logic runs them with no energy
 * hatch — the 1.12.2 NoEnergyMultiblockRecipeLogic energy spoof is intentionally not ported. The
 * 1.12.2 dual-ability inventory merge (SuSy PRIMITIVE_IMPORT/EXPORT + standard buses) is also
 * dropped: the Phase 4a primitive item buses use the standard IMPORT_ITEMS/EXPORT_ITEMS
 * PartAbility, which the stock workable machine already aggregates.
 *
 * The only behavior that forces a subclass is the 1.12.2 active-state client effect:
 * exhaust smoke off the rear of the structure (legacy update() -> runMufflerEffect) plus
 * crackle sound and smoke/flame at the front face (legacy randomDisplayTick). Modern folds both
 * into clientTick(); mirrors GTCEu's own PrimitiveBlastFurnaceMachine.clientTick().
 *
 * No @Persisted/@DescSynced fields, so the inherited ManagedFieldHolder is sufficient.
 */
public class ReverberatoryFurnaceMachine extends WorkableElectricMultiblockMachine {

    public ReverberatoryFurnaceMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick() {
        super.clientTick();
        if (!isFormed() || !isActive()) {
            return;
        }
        Level level = getLevel();
        if (level == null) {
            return;
        }

        // 1.12.2 update(): muffler-style smoke from the exhaust end of the furnace, 5 blocks
        // opposite the front face (the rear of the 7-long structure), drifting upward.
        Direction exhaust = getFrontFacing().getOpposite();
        double x = getPos().getX() + 0.5 + exhaust.getStepX() * 5.0;
        double y = getPos().getY() + 0.25 + exhaust.getStepY() * 0.76;
        double z = getPos().getZ() + 0.5 + exhaust.getStepZ() * 5.0;
        double ySpeed = exhaust.getStepY() * 0.1 + 0.2 + 0.1 * GTValues.RNG.nextFloat();
        level.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, 0.0, ySpeed, 0.0);

        // 1.12.2 randomDisplayTick(): ~10% crackle plus smoke/flame, jittered 0.52 off the
        // front face. Modern has no per-block randomDisplayTick for machines; clientTick is
        // the equivalent hook (same idiom as CokeOvenMachine/PrimitiveBlastFurnaceMachine).
        Direction front = getFrontFacing();
        float horizontalOffset = GTValues.RNG.nextFloat() * 0.6F - 0.3F;
        double fx = getPos().getX() + 0.5;
        double fy = getPos().getY() + GTValues.RNG.nextFloat() * 0.375F + 0.3F;
        double fz = getPos().getZ() + 0.5;
        if (front.getAxis() == Direction.Axis.X) {
            fx += front.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 0.52F : -0.52F;
            fz += horizontalOffset;
        } else if (front.getAxis() == Direction.Axis.Z) {
            fz += front.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 0.52F : -0.52F;
            fx += horizontalOffset;
        }
        if (GTValues.RNG.nextDouble() < 0.1) {
            level.playLocalSound(fx, fy, fz, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS,
                    1.0F, 1.0F, false);
        }
        level.addParticle(ParticleTypes.LARGE_SMOKE, fx, fy, fz, 0.0, 0.0, 0.0);
        level.addParticle(ParticleTypes.FLAME, fx, fy, fz, 0.0, 0.0, 0.0);
    }
}
