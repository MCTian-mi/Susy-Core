package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.block.CasingBlock.MetalCasingType;
import com.gregtechceu.gtceu.common.blocks.BlockTurbineCasing;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityLargeWeaponsFactory extends WorkableElectricMultiblockMachine {

    public MetaTileEntityLargeWeaponsFactory(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.LARGE_WEAPONS_FACTORY_RECIPES);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityLargeWeaponsFactory(metaTileEntityId);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        TraceabilityPredicate casingPredicate = states(getCasingState()).setMinGlobalLimited(4);
        return FactoryBlockPattern.start()
                .aisle("FBF", "FFF")
                .aisle("CBC", " A ")
                .aisle("CBC", " A ")
                .aisle("CBC", "EAE")
                .aisle("CBC", "EAE")
                .aisle("CBC", " A ")
                .aisle("CBC", " A ")
                .aisle("DDD", "DSD")
                .where('S', selfPredicate())
                .where('A', casingPredicate)
                .where('B',
                        states(MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX)))
                .where('C', frames(Materials.Steel)
                        .or(autoAbilities(false, true, false, false, false, false, false).setExactLimit(1)))
                .where('D', casingPredicate
                        .or(autoAbilities(false, false, true, false, true, false, false)))
                .where('E', casingPredicate
                        .or(autoAbilities(true, false, false, false, false, false, false)))
                .where('F', casingPredicate
                        .or(autoAbilities(false, false, false, true, false, false, false)))
                .where(' ', any())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return SusyTextures.LARGE_WEAPONS_FACTORY_OVERLAY;
    }

    protected static BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(MetalCasingType.STEEL_SOLID);
    }
}
