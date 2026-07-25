package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;

import static io.github.symmetricdevs.supersymmetry.api.util.SuSyUtility.susyId;

/**
 * Stub — the 1.12.2 version used OBJ models (OBJModelRender) applied as
 * ModelBiped children. OBJ rendering and the ModelBiped base class are
 * removed in 1.20.1. Re-implement when OBJ model importing is re-added
 * or replace with GeckoLib 4/Bedrock JSON models.
 */
public class BreathingApparatusModel extends HumanoidModel<LivingEntity> {

    public BreathingApparatusModel(ModelPart modelPart, String name, EquipmentSlot slot) {
        super(modelPart);
    }

    public ResourceLocation modelLocationFromPart(String armor, String model) {
        return susyId("models/armor/" + armor + "_" + model + ".obj");
    }
}
