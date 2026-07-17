package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static com.gregtechceu.gtceu.api.capability.GregtechDataCodes.assignId;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.GregtechDataCodes;
import com.gregtechceu.gtceu.api.gui.Widget;
import com.gregtechceu.gtceu.api.gui.widgets.ImageCycleButtonWidget;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.PatternMatchContext;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.client.utils.TooltipHelper;

import com.gregtechceu.gtceu.common.block.CasingBlock;
import com.gregtechceu.gtceu.common.blocks.BlockTurbineCasing;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.blocks.StoneVariantBlock;
import com.gregtechceu.gtceu.common.metatileentities.MetaTileEntities;
import it.unimi.dsi.fastutil.ints.IntLists;
import io.github.symmetricdevs.supersymmetry.api.capability.impl.QuarryLogic;
import io.github.symmetricdevs.supersymmetry.api.gui.SusyGuiTextures;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.DimensionProperty;
import io.github.symmetricdevs.supersymmetry.api.util.Grid3D;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.SuSyMetaTileEntities;

/**
 * The quarry multiblock is a regular {@link WorkableElectricMultiblockMachine}, with an additional mode
 * similar to {@link gregtech.common.metatileentities.multi.electric.MetaTileEntityLargeMiner} that allows
 * from breaking block in game.
 * For this it uses {@link QuarryLogic}
 *
 * @author h3tR / RMI / Crindigo
 */

public class MetaTileEntityQuarry extends WorkableElectricMultiblockMachine {

    private static final int MAX_DIAMETER = 31;
    private static final int MIN_DIAMETER = 15;

    private static final int BASE_TICKS_PER_EXCAVATION = 10;

    private static final int QUARRY_EXCAVATION_ACTIVE = assignId();

    private boolean isInitialized = false;

    private boolean excavationMode = false;
    private boolean excavationActive = false;
    private int excavationProgress = 0;
    private final QuarryLogic quarryLogic;
    private int width;
    private int depth;

    public MetaTileEntityQuarry(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.QUARRY_RECIPES);
        this.quarryLogic = new QuarryLogic(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityQuarry(this.metaTileEntityId);
    }

    @Override
    public void checkStructurePattern() {
        if (!this.isStructureFormed()) {
            reinitializeStructurePattern();
        }
        super.checkStructurePattern();
    }

    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean updateStructureDimensions() {
        World world = getWorld();
        Direction front = getFrontFacing();
        Direction back = front.getOpposite();
        Direction right = front.rotateYCCW(); /// right as if you were looking at it, not controller's left
        Direction left = right.getOpposite();

        // Start looking at the min diameter
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(getPos().offset(back, MIN_DIAMETER - 1));

        // Go behind the controller until it sees a frame to find the depth. Only support odd dimensions.
        int depth = 0;
        for (int i = MIN_DIAMETER; i <= MAX_DIAMETER; i += 2) {
            if (world.getBlockState(pos) == getSteelFrameState()) {
                depth = i;
                break;
            }
            pos.move(back, 2);
        }

        if (depth < MIN_DIAMETER) {
            invalidateStructure();
            return false;
        }

        // Then take the depth divided by 2 and look to the left to find a gearbox. It is inset
        // by one block, so the radius is that plus 1.
        final int minRadius = MIN_DIAMETER / 2;
        final int maxRadius = MAX_DIAMETER / 2;

        pos.move(front, depth / 2); // midpoint of the quarry
        pos.move(left, minRadius - 1); // earliest possible position

        int width = 0;
        for (int i = minRadius - 1; i <= maxRadius - 1; i++) {
            if (world.getBlockState(pos) == getGearboxState()) {
                width = 3 + (i * 2);
            }
            pos.move(left);
        }

        if (width < MIN_DIAMETER) {
            invalidateStructure();
            return false;
        }

        this.width = width;
        this.depth = depth;

        writeCustomData(GregtechDataCodes.UPDATE_STRUCTURE_SIZE, buf -> {
            buf.writeInt(this.width);
            buf.writeInt(this.depth);
        });

        return true;
    }

