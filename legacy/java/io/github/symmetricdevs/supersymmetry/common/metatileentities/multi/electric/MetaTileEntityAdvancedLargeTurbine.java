package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static io.github.symmetricdevs.supersymmetry.api.blocks.VariantHorizontalRotatableBlock.FACING;

import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.util.BlockInfo;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.blocks.BlockBoilerCasing;
import com.gregtechceu.gtceu.common.blocks.BlockTurbineCasing;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockTurbineRotor;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class MetaTileEntityAdvancedLargeTurbine extends MetaTileEntitySUSYLargeTurbine {

    public MetaTileEntityAdvancedLargeTurbine(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.ADVANCED_STEAM_TURBINE, 4, 3600, 2, 2,
                MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.TITANIUM_TURBINE_CASING),
                SuSyBlocks.TURBINE_ROTOR.getState(BlockTurbineRotor.BlockTurbineRotorType.LOW_PRESSURE),
                SusyTextures.TITANIUM_TURBINE_CASING, SusyTextures.ADVANCED_STEAM_TURBINE_OVERLAY);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityAdvancedLargeTurbine(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        // Different characters use common constraints. Copied from GCyM
        TraceabilityPredicate casingPredicate = states(this.casingState).setMinGlobalLimited(52)
                .or(abilities(MultiblockAbility.IMPORT_ITEMS).setPreviewCount(1));
        TraceabilityPredicate maintenance = abilities(MultiblockAbility.MAINTENANCE_HATCH).setMaxGlobalLimited(1);

        return FactoryBlockPattern.start()
                .aisle("GAAAAAAAAAAAO", "GAAAAAAAAAAAO", "G   A   A   O")
                .aisle("GAAAAAAAAAAAO", "GHHHPLLLLCCCF", "GAAAAAAAAAAAO")
                .aisle("GAAAAAAAAAAAO", "GSAAAAAAAAAAO", "G   A   A   O")
                .where('S', selfPredicate())
                .where('A', casingPredicate
                        .or(autoAbilities(false, false, false, false, false, false, false))
                        .or(maintenance))
                .where('O', casingPredicate
                        .or(autoAbilities(false, false, false, false, false, true, false))
                        .or(maintenance))
                .where('C', coilOrientation())
                .where('L', rotorOrientation())
                .where('H', rotorOrientation2())
                .where('F', abilities(MultiblockAbility.OUTPUT_ENERGY))
                .where('G', casingPredicate
                        .or(autoAbilities(false, false, false, false, true, false, false))
                        .or(maintenance))
                .where('P', states(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TITANIUM_PIPE)))
                .where(' ', any())
                .build();
    }

    protected TraceabilityPredicate rotorOrientation2() {
        // makes sure rotor's front faces the left side (relative to the player) of controller front
        Direction leftFacing = RelativeDirection.RIGHT.getRelativeFacing(getFrontFacing(), getUpwardsFacing(),
                isFlipped());

        // converting the left facing to positive x or z axis direction
        // this is needed for the following update which converts this rotatable block from horizontal directional into
        // axial directional.
        Direction axialFacing = leftFacing.getIndex() < 4 ? Direction.SOUTH : Direction.WEST;

        Supplier<BlockInfo[]> supplier = () -> new BlockInfo[] {
                new BlockInfo(this.rotorState2().withProperty(FACING, axialFacing)) };
        return new TraceabilityPredicate(blockWorldState -> {
            BlockState state = blockWorldState.getBlockState();
            if (state.getBlock() != this.rotorState2().getBlock()) return false;

            // auto-correct rotor orientation
            if (state != this.rotorState2().withProperty(FACING, axialFacing))
                getWorld().setBlockState(blockWorldState.getPos(),
                        this.rotorState2().withProperty(FACING, axialFacing));

            return true;
        }, supplier);
    }

    public BlockState rotorState2() {
        return SuSyBlocks.TURBINE_ROTOR.getState(BlockTurbineRotor.BlockTurbineRotorType.HIGH_PRESSURE);
    }
}
