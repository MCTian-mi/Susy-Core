package io.github.symmetricdevs.supersymmetry.client.renderer.handler;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Baked vanilla-model replacements for the legacy OBJ breathing armor.
 *
 * <p>Each lookup bakes a new model wrapper from the current entity model set. This avoids
 * retaining stale {@code ModelPart}s after a resource reload while the layer definitions stay
 * registered through Forge's normal client lifecycle.</p>
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Supersymmetry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BreathingArmorModels {

    private static final ModelLayerLocation SIMPLE_GAS_MASK = layer("simple_gas_mask");
    private static final ModelLayerLocation GAS_MASK = layer("gas_mask");
    private static final ModelLayerLocation GAS_TANK = layer("gas_tank");
    private static final ModelLayerLocation PROTECTIVE_SUIT = layer("protective_suit");
    private static final ModelLayerLocation PROTECTIVE_SUIT_BOOTS = layer("protective_suit_boots");
    private static final ModelLayerLocation JET_WINGPACK = layer("jet_wingpack");

    private BreathingArmorModels() {
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SIMPLE_GAS_MASK, BreathingArmorModels::createSimpleGasMaskLayer);
        event.registerLayerDefinition(GAS_MASK, BreathingArmorModels::createGasMaskLayer);
        event.registerLayerDefinition(GAS_TANK, BreathingArmorModels::createGasTankLayer);
        event.registerLayerDefinition(PROTECTIVE_SUIT, BreathingArmorModels::createProtectiveSuitLayer);
        event.registerLayerDefinition(PROTECTIVE_SUIT_BOOTS, BreathingArmorModels::createProtectiveSuitBootsLayer);
        event.registerLayerDefinition(JET_WINGPACK, BreathingArmorModels::createJetWingpackLayer);
    }

    public static HumanoidModel<LivingEntity> simpleGasMask(HumanoidModel<?> source, EquipmentSlot slot) {
        return prepare(SIMPLE_GAS_MASK, source, slot);
    }

    public static HumanoidModel<LivingEntity> gasMask(HumanoidModel<?> source, EquipmentSlot slot) {
        return prepare(GAS_MASK, source, slot);
    }

    public static HumanoidModel<LivingEntity> gasTank(HumanoidModel<?> source, EquipmentSlot slot) {
        return prepare(GAS_TANK, source, slot);
    }

    public static HumanoidModel<LivingEntity> protectiveSuit(HumanoidModel<?> source, EquipmentSlot slot) {
        return prepare(slot == EquipmentSlot.FEET ? PROTECTIVE_SUIT_BOOTS : PROTECTIVE_SUIT, source, slot);
    }

    public static HumanoidModel<LivingEntity> jetWingpack(HumanoidModel<?> source, EquipmentSlot slot) {
        return prepare(JET_WINGPACK, source, slot);
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Supersymmetry.MOD_ID, name), "main");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static HumanoidModel<LivingEntity> prepare(ModelLayerLocation layer, HumanoidModel<?> source,
                                                       EquipmentSlot slot) {
        var model = new HumanoidModel<LivingEntity>(Minecraft.getInstance().getEntityModels().bakeLayer(layer));
        ((HumanoidModel) source).copyPropertiesTo(model);
        setPartVisibility(model, slot);
        return model;
    }

    private static void setPartVisibility(HumanoidModel<?> model, EquipmentSlot slot) {
        model.head.visible = false;
        model.hat.visible = false;
        model.body.visible = false;
        model.rightArm.visible = false;
        model.leftArm.visible = false;
        model.rightLeg.visible = false;
        model.leftLeg.visible = false;

        switch (slot) {
            case HEAD -> model.head.visible = true;
            case CHEST -> {
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
            }
            case LEGS -> {
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            case FEET -> {
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            default -> {
            }
        }
    }

    private static LayerDefinition createSimpleGasMaskLayer() {
        var parts = humanoidParts();
        parts.head.addOrReplaceChild("simple_mask", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, -5.0F, -5.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.15F))
                .texOffs(0, 6).addBox(-1.5F, -2.0F, -6.0F, 3.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(10, 6).addBox(-4.0F, -4.0F, -4.75F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(14, 6).addBox(3.0F, -4.0F, -4.75F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.ZERO);
        return LayerDefinition.create(parts.mesh, 64, 64);
    }

    private static LayerDefinition createGasMaskLayer() {
        var parts = humanoidParts();
        parts.head.addOrReplaceChild("gas_mask", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -7.0F, -5.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(0, 10).addBox(-3.5F, -5.5F, -5.75F, 3.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(8, 10).addBox(0.5F, -5.5F, -5.75F, 3.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 0).addBox(-1.5F, -2.0F, -6.5F, 3.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(16, 6).addBox(-5.0F, -3.0F, -5.5F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(24, 6).addBox(3.0F, -3.0F, -5.5F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.ZERO);
        return LayerDefinition.create(parts.mesh, 64, 64);
    }

    private static LayerDefinition createGasTankLayer() {
        var parts = humanoidParts();
        parts.body.addOrReplaceChild("gas_tank", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.5F, 1.0F, 2.0F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.2F))
                .texOffs(10, 0).addBox(0.5F, 1.0F, 2.0F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.2F))
                .texOffs(0, 12).addBox(-4.5F, 0.0F, -2.5F, 1.0F, 10.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(12, 12).addBox(3.5F, 0.0F, -2.5F, 1.0F, 10.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-2.5F, 0.0F, 3.5F, 5.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.ZERO);
        return LayerDefinition.create(parts.mesh, 64, 64);
    }

    private static LayerDefinition createProtectiveSuitLayer() {
        var parts = humanoidParts();
        var outer = new CubeDeformation(0.3F);
        var shell = new CubeDeformation(0.5F);

        parts.head.addOrReplaceChild("helmet", CubeListBuilder.create()
                .texOffs(32, 20).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, outer)
                .texOffs(2, 2).addBox(-1.1F, -6.3F, -5.2F, 2.2F, 2.2F, 1.1F, CubeDeformation.NONE)
                .texOffs(113, 104).addBox(-3.3F, -6.3F, -5.2F, 1.1F, 2.2F, 1.1F, CubeDeformation.NONE)
                .texOffs(113, 104).addBox(2.2F, -6.3F, -5.2F, 1.1F, 2.2F, 1.1F, CubeDeformation.NONE), PartPose.ZERO);
        parts.body.addOrReplaceChild("suit_body", CubeListBuilder.create()
                .texOffs(0, 112).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, outer)
                .texOffs(0, 96).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, shell)
                .texOffs(60, 105).addBox(-3.0F, 2.0F, 2.0F, 2.0F, 9.0F, 2.0F, outer)
                .texOffs(68, 105).addBox(1.0F, 2.0F, 2.0F, 2.0F, 9.0F, 2.0F, outer), PartPose.ZERO);
        parts.rightArm.addOrReplaceChild("right_sleeve", CubeListBuilder.create()
                .texOffs(24, 112).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, outer)
                .texOffs(112, 86).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, shell), PartPose.ZERO);
        parts.leftArm.addOrReplaceChild("left_sleeve", CubeListBuilder.create()
                .texOffs(40, 112).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, outer)
                .texOffs(112, 62).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, shell), PartPose.ZERO);
        parts.rightLeg.addOrReplaceChild("right_suit_leg", CubeListBuilder.create()
                .texOffs(56, 112).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, outer)
                .texOffs(112, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, shell), PartPose.ZERO);
        parts.leftLeg.addOrReplaceChild("left_suit_leg", CubeListBuilder.create()
                .texOffs(56, 96).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, outer)
                .texOffs(112, 23).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, shell), PartPose.ZERO);
        return LayerDefinition.create(parts.mesh, 128, 128);
    }

    private static LayerDefinition createProtectiveSuitBootsLayer() {
        var parts = humanoidParts();
        var outer = new CubeDeformation(0.3F);
        var shell = new CubeDeformation(0.5F);

        parts.rightLeg.addOrReplaceChild("right_boot", CubeListBuilder.create()
                .texOffs(56, 120).addBox(-2.2F, 8.0F, -2.4F, 4.4F, 4.0F, 4.8F, outer)
                .texOffs(112, 0).addBox(-2.0F, 8.0F, -2.0F, 4.0F, 4.0F, 4.0F, shell), PartPose.ZERO);
        parts.leftLeg.addOrReplaceChild("left_boot", CubeListBuilder.create()
                .texOffs(56, 104).addBox(-2.2F, 8.0F, -2.4F, 4.4F, 4.0F, 4.8F, outer)
                .texOffs(112, 23).addBox(-2.0F, 8.0F, -2.0F, 4.0F, 4.0F, 4.0F, shell), PartPose.ZERO);
        return LayerDefinition.create(parts.mesh, 128, 128);
    }

    private static LayerDefinition createJetWingpackLayer() {
        var parts = humanoidParts();
        var outer = new CubeDeformation(0.25F);

        // Backpack thruster mounted on the torso back, with two folded wings sweeping down and out.
        parts.body.addOrReplaceChild("wingpack", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, 1.0F, 2.0F, 6.0F, 8.0F, 3.0F, outer)
                .texOffs(0, 12).addBox(-1.5F, 9.0F, 2.5F, 3.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.ZERO);
        parts.body.addOrReplaceChild("right_wing", CubeListBuilder.create()
                .texOffs(18, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 11.0F, 16.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(-3.0F, 1.5F, 4.5F, 0.0F, 0.35F, 0.15F));
        parts.body.addOrReplaceChild("left_wing", CubeListBuilder.create()
                .texOffs(18, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 1.0F, 11.0F, 16.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(3.0F, 1.5F, 4.5F, 0.0F, -0.35F, -0.15F));
        return LayerDefinition.create(parts.mesh, 64, 64);
    }

    private static HumanoidParts humanoidParts() {
        var mesh = new MeshDefinition();
        var root = mesh.getRoot();
        var head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        var body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        var rightArm = root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        var leftArm = root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        var rightLeg = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        var leftLeg = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        return new HumanoidParts(mesh, head, body, rightArm, leftArm, rightLeg, leftLeg);
    }

    private record HumanoidParts(MeshDefinition mesh, PartDefinition head, PartDefinition body,
                                 PartDefinition rightArm, PartDefinition leftArm,
                                 PartDefinition rightLeg, PartDefinition leftLeg) {
    }
}
