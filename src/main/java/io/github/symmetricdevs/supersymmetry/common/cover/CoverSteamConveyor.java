package io.github.symmetricdevs.supersymmetry.common.cover;

import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.common.cover.ConveyorCover;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;

/**
 * Ported from the 1.12.2 {@code supersymmetry.common.covers.CoverSteamConveyor}.
 * <p>
 * A steam-tier conveyor cover. Extends the modern {@link ConveyorCover} with
 * a reduced transfer rate (4 items/s) and no tier (tier = -1, since steam
 * covers are not voltage-tiered).
 */
public class CoverSteamConveyor extends ConveyorCover {

    public CoverSteamConveyor(@NotNull CoverDefinition definition, @NotNull ICoverable coverHolder,
                              @NotNull Direction attachedSide) {
        super(definition, coverHolder, attachedSide, -1, 4);
    }

    @Override
    @NotNull
    protected String getUITitle() {
        return "cover.conveyor.steam.title";
    }
}
