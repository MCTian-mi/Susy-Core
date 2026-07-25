package io.github.symmetricdevs.supersymmetry.common.item.armor;

import net.minecraft.world.entity.player.Player;

/**
 * Helper for hazardous-environment checks used by the breathing armour
 * logic classes. Replicates the logic that was in the 1.12.2
 * {@code DimensionBreathabilityHandler} so the armour files are
 * self-contained.
 * <p>
 * A future integration pass can wire this into a proper dimension-config
 * system (e.g. GTCEu's radiation API or a custom capability).
 */
public final class BreathabilityHelper {

    /** Sentinel value meaning "absorb all incoming damage". */
    public static final double ABSORB_ALL = -1;

    private static final int BENEATH_ID = 10;
    private static final int NETHER_ID = -1;

    private BreathabilityHelper() {}

    /**
     * @return true if the player is currently in a dimension or position
     *         that requires breathing equipment
     */
    public static boolean isInHazardousEnvironment(Player player) {
        var dimLoc = player.level().dimension().location();
        return dimLoc.toString().equals("beneath") ||
               dimLoc.toString().equals("minecraft:the_nether");
    }
}
