package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static com.gregtechceu.gtceu.api.util.RelativeDirection.*;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;


import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.client.utils.TooltipHelper;
import com.gregtechceu.gtceu.common.blocks.BlockBoilerCasing.BoilerCasingType;
import com.gregtechceu.gtceu.common.blocks.BlockGlassCasing;
import com.gregtechceu.gtceu.common.block.CasingBlock.MetalCasingType;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MetaTileEntityMultiFluidHatch;
import io.github.symmetricdevs.supersymmetry.api.capability.impl.ExtendedDTLogicHandler;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.MetaTileEntityOrderedDT;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityVacuumDistillationTower extends MetaTileEntityOrderedDT {

    public MetaTileEntityVacuumDistillationTower(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.VACUUM_DISTILLATION_RECIPES, true);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityVacuumDistillationTower(this.metaTileEntityId);
    }

    @Override
    @NotNull
    public DistillationTowerLogicHandler createHandler() {
        return new ExtendedDTLogicHandler(this, 3, i -> 3);
    }

    @Override
    @NotNull
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RIGHT, FRONT, UP)
                .aisle(" CSC  ", "CCCCCC", "CCCCCC", "CCCCCC", " CCC  ")
                .aisle(" CGC  ", "C#F#CC", "IFFF#P", "C#F#CC", " CCC  ")
                .aisle(" CCC  ", "C#F#CC", "CFFFCC", "C#F#CC", " CCC  ")
                .aisle(" XXX  ", "X#F#D ", "XFFFD ", "X#F#D ", " XXX  ").setRepeatable(1, 12)
                .aisle(" DDD  ", "DDDDD ", "DDDDD ", "DDDDD ", " DDD  ")
                .where('S', selfPredicate())
                .where('G', states(getGlassState()))
                .where('P', states(getPipeCasingState()))
                .where('F', frames(Materials.Steel))
                .where('C', states(getCasingState())
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(1)))
                .where('I', states(getCasingState())
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(1)))
                .where('D', states(getCasingState()))
                .where('X', states(getCasingState())
                        .or(metaTileEntities(MultiblockAbility.REGISTRY.get(MultiblockAbility.EXPORT_FLUIDS).stream()
                                .filter(mte -> !(mte instanceof MetaTileEntityMultiFluidHatch))
                                .toArray(MetaMachine[]::new))
                                        .setMaxLayerLimited(1))
                        .or(autoAbilities(true, false)))
                .where('#', air())
                .build();
    }

    protected static BlockState getGlassState() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS);
    }

    protected static BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(MetalCasingType.STEEL_SOLID);
    }

    protected static BlockState getPipeCasingState() {
        return MetaBlocks.BOILER_CASING.getState(BoilerCasingType.STEEL_PIPE);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(TooltipHelper.RAINBOW_SLOW + I18n.format("gregtech.machine.perfect_oc", new Object[0]));
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return SusyTextures.VDT_OVERLAY;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }
}
