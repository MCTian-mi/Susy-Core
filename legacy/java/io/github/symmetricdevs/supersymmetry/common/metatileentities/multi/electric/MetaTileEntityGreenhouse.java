package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static com.gregtechceu.gtceu.api.util.RelativeDirection.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.capability.GregtechDataCodes;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.block.CasingBlock;
import com.gregtechceu.gtceu.common.data.GTMachines;
import gregtechfoodoption.block.GTFOGlassCasing;
import gregtechfoodoption.block.GTFOMetaBlocks;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;

public class MetaTileEntityGreenhouse extends WorkableElectricMultiblockMachine {

    public static final int MAX_LENGTH = 25;
    private int cellCount;
    private int length;

    public MetaTileEntityGreenhouse(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.GREENHOUSE_PLANT);
        this.recipeMapWorkable = new GreenhouseRecipeLogic(this);
    }

    protected boolean updateStructureDimensions() {
        World world = getWorld();
        Direction back = getFrontFacing().getOpposite();
        BlockPos.MutableBlockPos bPos = new BlockPos.MutableBlockPos(getPos());

        int length = 0;

        for (int i = 1; i <= MAX_LENGTH; i++) {
            if (isBlockEdge(world, bPos, back)) {
                length = i;
                break;
            }
        }

        if (length < 4 || (length % 4) != 0) {
            invalidateStructure();
            return false;
        }

        if (!this.getWorld().isRemote) {
            writeCustomData(GregtechDataCodes.UPDATE_STRUCTURE_SIZE, buf -> {
                buf.writeInt(this.length);
            });
        }

        int oldLength = this.length;
        this.length = length;
        this.cellCount = length / 4;
        return !isStructureFormed() || this.length != oldLength;
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag data) {
        data.setInteger("cellCount", this.cellCount);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.cellCount = data.getInteger("cellCount");
    }

    @Override
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.UPDATE_STRUCTURE_SIZE) {
            this.length = buf.readInt();
        }
    }

    @Override
    public void checkStructurePattern() {
        if (updateStructureDimensions()) {
            reinitializeStructurePattern();
        }
        super.checkStructurePattern();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityGreenhouse(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        if (getWorld() != null) {
            updateStructureDimensions();
        }
        if (cellCount < 1) {
            cellCount = 1;
        }
        return createStructurePattern(this.cellCount);
    };

    protected @NotNull BlockPattern createStructurePattern(int cells) {
        var builder = FactoryBlockPattern.start(RIGHT, UP, FRONT);

        builder.aisle("CCSCC", "FGGGF", "FGGGF", " FFF ");
        builder.aisle("CDDDC", "G###G", "G###G", " GGG ");
        builder.aisle("CDDDC", "G###G", "G###G", " GGG ");
        builder.aisle("CDDDC", "G###G", "G###G", " GGG ");

        for (int i = 1; i < cells; i++) {
            builder.aisle("CDDDC", "F###F", "F###F", " FFF ");
            builder.aisle("CDDDC", "G###G", "G###G", " GGG ");
            builder.aisle("CDDDC", "G###G", "G###G", " GGG ");
            builder.aisle("CDDDC", "G###G", "G###G", " GGG ");
        }
        return builder
                .aisle("CCCCC", "FGGGF", "FGGGF", " FFF ")
                .where('S', selfPredicate())
                .where('C', states(getCasingState()).or(this.autoAbilities()))
                .where('D', states(Blocks.DIRT.getDefaultState(), Blocks.GRASS.getDefaultState()))
                .where('G', states(getGlassState()))
                .where('F', frames(Materials.Steel))
                .where(' ', any())
                .where('#', air())
                .build();
    }

    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.SOLID_STEEL_CASING;
    }

    protected static BlockState getGlassState() {
        return GTFOMetaBlocks.GTFO_GLASS_CASING.getState(GTFOGlassCasing.CasingType.GREENHOUSE_GLASS);
    }

    protected static BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    public boolean isBlockEdge(@Nonnull World world, @Nonnull BlockPos.MutableBlockPos pos,
                               @Nonnull Direction direction) {
        pos.move(direction);
        return world.getBlockState(pos) == getCasingState() ||
                GTUtility.getMetaTileEntity(world, pos) instanceof IMultiblockPart;
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        List<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            BlockPattern pattern = createStructurePattern(i);
            int[] repetition = new int[pattern.aisleRepetitions.length];
            Arrays.fill(repetition, 1);
            shapeInfo.add(new MultiblockShapeInfo(pattern.getPreview(repetition)));
        }
        return shapeInfo;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(ChatFormatting.GRAY + I18n.format("susy.machine.greenhouse.tooltip.1"));
        tooltip.add(ChatFormatting.GRAY + I18n.format("susy.machine.greenhouse.tooltip.2"));
    }

    @Override
    public boolean isMultiblockPartWeatherResistant(@Nonnull IMultiblockPart part) {
        return true;
    }

    @Override
    public boolean getIsWeatherOrTerrainResistant() {
        return true;
    }

    private class GreenhouseRecipeLogic extends RecipeLogic {

        public GreenhouseRecipeLogic(WorkableElectricMultiblockMachine BlockEntity) {
            super(BlockEntity);
        }

        public int getParallelLimit() {
            return cellCount;
        }
    }
}
