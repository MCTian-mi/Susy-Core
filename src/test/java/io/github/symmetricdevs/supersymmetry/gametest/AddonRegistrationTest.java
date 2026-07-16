package io.github.symmetricdevs.supersymmetry.gametest;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.item.MillBallItem;
import io.github.symmetricdevs.supersymmetry.api.unification.ore.SusyTagPrefixes;
import io.github.symmetricdevs.supersymmetry.common.data.SuSyRecipeTypes;
import io.github.symmetricdevs.supersymmetry.common.data.SusyMachines;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@PrefixGameTestTemplate(false)
@GameTestHolder(Supersymmetry.MOD_ID)
public final class AddonRegistrationTest {

    private static final ResourceLocation BALL_MILL_ID = SuSyValues.susyId("ball_mill");
    private static final ResourceLocation EVAPORATION_POOL_ID = SuSyValues.susyId("evaporation_pool");
    private static final ResourceLocation ATTRITION_SCRUBBER_ID = SuSyValues.susyId("attrition_scrubber");
    private static final ResourceLocation CURTAIN_COATER_ID = SuSyValues.susyId("curtain_coater");
    private static final ResourceLocation HOT_ISOSTATIC_PRESS_ID = SuSyValues.susyId("hot_isostatic_press");
    private static final ResourceLocation INDUCTION_FURNACE_ID = SuSyValues.susyId("induction_furnace");
    private static final ResourceLocation METALLURGICAL_CONVERTER_ID = SuSyValues.susyId("metallurgical_converter");
    private static final ResourceLocation REVERBERATORY_FURNACE_ID = SuSyValues.susyId("reverberatory_furnace");
    private static final ResourceLocation RAILROAD_ENGINEERING_STATION_ID = SuSyValues.susyId("railroad_engineering_station");
    private static final ResourceLocation HEAT_RADIATOR_ID = SuSyValues.susyId("heat_radiator");
    private static final ResourceLocation LARGE_FLUID_PUMP_ID = SuSyValues.susyId("large_fluid_pump");
    private static final ResourceLocation MIXER_SETTLER_V2_ID = SuSyValues.susyId("mixer_settler_v2");
    private static final ResourceLocation MIXER_SETTLER_ID = SuSyValues.susyId("mixer_settler");
    private static final ResourceLocation MINING_DRILL_ID = SuSyValues.susyId("mining_drill");
    private static final ResourceLocation GREENHOUSE_ID = SuSyValues.susyId("greenhouse");
    private static final ResourceLocation STEEL_MILL_BALL_ID = GTCEu.id("steel_mill_ball");
    private static final ResourceLocation STEEL_MILL_BALL_RECIPE_ID =
            SuSyValues.susyId("fluid_solidifier/ore_processing/solidify_mill_ball_gtceu_steel");
    private static final ResourceLocation IRON_BALL_MILL_RECIPE_ID =
            SuSyValues.susyId("ball_mill/ore_processing/ball_mill_gtceu_iron");

