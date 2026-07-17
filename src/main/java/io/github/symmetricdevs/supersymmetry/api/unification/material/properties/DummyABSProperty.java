package io.github.symmetricdevs.supersymmetry.api.unification.material.properties;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.recipes.FinishedRecipe;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.AlloyBlastProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;
import com.gregtechceu.gtceu.data.recipe.misc.alloyblast.AlloyBlastRecipeProducer;

import java.util.function.Consumer;

public class DummyABSProperty extends AlloyBlastProperty {

    public DummyABSProperty(int temperature) {
        super(temperature);
        this.setRecipeProducer(new AlloyBlastRecipeProducer() {

            @Override
            public void produce(@NotNull Material material, @NotNull BlastProperty blastProperty,
                                Consumer<FinishedRecipe> provider) {
                // Nothing!
            }
        });
    }

    public DummyABSProperty() {
        this(373);
    }
}
