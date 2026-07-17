package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric.strand;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.blocks.BlockBoilerCasing;
import com.gregtechceu.gtceu.common.block.CasingBlock;
import com.gregtechceu.gtceu.common.blocks.BlockTurbineCasing;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.capability.Strand;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyMultiblockAbilities;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityClusterMill extends MetaTileEntityRollingMill {

    public MetaTileEntityClusterMill(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityClusterMill(metaTileEntityId);
    }

    @Override
    protected boolean consumeInputsAndSetupRecipe() {
        Strand orig = this.input.take();
        if (orig == null) return false;
        maxProgress = (int) Math.ceil(1 / (8.0 * orig.thickness));
        return true;
    }

    @Override
    protected Strand resultingStrand() {
        if (this.input.getStrand() == null || this.input.getStrand().isCut) return null;
        Strand str = new Strand(this.input.getStrand());
        // t / (2 - e^(-8t)) is a pretty good function for balancing
        double scaling = 2 - Math.pow(Math.E, -8 * str.thickness);
        str.thickness /= scaling;
        str.width *= scaling;
        return str;
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("  P P  ", "  P P  ", "  P P  ", "CCGCGCC", "F P P F", "  P P  ", "  P P  ", "  P P  ",
                        "  P P  ")
                .aisle("  P P  ", "  h h  ", "  R R  ", "RRRRRRR", "F  A  F", "   R   ", "  R R  ", "  H H  ",
                        "  P P  ")
                .aisle("  P P  ", "  h h  ", "  R R  ", "RRRRRRR", "I  A  O", "   R   ", "  R R  ", "  H H  ",
                        "  P P  ")
                .aisle("  P P  ", "  h h  ", "  R R  ", "RRRRRRR", "F  A  F", "   R   ", "  R R  ", "  H H  ",
                        "  P P  ")
                .aisle("  P P  ", "  P P  ", "  P P  ", "CCGSGCC", "F P P F", "  P P  ", "  P P  ", "  P P  ",
                        "  P P  ")
                .where('R', rollOrientation(RelativeDirection.FRONT))
                .where('H', hydraulicOrientation(RelativeDirection.UP))
                .where('h', hydraulicOrientation(RelativeDirection.DOWN))
                .where('F', frames(Materials.Steel))
                .where('S', selfPredicate())
                .where('I', abilities(SuSyMultiblockAbilities.STRAND_IMPORT))
                .where('O', abilities(SuSyMultiblockAbilities.STRAND_EXPORT))
                .where('C',
                        autoAbilities().or(
                                states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID))))
                .where('G',
                        states(MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX)))
                .where('P', states(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE)))
                .where(' ', any())
                .where('A', air())
                .build();
    }

    @Override
    protected void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return SusyTextures.CLUSTER_MILL_OVERLAY;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }
}
