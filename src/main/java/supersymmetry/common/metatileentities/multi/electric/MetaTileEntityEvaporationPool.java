package supersymmetry.common.metatileentities.multi.electric;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GregTechAPI;
import gregtech.api.block.IHeatingCoilBlockStats;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.*;
import gregtech.api.util.*;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.blocks.StoneVariantBlock;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import supersymmetry.api.capability.impl.EvapRecipeLogic;
import supersymmetry.api.integration.EvaporationPoolInfoProvider;
import supersymmetry.api.recipes.SuSyRecipeMaps;
import supersymmetry.api.recipes.properties.EvaporationEnergyProperty;
import supersymmetry.common.blocks.SuSyBlocks;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MetaTileEntityEvaporationPool extends RecipeMapMultiblockController {

    /*
        For future reference: "((IGregTechTileEntity)world.getTileEntity(pos)).getMetaTileEntity() instanceof IMultiblockAbilityPart"
        is the way ceu gets mte from te. You might also try using MetaTileEntityHolder.
     */

    public static final int coilDataID = 10142156;
    public boolean isHeated = false;
    public int[] rollingAverage = new int[20];
    public boolean areCoilsHeating = false;
    public int coilStateMeta = -1; //order is last in order dependent ops because I'm lazy

    // The sizes below indicates the total size of the evapool, excluding edges.
    public static final int MIN_SIZE = 5; // 7 x 7 evapool with 9 evabed blocks
    public static final int MAX_SIZE = 30; // 32 x 32 evapool with 784 evabed blocks. CEu multis should work fine with chunk borders.
    public static final int energyValuesID = 10868607;
    byte[] wasExposed; //indexed with row*col + col with row = 0 being furthest and col 0 being leftmost when looking at controller
    public boolean isRecipeStalled = false;
    public Executor executor;
    @Setter
    @Getter
    int kiloJoules = 0; //about 1000J/s on a sunny day for 1/m^2 of area
    @Setter
    @Getter
    int joulesBuffer = 0;
    int tickTimer = 0;
    int exposedBlocks = 0;
    private int lDist = 0;
    private int rDist = 0;
    private int bDist = 0;
    private int coilTier = -1; // -1 if no coils are present\
    // I know this is quite tricky, but... ahh... hmm...
    public static final TraceabilityPredicate COILS_OR_EVABED = new TraceabilityPredicate(blockWorldState -> {
        IBlockState blockState = blockWorldState.getBlockState();
        if (blockState == SuSyBlocks.EVAPORATION_BED.getDefaultState() || GregTechAPI.HEATING_COILS.containsKey(blockState)) {
            Object currentCoil = blockWorldState.getMatchContext().getOrPut("CoilType", blockState);
            if (blockState.equals(currentCoil)) {
                if (GregTechAPI.HEATING_COILS.containsKey(blockState)) {
                    blockWorldState.getMatchContext().getOrPut("VABlock", new LinkedList<>()).add(blockWorldState.getPos());
                }
                return true;
            }
        }
        return false;
    }, () -> GregTechAPI.HEATING_COILS.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().getTier()))
            .map(entry -> new BlockInfo(entry.getKey(), null)).toArray(BlockInfo[]::new)).addTooltips("gregtech.multiblock.pattern.error.coils")
            .or(new TraceabilityPredicate(blockWorldState -> false, () -> new BlockInfo[]{new BlockInfo(SuSyBlocks.EVAPORATION_BED.getDefaultState())}));




    public MetaTileEntityEvaporationPool(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.EVAPORATION_POOL);
        this.recipeMapWorkable = new EvapRecipeLogic(this);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityEvaporationPool(metaTileEntityId);
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.executor = Executors.newSingleThreadExecutor();
        this.lDist = 0;
        this.rDist = 0;
        this.bDist = 0;
        this.coilTier = -1;
        this.exposedBlocks = 0;
        this.tickTimer = 0;



        this.isHeated = false;
        this.areCoilsHeating = false;
        this.coilStateMeta = -1;

        this.wasExposed = new byte[0];
        this.kiloJoules = 0;
        this.isRecipeStalled = false;

        this.writeCustomData(coilDataID, (buf) -> {
            buf.writeBoolean(isHeated);
            buf.writeBoolean(areCoilsHeating);
            buf.writeInt(coilStateMeta);
        });

        this.writeCustomData(energyValuesID, buf -> {
            buf.writeByteArray(wasExposed);
            buf.writeInt(kiloJoules);
            buf.writeInt(tickTimer);
            buf.writeBoolean(isRecipeStalled);
        });
    }

    @SuppressWarnings("UnusedReturnValue") // Nobody cares
    public boolean updateStructureDimensions() {

        World world = getWorld();
        EnumFacing front = getFrontFacing();
        EnumFacing back = front.getOpposite();
        EnumFacing up = getUpwardsFacing();
        EnumFacing right = RelativeDirection.RIGHT.getRelativeFacing(front, up, isFlipped());
        EnumFacing left = right.getOpposite();

        BlockPos offPos = getPos().offset(back).offset(back);
        BlockPos.MutableBlockPos lPos = new BlockPos.MutableBlockPos(offPos);
        BlockPos.MutableBlockPos rPos = new BlockPos.MutableBlockPos(offPos);
        BlockPos.MutableBlockPos bPos = new BlockPos.MutableBlockPos(offPos);

        int lDist = 0;
        int rDist = 0;
        int bDist = 0;

        //find when container block section is exited left, right, and back
        for (int i = 1; i < MAX_SIZE; i++) {
            if (lDist == 0 && isBlockEdge(world, lPos, left)) lDist = i;
            if (rDist == 0 && isBlockEdge(world, rPos, right)) rDist = i;
            if (bDist == 0 && isBlockEdge(world, bPos, back)) bDist = i + 1; // since we pushed 1 block further
            if (lDist != 0 && rDist != 0 && bDist != 0) break;
        }

        //if width or length dist exceed max, or is less than min, invalidate structure
        int width = lDist + rDist;
        if (bDist < MIN_SIZE - 1 || width < MIN_SIZE - 1 || width > MAX_SIZE - 1) {
            invalidateStructure();
            return false;
        }

        //store the known dimensions for structure check
        this.lDist = lDist;
        this.rDist = rDist;
        this.bDist = bDist;

        writeCustomData(GregtechDataCodes.UPDATE_STRUCTURE_SIZE, buf -> {
            buf.writeInt(this.lDist);
            buf.writeInt(this.rDist);
            buf.writeInt(this.bDist);
        });
        return true; //successful formation
    }

    public boolean isBlockEdge(@NotNull World world, @NotNull BlockPos.MutableBlockPos pos,
                               @NotNull EnumFacing direction) {
        return world.getBlockState(pos.move(direction)) == getCasingState() || world.getTileEntity(pos) instanceof MetaTileEntityHolder; // Tbh I have 0 idea why ceu cleanroom doesn't need this.
    }

    public static IBlockState getCasingState() {
        return MetaBlocks.STONE_BLOCKS.get(StoneVariantBlock.StoneVariant.SMOOTH).getState(StoneVariantBlock.StoneType.CONCRETE_LIGHT);
    }

    public static TraceabilityPredicate evaporationBedPredicate() {
        return states(SuSyBlocks.EVAPORATION_BED.getDefaultState());
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.coilTier = context.get("CoilType") instanceof IHeatingCoilBlockStats coilType ? coilType.getTier() : -1;
        this.executor = Executors.newSingleThreadExecutor();
        updateExposedBlocks();
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        // return the default structure, even if there is no valid size found
        // this means auto-build will still work, and prevents terminal crashes.
        if (getWorld() != null) updateStructureDimensions();

        // these can sometimes get set to 0 when loading the game, breaking JEI (Apparently; text from cleanroom impl)
        if (lDist + rDist + 1 < MIN_SIZE) lDist = rDist = (MIN_SIZE - 1) / 2;
        if (bDist < MIN_SIZE - 1) bDist = MIN_SIZE - 1;

        int width = lDist + rDist + 1;
        int length = bDist + 1;

        // swap the left and right distances if the front facing is east or west
        // i guess allows BlockPattern checkPatternAt to get the correct relative position, somehow.
        if (this.frontFacing == EnumFacing.EAST || this.frontFacing == EnumFacing.WEST) {
            int tmp = lDist;
            lDist = rDist;
            rDist = tmp;
        }

        TraceabilityPredicate concretePredicate = states(getCasingState()).or(autoAbilities());

        FactoryBlockPattern pattern = FactoryBlockPattern.start()
                .aisle(RowTypes.EDGE.build(width), RowTypes.EMPTY.build(width))
                .aisle(RowTypes.EDGE.build(width), RowTypes.EDGE_2.build(width));

        for (int i = length - 2; i > 0; i -= 1) {
            pattern = switch (i % 4) {
                case 0 -> pattern.aisle(RowTypes.LEFT.build(width), RowTypes.INNER_2.build(width));
                case 1, 3 -> pattern.aisle(RowTypes.ALL.build(width), RowTypes.INNER_2.build(width));
                case 2 -> pattern.aisle(RowTypes.RIGHT.build(width), RowTypes.INNER_2.build(width));
                default -> pattern;
            };
        }

        return pattern.aisle(RowTypes.EDGE.build(width), RowTypes.EDGE_2.build(width))
                .aisle(RowTypes.EDGE.buildWithController(rDist, lDist), RowTypes.EMPTY.build(width))
                .where('S', selfPredicate())
                .where('C', concretePredicate)
                .where('B', evaporationBedPredicate())
                .where('H', COILS_OR_EVABED)
                .where(' ', any())
                .build();
    }

    // Overriding this to apply upper/lower limits to the hatches
    @Override
    public TraceabilityPredicate autoAbilities(boolean checkEnergyIn, boolean checkMaintenance, boolean checkItemIn, boolean checkItemOut, boolean checkFluidIn, boolean checkFluidOut, boolean checkMuffler) {
        TraceabilityPredicate predicate = super.autoAbilities(checkMaintenance, checkMuffler);
        if (checkEnergyIn) {
            predicate = predicate.or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(1));
        }

        if (checkItemIn && this.recipeMap.getMaxInputs() > 0) {
            predicate = predicate.or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(4).setPreviewCount(1));
        }

        if (checkItemOut && this.recipeMap.getMaxOutputs() > 0) {
            predicate = predicate.or(abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(4).setPreviewCount(1));
        }

        if (checkFluidIn && this.recipeMap.getMaxFluidInputs() > 0) {
            predicate = predicate.or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setMaxGlobalLimited(4).setPreviewCount(1));
        }

        if (checkFluidOut && this.recipeMap.getMaxFluidOutputs() > 0) {
            predicate = predicate.or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxGlobalLimited(4).setPreviewCount(1));
        }

        return predicate;
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
//        ArrayList<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
//        MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder()
//                .aisle("efFIEEEEEE", "          ")
//                .aisle("EEEEEEEEEE", " EEEEEEEE ").aisle("EECCCGCCEE", " E######E ")
//                .aisle("EECGCGCGEE", " E######E ").aisle("EECGCGCGEE", " E######E ")
//                .aisle("EECGCGCGEE", " E######E ").aisle("EECGCGCGEE", " E######E ")
//                .aisle("EECGCCCGEE", " E######E ").aisle("EEEEEEEEEE", " EEEEEEEE ")
//                .aisle("EEESEEEEEE", "          ")
//                .where('S', GregTechAPI.MTE_REGISTRY.getObjectById(EVAPORATION_POOL_ID), EnumFacing.SOUTH)
//                .where('E', MetaBlocks.STONE_BLOCKS.get(StoneVariantBlock.StoneVariant.SMOOTH).getState(StoneVariantBlock.StoneType.CONCRETE_LIGHT))
//                .where('G', SuSyBlocks.EVAPORATION_BED.getState(BlockEvaporationBed.EvaporationBedType.DIRT)).where('#', Blocks.AIR.getDefaultState())
//                .where(' ', Blocks.AIR.getDefaultState()) //supposed to be any
//                .where('e', MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.LV], EnumFacing.NORTH).where('f', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.LV], EnumFacing.NORTH)
//                .where('F', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.LV], EnumFacing.NORTH).where('I', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.LV], EnumFacing.NORTH);
//
//        GregTechAPI.HEATING_COILS.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().getTier())).forEach(entry -> shapeInfo.add(builder.where('C', entry.getKey()).build()));

        return super.getMatchingShapes(); // TODO
    }

    @Override
    public void checkStructurePattern() {
        if (!isStructureFormed() || structurePattern == null) {
            reinitializeStructurePattern();
        }
        super.checkStructurePattern();
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) { // TODO
        super.addInformation(stack, player, tooltip, advanced);
//        tooltip.add(I18n.format("gregtech.machine.evaporation_pool.tooltip.info", MAX_SQUARE_SIDE_LENGTH, MAX_SQUARE_SIDE_LENGTH));
//        if (TooltipHelper.isShiftDown()) {
//            tooltip.add(I18n.format("gregtech.machine.evaporation_pool.tooltip.structure_info", MAX_SQUARE_SIDE_LENGTH, MAX_SQUARE_SIDE_LENGTH) + "\n");
//        }
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
//        if (getWorld() != null) {
//            CCRenderState.instance().reset();
//            CCRenderState.instance().pullLightmap();
//            RenderUtils.renderFluidCuboid(water,
//                    Cuboid6.full,
//                    1, 0.8);
//        }
//        if (recipeMapWorkable.isActive() && isStructureFormed()) {
//            if (recipeMapWorkable != null) {
//
//                FluidStack fluidStack = ((EvapRecipeLogic) recipeMapWorkable).currentEvaporationFluid;
//                RenderUtils.renderFluidCuboidGL(fluidStack,
//                        Cuboid6.full,
//                        1, 0.8);


//                Matrix4 offset = translation.copy().translate(back.getXOffset(), 2, back.getZOffset());
////                Textures.RENDER_STATE.set(new CubeRendererState(op.layer, CubeRendererState.PASS_MASK, op.world));
//                renderState.setFluidColour(fluidStack);
//                renderState.setBrightness(getWorld(), getPos().offset(EnumFacing.UP, 2));
//                Textures.renderFace(renderState, offset, pipeline, EnumFacing.UP, Cuboid6.full, fluidStillSprite,
//                        BlockRenderLayer.CUTOUT_MIPPED);
//                GlStateManager.resetColor();
//            }
//        }
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
//        updateExposedBlocksInstantly();
        return super.createUI(entityPlayer);
    }

    @Override
    public void update() {
        super.update(); //means recipe logic happens before heating is added
        if (this.getWorld().isRemote) {
            if (this.isActive() && !isRecipeStalled) {
                //if world is clientside (remote from server) do custom rendering
                evaporationParticles();
            }

            return; //dont do logic on client
        }


        // Lazy updates
        if (this.tickTimer++ % 100 == 0) {
            if (isActive() && getWorld().isRemote) {
                updateExposedBlocks();
            } // TODO: THIS IS NOT A SOLUTION!!!
        }


//        setCoilActivity(false); // solves world issue reload where coils would be active even if multi was not running
//        if (structurePattern.getError() != null) return; //dont do processing for unformed multis
//
//        //ensure timer is non-negative by anding sign bit with 0
//        tickTimer = tickTimer & 0b01111111111111111111111111111111;
//        checkCoilActivity(); // make coils active if they should be
//        rollingAverage[tickTimer % 20] = 0; // reset rolling average for this tick index
//
//        //should skip/cost an extra tick the first time and then anywhere from 1-9 extra when rolling over. Determines exposedblocks
//        if (tickTimer % 10 == 0 && tickTimer != 0) {
//            //no sunlight heat generated when raining or during night. May be incongruent with partial exposure to sun, but oh well
//            if (getWorld().isRainingAt(getPos().offset(getFrontFacing().getOpposite(), 2)) || !getWorld().isDaytime()) {
//                exposedBlocks = 0;
//            }
//            //checks for ow and skylight access to prevent beneath portal issues (-1 = the Nether, 0 = normal world)
//            else if (getWorld().provider.getDimension() == 0) {
//                //tickTimer is always multiple of 20 at this point, so division by 20 yields proper counter. You can treat (tickTimer/20) as 'i'
//                int row = ((tickTimer / 20) / columnCount) % rowCount; //going left to right, further to closer checking skylight access.
//                int col = ((tickTimer / 20) % columnCount);
//                //places blockpos for skycheck into correct position. Row counts from furthest to closest (kinda inconsistent but oh well)
//                BlockPos.MutableBlockPos skyCheckPos = new BlockPos.MutableBlockPos(getPos().offset(EnumFacing.UP, 2));
//                skyCheckPos.move(getFrontFacing().getOpposite(), rowCount - row + 1);
//                skyCheckPos.move(getFrontFacing().rotateY(), controllerPosition); //move to the furthest left
//                skyCheckPos.move(getFrontFacing().rotateYCCW(), col); //traverse down row
//
//                if (wasExposed == null || wasExposed.length != rowCount * columnCount) {
//                    wasExposed = new byte[rowCount * columnCount];
//                    exposedBlocks = 0;
//                    SusyLog.logger.atError().log("Detected evaporation pool with invalid wasExposed array at pos: " + this.getPos() + "; setting explosed blocks to 0");
//                }
//
//                //Perform skylight check
//                if (!getWorld().canBlockSeeSky(skyCheckPos)) {
//                    //only decrement exposedBlocks if previously exposed block is found to no longer be exposed and one full pass has occurred
//                    if (wasExposed[(row * columnCount) + col] != 0 && tickTimer / 20 > rowCount * columnCount) {
//                        exposedBlocks = Math.max(0, exposedBlocks - 1);
//                        wasExposed[(row * columnCount) + col] = 0;
//                    }
//                } else {
//                    //only increment if block was not previously exposed
//                    if (wasExposed[(row * columnCount) + col] == 0) {
//                        if (exposedBlocks < rowCount * columnCount) ++exposedBlocks;
//                        wasExposed[(row * columnCount) + col] = 1;
//                    }
//                }
//            }
//
//        } //finish once a ~second check
//
//        inputEnergy(exposedBlocks * 50); //1kJ/s /m^2 -> 50J/t
//
//        //convert joules in buffer to kJ
//        if (joulesBuffer >= 1000) {
//            int tempBuffer = joulesBuffer;
//            joulesBuffer = 0;
//            //if energy was not stored into kiloJoules, place everything back into buffer manually
//            if (!inputEnergy(tempBuffer)) joulesBuffer = tempBuffer;
//        }
//
//        ++tickTimer;
//
//        //store relevant values
////        writeCustomData(energyValuesID, buf -> {
////            buf.writeInt(exposedBlocks);
////            buf.writeByteArray(wasExposed);
////            buf.writeInt(kiloJoules);
////            buf.writeInt(tickTimer);
////            buf.writeBoolean(isRecipeStalled);
////        });
    }

    // Will probably be called when a player opens GUI, idk if I need this
    @SideOnly(Side.SERVER)
    public void updateExposedBlocksInstantly() {
        if (getWorld().isRemote || variantActiveBlocks.isEmpty()) return;
        this.exposedBlocks = (int) variantActiveBlocks.parallelStream()
                .filter(pos -> GTUtility.canSeeSunClearly(getWorld(), pos))
                .count();
    }

    @SideOnly(Side.SERVER)
    public void updateExposedBlocks() {
        executor.execute(() -> {
            if (getWorld() != null) {
                if (getWorld().isRemote || variantActiveBlocks.isEmpty()) return;
                this.exposedBlocks = (int) variantActiveBlocks.parallelStream()
                        .filter(pos -> {
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            return GTUtility.canSeeSunClearly(getWorld(), pos);
                        })
                        .count();
            }
        });
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.builder(textList, isStructureFormed()).setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive()).addEnergyUsageLine(getEnergyContainer()).addCustom(tl -> {
            // coil coefficient
            if (isStructureFormed()) {
                // handle heating contributions
                if (isHeated()) {
                    tl.add(TextComponentUtil.translationWithColor(TextFormatting.WHITE, "gregtech.top.evaporation_pool_heated_preface").appendText(" ").appendSibling(TextComponentUtil.translationWithColor(TextFormatting.GREEN, "gregtech.top.evaporation_pool_is_heated")));

                } else {
                    tl.add(TextComponentUtil.translationWithColor(TextFormatting.WHITE, "gregtech.top.evaporation_pool_heated_preface").appendText(" ").appendSibling(TextComponentUtil.translationWithColor(TextFormatting.RED, "gregtech.top.evaporation_pool_not_heated")));
                }

                tl.add(TextComponentUtil.translationWithColor(TextFormatting.WHITE, "gregtech.multiblock.evaporation_pool.exposed_blocks").appendText(" ").appendSibling(TextComponentUtil.translationWithColor(TextFormatting.GREEN, TextFormattingUtil.formatNumbers(exposedBlocks))));

                tl.add(TextComponentUtil.translationWithColor(TextFormatting.WHITE, "gregtech.top.evaporation_pool.energy_transferred").appendText(" ").appendSibling(TextComponentUtil.stringWithColor(TextFormatting.YELLOW, TextFormattingUtil.formatNumbers(this.getKiloJoules())).appendText(".").appendSibling(TextComponentUtil.stringWithColor(TextFormatting.YELLOW, EvaporationPoolInfoProvider.constLengthToString(this.getJoulesBuffer()))).appendText(" ").appendSibling(TextComponentUtil.translationWithColor(TextFormatting.WHITE, "gregtech.top.evaporation_pool.kilojoules"))));

                tl.add(TextComponentUtil.translationWithColor(TextFormatting.WHITE, "gregtech.mutliblock.evaporation_pool.rolling_average").appendText(" ").appendSibling(TextComponentUtil.stringWithColor(TextFormatting.YELLOW, TextFormattingUtil.formatNumbers(getRollingAverageJt()))).appendText(" ").appendSibling(TextComponentUtil.translationWithColor(TextFormatting.WHITE, "gregtech.multiblock.evaporation_pool.joules_per_tick")));

                tl.add(TextComponentUtil.translationWithColor(TextFormatting.WHITE, "gregtech.multiblock.evaporation_pool.average_speed").appendText(" ").appendSibling(TextComponentUtil.stringWithColor(TextFormatting.GREEN, getAverageRecipeSpeedString())).appendSibling(TextComponentUtil.stringWithColor(TextFormatting.WHITE, "x"))); // add empty space to visually separate evap pool custom stats
            }
        }).addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage())).addParallelsLine(recipeMapWorkable.getParallelLimit()).addWorkingStatusLine().addProgressLine(recipeMapWorkable.getProgressPercent());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("lDist", lDist);
        data.setInteger("rDist", rDist);
        data.setInteger("bDist", bDist);
        data.setInteger("exposedBlocks", exposedBlocks);




        data.setBoolean("isHeated", this.isHeated);
        data.setBoolean("areCoilsHeating", this.areCoilsHeating);
        data.setInteger("kiloJoules", this.kiloJoules);
        data.setInteger("tickTimer", this.tickTimer);
        data.setBoolean("isRecipeStalled", this.isRecipeStalled);
        data.setInteger("coilStateMeta", this.coilStateMeta);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.lDist = data.hasKey("lDist") ? data.getInteger("lDist") : lDist;
        this.rDist = data.hasKey("rDist") ? data.getInteger("rDist") : rDist;
        this.bDist = data.hasKey("bDist") ? data.getInteger("bDist") : bDist;
        reinitializeStructurePattern();

        this.exposedBlocks = data.hasKey("exposedBlocks") ? data.getInteger("exposedBlocks") : exposedBlocks;





        if (data.hasKey("isHeated")) {
            this.isHeated = data.getBoolean("isHeated");
        }
        if (data.hasKey("areCoilsHeating")) {
            this.areCoilsHeating = data.getBoolean("areCoilsHeating");
        }
        if (data.hasKey("wasExposed")) {
            this.wasExposed = data.getByteArray("wasExposed");
        }
        if (data.hasKey("kiloJoules")) {
            this.kiloJoules = data.getInteger("kiloJoules");
        }
        if (data.hasKey("isRecipeStalled")) {
            this.isRecipeStalled = data.getBoolean("isRecipeStalled");
        }
        if (data.hasKey("coilStateMeta")) {
            this.coilStateMeta = data.getInteger("coilStateMeta");
        }
        if (rollingAverage == null) rollingAverage = new int[20];
        reinitializeStructurePattern();
    }

    //order matters for these
    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(lDist);
        buf.writeInt(rDist);
        buf.writeInt(bDist);
        buf.writeInt(exposedBlocks);




        buf.writeBoolean(this.isHeated);
        buf.writeBoolean(this.areCoilsHeating);
        buf.writeInt(this.kiloJoules);
        buf.writeBoolean(this.isRecipeStalled);
        buf.writeInt(this.coilStateMeta);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.lDist = buf.readInt();
        this.rDist = buf.readInt();
        this.bDist = buf.readInt();




        this.isHeated = buf.readBoolean();
        this.areCoilsHeating = buf.readBoolean();
        this.exposedBlocks = buf.readInt();
        this.wasExposed = buf.readByteArray();
        this.kiloJoules = buf.readInt();
        this.isRecipeStalled = buf.readBoolean();
        this.coilStateMeta = buf.readInt();

        if (rollingAverage == null) rollingAverage = new int[20];
        reinitializeStructurePattern();
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.UPDATE_STRUCTURE_SIZE) {
            this.lDist = buf.readInt();
            this.rDist = buf.readInt();
            this.bDist = buf.readInt();




        } else if (dataId == coilDataID) {
            this.isHeated = buf.readBoolean();
            this.areCoilsHeating = buf.readBoolean();
            this.coilStateMeta = buf.readInt();

        } else if (dataId == energyValuesID) {
            this.exposedBlocks = buf.readInt();
            this.wasExposed = buf.readByteArray();
            this.kiloJoules = buf.readInt();
            this.isRecipeStalled = buf.readBoolean();
        }
    }

    @SideOnly(Side.CLIENT)
    private void evaporationParticles() { // TODO
//        final EnumFacing back = this.getFrontFacing().getOpposite();
//        final EnumFacing left = back.rotateYCCW();
//
//        //conversion from blockpos to in world pos places particle position at corner of block such that relative direction must be accounted for
//        //add 1 if x pos or z pos (offsets mutually exclusively non-zero for given facing) as conversion to float pos rounds down in both coords
//        int leftOffset = ((left.getXOffset() * -1) >>> 31) + ((left.getZOffset() * -1) >>> 31);
//        int backOffset = ((back.getXOffset() * -1) >>> 31) + ((back.getZOffset() * -1) >>> 31);
//
//        //place pos in closest leftmost corner
//        final BlockPos pos = this.getPos().offset(left, controllerPosition + leftOffset).offset(back, 1 + backOffset);
//
//        //Spawn number of particles on range [1, 3 * colCount * rowCount/9] (~3 particles for every 3x3 = 9m^2)
//        for (int i = 0; i < Math.max(1, columnCount * rowCount / 3); i++) {
//            //either single line along x axis intersects all rows, or it intersects all columns. Same applies to z axis. getXOffset indicates +, -, or "0" direction of facing for given axis
//            float xLength = (back.getXOffset() * rowCount) + (left.getXOffset() * columnCount * -1); //we start on leftmost closest corner and want to go back and to the right
//            float zLength = (back.getZOffset() * rowCount) + (left.getZOffset() * columnCount * -1); //so we invert the sign of the left offsets to effectively get right displacement
//
//            //if (tickTimer % 100 == 0) SusyLog.logger.atError().log("xLength: " + xLength + ", zLength: " + zLength + ", leftOffset: " + leftOffset + ", backOffset: " + backOffset + ", pos: " + pos + ", controller pos: " + getPos());
//
//            float xPos = pos.getX() + (xLength * GTValues.RNG.nextFloat()); //scale x length by random amount to get output coord
//            float yPos = pos.getY() + 0.75F; //shit out particles one quarter of a block below the surface of the interior to give effect of gases rising from bottom
//            float zPos = pos.getZ() + (zLength * GTValues.RNG.nextFloat());
//
//            float ySpd = 0.4F + 0.2F * GTValues.RNG.nextFloat();
//            getWorld().spawnParticle(EnumParticleTypes.CLOUD, xPos, yPos, zPos, 0, ySpd, 0);
//        }
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.BLAST_FURNACE_OVERLAY;
    }

    public int getRollingAverageJt() {
        // sunlight => 1kJ/s/m^2 -> 50J/t/m^2
        return exposedBlocks * 50 + Arrays.stream(rollingAverage).sum() / 20;
    }

    public float getAverageRecipeSpeed() {
        if (!recipeMapWorkable.isActive() || recipeMapWorkable.getPreviousRecipe() == null) return 0;
        float recipeJt = recipeMapWorkable.getPreviousRecipe().getProperty(EvaporationEnergyProperty.getInstance(), -1);
        return (exposedBlocks * 50 + Arrays.stream(rollingAverage).sum() / 20F) / recipeJt;
    }

    public String getAverageRecipeSpeedString() {
        return Float.toString(((int) (getAverageRecipeSpeed() * 100) / 100F));
    }

    @Override
    protected void replaceVariantBlocksActive(boolean isActive) { // TODO
//        super.replaceVariantBlocksActive(isActive && isRunningHeated());
        super.replaceVariantBlocksActive(isActive);
    }

    public boolean isHeated() {
        return isHeated;
    }

    public boolean inputEnergy(int joules) {
        // TODO
//        //limit amount of energy stored
//        final int CUBE_HEAT_CAPACITY = 100; //kJ/m^3
//        if (getKiloJoules() > CUBE_HEAT_CAPACITY * columnCount * rowCount) {
//            return false;
//        }
//
//        int kJ = joules / 1000;
//        joules -= kJ * 1000;
//        joulesBuffer += joules;
//
//        //store kJ
//        setKiloJoules(getKiloJoules() + kJ);
//        rollingAverage[tickTimer % 20] += joules + 1000 * kJ;
        return true;
    }

    public int calcMaxSteps(int jStepSize) {
        int stepCount = (getKiloJoules() * 1000) / jStepSize; //max number of times jStepSize can cleanly be deducted from kiloJoules
        int remainder = (stepCount + 1) * jStepSize - getKiloJoules() * 1000; //remaining joules needed to not waste partial kJ

        if (joulesBuffer >= remainder) ++stepCount;
        else remainder = 0;

        stepCount += (joulesBuffer - remainder) / jStepSize; //number of jSteps which can come entirely from joulesBuffer
        return stepCount;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean getIsWeatherOrTerrainResistant() {
        return true;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    private enum RowTypes {

        // 'C' for concrete or autoAbilities (concrete-only when used for preview),
        // 'B' for evaporation bed, 'H' for heating coil, 'S' for controller, 'A' for air, ' ' for any other block
        // TODO: add JEI-only row types

        // For example, a 9 x 9 evaporation pool with coils would look like this:

        // First layer:
        // CCCCCCCCC <- EDGE
        // CCCCCCCCC <- EDGE
        // CCHHHHHCC <- ALL
        // CCHBBBBCC <- LEFT
        // CCHHHHHCC <- ALL
        // CCBBBBHCC <- RIGHT
        // CCHHHHHCC <- ALL
        // CCCCCCCCC <- EDGE
        // CCCCSCCCC <- EDGE (with @RowTypes#buildWithController())

        // Second layer:
        //           <- EMPTY
        //  CCCCCCC  <- EDGE_2
        //  C     C  <- INNER_2
        //  C     C  <- INNER_2
        //  C     C  <- INNER_2
        //  C     C  <- INNER_2
        //  C     C  <- INNER_2
        //  CCCCCCC  <- EDGE_2
        //           <- EMPTY

        // rows 1st layer
        EDGE('C', 'C', 'C', 'C', 'C'),    // CCCCCCCCC
        LEFT('C', 'C', 'H', 'B', 'B'),    // CCHBBBBCC
        ALL('C', 'C', 'H', 'H', 'H'),     // CCHHHHHCC
        RIGHT('C', 'C', 'B', 'H', 'B'),   // CCBBBBHCC
        NONE('C', 'C', 'B', 'B', 'B'),    // CCBBBBBCC

        // row for 2nd layer
        EDGE_2(' ', 'C', 'C', 'C', 'C'),  //  CCCCCCC
        // should I set this to air()?
        INNER_2(' ', 'C', ' ', ' ', ' '), //  C     C
        EMPTY(' ', ' ', ' ', ' ', ' ');  //

        // rows for JEI only
        // TODO

        private final char outerBorder;
        private final char innerBorder;
        private final char leftEdge;
        private final char rightEdge;
        private final char fill;

        RowTypes(char outerBorder, char innerBorder, char leftEdge, char rightEdge, char fill) {
            this.outerBorder = outerBorder;
            this.innerBorder = innerBorder;
            this.leftEdge = leftEdge;
            this.rightEdge = rightEdge;
            this.fill = fill;
        }

        public String build(int width) { // the width here also indicates the width of the 2nd layer
            StringBuilder builder = new StringBuilder();
            builder.append(outerBorder)
                    .append(innerBorder)
                    .append(leftEdge);
            for (int i = 0; i < Math.max(0, width - 4); i++) builder.append(fill);
            builder.append(rightEdge)
                    .append(innerBorder)
                    .append(outerBorder);
            return builder.toString();
        }

        public String buildWithController(int lDist, int rDist) {
            if (this != EDGE) throw new IllegalStateException("This method should only be called on EDGE rowType!");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < Math.max(0, lDist + 1); i++) builder.append(fill);
            builder.append('S');
            for (int i = 0; i < Math.max(0, rDist + 1); i++) builder.append(fill);
            return builder.toString();
        }
    }
}
