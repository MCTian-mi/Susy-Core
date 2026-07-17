package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;

import static io.github.symmetricdevs.supersymmetry.api.util.SuSyUtility.susyId;

/**
 * Stub — the 1.12.2 version used OBJ models (OBJModelRender) as children.
 * OBJ rendering and ModelBiped are removed in 1.20.1.
 */
public class SimpleBreathingApparatusModel extends HumanoidModel<LivingEntity> {

    public SimpleBreathingApparatusModel(ModelPart modelPart, String name, EquipmentSlot slot) {
        super(modelPart);
    }

    public ResourceLocation modelLocationFromPart(String armor, String model) {
        return susyId("models/armor/" + armor + "_" + model + ".obj");
    }
}
