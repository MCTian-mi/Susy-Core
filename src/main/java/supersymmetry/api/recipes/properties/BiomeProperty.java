package supersymmetry.api.recipes.properties;

import gregtech.api.recipes.recipeproperties.RecipeProperty;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BiomeProperty extends RecipeProperty<BiomeProperty.BiomePropertyList> {

    public static final String KEY = "biome";

    private static BiomeProperty INSTANCE;

    private BiomeProperty() {
        super(KEY, BiomePropertyList.class);
    }

    public static BiomeProperty getInstance() {
        if (INSTANCE == null)
            INSTANCE = new BiomeProperty();
        return INSTANCE;
    }

    private static String getBiomesForRecipe(List<Biome> value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.size(); i++) {
            builder.append(value.get(i).getBiomeName());
            if (i != value.size() - 1)
                builder.append(", ");
        }
        String str = builder.toString();

        if (str.length() >= 13) {
            str = str.substring(0, 10) + "..";
        }
        return str;
    }

    private static String getBiomeTypesForRecipe(List<BiomeDictionary.Type> value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.size(); i++) {
            builder.append(value.get(i).getName());
            if (i != value.size() - 1)
                builder.append(", ");
        }
        String str = builder.toString();

        if (str.length() >= 13) {
            str = str.substring(0, 10) + "..";
        }
        return str;
    }

    @SideOnly(Side.CLIENT)
    public void getTooltipStrings(List<String> tooltip, int mouseX, int mouseY, Object value) {
        // TODO
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
        BiomePropertyList list = castValue(value);

        if (list.whiteListBiomes.size() > 0)
            minecraft.fontRenderer.drawString(I18n.format("gregtech.recipe.biomes",
                    getBiomesForRecipe(castValue(value).whiteListBiomes)), x, y, color);
        if (list.blackListBiomes.size() > 0)
            minecraft.fontRenderer.drawString(I18n.format("gregtech.recipe.biomes_blocked",
                    getBiomesForRecipe(castValue(value).blackListBiomes)), x, y, color);

        if (list.whiteListBiomeTypes.size() > 0)
            minecraft.fontRenderer.drawString(I18n.format("gregtech.recipe.biomeTypes",
                    getBiomeTypesForRecipe(castValue(value).whiteListBiomeTypes)), x, y, color);
        if (list.blackListBiomeTypes.size() > 0)
            minecraft.fontRenderer.drawString(I18n.format("gregtech.recipe.biomeTypes_blocked",
                    getBiomeTypesForRecipe(castValue(value).blackListBiomeTypes)), x, y, color);
    }


    // It would've been better to have one list and swap between blacklist and whitelist, but that would've been
    // a bit awkward to apply to the property in practice.
    public static class BiomePropertyList {

        public static BiomePropertyList EMPTY_LIST = new BiomePropertyList();

        public final List<Biome> whiteListBiomes = new ObjectArrayList<>();
        public final List<BiomeDictionary.Type> whiteListBiomeTypes = new ObjectArrayList<>();
        public final List<Biome> blackListBiomes = new ObjectArrayList<>();
        public final List<BiomeDictionary.Type> blackListBiomeTypes = new ObjectArrayList<>();

        public void add(BiomeDictionary.Type type, boolean toBlacklist) {
            if (toBlacklist) {
                blackListBiomeTypes.add(type);
                whiteListBiomeTypes.remove(type);
            } else {
                whiteListBiomeTypes.add(type);
                blackListBiomeTypes.remove(type);
            }
        }

        public void add(Biome biome, boolean toBlacklist) {
            if (toBlacklist) {
                blackListBiomes.add(biome);
                whiteListBiomes.remove(biome);
            } else {
                whiteListBiomes.add(biome);
                blackListBiomes.remove(biome);
            }
        }

        public void merge(@NotNull BiomeProperty.BiomePropertyList list) {
            this.whiteListBiomes.addAll(list.whiteListBiomes);
            this.blackListBiomes.addAll(list.blackListBiomes);
            this.whiteListBiomeTypes.addAll(list.whiteListBiomeTypes);
            this.blackListBiomeTypes.addAll(list.blackListBiomeTypes);
        }

        public boolean checkBiome(Biome biome) {
            if (blackListBiomes.contains(biome) || !whiteListBiomes.contains(biome)) return false;
            boolean foundWhitelist = whiteListBiomeTypes.isEmpty();
            for (BiomeDictionary.Type type : BiomeDictionary.getTypes(biome)) {
                if (blackListBiomeTypes.contains(type)) return false;
                if (!foundWhitelist && whiteListBiomeTypes.contains(type)) foundWhitelist = true;
            }
            return foundWhitelist;
        }
    }
}
