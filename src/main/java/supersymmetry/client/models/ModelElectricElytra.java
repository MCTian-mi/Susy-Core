package supersymmetry.client.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelElectricElytra extends ModelBiped {

    public static final ModelElectricElytra INSTANCE = new ModelElectricElytra();

    private final ModelRenderer Thingy;
    private final ModelRenderer bone2;
    private final ModelRenderer bone;
    private final ModelRenderer bone4;
    private final ModelRenderer bone3;

    public ModelElectricElytra() {
        textureWidth = 256;
        textureHeight = 256;

        Thingy = new ModelRenderer(this);
        Thingy.setRotationPoint(0.0F, 24.0F, 0.0F);
        Thingy.cubeList.add(new ModelBox(Thingy, 32, 23, -4.0F, -23.0F, 2.0F, 8, 10, 4, 0.0F, false));
        Thingy.cubeList.add(new ModelBox(Thingy, 0, 0, -40.0F, -23.0F, 6.01F, 80, 23, 0, 0.0F, false));
        Thingy.cubeList.add(new ModelBox(Thingy, 0, 23, 32.0F, -13.0F, 2.0F, 0, 17, 8, 0.0F, false));
        Thingy.cubeList.add(new ModelBox(Thingy, 16, 23, -32.0F, -13.0F, 2.0F, 0, 17, 8, 0.0F, false));

        bone2 = new ModelRenderer(this);
        bone2.setRotationPoint(-9.0F, 0.0F, 0.0F);
        Thingy.addChild(bone2);
        bone2.cubeList.add(new ModelBox(bone2, 0, 48, -5.0F, -16.0F, 0.0F, 4, 13, 4, 0.0F, false));
        bone2.cubeList.add(new ModelBox(bone2, 40, 54, -4.0F, -14.0F, 4.0F, 2, 9, 2, 0.0F, false));

        bone = new ModelRenderer(this);
        bone.setRotationPoint(-15.0F, 0.0F, 0.0F);
        Thingy.addChild(bone);
        bone.cubeList.add(new ModelBox(bone, 32, 37, -5.0F, -16.0F, 0.0F, 4, 13, 4, 0.0F, false));
        bone.cubeList.add(new ModelBox(bone, 32, 54, -4.0F, -14.0F, 4.0F, 2, 9, 2, 0.0F, false));

        bone4 = new ModelRenderer(this);
        bone4.setRotationPoint(30.0F, 0.0F, 0.0F);
        bone.addChild(bone4);
        bone4.cubeList.add(new ModelBox(bone4, 48, 37, -5.0F, -16.0F, 0.0F, 4, 13, 4, 0.0F, false));
        bone4.cubeList.add(new ModelBox(bone4, 56, 23, -4.0F, -14.0F, 4.0F, 2, 9, 2, 0.0F, false));

        bone3 = new ModelRenderer(this);
        bone3.setRotationPoint(21.0F, 0.0F, 0.0F);
        Thingy.addChild(bone3);
        bone3.cubeList.add(new ModelBox(bone3, 16, 48, -5.0F, -16.0F, 0.0F, 4, 13, 4, 0.0F, false));
        bone3.cubeList.add(new ModelBox(bone3, 48, 54, -4.0F, -14.0F, 4.0F, 2, 9, 2, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Thingy.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