    @GameTest(template = "empty", batch = "AddonRegistration")
    public static void addonRegistrationSmoke(GameTestHelper helper) {
        helper.assertTrue(GTRegistries.MACHINES.get(BALL_MILL_ID) == SusyMachines.BALL_MILL,
                "Ball Mill machine definition was not registered");
        helper.assertTrue(GTRegistries.MACHINES.get(EVAPORATION_POOL_ID) == SusyMachines.EVAPORATION_POOL,
                "Evaporation Pool machine definition was not registered");
        helper.assertTrue(GTRegistries.MACHINES.get(ATTRITION_SCRUBBER_ID) == SusyMachines.ATTRITION_SCRUBBER,
                "Attrition Scrubber machine definition was not registered");
        helper.assertTrue(GTRegistries.MACHINES.get(CURTAIN_COATER_ID) == SusyMachines.CURTAIN_COATER,
                "Curtain Coater machine definition was not registered");
        helper.assertTrue(GTRegistries.MACHINES.get(HOT_ISOSTATIC_PRESS_ID) == SusyMachines.HOT_ISOSTATIC_PRESS,
                "Hot Isostatic Press machine definition was not registered");

        helper.assertTrue(GTRegistries.MACHINES.get(INDUCTION_FURNACE_ID) == SusyMachines.INDUCTION_FURNACE,
                "Induction Furnace machine definition was not registered");
        helper.assertTrue(GTRegistries.MACHINES.get(METALLURGICAL_CONVERTER_ID) == SusyMachines.METALLURGICAL_CONVERTER,
                "Metallurgical Converter machine definition was not registered");
        helper.assertTrue(GTRegistries.MACHINES.get(REVERBERATORY_FURNACE_ID) == SusyMachines.REVERBERATORY_FURNACE,
                "Reverberatory Furnace machine definition was not registered");
        helper.assertTrue(GTRegistries.MACHINES.get(RAILROAD_ENGINEERING_STATION_ID) == SusyMachines.RAILROAD_ENGINEERING_STATION,
                "Railroad Engineering Station machine definition was not registered");
        helper.assertTrue(GTRegistries.MACHINES.get(HEAT_RADIATOR_ID) == SusyMachines.HEAT_RADIATOR,
                "Heat Radiator machine definition was not registered");
        helper.assertTrue(GTRegistries.MACHINES.get(LARGE_FLUID_PUMP_ID) == SusyMachines.LARGE_FLUID_PUMP,
                "Large Fluid Pump machine definition was not registered");
        helper.assertTrue(GTRegistries.MACHINES.get(MIXER_SETTLER_V2_ID) == SusyMachines.MIXER_SETTLER_V2,
                "Mixer Settler V2 machine definition was not registered");
        helper.assertTrue(GTRegistries.MACHINES.get(MIXER_SETTLER_ID) == SusyMachines.MIXER_SETTLER,
                "Mixer Settler machine definition was not registered");
        helper.assertTrue(GTRegistries.MACHINES.get(MINING_DRILL_ID) == SusyMachines.MINING_DRILL,
                "Mining Drill machine definition was not registered");
        helper.assertTrue(GTRegistries.MACHINES.get(GREENHOUSE_ID) == SusyMachines.GREENHOUSE,
                "Greenhouse machine definition was not registered");

        Item millBallItem = BuiltInRegistries.ITEM.get(STEEL_MILL_BALL_ID);
        helper.assertTrue(BuiltInRegistries.ITEM.containsKey(STEEL_MILL_BALL_ID),
                "Steel mill ball item was not registered");
        helper.assertTrue(millBallItem instanceof MillBallItem,
                "Steel mill ball item does not use MillBallItem");

        ItemStack unifiedMillBall = ChemicalHelper.get(SusyTagPrefixes.millBall, GTMaterials.Steel);
        helper.assertFalse(unifiedMillBall.isEmpty(), "Steel mill ball unification lookup is empty");
        helper.assertTrue(unifiedMillBall.is(millBallItem),
                "Steel mill ball unification lookup resolved to another item");

        Recipe<?> millBallRecipe = helper.getLevel().getRecipeManager().byKey(STEEL_MILL_BALL_RECIPE_ID).orElse(null);
        helper.assertTrue(millBallRecipe instanceof GTRecipe gtRecipe &&
                        gtRecipe.recipeType == GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES,
                "Steel mill ball solidification recipe was not loaded in the fluid solidifier");

        Recipe<?> ballMillRecipe = helper.getLevel().getRecipeManager().byKey(IRON_BALL_MILL_RECIPE_ID).orElse(null);
        helper.assertTrue(ballMillRecipe instanceof GTRecipe gtRecipe &&
                        gtRecipe.recipeType == SuSyRecipeTypes.BALL_MILL_RECIPES,
                "Iron ore-processing recipe was not loaded in the Ball Mill");
        helper.succeed();
    }

    private AddonRegistrationTest() {}
}
