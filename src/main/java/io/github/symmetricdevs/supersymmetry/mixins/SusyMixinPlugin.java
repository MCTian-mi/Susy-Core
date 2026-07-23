package io.github.symmetricdevs.supersymmetry.mixins;

import net.minecraftforge.fml.loading.LoadingModList;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Gates optional-mod mixins so their absent target classes never crash mixin application.
 *
 * <p>The Embeddium meshing mixin targets Embeddium's own classes; those exist only when Embeddium
 * (or a rebrand such as Rubidium) is present, so it is skipped otherwise. All other mixins apply
 * unconditionally. Uses {@link LoadingModList} because this runs before {@code ModList} exists.
 */
public class SusyMixinPlugin implements IMixinConfigPlugin {

    private static final String EMBEDDIUM_MIXIN_PACKAGE =
            "io.github.symmetricdevs.supersymmetry.mixins.embeddium.";

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith(EMBEDDIUM_MIXIN_PACKAGE)) {
            return isModLoaded("embeddium") || isModLoaded("rubidium") || isModLoaded("sodium");
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    private static boolean isModLoaded(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }
}
