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
import com.gregtechceu.gtceu.common.blocks.BlockTurbineCasing;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.capability.Strand;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyMultiblockAbilities;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.*;

public class MetaTileEntityTurningZone extends MetaTileEntityStrandShaper {

    public MetaTileEntityTurningZone(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected boolean consumeInputsAndSetupRecipe() {
        if (this.input.getStrand() == null) {
            return false;
        }
        this.input.take();
        this.maxProgress = 20;
        return true;
    }

    @Override
    protected Strand resultingStrand() {
        return input.getStrand();
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
                .aisle("ABBBA",
                        "ABBBA",
                        "ABBBA",
                        "ABBBA",
                        "     ",
                        "     ",
                        "     ",
                        "     ",
                        "F F F")
                .aisle("  O  ",
                        "     ",
                        "     ",
                        "     ",
                        "ABBBA",
                        "ABBBA",
                        "     ",
                        "     ",
                        "F S F")
                .aisle("ABBBA",
                        "ABBBA",
                        "ABBBA",
                        "     ",
                        "     ",
                        "     ",
                        "ABBBA",
                        "     ",
                        "F F F")
                .aisle("     ",
                        "     ",
                        "     ",
                        "ABBBA",
                        "ABBBA",
                        "     ",
                        "     ",
                        "ABBBA",
                        "F F F")
                .aisle("     ",
                        "     ",
                        "     ",
                        "     ",
                        "     ",
                        "ABBBA",
                        "     ",
                        "ABBBA",
                        "F F F")
                .aisle("     ",
                        "     ",
                        "     ",
                        "     ",
                        "     ",
                        "ABBBA",
                        "     ",
                        "     ",
                        "ABBBA")
                .aisle("     ",
                        "     ",
                        "     ",
                        "     ",
                        "     ",
                        "     ",
                        "ABBBA",
                        "     ",
                        "ABBBA")
                .aisle("     ",
                        "     ",
                        "     ",
                        "     ",
                        "     ",
                        "     ",
                        "ABBBA",
                        "  I  ",
                        "ABBBA")
                .where('B', rollOrientation(RelativeDirection.RIGHT))
                .where('A',
                        states(MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX)))
                .where('I', abilities(SuSyMultiblockAbilities.STRAND_IMPORT))
                .where('O', abilities(SuSyMultiblockAbilities.STRAND_EXPORT))
                .where('S', selfPredicate())
                .where('F', states(SuSyMetaBlocks.SHEETED_FRAMES.get(Materials.Steel).getBlock(Materials.Steel)
                        .withProperty(BlockSheetedFrame.SHEETED_FRAME_AXIS, BlockSheetedFrame.FrameEnumAxis
                                .fromFacingAxis(getRelativeFacing(RelativeDirection.UP).getAxis())))
                                        .or(autoAbilities(true, true, false)))
                .where(' ', any())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return SusyTextures.TURNING_ZONE_OVERLAY;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityTurningZone(this.metaTileEntityId);
    }

    private BlockState rollState() {
        return SuSyBlocks.METALLURGY_ROLL.getState(BlockMetallurgyRoll.BlockMetallurgyRollType.ROLL);
    }
}
