package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static com.gregtechceu.gtceu.api.util.RelativeDirection.*;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;


import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.block.CasingBlock.MetalCasingType;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MetaTileEntityMultiFluidHatch;
import io.github.symmetricdevs.supersymmetry.api.capability.impl.ExtendedDTLogicHandler;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.ICryogenicProvider;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.ICryogenicReceiver;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.MetaTileEntityOrderedDT;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockSuSyMultiblockCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class MetaTileEntitySingleColumnCryogenicDistillationPlant extends MetaTileEntityOrderedDT
                                                                  implements ICryogenicProvider {

    private @Nullable ICryogenicReceiver receiver;

    public MetaTileEntitySingleColumnCryogenicDistillationPlant(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.SINGLE_COLUMN_CRYOGENIC_DISTILLATION);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntitySingleColumnCryogenicDistillationPlant(this.metaTileEntityId);
    }

    @Override
    @NotNull
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RIGHT, FRONT, UP)
                .aisle("CCC", "CCC", "CCC")
                .aisle("CSC", "CFC", "CCC")
                .aisle("XXX", "XFX", "XXX").setRepeatable(1, 16)
                .aisle("CCC", "CCC", "CCC")
                .aisle("CEC", "E E", "CEC")
                .aisle("DDD", "DED", "DDD")
                .where('S', this.selfPredicate())
                .where('C', states(getCasingState())
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                        .or(autoAbilities(false, true, false, false, false, false, false).setExactLimit(1)))
                .where('F',
                        states(SuSyBlocks.MULTIBLOCK_CASING
                                .getState(BlockSuSyMultiblockCasing.CasingType.STRUCTURAL_PACKING)))
                .where('X', states(getCasingState())
                        .or(metaTileEntities(MultiblockAbility.REGISTRY.get(MultiblockAbility.EXPORT_FLUIDS).stream()
                                .filter(mte -> !(mte instanceof MetaTileEntityMultiFluidHatch))
                                .toArray(MetaMachine[]::new))
                                        .setMaxLayerLimited(1))
                        .or(metaTileEntities(MultiblockAbility.REGISTRY.get(MultiblockAbility.IMPORT_FLUIDS).stream()
                                .filter(mte -> !(mte instanceof MetaTileEntityMultiFluidHatch))
                                .toArray(MetaMachine[]::new))
                                        .setMaxLayerLimited(4)))
                .where('D', states(getCasingState()))
                .where('E', states(getCasingState())
                        .or(abilities(MultiblockAbility.PASSTHROUGH_HATCH)))
                .where('#', air())
                .where(' ', cryogenicRecieverPredicate())
                .build();
    }

    @Override
    @NotNull
    public DistillationTowerLogicHandler createHandler() {
        return new ExtendedDTLogicHandler(this, 2, i -> 1);
    }

    protected static BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(MetalCasingType.ALUMINIUM_FROSTPROOF);
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        if (this.receiver != null) {
            this.receiver.setCryogenicProvider(null);
            this.receiver = null;
        }
    }

    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.FROST_PROOF_CASING;
    }

    @Override
    @NotNull
    protected ICubeRenderer getFrontOverlay() {
        return Textures.BLAST_FURNACE_OVERLAY;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    public void setReceiver(@NotNull ICryogenicReceiver receiver) {
        this.receiver = receiver;
    }
}