    protected static BlockState getSteelFrameState() {
        // Technically won't work with embedded pipes, but there's no huge reason to have them
        // on that part of the quarry.
        return MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel);
    }

    protected static BlockState getGearboxState() {
        return MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        if (getWorld() != null) updateStructureDimensions();

        // for auto build
        if (width < MIN_DIAMETER) {
            width = MIN_DIAMETER;
        }
        if (depth < MIN_DIAMETER) {
            depth = MIN_DIAMETER;
        }

        return createGrid(width, depth).build();

        // Original pattern below for reference:
        /*
         * return FactoryBlockPattern.start()
         * .aisle("CCCCCFFFFFCCCCC", " CC    F    CC ", "       F       ", "       F       ", "       F       ")
         * .aisle("CFS         SFC", "CFS         SFC", " FS         SF ", " FS         SF ", " FSFFFFFFFFFSF ")
         * .aisle("CS           SC", "CS           SC", " S           S ", " S           S ", " S           S ")
         * .aisle("C             C", "               ", "               ", "               ", " F           F ")
         * .aisle("C             C", "               ", "               ", "               ", " F           F ")
         * .aisle("F             F", "               ", "               ", "               ", " F           F ")
         * .aisle("AG           GA", "SS           SS", "               ", "               ", " F           F ")
         * .aisle("FG           GG", " G           G ", " F           F ", " F           F ", " F           F ")
         * .aisle("AG           GA", "SS           SS", "               ", "               ", " F           F ")
         * .aisle("F             F", "               ", "               ", "               ", " F           F ")
         * .aisle("C             C", "               ", "               ", "               ", " F           F ")
         * .aisle("C             C", "               ", "               ", "               ", " F           F ")
         * .aisle("CS           SC", "CS           SC", " S           S ", " S           S ", " S           S ")
         * .aisle("CFS         SFC", "CFS         SFC", " FS         SF ", " FS         SF ", " FSFFFFFFFFFSF ")
         * .aisle("CCCCCFAAAFCCCCC", " CC   AMA   CC ", "               ", "               ", "               ")
         * .where('M', selfPredicate())
         * .where('A', states(getCasingState())
         * .or(autoAbilities(true, true, true, true, false, false, false)))
         * .where('S', states(getCasingState()))
         * .where('G', states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID)))
         * .where('C',
         * states(MetaBlocks.STONE_BLOCKS.get(StoneVariantBlock.StoneVariant.SMOOTH).getState(StoneVariantBlock.
         * StoneType.CONCRETE_LIGHT)))
         * .where('F', frames(Materials.Steel))
         * .where('G', states(MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX)))
         * .build();
         */
    }

    private Grid3D createGrid(int quarryWidth, int quarryDepth) {
        final var grid = new Grid3D(quarryWidth, 5, quarryDepth)
                .where('M', selfPredicate())
                .where('A', states(getCasingState())
                        .or(autoAbilities(true, true, true, true, false, false, false)))
                .where('S', states(getCasingState()))
                .where('C', states(getConcreteState()))
                .where('F', frames(Materials.Steel))
                .where('G', states(getGearboxState()));

        // Layer 0
        // draw a concrete rectangle on the bottom first, we'll overwrite the middle parts after
        grid.rectXZ(0, 0, 0, quarryWidth - 1, quarryDepth - 1, 'C');
        // back has frames inset by 5
        // draw frames for all sides like this, and we'll change mid to AFA (left), AGA (right), AAA (front)

        // This code extends the quarry with concrete by painting over the middle 5 blocks on each side.
        grid.lineX(quarryWidth / 2 - 2, 0, -1, "FFFFF"); // back
        grid.lineX(quarryWidth / 2 - 2, 0, 0, "FAAAF"); // front
        grid.lineZ(0, 0, quarryDepth / 2 - 2, "FAFAF"); // left
        grid.lineZ(-1, 0, quarryDepth / 2 - 2, "FAGAF"); // right

        // This code extends the quarry with steel frames by painting between 5 and -6 on each side.
        // I think concrete looks better on the bottom, but if we want frames, we can swap this out.
        // grid.lineX(5, 0, -1, -6, 'F'); // back
        // grid.lineX(5, 0, 0, -6, 'F'); // front
        // grid.lineZ(0, 0, 5, -6, 'F'); // left
        // grid.lineZ(-1, 0, 5, -6, 'F'); // right
        //
        // grid.lineX(width / 2 - 1, 0, 0, "AAA"); // front
        // grid.lineZ(0, 0, depth / 2 - 1, "AFA"); // left
        // grid.lineZ(-1, 0, depth / 2 - 1, "AGA"); // right

        // inset gearboxes
        grid.lineZ(1, 0, quarryDepth / 2 - 1, "GGG"); // left + 1
        grid.lineZ(-2, 0, quarryDepth / 2 - 1, "GGG"); // right - 1

        // Layer 1
        // Concrete near corners
        grid.lineX(0, 1, -1, " CC");
        grid.lineX(-3, 1, -1, "CC ");
        grid.lineX(0, 1, 0, " CC");
        grid.lineX(-3, 1, 0, "CC ");
        grid.lineZ(0, 1, -3, "CC ");
        grid.lineZ(-1, 1, -3, "CC ");
        grid.lineZ(0, 1, 0, " CC");
        grid.lineZ(-1, 1, 0, " CC");

        // sides and front
        grid.lineZ(0, 1, quarryDepth / 2 - 1, "S S");
        grid.lineZ(1, 1, quarryDepth / 2 - 1, "SGS");
        grid.lineZ(-2, 1, quarryDepth / 2 - 1, "SGS");
        grid.lineZ(-1, 1, quarryDepth / 2 - 1, "S S");
        grid.lineX(quarryWidth / 2 - 1, 1, 0, "AMA");

        // Layer 2 and 3 just have a couple side frames. The rest are handled elsewhere.
        grid.set(1, 2, quarryDepth / 2, 'F');
        grid.set(-2, 2, quarryDepth / 2, 'F');
        grid.set(1, 3, quarryDepth / 2, 'F');
        grid.set(-2, 3, quarryDepth / 2, 'F');

        // Layer 4
        // draw the top as a rect of frames inset by 1, then replace the blocks next to the corners after
        grid.rectXZ(4, 1, 1, quarryWidth - 2, quarryDepth - 2, 'F');

        // 4 "F" columns inset 1 from the corners that go from y = 0 to 4
        grid.lineY(1, 0, -2, "FFFFF");
        grid.lineY(-2, 0, -2, "FFFFF");
        grid.lineY(1, 0, 1, "FFFFF");
        grid.lineY(-2, 0, 1, "FFFFF");
        grid.lineY(quarryWidth / 2, 0, -1, "FFFFF"); // also one in the back middle

        // 8 "S" columns near the corners that go from y = 0 to 4
        grid.lineY(2, 0, -2, "SSSSS");
        grid.lineY(-3, 0, -2, "SSSSS");
        grid.lineY(1, 0, -3, "SSSSS");
        grid.lineY(-2, 0, -3, "SSSSS");
        grid.lineY(1, 0, 2, "SSSSS");
        grid.lineY(-2, 0, 2, "SSSSS");
        grid.lineY(2, 0, 1, "SSSSS");
        grid.lineY(-3, 0, 1, "SSSSS");

        return grid;
    }

    private Grid3D createShapeGrid(int quarryWidth, int quarryDepth) {
        var grid = createGrid(quarryWidth, quarryDepth);
        // draw over the front to add an input bus, output bus, maint hatch, and energy hatch
        grid.lineX(quarryWidth / 2 - 1, 1, 0, "IMT");
        grid.lineX(quarryWidth / 2 - 1, 0, 0, "OAE");
        return grid;
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        var builder = new MultiblockShapeInfo.Builder();
        builder.where('M', SuSyMetaTileEntities.QUARRY, Direction.SOUTH)
                .where('A', getCasingState())
                .where('S', getCasingState())
                .where('C', getConcreteState())
                .where('F', getSteelFrameState())
                .where('G', getGearboxState())
                .where('I', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.LV], Direction.SOUTH)
                .where('O', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.LV], Direction.SOUTH)
                .where('E', MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.LV], Direction.SOUTH)
                .where('T',
                        () -> ConfigHolder.machines.enableMaintenance ? MetaTileEntities.MAINTENANCE_HATCH :
                                getCasingState(),
                        Direction.SOUTH);

        ArrayList<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        // add min, rectangular, max size
        // add current size first for multiblock preview
        shapeInfo
                .add(createShapeGrid(Math.max(width, MIN_DIAMETER), Math.max(depth, MIN_DIAMETER)).buildShape(builder));
        shapeInfo.add(createShapeGrid(23, 15).buildShape(builder));
        shapeInfo.add(createShapeGrid(31, 31).buildShape(builder));
        return shapeInfo;
    }

    protected static BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    private static BlockState getConcreteState() {
        return MetaBlocks.STONE_BLOCKS.get(StoneVariantBlock.StoneVariant.SMOOTH)
                .getState(StoneVariantBlock.StoneType.CONCRETE_LIGHT);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.CHUNK_MINER_OVERLAY; // TODO: custom texture?
    }

    @Override
    public boolean isMultiblockPartWeatherResistant(@Nonnull IMultiblockPart part) {
        return true;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        if (!this.isInitialized)
            quarryLogic.init();
        this.isInitialized = true;
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe, boolean consumeIfSuccess) {
        // Dimension gating deferred to Phase 8; accept all recipes for now.
        return super.checkRecipe(recipe, consumeIfSuccess);
    }

    @Override
    protected @NotNull Widget getFlexButton(int x, int y, int width, int height) {
        return new ImageCycleButtonWidget(x, y, width, height, SusyGuiTextures.BUTTON_QUARRY_MODES, 2,
                () -> this.excavationMode ? 1 : 0,
                this::setExcavationMode)
                        .setTooltipHoverString(mode -> mode == 1 ? "susy.multiblock.quarry.excavation_mode" :
                                "susy.multiblock.quarry.recipe_mode");
    }

    private void setExcavationMode(int mode) {
        this.excavationMode = mode == 1;
        if (this.excavationMode)
            this.quarryLogic.init(); // reset quarrylogic
    }

    @Override
    protected void updateFormedValid() {
        if (getWorld().isRemote || !this.recipeMapWorkable.isWorkingEnabled()) return;

        if (this.excavationMode && !this.quarryLogic.finished && this.drainEnergy(true) &&
                this.getNumMaintenanceProblems() <= 5) {
            this.drainEnergy(false);
            excavationProgress++;
            if (!excavationActive) toggleExcavationActive();

            if (excavationProgress % getTicksPerExcavation() == 0)
                this.quarryLogic.doQuarryOperation();
        } else {
            if (excavationActive) toggleExcavationActive();

            this.recipeMapWorkable.updateWorkable();
        }
    }

    private void toggleExcavationActive() {
        excavationActive = !excavationActive;
        writeCustomData(QUARRY_EXCAVATION_ACTIVE, buf -> buf.writeBoolean(excavationActive));
    }

    @Override
    protected void addDisplayText(List<Component> textList) {
        if (excavationMode)
            MultiblockDisplayText.builder(textList, isStructureFormed())
                    .setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), excavationActive)
                    .addEnergyUsageLine(recipeMapWorkable.getEnergyContainer())
                    .addWorkingStatusLine();
        else
            MultiblockDisplayText.builder(textList, isStructureFormed())
                    .setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                    .addEnergyUsageLine(recipeMapWorkable.getEnergyContainer())
                    .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                    .addParallelsLine(recipeMapWorkable.getParallelLimit())
                    .addWorkingStatusLine()
                    .addProgressLine(recipeMapWorkable.getProgressPercent());
    }

    public int getTicksPerExcavation() {
        return (int) (BASE_TICKS_PER_EXCAVATION / Math.pow(2, getEnergyTier() - 1));
    }

    // cap to EV tier
    public int getEnergyTier() {
        return Math.min(GTUtility.getFloorTierByVoltage(energyContainer.getInputVoltage()), GTValues.EV);
    }

    public boolean drainEnergy(boolean simulate) {
        long energyToDrain = GTValues.VA[this.getEnergyTier()];
        long resultEnergy = this.getEnergyContainer().getEnergyStored() - energyToDrain;
        if (resultEnergy >= 0L && resultEnergy <= this.getEnergyContainer().getEnergyCapacity()) {
            if (!simulate)
                this.getEnergyContainer().changeEnergy(-energyToDrain);
            return true;
        }
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("susy.machine.quarry.tooltip.info", MAX_DIAMETER, MIN_DIAMETER));
        if (TooltipHelper.isShiftDown()) {
            tooltip.add(I18n.format("susy.machine.quarry.tooltip.structure_info", MAX_DIAMETER, MIN_DIAMETER));
        }
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.excavationMode = data.getBoolean("excavationMode");
        this.excavationProgress = data.getInteger("excavationProgress");
        this.excavationActive = data.getBoolean("excavationActive");
        this.width = data.hasKey("width") ? data.getInteger("width") : this.width;
        this.depth = data.hasKey("depth") ? data.getInteger("depth") : this.depth;

        // If we didn't write the logic, then it wasn't initialized yet.
        if (data.hasKey("quarryLogic")) {
            this.quarryLogic.readFromNBT(data.getCompoundTag("quarryLogic"));
            this.isInitialized = true;
        } else {
            this.isInitialized = false;
        }
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag data) {
        // don't write logic before it's initialized since it will NPE
        if (isInitialized) {
            data.setTag("quarryLogic", this.quarryLogic.writeToNBT());
        }
        data.setBoolean("excavationMode", this.excavationMode);
        data.setInteger("excavationProgress", this.excavationProgress);
        data.setBoolean("excavationActive", this.excavationActive);
        data.setInteger("width", this.width);
        data.setInteger("depth", this.depth);
        return super.writeToNBT(data);
    }

    @Override
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        if (dataId == QUARRY_EXCAVATION_ACTIVE) {
            excavationActive = buf.readBoolean();
        }
        if (dataId == GregtechDataCodes.UPDATE_STRUCTURE_SIZE) {
            this.width = buf.readInt();
            this.depth = buf.readInt();
        }
        super.receiveCustomData(dataId, buf);
    }

    @Override
    public void writeInitialSyncData(FriendlyByteBuf buf) {
        buf.writeBoolean(excavationActive);
        buf.writeBoolean(excavationMode);
        buf.writeInt(width);
        buf.writeInt(depth);
        super.writeInitialSyncData(buf);
    }

    @Override
    public void receiveInitialSyncData(FriendlyByteBuf buf) {
        excavationActive = buf.readBoolean();
        excavationMode = buf.readBoolean();
        width = buf.readInt();
        depth = buf.readInt();
        super.receiveInitialSyncData(buf);
    }

    @Override
    public boolean isActive() {
        return (super.isActive() && !excavationMode) ||
                (excavationActive && this.isStructureFormed() && this.recipeMapWorkable.isWorkingEnabled());
    }
}
