package io.github.symmetricdevs.supersymmetry.api.capability;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.Set;

/**
 * Modern port of the 1.12.2 {@code StrandConversion}.
 *
 * <p>Maps a strand's width/thickness range to the GTCEu-Modern {@link TagPrefix}
 * produced when the strand is cut or converted to items.</p>
 */
public class StrandConversion {

    public static final Set<StrandConversion> CONVERSIONS = new ObjectArraySet<>();

    static {
        new StrandConversion(32, 40, 1. / 40, 1. / 32, TagPrefix.foil, 72);
        new StrandConversion(8, 10, 1. / 10, 1. / 8, TagPrefix.plate, 18);
        new StrandConversion(1, 2, 1. / 2, 1, TagPrefix.plateDense, 2);
        new StrandConversion(3, 5, 1. / 5, 1 / 3., TagPrefix.plateDouble, 9);
        new StrandConversion(4 / 9., 5 / 9., 1 / 5., 1 / 4., TagPrefix.ingot, 18);
        new StrandConversion(2 / 7., 2 / 5., 5 / 18., 7 / 18., TagPrefix.rod, 36);
    }

    public double minWidth;
    public double maxWidth;
    public double minThickness;
    public double maxThickness;
    public TagPrefix prefix;
    public int amount;

    public StrandConversion(double minWidth, double maxWidth, double minThickness, double maxThickness,
                            TagPrefix prefix, int amount) {
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.minThickness = minThickness;
        this.maxThickness = maxThickness;
        this.prefix = prefix;
        this.amount = amount;

        CONVERSIONS.add(this);
    }

    public static StrandConversion getConversion(Strand strand) {
        for (StrandConversion conversion : CONVERSIONS) {
            if (strand.width >= conversion.minWidth && strand.width <= conversion.maxWidth &&
                    strand.thickness >= conversion.minThickness && strand.thickness <= conversion.maxThickness) {
                return conversion;
            }
        }
        return null;
    }
}
