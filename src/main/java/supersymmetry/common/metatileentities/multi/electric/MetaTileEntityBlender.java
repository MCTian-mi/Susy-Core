package supersymmetry.common.metatileentities.multi.electric;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockTurbineCasing.TurbineCasingType;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import supersymmetry.api.recipes.SuSyRecipeMaps;
import supersymmetry.api.recipes.properties.BiomeProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


public class MetaTileEntityBlender extends RecipeMapMultiblockController {

    public MetaTileEntityBlender(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.BLENDER_RECIPES);
        /**
         * This is important, and its pretty much all you need to do in the MTE class
         */
        this.recipeMapWorkable = new BiomeRecipeLogic(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityBlender(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        TraceabilityPredicate casing = states(getCasingState()).setMinGlobalLimited(34);
        TraceabilityPredicate abilities = autoAbilities();
        return FactoryBlockPattern.start()
                .aisle(" XXX ", " XPX ", " XXX ", "  X  ")
                .aisle("XXXXX", "X D X", "X   X", "  X  ")
                .aisle("XXXXX", "PDDDP", "X E X", "XXCXX")
                .aisle("XXXXX", "X D X", "X   X", "  X  ")
                .aisle(" XXX ", " XSX ", " XXX ", "  X  ")
                .where('S', selfPredicate())
                .where('X', casing.or(abilities))
                .where('P', states(getPipeCasingState()))
                .where('D', states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STAINLESS_CLEAN))) 
                .where('E', frames(Materials.StainlessSteel))
                .where('C', states(MetaBlocks.TURBINE_CASING.getState(TurbineCasingType.STAINLESS_STEEL_GEARBOX)))
                .where(' ', any())
                .build();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.INERT_PTFE_CASING;
    }

    protected static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.PTFE_INERT_CASING);
    }

    protected static IBlockState getPipeCasingState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.POLYTETRAFLUOROETHYLENE_PIPE);
    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(TooltipHelper.RAINBOW_SLOW + I18n.format("gregtech.machine.perfect_oc"));
    }

    @SideOnly(Side.CLIENT)
    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.LARGE_CHEMICAL_REACTOR_OVERLAY;
    }


    /**
     * A custom recipeLogic class, for adding our check for biomes
     * This can be moved out to a stand-alone class.
     * But generally speaking if you do not plan to re-use this, making it an inner class should be fine.
     * CEu itself has many such cases.
     */
    public static class BiomeRecipeLogic extends MultiblockRecipeLogic {

        public BiomeRecipeLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity, true);
        }

        /**
         * Overriding this to add our own custom checks
         * Don't forget super calls
         */
        @Override
        public boolean checkRecipe(@NotNull Recipe recipe) {
            return checkBiomeRequirement(recipe) && super.checkRecipe(recipe);
        }

        /**
         * This is a method for biome checking
         */
        public boolean checkBiomeRequirement(@NotNull Recipe recipe) {
            if (!recipe.hasProperty(BiomeProperty.getInstance())) return true;
            return recipe.getProperty(BiomeProperty.getInstance(), BiomeProperty.BiomePropertyList.EMPTY_LIST)
                    .checkBiome(getMetaTileEntity().getWorld().getBiome(getMetaTileEntity().getPos()));
        }
    }
}
