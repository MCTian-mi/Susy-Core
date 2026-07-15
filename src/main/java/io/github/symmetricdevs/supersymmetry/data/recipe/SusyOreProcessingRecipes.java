package io.github.symmetricdevs.supersymmetry.data.recipe;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.OreProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.api.unification.material.properties.SuSyPropertyKey;
import io.github.symmetricdevs.supersymmetry.api.unification.ore.SusyTagPrefixes;
import io.github.symmetricdevs.supersymmetry.common.data.SuSyRecipeTypes;
import io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.L;
import static com.gregtechceu.gtceu.api.GTValues.LV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES;

/** Dynamic material recipes ported from SuSy's 1.12.2 ore and material handlers. */
public final class SusyOreProcessingRecipes {

    private static final List<OrePrefixEntry> LEGACY_STOCK_ORE_PREFIXES = List.of(
            new OrePrefixEntry("ore", TagPrefix.ore),
            new OrePrefixEntry("ore_endstone", TagPrefix.oreEndstone),
            new OrePrefixEntry("ore_netherrack", TagPrefix.oreNetherrack),
            new OrePrefixEntry("ore_granite", TagPrefix.oreGranite),
            new OrePrefixEntry("ore_diorite", TagPrefix.oreDiorite),
            new OrePrefixEntry("ore_andesite", TagPrefix.oreAndesite),
            new OrePrefixEntry("ore_basalt", TagPrefix.oreBasalt),
            new OrePrefixEntry("ore_marble", TagPrefix.oreMarble),
            new OrePrefixEntry("ore_red_granite", TagPrefix.oreRedGranite),
            new OrePrefixEntry("ore_sand", TagPrefix.oreSand),
            new OrePrefixEntry("ore_red_sand", TagPrefix.oreRedSand));

    private SusyOreProcessingRecipes() {}

    public static void init(Consumer<FinishedRecipe> provider) {
        for (Material material : GTCEuAPI.materialManager.getRegisteredMaterials()) {
            if (material.hasFlag(MaterialFlags.NO_UNIFICATION) ||
                    material.hasFlag(MaterialFlags.DISABLE_MATERIAL_RECIPES)) {
                continue;
            }

            OreProperty oreProperty = material.getProperty(PropertyKey.ORE);
            if (oreProperty != null) {
                ItemStack gemByproduct = ChemicalHelper.get(TagPrefix.gem,
                        oreProperty.getOreByProduct(0, material));
                addEccentricRollCrusherRecipes(provider, material, oreProperty, gemByproduct);
                addBallMillRecipe(provider, material, gemByproduct);
            }

            if (material.hasProperty(SuSyPropertyKey.MILL_BALL)) {
                addMillBallSolidificationRecipe(provider, material);
            }
        }

        // TODO)) Phase 5/8: generate the custom SuSy stone-prefix ERC recipes once
        // their blocks/ore prefixes exist. Anorthosite alone uses multiplier 2.
        // TODO)) Rotary Kiln V2 had no Java/Groovy recipe source in the legacy tree;
        // define its recipe contract before generating new content.
    }

    private static void addEccentricRollCrusherRecipes(Consumer<FinishedRecipe> provider, Material material,
                                                         OreProperty oreProperty, ItemStack gemByproduct) {
        long crushedAmountLong = (long) oreProperty.getOreMultiplier() * 4L;
        if (crushedAmountLong <= 0 || crushedAmountLong > Integer.MAX_VALUE ||
                ChemicalHelper.get(TagPrefix.crushed, material).isEmpty()) {
            return;
        }

        int crushedAmount = (int) crushedAmountLong;
        for (OrePrefixEntry entry : LEGACY_STOCK_ORE_PREFIXES) {
            if (!material.shouldGenerateRecipesFor(entry.prefix()) ||
                    ChemicalHelper.get(entry.prefix(), material).isEmpty()) {
                continue;
            }

            GTRecipeBuilder builder = SuSyRecipeTypes.ECCENTRIC_ROLL_CRUSHER_RECIPES
                    .recipeBuilder(recipeId("erc_" + entry.id(), material))
                    .inputItems(entry.prefix(), material, 1)
                    .outputItems(TagPrefix.crushed, material, crushedAmount)
                    .duration(50)
                    .EUt(24);
            addGemByproductChances(builder, gemByproduct);
            builder.save(provider);
        }
    }

    private static void addBallMillRecipe(Consumer<FinishedRecipe> provider, Material material,
                                           ItemStack gemByproduct) {
        if (ChemicalHelper.get(TagPrefix.crushed, material).isEmpty() ||
                ChemicalHelper.get(TagPrefix.dustImpure, material).isEmpty()) {
            return;
        }

        GTRecipeBuilder builder = SuSyRecipeTypes.BALL_MILL_RECIPES
                .recipeBuilder(recipeId("ball_mill", material))
                .inputItems(TagPrefix.crushed, material, 1)
                .outputItems(TagPrefix.dustImpure, material, 1)
                .inputFluids(SusyMaterials.PreheatedAir.getFluid(1))
                .duration(50)
                .EUt(16);
        addGemByproductChances(builder, gemByproduct);
        builder.save(provider);
    }

    private static void addMillBallSolidificationRecipe(Consumer<FinishedRecipe> provider, Material material) {
        if (!material.hasProperty(PropertyKey.FLUID) ||
                ChemicalHelper.get(SusyTagPrefixes.millBall, material).isEmpty()) {
            return;
        }

        FluidStack solidifyingFluid = material.getProperty(PropertyKey.FLUID).solidifiesFrom(L);
        if (solidifyingFluid.isEmpty()) {
            return;
        }

        FLUID_SOLIDFICATION_RECIPES
                .recipeBuilder(recipeId("solidify_mill_ball", material))
                .notConsumable(GTItems.SHAPE_MOLD_BALL)
                .inputFluids(solidifyingFluid)
                .outputItems(SusyTagPrefixes.millBall, material, 1)
                .duration(200)
                .EUt(VA[LV])
                .save(provider);
    }

    private static void addGemByproductChances(GTRecipeBuilder builder, ItemStack gemByproduct) {
        if (!gemByproduct.isEmpty()) {
            builder.chancedOutput(gemByproduct, 1400, 850)
                    .chancedOutput(gemByproduct, 1400, 850);
        }
    }

    private static ResourceLocation recipeId(String operation, Material material) {
        ResourceLocation materialId = material.getResourceLocation();
        return SuSyValues.susyId("ore_processing/" + operation + "_" + materialId.getNamespace() + "_" +
                materialId.getPath());
    }

    private record OrePrefixEntry(String id, TagPrefix prefix) {}
}
