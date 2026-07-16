package io.github.symmetricdevs.supersymmetry.config;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

/**
 * Mod configuration, resolved by the {@code dev.toma.configuration} library via
 * reflection (replaces 1.12.2's {@code net.minecraftforge.common.config.Config}
 * annotation config). dev.toma only supports <em>instance, non-final</em> fields,
 * so values live on the singleton {@link #INSTANCE} and are read as
 * {@code SusyConfig.INSTANCE.machine.disableLdItemPipes}. {@link #init()} must run
 * before any value is read.
 * <p>
 * Ported from the 1.12.2 {@code SusyConfig}; the structure mirrors GTCEu's
 * {@code ConfigHolder}.
 */
@Config(id = Supersymmetry.MOD_ID)
public class SusyConfig {

    public static SusyConfig INSTANCE;
    private static final Object LOCK = new Object();

    public static void init() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Configuration.registerConfig(SusyConfig.class, ConfigFormats.YAML).getConfigInstance();
            }
        }
    }

    @Configurable
    @Configurable.Comment("Config options for Machines")
    public MachineConfigs machine = new MachineConfigs();

    @Configurable
    @Configurable.Comment("Config options for Space")
    public SpaceConfigs space = new SpaceConfigs();

    public static class MachineConfigs {

        @Configurable
        @Configurable.Comment({ "Whether or not to disable Long-Distance Item Pipe recipes.", "Default: true" })
        public boolean disableLdItemPipes = true;
    }

    public static class SpaceConfigs {

        @Configurable
        @Configurable.Comment({ "Ban certain items from space." })
        public String[] bannedSpaceItems = new String[0];
    }
}
