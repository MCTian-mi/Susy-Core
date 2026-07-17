package io.github.symmetricdevs.supersymmetry.common.event;

import static net.minecraft.world.entity.EquipmentSlot.HEAD;

import java.util.*;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import io.github.symmetricdevs.supersymmetry.api.util.SuSyDamageSources;
import io.github.symmetricdevs.supersymmetry.common.item.SuSyArmorItem;

public final class DimensionBreathabilityHandler {

    private static final Map<Integer, BreathabilityInfo> dimensionBreathabilityMap = new HashMap<>();

    private static final BreathabilityInfo SPACE = new BreathabilityInfo(SuSyDamageSources.DEPRESSURIZATION, 3);
    public static final int BENEATH_ID = 10;
    public static final int NETHER_ID = -1;

    public static final double ABSORB_ALL = -1;

    private DimensionBreathabilityHandler() {}

    public static void loadConfig() {
        dimensionBreathabilityMap.clear();

        // Nether
        dimensionBreathabilityMap.put(-1, new BreathabilityInfo(SuSyDamageSources.getToxicAtmoDamage(), 2));
        // Beneath
        dimensionBreathabilityMap.put(10, new BreathabilityInfo(SuSyDamageSources.getSuffocationDamage(), 0.5));
    }

    public static boolean tickAir(Player player, FluidStack oxyStack) {
        // don't drain if we are in creative
        if (player.isCreative()) return true;
        // TODO: Use player.getInventory() approach once player inventory is available in modern
        return true; // placeholder
    }

    public static boolean isInHazardousEnvironment(Player player) {
        return dimensionBreathabilityMap.containsKey(player.level().dimension().location().hashCode()) ||
                (player.getY() > 600); // TODO: Add rocket riding check once EntityRocket is ported
    }

    public static void tickPlayer(Player player) {
        if (isInHazardousEnvironment(player)) {
            ItemStack headStack = player.getItemBySlot(HEAD);
            if (headStack.getItem() instanceof SuSyArmorItem item) {
                if (item.isValid(headStack, player)) {
                    double damageAbsorbed = item.getDamageAbsorbed(headStack, player);
                    if (damageAbsorbed != ABSORB_ALL)
                        applyDamage(player, damageAbsorbed);
                    return;
                }
            }
            applyDamage(player, 0);
        }
    }

    public static void applyDamage(Player player, double amountAbsorbed) {
        if (dimensionBreathabilityMap.containsKey(player.level().dimension().location().hashCode())) {
            dimensionBreathabilityMap.get(player.level().dimension().location().hashCode()).damagePlayer(player, amountAbsorbed);
        } else {
            SPACE.damagePlayer(player, amountAbsorbed);
        }
    }

    public static final class BreathabilityInfo {

        public DamageSource damageType;
        public double defaultDamage;

        public BreathabilityInfo(DamageSource damageType, double defaultDamage) {
            this.damageType = damageType;
            this.defaultDamage = defaultDamage;
        }

        public void damagePlayer(Player player) {
            player.hurt(damageType, (float) defaultDamage);
        }

        public void damagePlayer(Player player, double amountAbsorbed) {
            if (defaultDamage > amountAbsorbed) {
                player.hurt(damageType, (float) defaultDamage - (float) amountAbsorbed);
            }
        }
    }
}
