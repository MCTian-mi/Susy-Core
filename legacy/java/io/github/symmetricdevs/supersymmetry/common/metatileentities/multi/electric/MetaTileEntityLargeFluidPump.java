package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static com.gregtechceu.gtceu.api.util.RelativeDirection.FRONT;
import static com.gregtechceu.gtceu.api.util.RelativeDirection.RIGHT;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerBase;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.client.utils.TooltipHelper;
import com.gregtechceu.gtceu.common.blocks.BlockBoilerCasing.BoilerCasingType;
import com.gregtechceu.gtceu.common.block.CasingBlock.MetalCasingType;
import com.gregtechceu.gtceu.common.blocks.BlockTurbineCasing.TurbineCasingType;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.BiomeProperty;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityLargeFluidPump extends WorkableElectricMultiblockMachine {

    public MetaTileEntityLargeFluidPump(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.PUMPING_RECIPES);
        this.recipeMapWorkable = new LargePumpRecipeLogic(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityLargeFluidPump(this.metaTileEntityId);
    }

    @Override
    public boolean isMultiblockPartWeatherResistant(@Nonnull IMultiblockPart part) {
        return true;
    }

    @Override
    public boolean getIsWeatherOrTerrainResistant() {
        return true;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RIGHT, FRONT, RelativeDirection.UP)
                .aisle("       ", "      P", "       ")
                .aisle("       ", "      P", "       ")
                .aisle("FCCCC  ", "CCCCC P", "FCECC  ")
                .aisle("CCSGC  ", "OPPPPPP", "CCEGC  ")
                .aisle("FCCC   ", "CCCCC  ", "FCEC   ")
                .where(' ', any())
                .where('S', selfPredicate())
                .where('P', states(getPipeCasingState()))
                .where('G', states(getGearboxState()))
                .where('F', frames(Materials.Steel))
                .where('C', states(getCasingState())
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setExactLimit(1))
                        .or(autoAbilities(true, false)))
                .where('E', states(getCasingState())
                        .or(abilities(MultiblockAbility.INPUT_ENERGY)).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                .where('O', abilities(MultiblockAbility.EXPORT_FLUIDS))
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.SOLID_STEEL_CASING;
    }

    protected static BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(MetalCasingType.STEEL_SOLID);
    }

    protected static BlockState getPipeCasingState() {
        return MetaBlocks.BOILER_CASING.getState(BoilerCasingType.STEEL_PIPE);
    }

    protected static BlockState getGearboxState() {
        return MetaBlocks.TURBINE_CASING.getState(TurbineCasingType.STEEL_GEARBOX);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        tooltip.add(I18n.format("susy.machine.large_fluid_pump.tooltip.1"));
        tooltip.add(I18n.format("susy.machine.large_fluid_pump.tooltip.2"));
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(TooltipHelper.RAINBOW_SLOW + I18n.format("gregtech.machine.perfect_oc", new Object[0]));
    }

    @Override
    protected void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        Direction leftSide = RelativeDirection.LEFT
                .getRelativeFacing(
                        getFrontFacing(),
                        getUpwardsFacing(),
                        isFlipped());
        Direction backSide = RelativeDirection.BACK
                .getRelativeFacing(
                        getFrontFacing(),
                        getUpwardsFacing(),
                        isFlipped());
        BlockPos tempPos = getPos().offset(leftSide, 4).offset(backSide);
        int yLevel = getPos().getY();
        String biome = getWorld().getBiome(tempPos).biomeName;
        textList.add(Component.translatable("susy.large_fluid_pump.y_level", yLevel)
                .setStyle(new Style().setColor(ChatFormatting.YELLOW)));
        textList.add(Component.translatable("susy.large_fluid_pump.biome", biome)
                .setStyle(new Style().setColor(ChatFormatting.YELLOW)));
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return SusyTextures.LARGE_FLUID_PUMP_OVERLAY;
    }

    /**
     * A custom recipeLogic class, for adding our check for biomes
     * This can be moved out to a stand-alone class.
     * But generally speaking if you do not plan to re-use this, making it an inner class should be fine.
     * CEu itself has many such cases.
     */
    public static class LargePumpRecipeLogic extends RecipeLogic {

        public LargePumpRecipeLogic(WorkableElectricMultiblockMachine BlockEntity) {
            super(BlockEntity, true);
        }

        /**
         * Overriding this to add our own custom checks
         * Don't forget super calls
         */
        @Override
        public boolean checkRecipe(@NotNull Recipe recipe) {
            return checkHeightRequirement() && checkBiomeRequirement(recipe) && super.checkRecipe(recipe);
        }

        public boolean checkHeightRequirement() {
            return getMetaTileEntity().getPos().getY() == 64;
        }

        /**
         * This is a method for biome checking
         */
        public boolean checkBiomeRequirement(@NotNull Recipe recipe) {
            // Worldgen biome gating deferred to Phase 8; accept all recipes for now.
            return true;
            Direction leftSide = RelativeDirection.LEFT
                    .getRelativeFacing(
                            getMetaTileEntity().getFrontFacing(),
                            ((MultiblockControllerBase) getMetaTileEntity()).getUpwardsFacing(),
                            ((MultiblockControllerBase) getMetaTileEntity()).isFlipped());
            Direction backSide = RelativeDirection.BACK
                    .getRelativeFacing(
                            getMetaTileEntity().getFrontFacing(),
                            ((MultiblockControllerBase) getMetaTileEntity()).getUpwardsFacing(),
                            ((MultiblockControllerBase) getMetaTileEntity()).isFlipped());
            BlockPos tempPos = getMetaTileEntity().getPos().offset(leftSide, 4).offset(backSide);
            return true; // Worldgen biome gating deferred to Phase 8
        }

        @Override
        public int getParallelLimit() {
            return 256;
        }
    }
}
