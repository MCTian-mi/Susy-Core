package io.github.symmetricdevs.supersymmetry.api.capability;

/**
 * Modern port of the 1.12.2 {@code IStrandProvider}.
 * <p>
 * A strand is an abstract ingot-in-progress carried between strand-casting-line
 * machines via dedicated {@link io.github.symmetricdevs.supersymmetry.api.machine.multiblock.SuSyMultiblockAbilities#STRAND_IMPORT}
 * / {@code STRAND_EXPORT} hatches.
 */
public interface IStrandProvider {

    Strand getStrand();

    Strand take();

    /** Returns what could not be inserted. */
    Strand insertStrand(Strand strand);
}
