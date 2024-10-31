package supersymmetry.client.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ModelElectricElytra extends ModelBiped {

    public static final ModelElectricElytra INSTANCE = new ModelElectricElytra();

    private final ModelRenderer Thingy;
    private final ModelRenderer wingLeft;
    private final ModelRenderer bone5;
    private final ModelRenderer cube_r1;
    private final ModelRenderer bone3;
    private final ModelRenderer bone2;
    private final ModelRenderer wingRight;
    private final ModelRenderer bone4;
    private final ModelRenderer cube_r2;
    private final ModelRenderer bone6;
    private final ModelRenderer bone7;

    public ModelElectricElytra() {
        textureWidth = 128;
        textureHeight = 128;

        Thingy = new ModelRenderer(this);
        Thingy.setRotationPoint(0.0F, 24.0F, 0.0F);
        Thingy.cubeList.add(new ModelBox(Thingy, 34, 0, -4.0F, -23.0F, 2.0F, 8, 10, 7, 0.0F, false));

        wingLeft = new ModelRenderer(this);
        wingLeft.setRotationPoint(4.0F, -23.0F, 6.01F);
        Thingy.addChild(wingLeft);
        wingLeft.cubeList.add(new ModelBox(wingLeft, 0, 0, 0.0F, 0.0F, 0.0F, 16, 18, 1, 0.0F, false));
        wingLeft.cubeList.add(new ModelBox(wingLeft, 48, 46, 16.0F, 12.0F, -1.01F, 2, 6, 3, 0.0F, false));

        bone5 = new ModelRenderer(this);
        bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
        wingLeft.addChild(bone5);
        setRotationAngle(bone5, 0.0F, 0.0F, 0.6435F);
        bone5.cubeList.add(new ModelBox(bone5, 34, 19, 0.0F, 1.7326F, 1.0F, 22, 2, 0, 0.0F, false));
        bone5.cubeList.add(new ModelBox(bone5, 34, 21, 0.0F, 0.0F, -0.01F, 22, 2, 0, 0.0F, false));

        cube_r1 = new ModelRenderer(this);
        cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        bone5.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.523F, 0.0F, 0.0F);
        cube_r1.cubeList.add(new ModelBox(cube_r1, 34, 17, 0.0F, 0.0F, 0.0F, 22, 2, 0, 0.0F, false));

        bone3 = new ModelRenderer(this);
        bone3.setRotationPoint(6.0F, 22.0F, -6.01F);
        wingLeft.addChild(bone3);
        bone3.cubeList.add(new ModelBox(bone3, 34, 29, -5.0F, -16.0F, 0.0F, 4, 13, 4, 0.0F, false));
        bone3.cubeList.add(new ModelBox(bone3, 50, 29, -4.0F, -14.0F, 4.0F, 2, 9, 2, 0.0F, false));

        bone2 = new ModelRenderer(this);
        bone2.setRotationPoint(12.0F, 22.0F, -6.01F);
        wingLeft.addChild(bone2);
        bone2.cubeList.add(new ModelBox(bone2, 0, 38, -5.0F, -16.0F, 0.0F, 4, 13, 4, 0.0F, false));
        bone2.cubeList.add(new ModelBox(bone2, 0, 55, -4.0F, -14.0F, 4.0F, 2, 9, 2, 0.0F, false));

        wingRight = new ModelRenderer(this);
        wingRight.setRotationPoint(-4.0F, -23.0F, 6.01F);
        Thingy.addChild(wingRight);
        wingRight.cubeList.add(new ModelBox(wingRight, 0, 19, -16.0F, 0.0F, 0.0F, 16, 18, 1, 0.0F, true));
        wingRight.cubeList.add(new ModelBox(wingRight, 8, 55, -18.0F, 12.0F, -1.01F, 2, 6, 3, 0.0F, false));

        bone4 = new ModelRenderer(this);
        bone4.setRotationPoint(0.0F, 0.0F, 0.0F);
        wingRight.addChild(bone4);
        setRotationAngle(bone4, 0.0F, 0.0F, -0.6435F);
        bone4.cubeList.add(new ModelBox(bone4, 34, 25, -22.0F, 1.7326F, 1.0F, 22, 2, 0, 0.0F, false));
        bone4.cubeList.add(new ModelBox(bone4, 34, 27, -22.0F, 0.0F, -0.01F, 22, 2, 0, 0.0F, false));

        cube_r2 = new ModelRenderer(this);
        cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
        bone4.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.523F, 0.0F, 0.0F);
        cube_r2.cubeList.add(new ModelBox(cube_r2, 34, 23, -22.0F, 0.0F, 0.0F, 22, 2, 0, 0.0F, false));

        bone6 = new ModelRenderer(this);
        bone6.setRotationPoint(-6.0F, 22.0F, -6.01F);
        wingRight.addChild(bone6);
        bone6.cubeList.add(new ModelBox(bone6, 16, 38, 1.0F, -16.0F, 0.0F, 4, 13, 4, 0.0F, false));
        bone6.cubeList.add(new ModelBox(bone6, 18, 55, 2.0F, -14.0F, 4.0F, 2, 9, 2, 0.0F, false));

        bone7 = new ModelRenderer(this);
        bone7.setRotationPoint(-12.0F, 22.0F, -6.01F);
        wingRight.addChild(bone7);
        bone7.cubeList.add(new ModelBox(bone7, 32, 46, 1.0F, -16.0F, 0.0F, 4, 13, 4, 0.0F, false));
        bone7.cubeList.add(new ModelBox(bone7, 48, 55, 2.0F, -14.0F, 4.0F, 2, 9, 2, 0.0F, false));
    }

    @Override
    public void render(@NotNull Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Thingy.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
