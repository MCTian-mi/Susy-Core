package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfiguratorButton;
import com.gregtechceu.gtceu.api.gui.widget.ExtendedProgressWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import io.github.symmetricdevs.supersymmetry.api.pattern.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.common.data.SuSyLubricants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * SuSy rotation-generator multiblock base — the modern port of the 1.12.2
 * {@code RotationGeneratorController} + {@code SuSyTurbineRecipeLogic} (which extended
 * {@code FuelMultiblockController} / {@code MultiblockFuelRecipeLogic}).
 *
 * <p>
 * GTCEu-Modern has no {@code MultiblockFuelRecipeLogic}; a fuel generator is a
 * {@link WorkableElectricMultiblockMachine} with {@code .generator(true)} and recipes
 * that carry EU in {@code tickOutputs}. On top of that base this class re-derives the
 * SuSy generator behaviour:
 * <ul>
 * <li><b>Spool-ramp rotor speed</b> — a persisted {@code speed} ramps up by
 * {@code accel} each working tick and decays by {@code decel} otherwise (clamped to
 * {@code [0, maxSpeed]}). Generation scales <em>linearly</em> with speed
 * ({@code production = speed / maxSpeed}); this is the 1.12.2 SuSy curve, deliberately
 * not the Modern rotor-holder {@code (speed/max)²} curve.</li>
 * <li><b>Five lubricant tiers</b> — the best lubricant present is used; it both gates
 * whether the machine runs and lengthens the recipe duration ({@code boost}), and is
 * consumed periodically as the rotor turns.</li>
 * <li><b>Energy-void toggle</b> — when enabled the generator voids excess EU and keeps
 * spinning when its buffer is full; when disabled it stalls.</li>
 * <li><b>Rotor / coil block orientation</b> — applied post-form from the match context
 * (modern replacement for the 1.12.2 side-effecting
 * {@code horizontalOrientation} predicate).</li>
 * </ul>
 *
 * <p>
 * The 1.12.2 three-bar GUI (fuel / lubricant / rotor-speed) becomes three
 * {@link ExtendedProgressWidget}s added in {@link #createUIWidget()}.
 */
public class SuSyRotationGeneratorMachine extends WorkableElectricMultiblockMachine implements ITieredMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SuSyRotationGeneratorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    protected final int tier;
    protected final int maxSpeed;
    protected final int accel;
    protected final int decel;

    /** Lubricant candidates, best first; the first present in the input tanks is used. */
    private final SuSyLubricants.Lubricant[] lubricants;

    @Persisted
    @DescSynced
    protected int speed;
    @Persisted
    protected long lubricantCounter;
    @Persisted
    @DescSynced
    protected boolean voidEnergy;

    /** The currently-detected lubricant fluid + tier (recomputed each tick). */
    @Nullable
    @DescSynced
    private FluidStack lubricantStack;
    @Nullable
    private SuSyLubricants.Lubricant lubricant;

    public SuSyRotationGeneratorMachine(IMachineBlockEntity holder, int tier, int maxSpeed, int accel, int decel,
                                        SuSyLubricants.Lubricant[] lubricants) {
        super(holder);
        this.tier = tier;
        this.maxSpeed = maxSpeed;
        this.accel = accel;
        this.decel = decel;
        this.lubricants = lubricants;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public boolean isGenerator() {
        return true;
    }

    //////////////////////////////////////
    // ****** Structure / lifecycle *****//
    //////////////////////////////////////

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        // Apply the rotor / alternator-coil / crankshaft orientations recorded by the
        // pure SuSyPredicates.horizontalOrientation predicates (1.12.2 did this as a
        // world side-effect inside the predicate; modern predicates are pure).
        var level = getLevel();
        if (level != null && !level.isClientSide) {
            for (var fixup : SuSyPredicates.getOrientationFixups(this)) {
                var pos = fixup.getLeft();
                var facing = SuSyPredicates.resolveAxialFacing(this, fixup.getRight());
                var state = level.getBlockState(pos);
                level.setBlockAndUpdate(pos, SuSyPredicates.withHorizontalFacing(state, facing));
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            subscribeServerTick(this::rotationUpdate);
        }
    }

    //////////////////////////////////////
    // ****** Speed ramp + lubricant ****//
    //////////////////////////////////////

    /** Per-server-tick update: lubricant bookkeeping and the rotor-speed ramp. */
    protected void rotationUpdate() {
        if (!isFormed())
            return;

        detectLubricant();

        boolean working = recipeLogic.isWorking() && hasSufficientLubricant();
        if (working) {
            speed = Math.min(speed + accel, maxSpeed);
            lubricantCounter += speed;
        } else {
            speed = Math.max(speed - decel, 0);
        }

        // Consume lubricant periodically, scaled by how fast the rotor has been turning.
        if (lubricantStack != null && lubricant != null && lubricantCounter >= (600L * 3600L)) {
            lubricantCounter = 0;
            drainLubricant(lubricantStack.getFluid(), lubricant.amountRequired());
        }
    }

    /** Find the best lubricant currently present in the input tanks (best tier first). */
    protected void detectLubricant() {
        for (SuSyLubricants.Lubricant tier : lubricants) {
            FluidStack found = drainLubricant(tier.fluid(), Integer.MAX_VALUE, true);
            if (found != null && !found.isEmpty()) {
                this.lubricantStack = found;
                this.lubricant = tier;
                return;
            }
        }
        this.lubricantStack = null;
        this.lubricant = null;
    }

    /** The machine runs (and ramps up) only while a lubricant meeting its amount requirement is present. */
    protected boolean hasSufficientLubricant() {
        return lubricant != null && lubricantStack != null
                && lubricantStack.getAmount() >= lubricant.amountRequired();
    }

    /** Current speed fraction [0,1]; also the linear production multiplier. */
    public double getSpeedFraction() {
        return maxSpeed <= 0 ? 0 : (double) speed / maxSpeed;
    }

    @Nullable
    private FluidStack drainLubricant(Fluid fluid, int amount) {
        return drainLubricant(fluid, amount, false);
    }

    /**
     * Drain a lubricant fluid from the machine's input fluid handlers. With
     * {@code simulate=true} this only reports how much is present (used for detection).
     */
    @Nullable
    private FluidStack drainLubricant(Fluid fluid, int amount, boolean simulate) {
        FluidStack toDrain = new FluidStack(fluid, amount);
        for (IRecipeHandler<?> handler : getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP)) {
            if (handler instanceof IFluidHandler tank) {
                FluidStack drained = tank.drain(toDrain,
                        simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
                if (!drained.isEmpty())
                    return drained;
            }
        }
        return null;
    }

    //////////////////////////////////////
    // ****** Recipe modification *******//
    //////////////////////////////////////

    /**
     * Cap the generator's output voltage by the current rotor speed. Read each recipe
     * search by the EU parallel limiter, so the parallel ceiling rises as the rotor
     * spools up. The 1.12.2 {@code getMaximumAllowedVoltage()} was
     * {@code min(V[tier] * 16, speed-scaled output)}.
     */
    @Override
    public long getOverclockVoltage() {
        return (long) (GTValues.V[tier] * 16 * getSpeedFraction());
    }

    /**
     * Static {@link com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier} for SuSy
     * rotation generators — parallelize up to the (speed-scaled) output voltage and
     * scale the per-tick EU by the linear speed fraction. Returns
     * {@link ModifierFunction#NULL} while lubricant is insufficient, so the machine will
     * not start without it.
     */
    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof SuSyRotationGeneratorMachine gen)) {
            return ModifierFunction.NULL;
        }
        if (!gen.hasSufficientLubricant()) {
            return ModifierFunction.NULL;
        }

        var EUt = recipe.getOutputEUt();
        long maxVoltage = gen.getOverclockVoltage();
        if (EUt.isEmpty() || maxVoltage <= EUt.voltage()) {
            return ModifierFunction.NULL;
        }

        int maxParallel = (int) (maxVoltage / EUt.getTotalEU());
        if (maxVoltage % EUt.getTotalEU() != 0)
            maxParallel++;
        int actualParallel = com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic
                .getParallelAmountFast(gen, recipe, maxParallel);
        if (actualParallel <= 0)
            return ModifierFunction.NULL;

        // SuSy: linear speed scaling (production = speed/maxSpeed), unlike the Modern
        // rotor-holder's quadratic productionBoost.
        double speedRatio = gen.getSpeedFraction();
        double eutMultiplier = (maxParallel == actualParallel)
                ? speedRatio * maxVoltage / EUt.voltage()
                : speedRatio * actualParallel;
        double durationMultiplier = gen.lubricant != null ? gen.lubricant.boost() : 1.0;

        return ModifierFunction.builder()
                .inputModifier(com.gregtechceu.gtceu.api.recipe.content.ContentModifier.multiplier(actualParallel))
                .outputModifier(com.gregtechceu.gtceu.api.recipe.content.ContentModifier.multiplier(actualParallel))
                .eutMultiplier(eutMultiplier)
                .parallels(actualParallel)
                .durationMultiplier(durationMultiplier)
                .build();
    }

    //////////////////////////////////////
    // ****** Voiding *******************//
    //////////////////////////////////////

    @Override
    public boolean canVoidRecipeOutputs(RecipeCapability<?> capability) {
        if (capability == EURecipeCapability.CAP) {
            // EU voiding is gated by the energy-void toggle; when off, the generator
            // stalls once its buffer is full (1.12.2 behaviour).
            return voidEnergy;
        }
        // Fluids/items are always voided (fuel flue etc. never stall the machine).
        return true;
    }

    public void setVoidEnergy(boolean voidEnergy) {
        this.voidEnergy = voidEnergy;
    }

    //////////////////////////////////////
    // ****** GUI ***********************//
    //////////////////////////////////////

    @Override
    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {
        super.attachConfigurators(configuratorPanel);
        configuratorPanel.attachConfigurators(new IFancyConfiguratorButton.Toggle(
                GuiTextures.BUTTON_VOID_MULTIBLOCK.getSubTexture(0, 0, 1, 0.5),
                GuiTextures.BUTTON_VOID_MULTIBLOCK.getSubTexture(0, 0.5, 1, 0.5),
                this::isVoidEnergy,
                (cd, pressed) -> setVoidEnergy(pressed))
                .setTooltipsSupplier(pressed -> List.of(Component
                        .translatable("susy.gui.toggle_energy_voiding." + (pressed ? "enabled" : "disabled")))));
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = (WidgetGroup) super.createUIWidget();
        // 1.12.2 IProgressBarMultiblock: three bars — fuel, lubricant, rotor speed.
        // setServerTooltipSupplier is inherited from LDLib ProgressWidget (returns the
        // parent type), so each bar is built as a typed local to keep the fluent chain.
        // TODO)) Phase 6: proper SuSy bar textures; these reuse GTCEu stock progress textures.
        var fuelBar = new ExtendedProgressWidget(
                this::getFuelPercent, 4, 4, 90, 6, GuiTextures.PROGRESS_BAR_BOILER_FUEL.get(false));
        fuelBar.setFillDirection(ProgressTexture.FillDirection.LEFT_TO_RIGHT);
        fuelBar.setServerTooltipSupplier(this::addFuelTooltip);
        group.addWidget(fuelBar);

        var lubricantBar = new ExtendedProgressWidget(
                this::getLubricantPercent, 4, 12, 90, 6, GuiTextures.PROGRESS_BAR_BOILER_HEAT);
        lubricantBar.setFillDirection(ProgressTexture.FillDirection.LEFT_TO_RIGHT);
        lubricantBar.setServerTooltipSupplier(this::addLubricantTooltip);
        group.addWidget(lubricantBar);

        var speedBar = new ExtendedProgressWidget(
                this::getSpeedFraction, 4, 20, 90, 6, GuiTextures.PROGRESS_BAR_ARROW);
        speedBar.setFillDirection(ProgressTexture.FillDirection.LEFT_TO_RIGHT);
        speedBar.setServerTooltipSupplier(this::addSpeedTooltip);
        group.addWidget(speedBar);
        return group;
    }

    /** Fraction of the input-tank capacity holding the current fuel (best-effort). */
    protected double getFuelPercent() {
        long[] total = countInputFluid(null);
        return total[1] == 0 ? 0 : (double) total[0] / total[1];
    }

    protected double getLubricantPercent() {
        if (lubricantStack == null)
            return 0;
        long[] total = countInputFluid(lubricantStack.getFluid());
        return total[1] == 0 ? 0 : (double) total[0] / total[1];
    }

    /** @return {stored, capacity} of the given fluid across input tanks, or all fluid if {@code fluid==null}. */
    private long[] countInputFluid(@Nullable Fluid fluid) {
        long stored = 0, capacity = 0;
        for (IRecipeHandler<?> handler : getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP)) {
            if (handler instanceof IFluidHandler tank) {
                for (int i = 0; i < tank.getTanks(); i++) {
                    FluidStack fs = tank.getFluidInTank(i);
                    if (fluid == null || fs.getFluid() == fluid) {
                        stored += fs.getAmount();
                        capacity += tank.getTankCapacity(i);
                    }
                }
            }
        }
        return new long[] { stored, capacity };
    }

    private void addFuelTooltip(List<Component> tooltip) {
        long[] total = countInputFluid(null);
        tooltip.add(Component.translatable("susy.multiblock.rotation_generator.fuel_amount",
                FormattingUtil.formatNumbers(total[0]), FormattingUtil.formatNumbers(total[1])));
    }

    private void addLubricantTooltip(List<Component> tooltip) {
        if (lubricantStack != null && lubricant != null) {
            long[] total = countInputFluid(lubricantStack.getFluid());
            double rate = recipeLogic.isWorking() ? lubricant.amountRequired() * (2.0 * speed / 3600.0) : 0;
            tooltip.add(Component.translatable("susy.multiblock.rotation_generator.lubricant_amount",
                    FormattingUtil.formatNumbers(total[0]), FormattingUtil.formatNumbers(total[1]),
                    FormattingUtil.formatNumbers((long) rate)));
        } else {
            tooltip.add(Component.translatable("gregtech.multiblock.large_combustion_engine.no_lubricant"));
        }
    }

    private void addSpeedTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("gregtech.multiblock.turbine.rotor_speed",
                FormattingUtil.formatNumbers(speed), FormattingUtil.formatNumbers(maxSpeed)));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            textList.add(Component.translatable("gregtech.multiblock.turbine.rotor_speed",
                    FormattingUtil.formatNumbers(speed), FormattingUtil.formatNumbers(maxSpeed)));
            textList.add(Component.translatable("susy.multiblock.rotation_generator.power",
                    FormattingUtil.formatNumbers(getOverclockVoltage()),
                    FormattingUtil.formatNumbers(GTValues.V[tier] * 16)));
            if (lubricantStack == null || lubricantStack.isEmpty()) {
                textList.add(Component.translatable("gregtech.multiblock.large_combustion_engine.no_lubricant")
                        .withStyle(ChatFormatting.RED));
            }
        }
    }

    //////////////////////////////////////
    // ****** Accessors *****************//
    //////////////////////////////////////

    @Override
    public int getTier() {
        return tier;
    }

    public int getSpeed() {
        return speed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public boolean isVoidEnergy() {
        return voidEnergy;
    }
}
