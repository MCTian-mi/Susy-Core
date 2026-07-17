package io.github.symmetricdevs.supersymmetry.integration.jei;

import static io.github.symmetricdevs.supersymmetry.api.fluids.SusyGeneratedFluidHandler.CAST_MATERIALS;

import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import cam72cam.immersiverailroading.IRItems;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.modules.GregTechModule;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.api.util.Mods;
import com.gregtechceu.gtceu.integration.IntegrationSubmodule;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.particle.ParticleBeam;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.common.metatileentities.SuSyMetaTileEntities;
import io.github.symmetricdevs.supersymmetry.integration.jei.category.StrandCategory;
import io.github.symmetricdevs.supersymmetry.integration.jei.category.StrandInfo;
import io.github.symmetricdevs.supersymmetry.integration.jei.ingredient.ParticleBeamHelper;
import io.github.symmetricdevs.supersymmetry.integration.jei.ingredient.ParticleBeamListFactory;
import io.github.symmetricdevs.supersymmetry.integration.jei.ingredient.ParticleBeamRenderer;
import io.github.symmetricdevs.supersymmetry.integration.jei.ingredient.ParticleType;
import io.github.symmetricdevs.supersymmetry.modules.SuSyModules;

@JEIPlugin
@GregTechModule(
                moduleID = SuSyModules.MODULE_JEI,
                containerID = Supersymmetry.MODID,
                modDependencies = Mods.Names.JUST_ENOUGH_ITEMS,
                name = "SuSy JEI Integration",
                description = "SuSy JEI Integration Module")
public class JeiModule extends IntegrationSubmodule implements IModPlugin {

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(IRItems.ITEM_ROLLING_STOCK.internal,
                new RollingStockSubtypeHandler());
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
        List<ParticleBeam> particleBeamList = ParticleBeamListFactory.createList();
        ParticleBeamHelper particleBeamHelper = new ParticleBeamHelper();
        ParticleBeamRenderer particleBeamRenderer = new ParticleBeamRenderer();
        registry.register(ParticleType.Particle, particleBeamList, particleBeamHelper, particleBeamRenderer);
    }

    @Override
    public void register(IModRegistry registry) {
        String semiFluidMapId = GTValues.MODID + ":" + RecipeMaps.SEMI_FLUID_GENERATOR_FUELS.getUnlocalizedName();

        registry.addRecipeCatalyst(SuSyMetaTileEntities.LARGE_BRONZE_BOILER.getStackForm(), semiFluidMapId);
        registry.addRecipeCatalyst(SuSyMetaTileEntities.LARGE_STEEL_BOILER.getStackForm(), semiFluidMapId);
        registry.addRecipeCatalyst(SuSyMetaTileEntities.STEAM_BOILER_LIQUID_BRONZE.getStackForm(), semiFluidMapId);
        registry.addRecipeCatalyst(SuSyMetaTileEntities.STEAM_BOILER_LIQUID_STEEL.getStackForm(), semiFluidMapId);

        String solidMapId = GTValues.MODID + ":" + SuSyRecipeMaps.BOILER_RECIPES.getUnlocalizedName();

        registry.addRecipeCatalyst(SuSyMetaTileEntities.LARGE_BRONZE_BOILER.getStackForm(), solidMapId);
        registry.addRecipeCatalyst(SuSyMetaTileEntities.LARGE_STEEL_BOILER.getStackForm(), solidMapId);
        registry.addRecipeCatalyst(SuSyMetaTileEntities.STEAM_BOILER_COAL_BRONZE.getStackForm(), solidMapId);
        registry.addRecipeCatalyst(SuSyMetaTileEntities.STEAM_BOILER_COAL_STEEL.getStackForm(), solidMapId);

        String strandCastingId = GTValues.MODID + ":strand_casting";
        registry.addRecipes(CAST_MATERIALS.stream().map(StrandInfo::new).collect(Collectors.toList()), strandCastingId);
        registry.addRecipeCatalyst(SuSyMetaTileEntities.TURNING_ZONE.getStackForm(), strandCastingId);
        registry.addRecipeCatalyst(SuSyMetaTileEntities.ROLLING_MILL.getStackForm(), strandCastingId);
        registry.addRecipeCatalyst(SuSyMetaTileEntities.CLUSTER_MILL.getStackForm(), strandCastingId);
        registry.addRecipeCatalyst(SuSyMetaTileEntities.FLYING_SHEAR.getStackForm(), strandCastingId);
        registry.addRecipeCatalyst(SuSyMetaTileEntities.SLAB_MOLD.getStackForm(), strandCastingId);
        registry.addRecipeCatalyst(SuSyMetaTileEntities.BILLET_MOLD.getStackForm(), strandCastingId);
        registry.addRecipeCatalyst(SuSyMetaTileEntities.STRAND_COOLER.getStackForm(), strandCastingId);
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new StrandCategory(registry.getJeiHelpers().getGuiHelper()));
    }
}
