package supersymmetry.api.recipes.builders;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import supersymmetry.api.recipes.properties.BiomeProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A custom recipeBuilder, for allowing us to put our own recipeProperty {@link BiomeProperty} into a recipe
 */
public class BiomeRecipeBuilder extends RecipeBuilder<BiomeRecipeBuilder> {

    public BiomeRecipeBuilder() {
    }

    public BiomeRecipeBuilder(Recipe recipe, RecipeMap<BiomeRecipeBuilder> recipeMap) {
        super(recipe, recipeMap);
    }

    public BiomeRecipeBuilder(RecipeBuilder<BiomeRecipeBuilder> recipeBuilder) {
        super(recipeBuilder);
    }

    @Override
    public BiomeRecipeBuilder copy() {
        return new BiomeRecipeBuilder(this);
    }

    @Override
    public boolean applyProperty(@NotNull String key, Object value) {
        if (key.equals(BiomeProperty.KEY)) {
            if (value instanceof BiomeProperty.BiomePropertyList list) {
                BiomeProperty.BiomePropertyList biomes = getBiomePropertyList();
                if (biomes == BiomeProperty.BiomePropertyList.EMPTY_LIST) {
                    biomes = new BiomeProperty.BiomePropertyList();
                    this.applyProperty(BiomeProperty.getInstance(), biomes);
                }
                biomes.merge(list);
                return true;
            }
            return false;
        }
        return super.applyProperty(key, value);
    }

    public BiomeRecipeBuilder biomes(String... biomes) {
        return biomes(false, biomes);
    }

    public BiomeRecipeBuilder biomeTypes(String... biomeTypes) {
        return biomeTypes(false, biomeTypes);
    }

    private BiomeRecipeBuilder biomes(boolean toBlacklist, String... biomeRLs) {
        List<Biome> biomes = new ArrayList<>();
        for (String biomeRL : biomeRLs) {
            Biome biome = Biome.REGISTRY.getObject(new ResourceLocation(biomeRL));
            if (biome != null) {
                biomes.add(biome);
            } else {
                throw new NoSuchElementException("No biome with ResouceLocation \"" + biomeRL + "\" found");
            }
        }
        return biomesInternal(toBlacklist, biomes);
    }

    private BiomeRecipeBuilder biomeTypes(boolean toBlacklist, String... biomeTypeRLs) {
        List<BiomeDictionary.Type> biomeTypes = new ArrayList<>();
        for (String biomeTypeRL : biomeTypeRLs) {
            BiomeDictionary.Type biomeType = BiomeDictionary.Type.getType(biomeTypeRL); // TODO Forge why???
            biomeTypes.add(biomeType);
        }
        return biomeTypesInternal(toBlacklist, biomeTypes);
    }

    private BiomeRecipeBuilder biomesInternal(boolean toBlacklist, List<Biome> biomes) {
        BiomeProperty.BiomePropertyList biomePropertyList = getBiomePropertyList();
        if (biomePropertyList == BiomeProperty.BiomePropertyList.EMPTY_LIST) {
            biomePropertyList = new BiomeProperty.BiomePropertyList();
            this.applyProperty(BiomeProperty.getInstance(), biomePropertyList);
        }
        for (Biome biome : biomes) {
            biomePropertyList.add(biome, toBlacklist);
        }
        return this;
    }

    private BiomeRecipeBuilder biomeTypesInternal(boolean toBlacklist, List<BiomeDictionary.Type> biomeTypes) {
        BiomeProperty.BiomePropertyList biomePropertyList = getBiomePropertyList();
        if (biomePropertyList == BiomeProperty.BiomePropertyList.EMPTY_LIST) {
            biomePropertyList = new BiomeProperty.BiomePropertyList();
            this.applyProperty(BiomeProperty.getInstance(), biomePropertyList);
        }
        for (BiomeDictionary.Type biomeType : biomeTypes) {
            biomePropertyList.add(biomeType, toBlacklist);
        }
        return this;
    }

    public BiomeProperty.BiomePropertyList getBiomePropertyList() {
        return this.recipePropertyStorage == null ? BiomeProperty.BiomePropertyList.EMPTY_LIST :
                this.recipePropertyStorage.getRecipePropertyValue(BiomeProperty.getInstance(),
                        BiomeProperty.BiomePropertyList.EMPTY_LIST);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("biomes", getBiomePropertyList().toString())
                .appendSuper(super.toString())
                .toString();
    }
}
