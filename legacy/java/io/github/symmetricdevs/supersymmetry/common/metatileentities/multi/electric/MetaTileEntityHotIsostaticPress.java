package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static io.github.symmetricdevs.supersymmetry.api.blocks.VariantDirectionalRotatableBlock.FACING;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.unification.material.Material;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.blocks.BlockBoilerCasing;
import com.gregtechceu.gtceu.common.blocks.BlockWireCoil;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockMetallurgy;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockSuSyMultiblockCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class MetaTileEntityHotIsostaticPress extends WorkableElectricMultiblockMachine {

    public MetaTileEntityHotIsostaticPress(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.HOT_ISOSTATIC_PRESS);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("  SSS  ", "  SFS  ", "  SFS  ", "  SFS  ", "  SFS  ", "  SFS  ", "  SSS  ")
                .aisle(" SSSSS ", " SIIIS ", " SIIIS ", " SIIIS ", " SIIIS ", " SIIIS ", " SSSSS ")
                .aisle("SSSSSSS", "SIIIIIS", "SICCCIS", "SICCCIS", "SICCCIS", "SIIIIIS", "SSSSSSS")
                .aisle("SSSPSSS", "SIIHIIS", "SICXCIS", "SICXCIS", "SICXCIS", "SIIhIIS", "SSSPSSS")
                .aisle("SSSPSSS", "SIIIIIS", "SICXCIS", "SICXCIS", "SICXCIS", "SIIPIIS", "SSSPSSS")
                .aisle(" SSPSS ", " SIIIS ", " SIIIS ", " SIIIS ", " SIIIS ", " SIPIS ", " SSPSS ")
                .aisle("  SPS  ", "  SOS  ", "  SPS  ", "  SPS  ", "  SPS  ", "  SPS  ", "  SPS  ")
                .where(' ', any())
                .where('O', selfPredicate())
                .where('S',
                        states(SuSyBlocks.MULTIBLOCK_CASING
                                .getState(BlockSuSyMultiblockCasing.CasingType.SILICON_CARBIDE_CASING))
                                        .setMinGlobalLimited(27).or(autoAbilities()))
                .where('I',
                        states(SuSyBlocks.MULTIBLOCK_CASING
                                .getState(BlockSuSyMultiblockCasing.CasingType.SILICON_CARBIDE_CASING)))
                .where('P', states(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE)))
                .where('C', states(MetaBlocks.WIRE_COIL.getState(BlockWireCoil.CoilType.NICHROME)))
                .where('X', air())
                .where('H', hydraulicOrientation(RelativeDirection.UP))
                .where('h', hydraulicOrientation(RelativeDirection.DOWN))
                .where('F', frames(getFrameMaterial()))
                .build();
    }

    protected TraceabilityPredicate hydraulicOrientation(RelativeDirection direction) {
        return SuSyPredicates.orientation(this, hydraulicState(), direction, FACING);
    }

    protected Direction getRelativeFacing(RelativeDirection dir) {
        return dir.getRelativeFacing(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }

    private BlockState hydraulicState() {
        return SuSyBlocks.METALLURGY.getState(BlockMetallurgy.BlockMetallurgyType.HYDRAULIC_CYLINDER);
    }

    protected Material getFrameMaterial() {
        Material mat = GregTechAPI.materialManager.getMaterial("incoloy_nine_zero_eight");
        if (mat == null) mat = Materials.Invar;
        return mat;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return SusyTextures.SILICON_CARBIDE_CASING;
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return Textures.FORMING_PRESS_OVERLAY;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityHotIsostaticPress(metaTileEntityId);
    }
}
