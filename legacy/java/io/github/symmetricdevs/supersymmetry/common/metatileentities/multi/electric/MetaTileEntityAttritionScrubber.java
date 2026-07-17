package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyPredicates.hiddenGearTooth;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.common.block.CasingBlock.MetalCasingType;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockGrinderCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockSuSyMultiblockCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class MetaTileEntityAttritionScrubber extends WorkableElectricMultiblockMachine {

    public MetaTileEntityAttritionScrubber(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.ATTRITION_SCRUBBER);
        this.recipeMapWorkable = new RecipeLogic(this, false);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityAttritionScrubber(this.metaTileEntityId);
    }

    private static BlockState getAbrasionResistantCasingState() {
        return SuSyBlocks.GRINDER_CASING.getState(BlockGrinderCasing.Type.ABRASION_RESISTANT_CASING);
    }

    private static BlockState getAluminiumGearboxState() {
        return SuSyBlocks.MULTIBLOCK_CASING.getState(BlockSuSyMultiblockCasing.CasingType.ALUMINIUM_GEARBOX);
    }

    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle(" CCC CCC ", " CCCCCCC ", " CCCCCCC ", " CCCCCCC ", " CCC CCC ", " FGF FGF ")
                .aisle("CCCCCCCCC", "W#B###B#S", "C###C###C", "I#B#C#B#O", "C###C###C", " FGF FGF ")
                .aisle("CCCCCCCCC", "WBBB#BBBS", "C#A#C#A#C", "IBABCBABO", "C#A#C#A#C", " FGF FGF ")
                .aisle("CCCCCCCCC", "W#B###B#S", "C###C###C", "I#B#C#B#O", "C###C###C", " F F F F ")
                .aisle(" CCC CCC ", " CXCCCCC ", " CCCCCCC ", " CCCCCCC ", " CCC CCC ", " F F F F ")
                .where('C', states(getAbrasionResistantCasingState()).or(autoAbilities(
                        true, true, false,
                        false, false, false, false)))
                .where('I', abilities(MultiblockAbility.IMPORT_ITEMS).or(states(getAbrasionResistantCasingState())))
                .where('O', abilities(MultiblockAbility.EXPORT_ITEMS).or(states(getAbrasionResistantCasingState())))
                .where('W', abilities(MultiblockAbility.IMPORT_FLUIDS).or(states(getAbrasionResistantCasingState())))
                .where('S', abilities(MultiblockAbility.EXPORT_FLUIDS).or(states(getAbrasionResistantCasingState())))
                .where('G', states(getAluminiumGearboxState()))
                .where('B', states(MetaBlocks.METAL_CASING.getState(MetalCasingType.ALUMINIUM_FROSTPROOF)))
                .where('A', frames(Materials.Aluminium))
                .where('F', frames(Materials.Steel))
                .where('X', selfPredicate())
                .where('#', air())
                .where(' ', any())
                .where('M', hiddenGearTooth(
                        RelativeDirection.UP.getRelativeFacing(getFrontFacing(), getUpwardsFacing(), false)
                                .getAxis()))
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart multiblockPart) {
        return SusyTextures.ABRASION_RESISTANT_CASING;
    }

    protected static BlockState getCasingState() {
        return SuSyBlocks.GRINDER_CASING.getState(BlockGrinderCasing.Type.ABRASION_RESISTANT_CASING);
    }

    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("susy.machine.parallel_pure", 32));
    }
}
