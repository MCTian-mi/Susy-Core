package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static io.github.symmetricdevs.supersymmetry.api.blocks.VariantHorizontalRotatableBlock.FACING;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.GTValues;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.Widget;
import com.gregtechceu.gtceu.api.gui.resources.TextureArea;
import com.gregtechceu.gtceu.api.gui.widgets.ToggleButtonWidget;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.IProgressBarMultiblock;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.util.*;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import io.github.symmetricdevs.supersymmetry.api.gui.SusyGuiTextures;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockAlternatorCoil;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class MetaTileEntitySUSYLargeTurbine extends RotationGeneratorController implements IProgressBarMultiblock {

    public final BlockState casingState;
    public final BlockState rotorState;
    public final ICubeRenderer casingRenderer;
    public final ICubeRenderer frontOverlay;

    public MetaTileEntitySUSYLargeTurbine(ResourceLocation metaTileEntityId, GTRecipeType<?> GTRecipeType, int tier,
                                          int maxSpeed, int accel, int decel, BlockState casingState,
                                          BlockState rotorState, ICubeRenderer casingRenderer,
                                          ICubeRenderer frontOverlay) {
        super(metaTileEntityId, GTRecipeType, tier, maxSpeed, accel, decel);
        this.casingState = casingState;
        this.rotorState = rotorState;
        this.casingRenderer = casingRenderer;
        this.frontOverlay = frontOverlay;
        this.recipeMapWorkable = new SuSyTurbineRecipeLogic(this);
        this.recipeMapWorkable.setMaximumOverclockVoltage(GTValues.V[tier]);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntitySUSYLargeTurbine(metaTileEntityId, GTRecipeType, tier, maxSpeed, accel, decel,
                casingState, rotorState, casingRenderer, frontOverlay);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        // Different characters use common constraints. Copied from GCyM
        TraceabilityPredicate casingPredicate = states(this.casingState).setMinGlobalLimited(52)
                .or(abilities(MultiblockAbility.IMPORT_ITEMS).setPreviewCount(1));
        TraceabilityPredicate maintenance = abilities(MultiblockAbility.MAINTENANCE_HATCH).setMaxGlobalLimited(1)
                .setMinGlobalLimited(1);

        return FactoryBlockPattern.start()
                .aisle("GAAAAAAAO", "GAAAAAAAO", "G   A   O")
                .aisle("GAAAAAAAO", "GDDDDCCCF", "GAAAAAAAO")
                .aisle("GAAAAAAAO", "GSAAAAAAO", "G   A   O")
                .where('S', selfPredicate())
                .where('A', casingPredicate
                        .or(autoAbilities(false, false, false, false, false, false, false))
                        .or(maintenance))
                .where('O', casingPredicate
                        .or(autoAbilities(false, false, false, false, false, true, false))
                        .or(maintenance))
                .where('C', coilOrientation())
                .where('D', rotorOrientation())
                .where('F', abilities(MultiblockAbility.OUTPUT_ENERGY))
                .where('G', casingPredicate
                        .or(autoAbilities(false, false, false, false, true, false, false))
                        .or(maintenance))
                .where(' ', any())
                .build();
    }

    protected TraceabilityPredicate rotorOrientation() {
        // makes sure rotor's front faces the left side (relative to the player) of controller front
        return SuSyPredicates.horizontalOrientation(this, rotorState, RelativeDirection.RIGHT, FACING);
    }

    protected TraceabilityPredicate coilOrientation() {
        // makes sure rotor's front faces the left side (relative to the player) of controller front
        return SuSyPredicates.horizontalOrientation(this, copperCoilState(), RelativeDirection.RIGHT, FACING);
    }

    protected BlockState copperCoilState() {
        return SuSyBlocks.ALTERNATOR_COIL.getState(BlockAlternatorCoil.AlternatorCoilType.COPPER);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return casingRenderer;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return frontOverlay;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public boolean canVoidRecipeItemOutputs() {
        return true;
    }

    @Override
    public boolean canVoidRecipeFluidOutputs() {
        return true;
    }

    @Override
    protected boolean shouldShowVoidingModeButton() {
        return false;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    // GUI stuff

    @Override
    public int getNumProgressBars() {
        return 3;
    }

    @Override
    public double getFillPercentage(int index) {
        if (index == 0) {
            int[] fuelAmount = new int[2];
            if (getInputFluidInventory() != null) {
                SuSyTurbineRecipeLogic recipeLogic = (SuSyTurbineRecipeLogic) recipeMapWorkable;
                if (recipeLogic.getInputFluidStack() != null) {
                    FluidStack testStack = recipeLogic.getInputFluidStack().copy();
                    testStack.amount = Integer.MAX_VALUE;
                    fuelAmount = getTotalFluidAmount(testStack, getInputFluidInventory());
                }
            }
            return fuelAmount[1] != 0 ? 1.0 * fuelAmount[0] / fuelAmount[1] : 0;
        } else if (index == 1) {
            int[] lubricantAmount = new int[2];
            if (lubricantStack != null) {
                FluidStack testStack = lubricantStack.copy();
                testStack.amount = Integer.MAX_VALUE;
                lubricantAmount = getTotalFluidAmount(testStack, getInputFluidInventory());
            }

            return lubricantAmount[1] != 0 ? 1.0 * lubricantAmount[0] / lubricantAmount[1] : 0;
        } else {
            return 1.0 * getRotationSpeed() / maxSpeed;
        }
    }

    @Override
    public TextureArea getProgressBarTexture(int index) {
        if (index == 0) {
            return GuiTextures.PROGRESS_BAR_LCE_FUEL;
        } else if (index == 1) {
            return GuiTextures.PROGRESS_BAR_LCE_LUBRICANT;
        } else {
            return GuiTextures.PROGRESS_BAR_TURBINE_ROTOR_SPEED;
        }
    }

    @Override
    public void addBarHoverText(List<Component> hoverList, int index) {
        if (index == 0) {
            addFuelText(hoverList);
        } else if (index == 1) {
            int lubricantStored = 0;
            int lubricantCapacity = 0;
            double lubricantConsumptionRate = 0;

            if (isStructureFormed() && lubricantStack != null) {
                int[] lubricantAmount;
                FluidStack testStack = lubricantStack.copy();
                testStack.amount = Integer.MAX_VALUE;
                lubricantAmount = getTotalFluidAmount(testStack, getInputFluidInventory());
                lubricantStored = lubricantAmount[0];
                lubricantCapacity = lubricantAmount[1];
                lubricantConsumptionRate = (generatingPower) ?
                        lubricantInfo.amount_required * (2.0 * getRotationSpeed() / 3600) : 0;
            }

            Component lubricantStorage = TextComponentUtil.stringWithColor(
                    ChatFormatting.GOLD,
                    TextFormattingUtil.formatNumbers(lubricantStored) + " / " +
                            TextFormattingUtil.formatNumbers(lubricantCapacity) + " L");

            Component lubricantConsumption = TextComponentUtil.stringWithColor(
                    ChatFormatting.GOLD,
                    TextFormattingUtil.formatNumbers(lubricantConsumptionRate) + " L/min ");

            hoverList.add(TextComponentUtil.translationWithColor(
                    ChatFormatting.GRAY,
                    "susy.multiblock.rotation_generator.lubricant_amount",
                    lubricantStorage, lubricantConsumption));
        } else {
            Component rpmTranslated = TextComponentUtil.translationWithColor(
                    getRotorSpeedColor(getRotationSpeed(), getMaxRotationSpeed()),
                    "gregtech.multiblock.turbine.rotor_rpm_unit_name");
            Component rotorInfo = TextComponentUtil.translationWithColor(
                    getRotorSpeedColor(getRotationSpeed(), getMaxRotationSpeed()),
                    "%s / %s %s",
                    TextFormattingUtil.formatNumbers(getRotationSpeed()),
                    TextFormattingUtil.formatNumbers(getMaxRotationSpeed()),
                    rpmTranslated);
            hoverList.add(TextComponentUtil.translationWithColor(
                    ChatFormatting.GRAY,
                    "gregtech.multiblock.turbine.rotor_speed",
                    rotorInfo));
        }
    }

    private ChatFormatting getRotorSpeedColor(int rotorSpeed, int maxRotorSpeed) {
        double speedRatio = 1.0 * rotorSpeed / maxRotorSpeed;
        if (speedRatio < 0.4) {
            return ChatFormatting.RED;
        } else if (speedRatio < 0.8) {
            return ChatFormatting.YELLOW;
        } else {
            return ChatFormatting.GREEN;
        }
    }

    @Override
    protected @NotNull Widget getFlexButton(int x, int y, int width, int height) {
        SuSyTurbineRecipeLogic logic = (SuSyTurbineRecipeLogic) this.recipeMapWorkable;

        return new ToggleButtonWidget(x, y, width, height, SusyGuiTextures.BUTTON_ENERGY_VOIDING,
                logic::getVoidingEnergy, logic::setVoidingEnergy)
                        .setTooltipText("susy.gui.toggle_energy_voiding");
    }

    public static void addFuelNeededLine(List<Component> textList, SuSyTurbineRecipeLogic recipeLogic) {
        Recipe previousRecipe = recipeLogic.getPreviousRecipe();
        int parallel = recipeLogic.getCurrentParallel();

        int amount = previousRecipe != null ? previousRecipe.getFluidInputs().getFirst().getInputFluidStack().amount :
                0;

        Component fuelCurrent = TextComponentUtil.stringWithColor(ChatFormatting.RED,
                amount * parallel + "L");
        Component fuelNeeded = TextComponentUtil.stringWithColor(ChatFormatting.RED, previousRecipe != null ?
                amount * recipeLogic.getMaximumAllowedVoltage() / previousRecipe.getEUt() + "L" : "0L");
        Component numTicks = TextComponentUtil.stringWithColor(ChatFormatting.AQUA,
                TextFormattingUtil.formatNumbers(recipeLogic.getPreviousRecipeDuration()));
        textList.add(TextComponentUtil.translationWithColor(
                ChatFormatting.GRAY,
                "susy.multiblock.rotation_generator.fuel_needed",
                fuelCurrent, fuelNeeded, numTicks));
    }

    @Override
    protected void addDisplayText(List<Component> textList) {
        MultiblockFuelRecipeLogic recipeLogic = (MultiblockFuelRecipeLogic) recipeMapWorkable;

        if (isStructureFormed()) {
            FluidStack fuelStack = ((SuSyTurbineRecipeLogic) recipeMapWorkable).getInputFluidStack();
            if (fuelStack != null && fuelStack.amount > 0) {
                Component fuelName = GTUtility.getFluidTranslation(fuelStack.getFluid());
                textList.add(TextComponentUtil.translationWithColor(ChatFormatting.GRAY,
                        "susy.multiblock.rotation_generator.fuel_name", fuelName));
                if (lubricantStack != null && lubricantStack.amount > 0) {
                    Component lubricantName = GTUtility.getFluidTranslation((lubricantStack.getFluid()));
                    textList.add(TextComponentUtil.translationWithColor(ChatFormatting.GRAY,
                            "susy.multiblock.rotation_generator.lubricant", lubricantName, lubricantInfo.boost));
                }
            }
            textList.add(Component.translatable("susy.multiblock.rotation_generator.power", getMaxVoltage(),
                    Math.min(recipeMapWorkable.getEnergyContainer().getOutputVoltage(), GTValues.V[tier] * 16)));

            if (isActive())
                addFuelNeededLine(textList, (SuSyTurbineRecipeLogic) recipeLogic);
        }

        MultiblockDisplayText.builder(textList, isStructureFormed())
                .setWorkingStatus(recipeLogic.isWorkingEnabled(), recipeLogic.isActive())
                .addWorkingStatusLine();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.universal.tooltip.max_voltage_out", GTValues.V[tier + 2],
                GTValues.VNF[tier + 2]));
        tooltip.add(I18n.format("susy.multiblock.rotation_generator.tooltip", maxSpeed, accel, decel));
    }
}
