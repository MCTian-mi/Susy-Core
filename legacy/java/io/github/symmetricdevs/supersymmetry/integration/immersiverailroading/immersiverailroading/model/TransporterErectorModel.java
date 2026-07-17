package io.github.symmetricdevs.supersymmetry.integration.immersiverailroading.model;

import cam72cam.immersiverailroading.model.StockModel;
import cam72cam.immersiverailroading.model.components.ComponentProvider;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityTransporterErector;
import io.github.symmetricdevs.supersymmetry.integration.immersiverailroading.model.part.Rocket;
import io.github.symmetricdevs.supersymmetry.integration.immersiverailroading.model.part.TransporterLifter;
import io.github.symmetricdevs.supersymmetry.integration.immersiverailroading.registry.TransporterErectorDefinition;

public class TransporterErectorModel extends StockModel<EntityTransporterErector, TransporterErectorDefinition> {

    public Rocket rocket;
    public TransporterLifter lifter;

    public TransporterErectorModel(TransporterErectorDefinition def) throws Exception {
        super(def);
    }

    @Override
    protected void parseComponents(ComponentProvider provider, TransporterErectorDefinition def) {
        super.parseComponents(provider, def);
        this.rocket = new Rocket(provider, this.base, (stock) -> stock.isRocketLoaded(),
                (stock) -> stock.getLifterAngle());
        this.lifter = new TransporterLifter(provider, this.base, (stock) -> stock.getLifterAngle());
    }
}
