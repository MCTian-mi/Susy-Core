package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import java.util.function.Supplier;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.api.util.BlockInfo;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.blocks.BlockBoilerCasing;
import com.gregtechceu.gtceu.common.block.CasingBlock;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockElectrodeAssembly;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockSuSyMultiblockCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class MetaTileEntityGasAtomizer extends WorkableElectricMultiblockMachine {

    public MetaTileEntityGasAtomizer(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.GAS_ATOMIZER);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("  O  ", "  P  ", "  P  ", "  M  ", "     ", "     ", " EIE ", "     ")
                .aisle("     ", "  P  ", "     ", "     ", "     ", " HHH ", " EXE ", " HHH ")
                .aisle("R   R", "R P R", "CCCCC", " HHH ", " HHH ", " HHH ", " HXH ", " HHH ")
                .aisle(" CCC ", " CPC ", "CHHHC", "HHHHH", "HXXXH", "HXXXH", "HXXXH", " HHH ")
                .aisle(" CCC ", " CXC ", "CHXHC", "HHXHH", "HXXXH", "HXXXH", "HXXXH", " HFH ")
                .aisle(" CCC ", " CCC ", "CHHHC", "HHHHH", "HXXXH", "HXXXH", "HXXXH", " HHH ")
                .aisle("R   R", "R   R", "CCSCC", " HHH ", " HHH ", " HHH ", " HHH ", "     ")
                .where('P', states(getPipeCasingState()))
                .where('H', states(getHighTempCasingState()))
                .where('C', states(getCasingState()).or(autoAbilities(true, true, false, false, false, false, false)))
                .where('S', selfPredicate())
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where('O', abilities(MultiblockAbility.EXPORT_ITEMS))
                .where('F', abilities(MultiblockAbility.IMPORT_FLUIDS))
                .where('I', abilities(MultiblockAbility.IMPORT_ITEMS))
                .where('E', states(getElectrodeCasingState()))
                .where('R', frames(Materials.Steel))
                .where('X', air())
                .where(' ', any())
                .build();
    }

    public BlockState getElectrodeCasingState() {
        return SuSyBlocks.ELECTRODE_ASSEMBLY.getState(BlockElectrodeAssembly.ElectrodeAssemblyType.CARBON);
    }

    protected TraceabilityPredicate orientation(BlockState state, RelativeDirection direction,
                                                IProperty<Direction> facingProperty) {
        Direction facing = this.getRelativeFacing(direction);

        Supplier<BlockInfo[]> supplier = () -> new BlockInfo[] {
                new BlockInfo(state.withProperty(facingProperty, facing)) };
        return new TraceabilityPredicate(blockWorldState -> {
            if (blockWorldState.getBlockState() != state.withProperty(facingProperty, facing)) {
                if (blockWorldState.getBlockState().getBlock() != state.getBlock()) return false;
                getWorld().setBlockState(blockWorldState.getPos(), state.withProperty(facingProperty, facing));
            }
            return true;
        }, supplier);
    }

    protected Direction getRelativeFacing(RelativeDirection dir) {
        return dir.getRelativeFacing(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }

    public BlockState getPipeCasingState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE);
    }

    public BlockState getHighTempCasingState() {
        return SuSyBlocks.MULTIBLOCK_CASING.getState(BlockSuSyMultiblockCasing.CasingType.SILICON_CARBIDE_CASING);
    }

    public BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityGasAtomizer(metaTileEntityId);
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return SusyTextures.GAS_ATOMIZER_OVERLAY;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }
}
