package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyPredicates.conveyorBelts;

import java.util.*;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import org.apache.commons.lang3.tuple.Pair;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.*;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;

import com.gregtechceu.gtceu.common.blocks.*;
import com.gregtechceu.gtceu.common.metatileentities.MetaTileEntities;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockConveyor;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.SuSyMetaTileEntities;

public class MetaTileEntityCurtainCoater extends WorkableElectricMultiblockMachine {

    private final List<Pair<BlockPos, RelativeDirection>> conveyorBlocks = new ArrayList<>();

    public MetaTileEntityCurtainCoater(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.CURTAIN_COATER);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityCurtainCoater(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCKCC", "CWGWC", "  G  ")
                .aisle("CCCCC", "I>>>O", "CCGCC")
                .aisle("CCSCC", "CWHWC", "  G  ")
                .where('S', selfPredicate())
                .where('I', abilities(MultiblockAbility.IMPORT_ITEMS))
                .where('O', abilities(MultiblockAbility.EXPORT_ITEMS))
                .where('H', abilities(MultiblockAbility.IMPORT_FLUIDS))
                .where('K', abilities(MultiblockAbility.EXPORT_FLUIDS))
                .where('C', states(getCasingState()).setMinGlobalLimited(17)
                        .or(autoAbilities(true, true, false, false, false, false, false)))
                .where('G', states(getGearBoxState()))
                .where('W', states(getGlassState()))
                .where('>', conveyorBelts(RelativeDirection.LEFT))
                .where(' ', any())
                .build();
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        ArrayList<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        MultiblockShapeInfo.Builder baseBuilder = MultiblockShapeInfo.builder()
                .where('S', SuSyMetaTileEntities.CURTAIN_COATER, Direction.SOUTH)
                .where('C', getCasingState())
                .where('I', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.LV], Direction.WEST)
                .where('i', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.LV], Direction.EAST)
                .where('O', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.LV], Direction.WEST)
                .where('o', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.LV], Direction.EAST)
                .where('H', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.LV], Direction.SOUTH)
                .where('K', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.LV], Direction.NORTH)
                .where('E', MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.LV], Direction.NORTH)
                .where('G', getGearBoxState())
                .where('W', getGlassState())
                .where('>',
                        SuSyBlocks.CONVEYOR_BELT.getDefaultState().withProperty(BlockConveyor.FACING, Direction.EAST))
                .where('<',
                        SuSyBlocks.CONVEYOR_BELT.getDefaultState().withProperty(BlockConveyor.FACING, Direction.WEST))
                .where('M',
                        () -> ConfigHolder.machines.enableMaintenance ? MetaTileEntities.MAINTENANCE_HATCH :
                                getCasingState(),
                        Direction.SOUTH);
        shapeInfo.add(baseBuilder.shallowCopy()
                .aisle("CCKCC", "CWGWC", "  G  ")
                .aisle("CCCCC", "I>>>o", "CCGCC")
                .aisle("CESMC", "CWHWC", "  G  ")
                .build());
        shapeInfo.add(baseBuilder.shallowCopy()
                .aisle("CCCCC", "CWGWC", "  G  ")
                .aisle("CCCCC", "O<<<i", "CCGCC")
                .aisle("CESMC", "CWHWC", "  G  ")
                .build());
        return shapeInfo;
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        conveyorBlocks.addAll(context.getOrDefault("ConveyorBelt", new LinkedList<>()));
    }

    public void invalidateStructure() {
        super.invalidateStructure();
        this.conveyorBlocks.clear();
    }

    protected void updateFormedValid() {
        super.updateFormedValid();

        World world = getWorld();
        for (Pair<BlockPos, RelativeDirection> posDirPair : conveyorBlocks) {
            // RelativeDirection will take into account of the multi flipping pattern
            Direction conveyorFacing = posDirPair.getRight().getRelativeFacing(getFrontFacing(), getUpwardsFacing(),
                    isFlipped());

            BlockPos blockPos = posDirPair.getLeft();
            BlockState blockState = world.getBlockState(blockPos);
            Block conveyor = blockState.getBlock();
            if (conveyor instanceof BlockConveyor && blockState.getValue(BlockConveyor.FACING) != conveyorFacing) {
                world.setBlockState(blockPos, blockState.withProperty(BlockConveyor.FACING, conveyorFacing));
            }
        }
    }

    protected BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STAINLESS_CLEAN);
    }

    protected BlockState getGearBoxState() {
        return MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STAINLESS_STEEL_GEARBOX);
    }

    protected static BlockState getGlassState() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.CLEAN_STAINLESS_STEEL_CASING;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.BLAST_FURNACE_OVERLAY;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    public boolean allowsFlip() {
        return true;
    }
}
