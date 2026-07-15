package io.github.symmetricdevs.supersymmetry.common.data;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.world.level.material.Fluid;

import io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials;

/**
 * The SuSy generator lubricant tiers, ported from the 1.12.2
 * {@code SuSyUtility.Lubricant} + {@code lubricants} map (and the
 * {@code POSSIBLE_LUBRICANTS} ordering in {@code RotationGeneratorController}).
 *
 * <p>
 * Each tier carries the amount (mB) the machine must hold to run and a {@code boost}
 * that lengthens the recipe duration (more total EU from the same fuel at the same
 * EU/t). Tiers are ordered <em>best first</em>; a generator uses the best lubricant
 * currently present in its input tanks.
 *
 * <p>
 * TODO)) The 1.12.2 {@code SuSyUtility} also carries coolant tiers, tank-size functions,
 * and space-item bans; those port with the machines / config that need them, not here.
 */
public final class SuSyLubricants {

    private SuSyLubricants() {}

    /** A lubricant tier: the fluid, the mB required to run, and the duration boost. */
    public record Lubricant(Fluid fluid, int amountRequired, double boost) {}

    // Tier fluids. GT Lubricant is a stock GTCEu fluid; the four SuSy grades are SuSy
    // materials whose fluids are registered with them (Phase 2). Amounts / boosts are
    // the 1.12.2 SuSyUtility.lubricants values.
    public static final Lubricant SUPREME = new Lubricant(fluidOf(SusyMaterials.SupremeLubricant), 1, 1.6);
    public static final Lubricant PREMIUM = new Lubricant(fluidOf(SusyMaterials.PremiumLubricant), 2, 1.4);
    public static final Lubricant MIDGRADE = new Lubricant(fluidOf(SusyMaterials.MidgradeLubricant), 4, 1.2);
    public static final Lubricant GT_LUBRICANT = new Lubricant(fluidOf(GTMaterials.Lubricant), 8, 1.0);
    public static final Lubricant LUBRICATING_OIL = new Lubricant(fluidOf(SusyMaterials.LubricatingOil), 16, 1.0);

    /**
     * Lubricant tiers, best first (matches the 1.12.2 {@code POSSIBLE_LUBRICANTS}
     * priority: Supreme > Premium > Midgrade > GT Lubricant > LubricatingOil).
     */
    public static final Lubricant[] TIERS = { SUPREME, PREMIUM, MIDGRADE, GT_LUBRICANT, LUBRICATING_OIL };

    private static Fluid fluidOf(Material material) {
        // TODO)) these lubricant materials are registered with a fluid in Phase 2; if a
        // configuration ever drops the fluid property, Material.getFluid() will throw at
        // class-init — surface that loudly rather than NPE-ing later.
        return material.getFluid();
    }
}
