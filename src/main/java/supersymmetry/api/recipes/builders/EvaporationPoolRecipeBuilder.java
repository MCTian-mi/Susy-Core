package supersymmetry.api.recipes.builders;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.recipeproperties.PrimitiveProperty;
import gregtech.api.util.EnumValidationResult;
import gregtech.api.util.ValidationResult;
import org.jetbrains.annotations.NotNull;
import supersymmetry.api.SusyLog;
import supersymmetry.api.recipes.properties.EvaporationEnergyProperty;

public class EvaporationPoolRecipeBuilder  extends RecipeBuilder<EvaporationPoolRecipeBuilder> {

    protected int Jt = -1;

    public EvaporationPoolRecipeBuilder() {
    }

    public EvaporationPoolRecipeBuilder(EvaporationPoolRecipeBuilder other) {
        super(other);
        this.Jt = other.Jt;
    }

    @Override
    public EvaporationPoolRecipeBuilder copy() {
        return new EvaporationPoolRecipeBuilder(this);
    }

    public EvaporationPoolRecipeBuilder Jt(int Jt) {
        if(Jt <= 0) {
            SusyLog.logger.error("Evaporation Pool required energy cannot be less then or equal to one."
                    , new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
        }
        this.applyProperty(EvaporationEnergyProperty.getInstance(), Jt);
        return this;
    }

    @Override
    public boolean applyProperty(@NotNull String key, Object value) {
        if (key.equals(EvaporationEnergyProperty.KEY)) {
            this.Jt((int) value);
            return true;
        }
        return super.applyProperty(key, value);
    }

    @Override
    public ValidationResult<Recipe> build() {
        if (this.recipePropertyStorage == null || !this.recipePropertyStorage.hasRecipeProperty(EvaporationEnergyProperty.getInstance())) {
            if (Jt <= 0) {
                //use latent heat of vaporization for water w/ 55mol/L in case of recipes with no energy specified, with 40800 / 10000 to give reasonable numbers
                this.Jt(408 * 55 * getFluidInputs().get(0).getAmount() / (100 * (getDuration() == 0 ? 200 : getDuration())));
            }
        }
        this.EUt(-1);
        this.applyProperty(PrimitiveProperty.getInstance(), true);
        return super.build();
    }
}
