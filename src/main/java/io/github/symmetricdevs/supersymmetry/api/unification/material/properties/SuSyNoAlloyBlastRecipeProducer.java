package io.github.symmetricdevs.supersymmetry.api.unification.material.properties;

import java.util.function.Consumer;

import net.minecraft.data.recipes.FinishedRecipe;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;
import com.gregtechceu.gtceu.data.recipe.misc.alloyblast.AlloyBlastRecipeProducer;

/**
 * An {@link AlloyBlastRecipeProducer} that generates no Alloy Blast Smelter
 * recipes. Installed onto {@code AlloyBlastProperty} (via
 * {@code setRecipeProducer}) for {@code CONTINUOUSLY_CAST} alloys, whose molten
 * output is produced by the continuous caster instead of the ABS.
 * <p>
 * Recovers the 1.12.2 {@code DummyABSProperty} (GCYM) as a Modern subclass of the
 * now-native {@link AlloyBlastRecipeProducer}.
 */
public class SuSyNoAlloyBlastRecipeProducer extends AlloyBlastRecipeProducer {

    public static final SuSyNoAlloyBlastRecipeProducer INSTANCE = new SuSyNoAlloyBlastRecipeProducer();

    @Override
    public void produce(@NotNull Material material, @NotNull BlastProperty property,
                        Consumer<FinishedRecipe> provider) {
        // Nothing — continuously cast alloys have no Alloy Blast Smelter recipe.
    }
}
