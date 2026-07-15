package io.github.symmetricdevs.supersymmetry.config;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

/**
 * Mod configuration, resolved by the {@code dev.toma.configuration} library via
 * reflection (replaces 1.12.2's {@code net.minecraftforge.common.config.Config}
 * annotation config). Fields are read statically via the {@code public static}
 * leaves below; {@link #init()} must run before any of them is read.
 * <p>
 * Ported from the 1.12.2 {@code SusyConfig}.
 */
@Config(id = Supersymmetry.MOD_ID)
public class SusyConfig {

    @Configurable
    @Configurable.Comment({ "Whether or not to disable Long-Distance Item Pipe recipes.", "Default: true" })
    public static boolean disableLdItemPipes = true;

    @Configurable
    @Configurable.Comment({ "Ban certain items from space." })
    public static String[] bannedSpaceItems = {};

    private SusyConfig() {}

    public static void init() {
        Configuration.registerConfig(SusyConfig.class, ConfigFormats.YAML);
    }
}
