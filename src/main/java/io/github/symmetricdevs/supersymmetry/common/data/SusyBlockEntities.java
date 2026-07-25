package io.github.symmetricdevs.supersymmetry.common.data;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import io.github.symmetricdevs.supersymmetry.api.registry.SusyRegistration;
import io.github.symmetricdevs.supersymmetry.common.blockentity.EccentricRollBlockEntity;

/**
 * SuSy block-entity registry. Registered through the same {@link GTRegistrate} as blocks so the
 * types are created during the mod's normal registrate lifecycle.
 */
public final class SusyBlockEntities {

    private static final GTRegistrate REGISTRATE = SusyRegistration.REGISTRATE;

    /** Animated eccentric crusher roll (see {@link EccentricRollBlockEntity}). */
    public static final BlockEntityEntry<EccentricRollBlockEntity> ECCENTRIC_ROLL = REGISTRATE
            .<EccentricRollBlockEntity>blockEntity("eccentric_roll",
                    (type, pos, state) -> new EccentricRollBlockEntity(type, pos, state))
            .validBlocks(SusyBlocks.STEEL_ECCENTRIC_ROLL)
            .register();

    private SusyBlockEntities() {}

    public static void init() {}
}
