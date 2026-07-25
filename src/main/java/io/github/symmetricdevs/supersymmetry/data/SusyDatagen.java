package io.github.symmetricdevs.supersymmetry.data;

import com.tterrag.registrate.providers.ProviderType;

import io.github.symmetricdevs.supersymmetry.api.registry.SusyRegistration;
import io.github.symmetricdevs.supersymmetry.data.lang.SusyLangHandler;

public final class SusyDatagen {

    public static void init() {
        SusyRegistration.REGISTRATE.addDataGenerator(ProviderType.LANG, SusyLangHandler::init);
    }

    private SusyDatagen() {}
}
