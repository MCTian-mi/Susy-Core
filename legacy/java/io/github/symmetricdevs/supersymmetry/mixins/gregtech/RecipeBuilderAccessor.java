package io.github.symmetricdevs.supersymmetry.mixins.gregtech;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.gregtechceu.gtceu.api.recipe.RecipeBuilder;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

@Mixin(value = RecipeBuilder.class, remap = false)
public interface RecipeBuilderAccessor {

    @Accessor("onBuildAction")
    Consumer<RecipeBuilder<?>> getOnBuildAction();

    @Accessor("GTRecipeType")
    GTRecipeType<?> getRecipeMap();
}
