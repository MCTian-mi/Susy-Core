package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

import static io.github.symmetricdevs.supersymmetry.api.util.SuSyUtility.susyId;

/**
 * Stub — the 1.12.2 version used an OBJModelRender child on bipedBody.
 * OBJ rendering and ModelBiped are removed in 1.20.1.
 */
public class JetWingpackModel extends HumanoidModel<LivingEntity> {

    public static final JetWingpackModel INSTANCE = new JetWingpackModel(null);

    public JetWingpackModel(ModelPart modelPart) {
        super(modelPart);
    }
}
