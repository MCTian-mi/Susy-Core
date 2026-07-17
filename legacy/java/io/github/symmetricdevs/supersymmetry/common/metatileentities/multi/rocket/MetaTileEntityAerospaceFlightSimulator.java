package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.rocket;

import static supercritical.api.pattern.SCPredicates.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
// FluidRegistry removed in 1.20.1
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.capability.GregtechDataCodes;
import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;

import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.capability.EnergyContainerList;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.ModularUI;
import com.gregtechceu.gtceu.api.gui.widgets.*;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.PatternMatchContext;
import com.gregtechceu.gtceu.api.util.Position;
import com.gregtechceu.gtceu.api.util.Size;
import com.gregtechceu.gtceu.api.util.TextComponentUtil;
import com.gregtechceu.gtceu.api.util.world.DummyWorld;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;

import com.gregtechceu.gtceu.common.blocks.BlockGlassCasing;
import com.gregtechceu.gtceu.common.block.CasingBlock.MetalCasingType;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.SusyLog;
import io.github.symmetricdevs.supersymmetry.api.blocks.VariantHorizontalRotatableBlock;
import io.github.symmetricdevs.supersymmetry.api.gui.SusyGuiTextures;
import io.github.symmetricdevs.supersymmetry.api.rocketry.fuels.RocketFuelEntry;
import io.github.symmetricdevs.supersymmetry.api.rocketry.rockets.AbstractRocketBlueprint;
import io.github.symmetricdevs.supersymmetry.api.rocketry.rockets.RocketStage;
import io.github.symmetricdevs.supersymmetry.api.util.DataStorageLoader;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;
import io.github.symmetricdevs.supersymmetry.common.blocks.rocketry.BlockProcessorCluster;
import io.github.symmetricdevs.supersymmetry.common.item.SuSyMetaItems;
import io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials;
import io.github.symmetricdevs.supersymmetry.common.mui.widget.ConditionalWidget;
import io.github.symmetricdevs.supersymmetry.common.mui.widget.RocketRenderWidget;
import io.github.symmetricdevs.supersymmetry.common.mui.widget.SlotWidgetMentallyStable;
import io.github.symmetricdevs.supersymmetry.common.rocketry.SuccessCalculation;

