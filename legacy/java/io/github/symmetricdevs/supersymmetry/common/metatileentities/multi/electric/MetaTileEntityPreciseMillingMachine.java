package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;

import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;

import com.gregtechceu.gtceu.common.blocks.BlockGlassCasing;
import com.gregtechceu.gtceu.common.block.CasingBlock.MetalCasingType;
import com.gregtechceu.gtceu.common.blocks.BlockTurbineCasing;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.metatileentities.MetaTileEntities;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockDrillBit;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.SuSyMetaTileEntities;

public class MetaTileEntityPreciseMillingMachine extends WorkableElectricMultiblockMachine {

    public MetaTileEntityPreciseMillingMachine(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.MILLING_RECIPES);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityPreciseMillingMachine(this.metaTileEntityId);
    }

    @NotNull
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("BBBBBB", "CCCCCC", "CGGGGC", "CCCCCC")
                .aisle("BBBBBB", "C    C", "CDDDDC", "CCCCCC")
                .aisle("BBBBBB", "C    C", "C    C", "CCCCCC")
                .aisle("BBBBBB", "CWWWWS", "CWWWWC", "CCCCCC")
                .where('S', selfPredicate())
                .where('B', states(getBaseCasingState()).setMinGlobalLimited(18)
                        .or(abilities(MultiblockAbility.INPUT_ENERGY)
                                .setMinGlobalLimited(1).setMaxGlobalLimited(2)
                                .addTooltip("susy.multiblock.pattern.error.milling.lower"))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH)
                                .setExactLimit(1)
                                .addTooltip("susy.multiblock.pattern.error.milling.lower")))
                .where('C', states(getUpperCasingState()).setMinGlobalLimited(35)
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS)
                                .setMinGlobalLimited(1)
                                .addTooltip("susy.multiblock.pattern.error.milling.upper"))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS)
                                .setMinGlobalLimited(1)
                                .addTooltip("susy.multiblock.pattern.error.milling.upper")))
                .where('D', states(getDrillBitState()))
                .where('G', states(getGearBoxState()))
                .where('W', states(getGlassState()))
                .build();
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        ArrayList<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        MultiblockShapeInfo.Builder baseBuilder = MultiblockShapeInfo.builder()
                .where('S', SuSyMetaTileEntities.MILLING, Direction.SOUTH)
                .where('B', getBaseCasingState())
                .where('C', getUpperCasingState())
                .where('D', getDrillBitState())
                .where('G', getGearBoxState())
                .where('W', getGlassState())
                .where('I', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.HV], Direction.SOUTH)
                .where('O', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.HV], Direction.SOUTH)
                .where('E', MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.HV], Direction.SOUTH)
                .where('G', getGearBoxState())
                .where('M',
                        () -> ConfigHolder.machines.enableMaintenance ? MetaTileEntities.MAINTENANCE_HATCH :
                                getBaseCasingState(),
                        Direction.SOUTH);
        shapeInfo.add(baseBuilder.shallowCopy()
                .aisle("BBBBBB", "CCCCCC", "CGGGGC", "CCCCCC")
                .aisle("BBBBBB", "C    C", "CDDDDC", "CCCCCC")
                .aisle("BBBBBB", "C    C", "C    C", "CCCCCC")
                .aisle("BBBBEM", "OWWWWS", "IWWWWC", "CCCCCC")
                .build());
        return shapeInfo;
    }

    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart instanceof IMultiblockAbilityPart<?>) {
            MultiblockAbility<?> ability = ((IMultiblockAbilityPart<?>) sourcePart).getAbility();
            if (ability.equals(MultiblockAbility.MAINTENANCE_HATCH) || ability.equals(MultiblockAbility.INPUT_ENERGY)) {
                return Textures.SOLID_STEEL_CASING;
            }
        }
        // for IO Buses and other unrecognized parts
        return Textures.CLEAN_STAINLESS_STEEL_CASING;
    }

    protected static BlockState getBaseCasingState() {
        return MetaBlocks.METAL_CASING.getState(MetalCasingType.STEEL_SOLID);
    }

    protected static BlockState getUpperCasingState() {
        return MetaBlocks.METAL_CASING.getState(MetalCasingType.STAINLESS_CLEAN);
    }

    protected static BlockState getDrillBitState() {
        return SuSyBlocks.DRILL_BIT.getState(BlockDrillBit.DrillBitType.STEEL);
    }

    protected static BlockState getGlassState() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS);
    }

    protected static BlockState getGearBoxState() {
        return MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STAINLESS_STEEL_GEARBOX);
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return SusyTextures.MILLING_OVERLAY;
    }
}
