package io.github.symmetricdevs.supersymmetry.client.renderer.armor;

import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.common.item.armor.SuSyGeoArmorItem;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.function.Supplier;

/**
 * Shared GeckoLib armor renderer for all SuSy breathing armor sets.
 *
 * <p>The existing SuSy {@code .geo.json} files use Blockbench-standard bone names
 * ({@code head}, {@code body}, {@code rightArm}, {@code leftArm}, {@code rightLeg},
 * {@code leftLeg}, {@code rightBoot}, {@code leftBoot}) rather than GeckoLib's
 * default {@code armorHead}/{@code armorBody} names, so this renderer remaps the
 * bone accessors.</p>
 *
 * <p>Also uses {@link RenderType#entityTranslucent(ResourceLocation)} so that semi-transparent
 * parts of the armor texture (gas-mask lenses, astronaut visor, etc.) render
 * correctly; the default {@code armorCutoutNoCull} discards alpha.</p>
 */
public class SuSyGeoArmorRenderer extends GeoArmorRenderer<SuSyGeoArmorItem> {

    private final Supplier<ResourceLocation> modelResource;
    private final Supplier<ResourceLocation> textureResource;
    private final Supplier<ResourceLocation> animationResource;

    public SuSyGeoArmorRenderer(String geoName, String texturePath) {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(SuSyGeoArmorItem animatable) {
                return SuSyValues.susyId("geo/" + geoName + ".geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(SuSyGeoArmorItem animatable) {
                return SuSyValues.susyId(texturePath);
            }

            @Override
            public ResourceLocation getAnimationResource(SuSyGeoArmorItem animatable) {
                return SuSyValues.susyId("animations/dummy.animation.json");
            }
        });
        this.modelResource = () -> SuSyValues.susyId("geo/" + geoName + ".geo.json");
        this.textureResource = () -> SuSyValues.susyId(texturePath);
        this.animationResource = () -> SuSyValues.susyId("animations/dummy.animation.json");
    }

    public SuSyGeoArmorRenderer(String geoName, String texturePath, String animationPath) {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(SuSyGeoArmorItem animatable) {
                return SuSyValues.susyId("geo/" + geoName + ".geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(SuSyGeoArmorItem animatable) {
                return SuSyValues.susyId(texturePath);
            }

            @Override
            public ResourceLocation getAnimationResource(SuSyGeoArmorItem animatable) {
                return SuSyValues.susyId(animationPath);
            }
        });
        this.modelResource = () -> SuSyValues.susyId("geo/" + geoName + ".geo.json");
        this.textureResource = () -> SuSyValues.susyId(texturePath);
        this.animationResource = () -> SuSyValues.susyId(animationPath);
    }

    @Override
    public RenderType getRenderType(SuSyGeoArmorItem animatable, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        // Use the entity translucent render type so semi-transparent armor pixels
        // (gas-mask lenses, astronaut visor, etc.) are blended rather than cut out.
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public @Nullable GeoBone getHeadBone() {
        return getGeoModel().getBone("head").orElse(null);
    }

    @Override
    public @Nullable GeoBone getBodyBone() {
        return getGeoModel().getBone("body").orElse(null);
    }

    @Override
    public @Nullable GeoBone getRightArmBone() {
        return getGeoModel().getBone("rightArm").orElse(null);
    }

    @Override
    public @Nullable GeoBone getLeftArmBone() {
        return getGeoModel().getBone("leftArm").orElse(null);
    }

    @Override
    public @Nullable GeoBone getRightLegBone() {
        return getGeoModel().getBone("rightLeg").orElse(null);
    }

    @Override
    public @Nullable GeoBone getLeftLegBone() {
        return getGeoModel().getBone("leftLeg").orElse(null);
    }

    @Override
    public @Nullable GeoBone getRightBootBone() {
        return getGeoModel().getBone("rightBoot").orElse(null);
    }

    @Override
    public @Nullable GeoBone getLeftBootBone() {
        return getGeoModel().getBone("leftBoot").orElse(null);
    }
}
