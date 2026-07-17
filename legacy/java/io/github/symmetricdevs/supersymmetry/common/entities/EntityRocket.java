package io.github.symmetricdevs.supersymmetry.common.entities;

import java.util.List;
import java.util.Random;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import io.github.symmetricdevs.supersymmetry.api.items.CargoItemStackHandler;
import io.github.symmetricdevs.supersymmetry.client.renderer.handler.IAlwaysRender;

/**
 * Stub — GeckoLib 3 animation and rocketry fuel system removed.
 * <p>
 * The rocket entity is a 1.12.2 GeckoLib-3-animated entity used by the
 * {@code MetaTileEntityLaunchPad} machine. Render logic has been stripped
 * pending GeckoLib 4 port.
 */
public class EntityRocket extends EntityAbstractRocket implements IAlwaysRender {

    private static final Random rnd = new Random();
    protected static final float jerk = 0.0001F;
    public CargoItemStackHandler cargo;

    public EntityRocket(EntityType<? extends EntityRocket> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    @Override
    public void launchRocket() {
        super.launchRocket();
    }

    @Override
    protected float getExplosionStrength() {
        return 24;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setLaunched(compound.getBoolean("Launched"));
        this.setCountdownStarted(compound.getBoolean("CountdownStarted"));
        this.setAge(compound.getInt("Age"));
        this.setActed(compound.getBoolean("Acted"));
        this.setLaunchTime(compound.getInt("LaunchTime"));
        this.setFlightTime(compound.getInt("FlightTime"));
        this.setStartPos(compound.getFloat("StartPos"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Launched", this.isLaunched());
        compound.putBoolean("CountdownStarted", this.isCountDownStarted());
        compound.putInt("Age", this.getAge());
        compound.putBoolean("Acted", this.hasActed());
        compound.putInt("LaunchTime", this.getLaunchTime());
        compound.putInt("FlightTime", this.getFlightTime());
        compound.putFloat("StartPos", this.getStartPos());
    }

    @Override
    public void tick() {
        super.tick();

        boolean launched = this.isLaunched();
        int age = this.getAge();
        int launchTime = this.getLaunchTime();

        if (this.isCountDownStarted() && !launched && age >= launchTime) {
            this.launchRocket();
        }

        if (launched) {
            int flightTime = getFlightTime();
            float startPos = this.getStartPos();
            this.setFlightTime(flightTime + 1);
        }
        this.setAge(age + 1);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }
}
