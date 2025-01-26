package supersymmetry.datafix;

import org.jetbrains.annotations.NotNull;

/**
 * Versions of SuSy data.
 */
public enum SuSyDataVersion {

    // Before any fix is shipped
    V0_BEFORE_EVERYTHING,

    // After groovy-registered zircon is migrated to the scrit one
    V1_MIGRATE_REGISTRY,

    // After groovy-registered zircon is migrated to the scrit one
    V2_MIGRATE_ZIRCON;

    static final @NotNull SuSyDataVersion @NotNull [] VALUES = values();

    /**
     * @return the current version of GT data
     */
    public static @NotNull SuSyDataVersion currentVersion() {
        return VALUES[VALUES.length - 1];
    }
}
