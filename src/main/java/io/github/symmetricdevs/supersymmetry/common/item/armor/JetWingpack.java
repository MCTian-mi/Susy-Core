package io.github.symmetricdevs.supersymmetry.common.item.armor;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.item.armor.ArmorComponentItem;
import com.gregtechceu.gtceu.api.item.armor.ArmorLogicSuite;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.utils.input.SyncedKeyMappings;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Jet-powered wingpack for HV-tier elytra-like flight.
 * Consumes fluid fuel from an internal tank.
 * <p>
 * Fuel recipes are defined in {@link SuSyRecipeMaps#JET_WINGPACK_FUELS}.
 */
public class JetWingpack extends ArmorLogicSuite {

    protected static final int TANK_CAPACITY = 32000;

    protected static final int MAX_SPEED = 2;
    protected static final double MIN_SPEED = 0.02;
    protected static final double THRUST = 0.05;
    protected static final double REVERSE_THRUST = 0.05;
    protected static final double FALLING = 0.005;

    private static final int ENERGY_PER_USE = 1;

    public JetWingpack() {
        super(ENERGY_PER_USE, TANK_CAPACITY, GTValues.HV, ArmorItem.Type.CHESTPLATE);
    }

    @Override
    public ResourceLocation getArmorTexture(@NotNull ItemStack stack, @Nullable Entity entity,
                                            @NotNull EquipmentSlot slot, @Nullable String type) {
        return ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "textures/armor/jet_wingpack.png");
    }

    @Override
    public void onArmorTick(@NotNull Level world, @NotNull Player player, @NotNull ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();

        byte toggleTimer = tag.getByte("toggleTimer");
        boolean pressed = tag.getBoolean("pressed");
        boolean wingActive = tag.getBoolean("wingActive");
        boolean engineActive = tag.getBoolean("engineActive");

        // toggle engine on/off with the mode-switch key
        if (toggleTimer == 0 && SyncedKeyMappings.ARMOR_MODE_SWITCH.isKeyDown(player)) {
            engineActive = !engineActive;
            toggleTimer = 5;
            if (!world.isClientSide) {
                player.sendSystemMessage(
                        Component.translatable(engineActive ?
                                "metaarmor.jet_wingpack.engine_active" :
                                "metaarmor.jet_wingpack.engine_inactive"));
            }
        }

        // handle engine-assisted flight while gliding
        if (engineActive && player.isFallFlying() && drainFuel(world, itemStack, 1, true)) {
            Vec3 lookVec = player.getLookAngle();
            if (player.isShiftKeyDown()) {
                // braking
                player.setDeltaMovement(
                        player.getDeltaMovement().x - REVERSE_THRUST *
                                (player.getDeltaMovement().x - MIN_SPEED * lookVec.x),
                        player.getDeltaMovement().y - REVERSE_THRUST * player.getDeltaMovement().y + FALLING,
                        player.getDeltaMovement().z - REVERSE_THRUST *
                                (player.getDeltaMovement().z - MIN_SPEED * lookVec.z)
                );
            } else {
                // acceleration
                player.setDeltaMovement(
                        player.getDeltaMovement().x + THRUST *
                                (MAX_SPEED * lookVec.x - player.getDeltaMovement().x),
                        player.getDeltaMovement().y + THRUST *
                                (MAX_SPEED * lookVec.y - player.getDeltaMovement().y),
                        player.getDeltaMovement().z + THRUST *
                                (MAX_SPEED * lookVec.z - player.getDeltaMovement().z)
                );
                world.addParticle(ParticleTypes.CLOUD,
                        player.getX(), player.getY(), player.getZ(), 0.0, 0.0, 0.0);
            }
            drainFuel(world, itemStack, 1, false);
        }

        // jump to toggle elytra flight
        if (!pressed && SyncedKeyMappings.VANILLA_JUMP.isKeyDown(player)) {
            pressed = true;
            if (!world.isClientSide) {
                if (!wingActive) {
                    if (canTakeOff(player, engineActive)) {
                        player.startFallFlying();
                        wingActive = true;
                    }
                } else {
                    player.stopFallFlying();
                    wingActive = false;
                }
            }
        }

        if (pressed && !SyncedKeyMappings.VANILLA_JUMP.isKeyDown(player)) pressed = false;
        if (toggleTimer > 0) toggleTimer--;

        tag.putByte("toggleTimer", toggleTimer);
        tag.putBoolean("pressed", pressed);
        tag.putBoolean("wingActive", wingActive);
        tag.putBoolean("engineActive", engineActive);
    }

    @Override
    public void addToolComponents(ArmorComponentItem item) {
        super.addToolComponents(item);
    }

    // ----------------------------------------------------------------
    // Fuel drain
    // ----------------------------------------------------------------

    protected boolean drainFuel(Level level, @NotNull ItemStack stack, int amount, boolean simulate) {
        var tag = stack.getOrCreateTag();
        short burnTimer = tag.getShort("burnTimer");

        if (burnTimer > 0) {
            if (!simulate) {
                tag.putShort("burnTimer", (short) (burnTimer - 1));
            }
            return true;
        }

        // Read stored fuel from NBT
        CompoundTag fuelTag = tag.getCompound("fuelFluid");
        FluidStack storedFuel = FluidStack.loadFluidStackFromNBT(fuelTag);
        if (storedFuel == null || storedFuel.isEmpty()) return false;

        int burnTime = getFuelBurnTime(level, storedFuel);
        if (burnTime <= 0) return false;

        if (!simulate) {
            tag.putShort("burnTimer", (short) burnTime);
            storedFuel.shrink(amount);
            if (storedFuel.getAmount() <= 0) {
                tag.remove("fuelFluid");
            } else {
                tag.put("fuelFluid", storedFuel.writeToNBT(new CompoundTag()));
            }
        }
        return true;
    }

    private static int getFuelBurnTime(Level level, FluidStack fluidStack) {
        var recipeType = SuSyRecipeMaps.JET_WINGPACK_FUELS;
        if (recipeType == null) return 0;
        for (GTRecipe recipe : level.getRecipeManager().getAllRecipesFor(recipeType)) {
            for (var content : recipe.getInputContents(FluidRecipeCapability.CAP)) {
                var ingredient = FluidRecipeCapability.CAP.of(content.content);
                if (!ingredient.isEmpty() && ingredient.test(fluidStack)) {
                    return recipe.duration;
                }
            }
        }
        return 0;
    }

    private static boolean canTakeOff(Player player, boolean engineActive) {
        return engineActive && !player.onGround();
    }

    @Override
    public int getArmorDisplay(@NotNull Player player, @NotNull ItemStack armor, @NotNull EquipmentSlot slot) {
        return 0;
    }
}
