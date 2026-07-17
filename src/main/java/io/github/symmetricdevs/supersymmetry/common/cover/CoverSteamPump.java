package io.github.symmetricdevs.supersymmetry.common.cover;

import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.common.cover.PumpCover;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;

/**
 * Ported from the 1.12.2 {@code supersymmetry.common.covers.CoverSteamPump}.
 * <p>
 * A steam-tier pump cover. Extends the modern {@link PumpCover} with
 * a reduced transfer rate (640 mB/t for steam age) and no tier (tier = -1,
 * since steam covers are not voltage-tiered).
 */
public class CoverSteamPump extends PumpCover {

    public CoverSteamPump(@NotNull CoverDefinition definition, @NotNull ICoverable coverHolder,
                          @NotNull Direction attachedSide) {
        super(definition, coverHolder, attachedSide, -1, 640);
    }

    @Override
    @NotNull
    protected String getUITitle() {
        return "cover.pump.steam.title";
    }
}
