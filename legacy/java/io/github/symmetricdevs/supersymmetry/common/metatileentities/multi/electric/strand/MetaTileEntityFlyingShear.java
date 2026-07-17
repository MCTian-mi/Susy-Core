package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric.strand;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;

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
import com.gregtechceu.gtceu.common.block.CasingBlock;
import com.gregtechceu.gtceu.common.blocks.BlockTurbineCasing;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.blocks.VariantHorizontalRotatableBlock;
import io.github.symmetricdevs.supersymmetry.api.capability.Strand;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyMultiblockAbilities;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockMetallurgy2;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class MetaTileEntityFlyingShear extends MetaTileEntityStrandShaper {

    public MetaTileEntityFlyingShear(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected boolean consumeInputsAndSetupRecipe() {
        Strand orig = this.input.take();
        if (orig == null) return false;
        this.progress = 10;
        return true;
    }

    @Override
    protected Strand resultingStrand() {
        if (this.input.getStrand() == null) return null;
        Strand str = new Strand(this.input.getStrand());
        str.isCut = true;
        return str;
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCCCCCCC", "FFFFGFFFF", "    F    ", "    G    ")
                .aisle("CRRRRRRRC", "F   A   F", "    X    ", "    F    ")
                .aisle("CRRRRRRRC", "I   A   O", "    X    ", "    F    ")
                .aisle("CRRRRRRRC", "F   A   F", "    X    ", "    F    ")
                .aisle("CCCCSCCCC", "FFFFGFFFF", "    F    ", "    G    ")
                .where('S', this.selfPredicate())
                .where('A', air())
                .where('C', states(getCasingState()).or(autoAbilities()))
                .where('I', abilities(SuSyMultiblockAbilities.STRAND_IMPORT))
                .where('O', abilities(SuSyMultiblockAbilities.STRAND_EXPORT))
                .where('X',
                        SuSyPredicates.orientation(this, getSawbladeState(), RelativeDirection.RIGHT,
                                VariantHorizontalRotatableBlock.FACING))
                .where('F', frames(Materials.Steel))
                .where('R', rollOrientation(RelativeDirection.FRONT))
                .where('G',
                        states(MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX)))
                .where(' ', any())
                .build();
    }

    private BlockState getSawbladeState() {
        return SuSyBlocks.METALLURGY_2.getState(BlockMetallurgy2.BlockMetallurgy2Type.FLYING_SHEAR_SAW);
    }

    private BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityFlyingShear(metaTileEntityId);
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return SusyTextures.FLYING_SHEAR_OVERLAY;
    }
}