// TODO add a tooltip to the controller item that mentions losing progress if power/coolant is cut
public class MetaTileEntityAerospaceFlightSimulator extends MultiblockControllerMachine
                                                    implements IWorkable {

    private static Fluid COOLANT_IN;

    private static Fluid COOLANT_OUT;

    private static final double k = 1.0;

    public static Entity createEntityByResource(ResourceLocation rl, World world) {
        EntityEntry entry = ForgeRegistries.ENTITIES.getValue(rl);
        if (entry == null) {
            throw new IllegalArgumentException("No entity registered under " + rl);
        }
        try {
            return entry.newInstance(world);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate entity with constructor (World)", e);
        }
    }

    public static double getSuccessProbability(double f0, double progress) {
        double xh = -1000.0 / k * Math.log(1.0 - f0);
        return 1.0 - Math.exp(-k * (progress + xh) / 1000.0);
    }

    private IEnergyContainer energyContainer;
    public IMultipleTankHandler inputCoolant;

    public IMultipleTankHandler outputCoolant;

    private boolean isActive = false;

    private boolean isWorkingEnabled = true;

    protected boolean hasNotEnoughEnergy;

    private int progress = 0;

    private boolean coolantFilled;
    private List<BlockPos> coolantPositions;

    public DataStorageLoader rocketBlueprintSlot = new DataStorageLoader(
            this,
            item -> SuSyMetaItems.isMetaItem(item) == SuSyMetaItems.DATA_CARD_MASTER_BLUEPRINT.metaValue &&
                    item.getTagCompound() != null && item.getTagCompound().getBoolean("buildstat"));

    private boolean hasNotEnoughCoolant = false;

    public RocketFuelEntry fuel;

    private double gravity = 9.81;

    public MetaTileEntityAerospaceFlightSimulator(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);

        COOLANT_IN = SusyMaterials.Perfluoro2Methyl3Pentanone.getFluid();

        COOLANT_OUT = SusyMaterials.WarmPerfluoro2Methyl3Pentanone.getFluid();
        if (COOLANT_OUT == null) {
            COOLANT_OUT = FluidRegistry.getFluid("lava");
        }

        this.energyContainer = new EnergyContainerList(new ArrayList<>());
        this.inputCoolant = new FluidTankList(true);
        this.outputCoolant = new FluidTankList(true);
    }

    @Override
    public int getProgress() {
        return this.progress;
    }

    @Override
    public int getMaxProgress() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isActive() {
        return super.isActive() && this.isActive;
    }

    @Override
    public boolean isWorkingEnabled() {
        return this.isWorkingEnabled;
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        this.isWorkingEnabled = isWorkingAllowed;
        markDirty();
        World world = getWorld();
        if (world != null && !world.isRemote) {
            writeCustomData(GregtechDataCodes.WORKING_ENABLED, buf -> buf.writeBoolean(isWorkingEnabled));
        }
    }

    public void setActive(boolean active) {
        if (this.isActive != active && this.isStructureFormed()) {
            // SusyLog.logger.info("setActive({})", active);
            this.isActive = active;
            markDirty();
            World world = getWorld();
            if (world != null && !world.isRemote) {
                writeCustomData(GregtechDataCodes.WORKABLE_ACTIVE, buf -> buf.writeBoolean(active));
            }
        }
    }

    @Override
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.WORKING_ENABLED) {
            this.setWorkingEnabled(buf.readBoolean());
        }
        if (dataId == GregtechDataCodes.WORKABLE_ACTIVE) {
            this.setActive(buf.readBoolean());
        }
        if (dataId == GregtechDataCodes.LOCK_OBJECT_HOLDER) {
            this.rocketBlueprintSlot.setLocked(buf.readBoolean());
        }
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.energyContainer = new EnergyContainerList(new ArrayList<>());
        this.inputCoolant = new FluidTankList(true);
        this.outputCoolant = new FluidTankList(true);
        this.progress = 0;

        this.coolantPositions = null; // Clear water fill data when the structure is invalidated
        this.coolantFilled = false;
    }

    public void fillCoolant(List<BlockPos> toFill, Fluid fluid, IMultipleTankHandler fluidInputs) {
        if (fluidInputs != null) {
            FluidStack toDrain = new FluidStack(fluid, 1000);
            FluidStack drained = fluidInputs.drain(toDrain, false);
            if (drained != null && drained.amount != 0) {
                if (drained.amount == 1000) {
                    World world = this.getWorld();
                    BlockPos pos = toFill.get(0);
                    if (world.isBlockLoaded(pos) &&
                            (world.isAirBlock(pos) || world.getBlockState(pos).getBlock() == fluid.getBlock())) {
                        world.setBlockState(pos, fluid.getBlock().getDefaultState(), 2);
                        fluidInputs.drain(drained, true);
                        toFill.remove(0);
                    }
                }
            }
        }
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    public BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(MetalCasingType.STEEL_SOLID);
    }

    public BlockState getComputerState() {
        return SuSyBlocks.PROCESSOR_CLUSTER.getState(BlockProcessorCluster.TierType.TIER_1);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityAerospaceFlightSimulator(metaTileEntityId);
    }

    // doesnt check if the blueprint itself is complete but that should go onto the slot check
    public boolean hasBlueprint() {
        return !this.rocketBlueprintSlot.isEmpty() && this.rocketBlueprintSlot.getStackInSlot(0).hasTagCompound() &&
                this.rocketBlueprintSlot.getStackInSlot(0).getMetadata() ==
                        SuSyMetaItems.DATA_CARD_MASTER_BLUEPRINT.metaValue;
    }

    // can return null if something breaks
    public AbstractRocketBlueprint getBlueprint() {
        if (!this.rocketBlueprintSlot.isEmpty() && this.rocketBlueprintSlot.getStackInSlot(0).hasTagCompound()) {
            CompoundTag tag = this.rocketBlueprintSlot.getStackInSlot(0).getTagCompound();
            AbstractRocketBlueprint bp = AbstractRocketBlueprint.getCopyOf(tag.getString("name"));
            if (bp != null && bp.readFromNBT(tag) && bp.isFullBlueprint()) {
                return bp;
            }
        }
        return null;
    }

    public void stop() {
        setActive(false);
        setWorkingEnabled(false);
        this.rocketBlueprintSlot.setLocked(false);
        AbstractRocketBlueprint bp = getBlueprint();
        if (bp == null || !bp.isFullBlueprint()) {
            SusyLog.logger.info("bp == {}", bp);
            return;
        }
        // used as a measure of how complex the entire rocket is -> should take longer to simulate i
        // guess?
        double totalAssemblyTime = bp.getStages().stream()
                .flatMap(x -> x.getComponents().values().stream().flatMap(y -> y.stream()))
                .mapToDouble(x -> x.getAssemblyDuration())
                .sum();
        // TODO this is likely wrong and will increase the success chance by a lot if you just spam turn
        // on/off signals
        double minimalChance = Math.max(bp.AFSSuccessChance,
                new SuccessCalculation(bp).calculateInitialSuccess(gravity));
        double successProb = getSuccessProbability(minimalChance, (double) this.progress / totalAssemblyTime);
        bp.AFSSuccessChance = successProb;

        this.rocketBlueprintSlot.setNBT(
                (t) -> {
                    CompoundTag n = bp.writeToNBT();
                    n.setBoolean("buildstat", true);
                    return n;
                });
        this.progress = 0;
    }

    // wipe the progress when there is not enough power/coolant to prevent the player from having too
    // much fun
    public void crash() {
        setActive(false);
        setWorkingEnabled(false);
        this.rocketBlueprintSlot.setLocked(false);
        this.progress = 0;
    }

    public void start() {
        this.progress = 0;
        setActive(true);
        setWorkingEnabled(true);
        this.rocketBlueprintSlot.setLocked(true);
    }

    @Override
    public boolean isStructureObstructed() {
        return super.isStructureObstructed() || !coolantFilled;
    }

    @Override
    protected @Nonnull ICubeRenderer getFrontOverlay() {
        return SusyTextures.ORE_SORTER_OVERLAY;
    }

    // fix getEnergyToConsume and getCompute when some generic computer blocks are added
    protected int getEnergyToConsume() {
        return 1000;
    }

    // per tick
    protected int getCompute() {
        return 1;
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.energyContainer = new EnergyContainerList(getAbilities(MultiblockAbility.INPUT_ENERGY));
        this.inputCoolant = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputCoolant = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
        this.progress = 0;

        this.coolantPositions = context.getOrDefault(FLUID_BLOCKS_KEY, new ArrayList<>());
        this.coolantFilled = coolantPositions.isEmpty();
    }

    @Override
    protected void addErrorText(List<Component> textList) {
        super.addErrorText(textList);
        if (hasNotEnoughCoolant) {
            textList.add(Component.translatable(this.getMetaName() + ".gui.no_coolant_warning"));
        }
        if (isStructureFormed() && !coolantFilled) {
            textList.add(
                    TextComponentUtil.translationWithColor(
                            ChatFormatting.RED, this.getMetaName() + ".obstructed"));
            textList.add(
                    TextComponentUtil.translationWithColor(
                            ChatFormatting.GRAY, this.getMetaName() + ".obstructed.desc"));
        }
    }

    // mb/tick
    protected int getCoolantToConsume(int energyUsage) {
        return (int) Math.ceil(Math.sqrt((double) energyUsage / 20));
    }

    @Override
    protected void updateFormedValid() {
        if (!coolantFilled && getOffsetTimer() % 5 == 0) {
            fillCoolant(
                    this.coolantPositions, SusyMaterials.Perfluoro2Methyl3Pentanone.getFluid(), inputCoolant);
            if (this.coolantPositions.isEmpty()) {
                this.coolantFilled = true;
            }
        }

        if (!this.isActive() || !this.isWorkingEnabled() || this.isStructureObstructed()) {
            return;
        }
        int energyToConsume = getEnergyToConsume();
        boolean maintenance = ConfigHolder.machines.enableMaintenance && hasMaintenanceMechanics();
        if (maintenance) {
            energyToConsume += getNumMaintenanceProblems() * energyToConsume / 10;
        }
        int coolantToConsume = getCoolantToConsume(energyToConsume);
        FluidStack drainedFluid = inputCoolant.drain(new FluidStack(COOLANT_IN, coolantToConsume), false);
        boolean enoughCoolant = false;
        if (drainedFluid != null) {
            enoughCoolant = drainedFluid.amount == coolantToConsume;
        }
        boolean enoughSpaceForCoolant = outputCoolant.fill(new FluidStack(COOLANT_OUT, coolantToConsume), false) ==
                coolantToConsume;
        if (enoughCoolant && enoughSpaceForCoolant) {
            hasNotEnoughCoolant = false;
        } else {
            hasNotEnoughCoolant = true;
            crash();
        }
        if (hasNotEnoughEnergy && energyContainer.getInputPerSec() > 19L * energyToConsume) {
            hasNotEnoughEnergy = false;
        }
        boolean enoughEnergy = energyContainer.getEnergyStored() >= energyToConsume && !hasNotEnoughEnergy;
        if (enoughEnergy && !hasNotEnoughCoolant) {
            long consumed = energyContainer.removeEnergy(energyToConsume);
            if (consumed == -energyToConsume) {
                inputCoolant.drain(new FluidStack(COOLANT_IN, coolantToConsume), true);
                outputCoolant.fill(new FluidStack(COOLANT_OUT, coolantToConsume), true);
                setActive(true);
                this.progress += this.getCompute();
            } else {
                hasNotEnoughEnergy = true;
                crash();
            }
        } else {
            hasNotEnoughEnergy = true;
            crash();
        }
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("IIIIIIIII", "IIIIIIIII", "IIIIIIIII", "IIIIIIIII", "IIIIIIIII")
                .aisle("IIIIIIIII", "IPFPFPFPI", "IPFPFPFPI", "IFFFFFFFI", "ITTTTTTTI")
                .aisle("IIIIIIIII", "IPFPFPFPI", "IPFPFPFPI", "IFFFFFFFI", "ITTTTTTTI")
                .aisle("IIIIIIIII", "IPFPFPFPI", "IPFPFPFPI", "IFFFFFFFI", "ITTTTTTTI")
                .aisle("IIIIIIIII", "IPFPFPFPI", "IPFPFPFPI", "IFFFFFFFI", "ITTTTTTTI")
                .aisle("IIIISIIII", "ITCTCTCTI", "ITCTCTCTI", "ITCTCTCTI", "IIIIIIIII")
                .where('S', selfPredicate())
                .where(' ', air())
                .where('C', states(getCasingState()))
                .where(
                        'P',
                        states(
                                getComputerState()
                                        .withProperty(VariantHorizontalRotatableBlock.FACING, Direction.SOUTH)))
                .where(
                        'T',
                        states(
                                MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS)))
                .where('F', fluid(SusyMaterials.Perfluoro2Methyl3Pentanone.getFluid()))
                .where(
                        'I',
                        abilities(MultiblockAbility.IMPORT_FLUIDS)
                                .setMaxGlobalLimited(1)
                                .setMinGlobalLimited(1, 1)
                                .or(
                                        abilities(MultiblockAbility.EXPORT_FLUIDS)
                                                .setMaxGlobalLimited(1)
                                                .setMaxGlobalLimited(1, 1))
                                .or(
                                        abilities(MultiblockAbility.INPUT_ENERGY)
                                                .setMaxGlobalLimited(2)
                                                .setMinGlobalLimited(1, 1)
                                                .or(states(getCasingState()))
                                                .or(
                                                        maintenancePredicate()
                                                                .setMaxGlobalLimited(1)
                                                                .setMinGlobalLimited(1, 1)))
                                .or(states(getCasingState())))
                .build();
    }

    @Override
    protected ModularUI createUI(Player Player) {
        return createGUITemplate(Player).build(this.getHolder(), Player);
    }

    private ModularUI.Builder createGUITemplate(Player Player) {
        if (!this.isStructureFormed()) {
            ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 165, 35);
            builder.widget(new ImageWidget(4, 4, 157, 27, GuiTextures.DISPLAY));
            builder.label(9, 12, this.getMetaName() + ".gui.not_formed", 0xAE5421);
            return builder;
        }
        int width = 280;
        int height = 210;

        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, width, height);
        builder.image(4, 4, width - 8, height - 91, GuiTextures.DISPLAY);
        builder.widget(
                new IndicatorImageWidget(width - 24, height / 2 - 3, 17, 17, getLogo())
                        .setWarningStatus(getWarningLogo(), this::addWarningText)
                        .setErrorStatus(getErrorLogo(), this::addErrorText));
        if (this instanceof IControllable controllable) {
            builder.widget(
                    new ImageCycleButtonWidget(
                            width - 42,
                            height - 23,
                            18,
                            18,
                            GuiTextures.BUTTON_POWER,
                            controllable::isWorkingEnabled,
                            controllable::setWorkingEnabled));
            builder.widget(
                    new ImageWidget(width - 42, height - 5, 18, 6, GuiTextures.BUTTON_POWER_DETAIL));
        }
        builder.bindPlayerInventory(Player.inventory, 125);

        // blueprint slot
        SlotWidgetMentallyStable blueprintSlot = new SlotWidgetMentallyStable(this.rocketBlueprintSlot, 0, 0, 0);

        blueprintSlot.setSelfPosition(new Position(12, height / 5 - 27));
        blueprintSlot.setChangeListener(
                () -> {
                    if (this.rocketBlueprintSlot.isEmpty()) {
                        blueprintSlot.setSelfPosition(new Position(12, height / 2 - 27));
                    } else {
                        blueprintSlot.setSelfPosition(new Position(width - 23, height - 23));
                    }
                });
        blueprintSlot.setBackgroundTexture(GuiTextures.SLOT_DARK);
        builder.widget(blueprintSlot);

        ConditionalWidget mainGroup = new ConditionalWidget(0, 0, width, height, () -> true);
        ConditionalWidget fuelGroup = new ConditionalWidget(-120, 0, 120, 80, () -> true);
        fuelGroup.addWidget(new ImageWidget(0, 0, 120, 80, GuiTextures.DISPLAY));
        fuelGroup.addWidget(
                new LabelWidget(
                        5, 5, I18n.format(this.getMetaName() + ".gui.fuel_selector_label"), 0xffffff));
        fuelGroup.addWidget(
                new FuelRegistrySelectorWidget(
                        4,
                        14,
                        80,
                        60,
                        (fuel) -> {
                            this.fuel = fuel;
                        }));
        // Gravity selector
        fuelGroup.addWidget(
                new LabelWidget(
                        5, 60, I18n.format(this.getMetaName() + ".gui.gravity_selector_label"), 0xffffff));
        fuelGroup.addWidget(
                new TextFieldWidget2(
                        5,
                        68,
                        60,
                        12,
                        () -> String.valueOf(gravity),
                        value -> {
                            if (!value.isEmpty()) {
                                try {
                                    gravity = Double.parseDouble(value);
                                } catch (NumberFormatException _) {
                                    gravity = 9.81;
                                }
                            }
                        })
                                .setAllowedChars(TextFieldWidget2.DECIMALS)
                                .setMaxLength(6));

        mainGroup.addWidgetWithTest(
                fuelGroup, () -> this.hasBlueprint() && !this.isActive() && this.fuel == null);

        mainGroup.addWidgetWithTest(
                new AdvancedTextWidget(
                        9,
                        19,
                        (l) -> {
                            AbstractRocketBlueprint bp = this.getBlueprint();
                            if (this.hasBlueprint() && bp != null) {
                                l.add(
                                        Component.translatable(
                                                this.getMetaName() + ".gui.rocket_name",
                                                I18n.format("susy.rocketry." + bp.name + ".name")));

                                l.add(
                                        Component.translatable(
                                                this.getMetaName() + ".gui.stages", bp.getStages().size()));
                                for (RocketStage stage : bp.getStages()) {
                                    l.add(Component.literal("  - " + I18n.format(stage.getLocalizationKey())));
                                }
                            }
                        },
                        0xffffff),
                () -> this.hasBlueprint() && !isActive());

        // multi information
        // these should probably be visible at all times in some different corner
        mainGroup.addWidgetWithTest(
                new LabelWidget(
                        width - 130,
                        9,
                        I18n.format(getMetaName() + ".gui.computation_power", this.getCompute()),
                        0xffffff),
                () -> !this.isActive());
        mainGroup.addWidgetWithTest(
                new LabelWidget(
                        width - 130,
                        20,
                        I18n.format(
                                getMetaName() + ".gui.coolant_flow",
                                this.getCoolantToConsume(this.getEnergyToConsume()) * 20),
                        0xffffff),
                () -> !this.isActive());
        mainGroup.addWidgetWithTest(
                new LabelWidget(
                        width - 130,
                        31,
                        I18n.format(getMetaName() + ".gui.energy_consumption", this.getEnergyToConsume()),
                        0xffffff),
                () -> !this.isActive());
        mainGroup.addWidgetConditionalInit(
                () -> !this.isActive() && this.hasBlueprint() && this.getBlueprint() != null,
                () -> {
                    return new DynamicLabelWidget(
                            width - 130,
                            42,
                            () -> {
                                var bp = this.getBlueprint();
                                if (bp != null) {
                                    double c = Math.max(
                                            bp.AFSSuccessChance,
                                            getSuccessProbability(bp.AFSSuccessChance, progress));
                                    return I18n.format(
                                            this.getMetaName() + ".gui.success_chance",
                                            String.format("%3.4f", c * 100));
                                }
                                return "";
                            },
                            0xffffff);
                });

        // label gets in the way a little
        mainGroup.addWidgetWithTest(
                new LabelWidget(9, 9, getMetaFullName(), 0xffffff), () -> !this.isActive());
        mainGroup.addWidgetWithTest(
                new ClickButtonWidget(
                        width - 61,
                        height - 23,
                        18,
                        18,
                        "",
                        (_) -> {
                            if (this.hasBlueprint() && !this.isActive() && this.fuel != null) {
                                int energyToConsume = getEnergyToConsume();
                                boolean maintenance = ConfigHolder.machines.enableMaintenance &&
                                        hasMaintenanceMechanics();
                                if (maintenance) {
                                    energyToConsume += getNumMaintenanceProblems() * energyToConsume / 10;
                                }
                                int coolantToConsume = getCoolantToConsume(energyToConsume);
                                // TODO: spam if null checks because when you break the input hatch and the
                                // structure
                                // forms again its messed up somehow
                                boolean enoughCoolant = inputCoolant.drain(new FluidStack(COOLANT_IN, coolantToConsume),
                                        false).amount == coolantToConsume;
                                boolean enoughSpaceForCoolant = outputCoolant
                                        .fill(new FluidStack(COOLANT_OUT, coolantToConsume), false) == coolantToConsume;
                                if (enoughCoolant && enoughSpaceForCoolant) {
                                    hasNotEnoughCoolant = false;
                                } else {
                                    hasNotEnoughCoolant = true;
                                    crash();
                                }
                                if (hasNotEnoughEnergy && energyContainer.getInputPerSec() > 19L * energyToConsume) {
                                    hasNotEnoughEnergy = false;
                                }
                                if (!hasNotEnoughEnergy && !hasNotEnoughCoolant) {
                                    this.start();
                                }
                            }
                        })
                                .setTooltipText(this.getMetaName() + ".gui.start_button")
                                .setButtonTexture(SusyGuiTextures.GREEN_CIRCLE),
                () -> !this.isActive() && this.hasBlueprint());
        mainGroup.addWidgetWithTest(
                new ClickButtonWidget(
                        width - 61,
                        height - 23,
                        18,
                        18,
                        "",
                        (_) -> {
                            if (this.hasBlueprint() && this.isActive()) {

                                // && !hasNotEnoughCoolant
                                // && !hasNotEnoughEnergy
                                this.stop();
                            }
                        })
                                .setTooltipText(this.getMetaName() + ".gui.stop_button")
                                .setButtonTexture(SusyGuiTextures.RED_CIRCLE),
                () -> this.isActive() && this.hasBlueprint());

        // button to open the fuel selector ui again, would be nice to give it a different texture
        mainGroup.addWidgetWithTest(
                new ClickButtonWidget(
                        width - 79,
                        height - 23,
                        18,
                        18,
                        "",
                        (_) -> {
                            if (!this.isActive() && this.fuel != null) {
                                this.fuel = null;
                            }
                        })
                                .setTooltipText(this.getMetaName() + ".gui.reset_fuel")
                                .setButtonTexture(SusyGuiTextures.RED_X)
                                .setShouldClientCallback(true),
                () -> !this.isActive() && this.fuel != null);

        // rocket render

        mainGroup.addWidgetConditionalInit(
                () -> {
                    if (this.hasBlueprint() && this.getBlueprint() != null && this.isActive()) {
                        return true;
                    }
                    return false;
                },
                () -> {
                    AbstractRocketBlueprint bp = this.getBlueprint();
                    if (bp != null && bp.isFullBlueprint()) {
                        ResourceLocation entity_res = bp.relatedEntity;
                        DummyWorld world = new DummyWorld();
                        Entity rocketentity = this.createEntityByResource(entity_res, world);
                        rocketentity.setPosition(0, 0, 0);
                        return new RocketRenderWidget(
                                new Size(width - 15, 100), new Position(7, 11), rocketentity);
                    }
                    return null;
                });
        // TODO nuke this
        builder.widget(
                new AdvancedTextWidget(
                        width - 100,
                        height - 82,
                        (l) -> {
                            int c = this.progress;

                            l.add(Component.translatable(Integer.toString(c)));
                        },
                        0xffffff));

        builder.widget(mainGroup);

        return builder;
    }
}
