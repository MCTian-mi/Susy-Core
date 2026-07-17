package io.github.symmetricdevs.supersymmetry.api.fluids;

import com.gregtechceu.gtceu.api.fluids.FluidState;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKey;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.unification.material.info.SuSyMaterialIconTypes;
import net.minecraft.resources.ResourceLocation;

public final class SusyFluidStorageKeys {

    public static final FluidStorageKey MOLTEN = new FluidStorageKey(
            ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "molten"),
            SuSyMaterialIconTypes.slurry,
            s -> "molten_" + s,
            m -> "susy.fluid.molten",
            FluidState.LIQUID, -1);

    public static final FluidStorageKey SLURRY = new FluidStorageKey(
            ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "slurry"),
            SuSyMaterialIconTypes.slurry,
            s -> s + "_slurry",
            m -> "susy.fluid.slurry",
            FluidState.LIQUID, -1);

    public static final FluidStorageKey IMPURE_SLURRY = new FluidStorageKey(
            ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "impure_slurry"),
            SuSyMaterialIconTypes.slurry,
            s -> "impure_" + s + "_slurry",
            m -> "susy.fluid.impure_slurry",
            FluidState.LIQUID, -1);

    public static final FluidStorageKey SOLUTION = new FluidStorageKey(
            ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "solution"),
            SuSyMaterialIconTypes.slurry,
            s -> s + "_solution",
            m -> "susy.fluid.solution",
            FluidState.LIQUID, -1);

    public static final FluidStorageKey SUSPENSION = new FluidStorageKey(
            ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "suspension"),
            SuSyMaterialIconTypes.slurry,
            s -> s + "_suspension",
            m -> "susy.fluid.suspension",
            FluidState.LIQUID, -1);

    public static final FluidStorageKey WASTE = new FluidStorageKey(
            ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "waste"),
            SuSyMaterialIconTypes.slurry,
            s -> s + "_waste",
            m -> "susy.fluid.waste",
            FluidState.LIQUID, -1);

    public static final FluidStorageKey SUPERCRITICAL = new FluidStorageKey(
            ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, "supercritical"),
            SuSyMaterialIconTypes.supercritical,
            s -> "supercritical_" + s,
            m -> "susy.fluid.supercritical",
            FluidState.GAS, -1);

    private SusyFluidStorageKeys() {}
}
