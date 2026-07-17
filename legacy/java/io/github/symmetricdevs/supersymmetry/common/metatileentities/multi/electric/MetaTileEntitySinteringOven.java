package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.PatternMatchContext;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.SinterProperty;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockSinteringBrick;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockSuSyMultiblockCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class MetaTileEntitySinteringOven extends WorkableElectricMultiblockMachine {

    private boolean canUsePlasma;

    public MetaTileEntitySinteringOven(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.SINTERING_RECIPES);
        this.recipeMapWorkable = new RecipeLogic(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntitySinteringOven(metaTileEntityId);
    }

    @Override
    protected void addDisplayText(List<Component> textList) {
        if (isStructureFormed()) {
            textList.add(Component.translatable("susy.multiblock.sintering_oven.can_use_plasma",
                    Component.translatable(
                            canUsePlasma ? "susy.multiblocks.sintering_oven.use_plasma.affirmative" :
                                    "susy.multiblocks.sintering_oven.use_plasma.negative")
                                            .setStyle(new Style().setColor(ChatFormatting.LIGHT_PURPLE))));
        }
        super.addDisplayText(textList);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart multiblockPart) {
        return SusyTextures.ULV_STRUCTURAL_CASING;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return SusyTextures.SINTERING_OVERLAY;
    }

    protected static BlockState getCasingState() {
        return SuSyBlocks.MULTIBLOCK_CASING.getState(BlockSuSyMultiblockCasing.CasingType.ULV_STRUCTURAL_CASING);
    }

    @NotNull
    @Override
    public BlockPattern createStructurePattern() {
        // Different characters use common constraints. Copied from GCyM
        TraceabilityPredicate casingPredicate = states(getCasingState()).setMinGlobalLimited(33);

        return FactoryBlockPattern.start()
                .aisle("CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC")
                .aisle("     ", " BBB ", " B#B ", " BBB ", "     ")
                .aisle("FFFFF", "FBBBF", "FB#BF", " BBB ", "     ")
                .aisle("     ", " BBB ", " B#B ", " BBB ", "     ")
                .aisle("FFFFF", "FBBBF", "FB#BF", " BBB ", "     ")
                .aisle("     ", " BBB ", " B#B ", " BBB ", "     ")
                .aisle("FFFFF", "FBBBF", "FB#BF", " BBB ", "     ")
                .aisle("     ", " BBB ", " B#B ", " BBB ", "     ")
                .aisle("FFFFF", "FBBBF", "FB#BF", " BBB ", "     ")
                .aisle("     ", " BBB ", " B#B ", " BBB ", "     ")
                .aisle("DDDDD", "DDSDD", "DDDDD", "DDDDD", "DDDDD")
                .where('S', selfPredicate())
                .where('D', casingPredicate
                        .or(autoAbilities(true, true, false, true, true, false, false)))
                .where('C', casingPredicate
                        .or(autoAbilities(false, false, true, false, false, true, false)))
                .where('F', frames(Materials.Steel))
                .where('B', SuSyPredicates.sinteringBricks())
                .where('#', air())
                .where(' ', any())
                .build();
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object type = context.get("SinteringBrickType");
        if (type instanceof BlockSinteringBrick.SinteringBrickType) {
            this.canUsePlasma = ((BlockSinteringBrick.SinteringBrickType) type).canResistPlasma;
        } else {
            this.canUsePlasma = false;
        }
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.canUsePlasma = false;
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe, boolean consumeIfSuccess) {
        return this.canUsePlasma || !SinterProperty.getPlasmaEnabled(recipe, false);
    }
}
