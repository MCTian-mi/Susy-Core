package io.github.symmetricdevs.supersymmetry.api.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import io.github.symmetricdevs.supersymmetry.api.capability.IStrandProvider;
import io.github.symmetricdevs.supersymmetry.api.particle.IParticleBeamProvider;

import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Modern port of the 1.12.2 {@code SuSyMultiblockAbilities}.
 *
 * <p>GTCEu-Modern {@link PartAbility} is non-generic; the type parameter from the old
 * {@code MultiblockAbility<T>} is dropped and the part machine itself implements the
 * corresponding interface (e.g. {@link IStrandProvider}).</p>
 */
public class SuSyMultiblockAbilities {

    public static final PartAbility PRIMITIVE_IMPORT_ITEMS = new PartAbility("primitive_import_items");
    public static final PartAbility PRIMITIVE_EXPORT_ITEMS = new PartAbility("primitive_export_items");

    public static final PartAbility STRAND_IMPORT = new PartAbility("strand_import");
    public static final PartAbility STRAND_EXPORT = new PartAbility("strand_export");

    public static final PartAbility BEAM_IMPORT = new PartAbility("beam_import");
    public static final PartAbility BEAM_EXPORT = new PartAbility("beam_export");

    public static final PartAbility SCANNER = new PartAbility("scanner");

    private SuSyMultiblockAbilities() {}
}
