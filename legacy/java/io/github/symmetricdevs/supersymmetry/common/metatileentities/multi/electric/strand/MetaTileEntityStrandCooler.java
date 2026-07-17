package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric.strand;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.utils.GTTransferUtils;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import io.github.symmetricdevs.supersymmetry.api.capability.Strand;
import io.github.symmetricdevs.supersymmetry.api.capability.StrandConversion;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyMultiblockAbilities;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockSuSyMultiblockCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class MetaTileEntityStrandCooler extends MetaTileEntityStrandShaper {

    private ItemStack current;

    public MetaTileEntityStrandCooler(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected boolean consumeInputsAndSetupRecipe() {
        if (input.getStrand() != null) {
            if (!input.getStrand().isCut) {
                return false;
            }
            StrandConversion conversion = StrandConversion.getConversion(input.getStrand());
            if (conversion == null) {
                return false;
            }
            int amount = (int) (conversion.amount * conversion.prefix.getMaterialAmount(input.getStrand().material) /
                    GTValues.M);
            FluidStack drained = inputFluidInventory.drain(Materials.Water.getFluid(amount), false);
            if (drained == null || drained.amount != amount) {
                return false;
            }
            current = OreDictUnifier.get(conversion.prefix, input.getStrand().material, conversion.amount);
            inputFluidInventory.drain(Materials.Water.getFluid(amount), true);
            input.take();
            maxProgress = amount * 4;
            return true;
        }
        return false;
    }

    @Override
    protected boolean outputsStrand() {
        return false;
    }

    @Override
    protected boolean hasRoom() {
        if (input.getStrand() == null) {
            return false;
        }
        StrandConversion conversion = StrandConversion.getConversion(input.getStrand());
        if (conversion == null) {
            return false;
        }
        if (!GTTransferUtils.insertItem(outputInventory,
                OreDictUnifier.get(conversion.prefix, input.getStrand().material, conversion.amount),
                true).isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    protected void output() {
        if (current == null || outputInventory == null) return;
        GTTransferUtils.insertItem(outputInventory, current, false);
    }

    @Override
    protected Strand resultingStrand() {
        return null;
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCCC", "CCOCC", "     ", "     ")
                .aisle("CCCCC", "CRRRC", "CAAAC", "CCCCC")
                .aisle("CCCCC", "CRRRC", "PAAAP", "PPPPP")
                .aisle("CCCCC", "CRRRC", "CAAAC", "CCCCC")
                .aisle("CCCCC", "CRRRC", "PAAAP", "PPPPP")
                .aisle("CCCCC", "CRRRC", "CAAAC", "CCCCC")
                .aisle("CCCCC", "CRRRC", "PAAAP", "PPPPP")
                .aisle("CCCCC", "CRRRC", "CAAAC", "CCCCC")
                .aisle("CCCCC", "CRRRC", "PAAAP", "PPPPP")
                .aisle("CCCCC", "CRRRC", "CAAAC", "CCCCC")
                .aisle("CCCCC", "CCICC", "CAAAC", "CCSCC")
                .where('S', selfPredicate())
                .where('P', states(getPipeCasingState()))
                .where('I', abilities(SuSyMultiblockAbilities.STRAND_IMPORT))
                .where('O', abilities(MultiblockAbility.EXPORT_ITEMS))
                .where('C', states(getCasingState())
                        .or(autoAbilities(true, false))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setMaxGlobalLimited(2)))
                .where('A', air())
                .where('R', rollOrientation(RelativeDirection.RIGHT))
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return SusyTextures.MONEL_500_CASING;
    }

    private BlockState getPipeCasingState() {
        return SuSyBlocks.MULTIBLOCK_CASING.getState(BlockSuSyMultiblockCasing.CasingType.MONEL_500_PIPE);
    }

    private BlockState getCasingState() {
        return SuSyBlocks.MULTIBLOCK_CASING.getState(BlockSuSyMultiblockCasing.CasingType.MONEL_500_CASING);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityStrandCooler(metaTileEntityId);
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return SusyTextures.STRAND_COOLER_OVERLAY;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }
}
