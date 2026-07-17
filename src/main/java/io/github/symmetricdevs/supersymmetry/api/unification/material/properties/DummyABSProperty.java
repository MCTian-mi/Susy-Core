package io.github.symmetricdevs.supersymmetry.api.unification.material.properties;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.AlloyBlastProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;
import com.gregtechceu.gtceu.data.recipe.misc.alloyblast.AlloyBlastRecipeProducer;

public class DummyABSProperty extends AlloyBlastProperty {

    public DummyABSProperty(int temperature) {
        super(temperature);
        this.setRecipeProducer(new AlloyBlastRecipeProducer() {

            @Override
            public void produce(@NotNull Material material, @NotNull BlastProperty blastProperty) {
                // Nothing!
            }
        });
    }

    public DummyABSProperty() {
        this(373);
    }
}
