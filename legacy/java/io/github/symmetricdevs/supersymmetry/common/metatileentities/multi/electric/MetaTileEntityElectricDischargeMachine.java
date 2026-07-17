package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
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

import com.gregtechceu.gtceu.common.blocks.BlockGlassCasing;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.metatileentities.MetaTileEntities;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockEDMElectrode;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.SuSyMetaTileEntities;

public class MetaTileEntityElectricDischargeMachine extends WorkableElectricMultiblockMachine {

    public MetaTileEntityElectricDischargeMachine(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.EDM_RECIPES);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityElectricDischargeMachine(this.metaTileEntityId);
    }

    @NotNull
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCCC", "CCCCC", "CCCCC", "CCCCC", " CCC ")
                .aisle("CCCCC", "C C C", "C E C", "C C C", " CCC ")
                .aisle("CCCCC", "C   C", "C   C", "C   C", " CCC ")
                .aisle(" CSC ", " GGG ", " GGG ", " CCC ", "     ")
                .where('S', selfPredicate())
                .where('C',
                        states(GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                                .getState(BlockLargeMultiblockCasing.CasingType.NONCONDUCTING_CASING))
                                        .setMinGlobalLimited(45)
                                        .or(abilities(MultiblockAbility.INPUT_ENERGY)
                                                .setMinGlobalLimited(1).setMaxGlobalLimited(2))
                                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH)
                                                .setExactLimit(1))
                                        .or(abilities(MultiblockAbility.IMPORT_ITEMS)
                                                .setMinGlobalLimited(1))
                                        .or(abilities(MultiblockAbility.EXPORT_ITEMS)
                                                .setMinGlobalLimited(1))
                                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS)
                                                .setMinGlobalLimited(1))
                                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS)))
                .where('E', states(getElectrodeState()))
                .where('G', states(getGlassState()))
                .build();
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        ArrayList<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        MultiblockShapeInfo.Builder baseBuilder = MultiblockShapeInfo.builder()
                .where('S', SuSyMetaTileEntities.ELECTRIC_DISCHARGE_MACHINE, Direction.SOUTH)
                .where('C', getCasingState())
                .where('E', getElectrodeState())
                .where('G', getGlassState())
                .where('F', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.LV], Direction.SOUTH)
                .where('I', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.LV], Direction.SOUTH)
                .where('O', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.LV], Direction.SOUTH)
                .where('N', MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.HV], Direction.SOUTH)
                .where('M',
                        () -> ConfigHolder.machines.enableMaintenance ? MetaTileEntities.MAINTENANCE_HATCH :
                                getCasingState(),
                        Direction.SOUTH);
        shapeInfo.add(baseBuilder.shallowCopy()
                .aisle("CCCCC", "CCCCC", "CCCCC", "CCCCC", " CCC ")
                .aisle("CCCCC", "C C C", "C E C", "C C C", " CCC ")
                .aisle("MCCCN", "F   C", "C   C", "C   C", " CCC ")
                .aisle(" ISO ", " GGG ", " GGG ", " CCC ", "     ")
                .build());
        return shapeInfo;
    }

    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart instanceof IMultiblockAbilityPart<?>) {
            MultiblockAbility<?> ability = ((IMultiblockAbilityPart<?>) sourcePart).getAbility();
        }
        return GCYMTextures.NONCONDUCTING_CASING;
    }

    protected static BlockState getCasingState() {
        return GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                .getState(BlockLargeMultiblockCasing.CasingType.NONCONDUCTING_CASING);
    }

    protected static BlockState getElectrodeState() {
        return SuSyBlocks.EDM_ELECTRODE.getState(BlockEDMElectrode.ElectrodeType.COPPER_TUNGSTEN);
    }

    protected static BlockState getGlassState() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.LAMINATED_GLASS);
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return SusyTextures.EDM_OVERLAY;
    }
}
