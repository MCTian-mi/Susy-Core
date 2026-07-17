package io.github.symmetricdevs.supersymmetry.common.blocks;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.Item;

import org.jetbrains.annotations.NotNull;

/**
 * Porting stub — the old {@code SusyStoneVariantBlock} was a {@code VariantBlock<StoneType>}
 * built on 1.12.2 APIs ({@code VariantBlock}, {@code PropertyEnum}, {@code BlockStateContainer},
 * {@code IStringSerializable}) that are removed in Modern.
 * <p>
 * Modern GTCEu generates stone blocks per-material via Registrate. The custom stone
 * types (Gabbro, Gneiss, etc.) are registered as plain blocks in {@link SuSyBlocks}
 * using standard {@link BlockBehaviour} + {@link Block#updateFromNeighbourShapes}.
 * <p>
 * This class and the inner {@code StoneType}/{@code StoneVariant} enums are retained
 * only for the {@link SusyStoneTypes} bridge. They are NOT used for actual block
 * registration and WILL be replaced when worldgen (Phase 8) is ported.
 */
@Deprecated
public class SusyStoneVariantBlock {

    public enum StoneVariant {
        SMOOTH("susy_stone_smooth"),
        COBBLE("susy_stone_cobble", 2.0F, 10.0F),
        BRICKS("susy_stone_bricks", 0.25);

        public final String id;
        public final String translationKey;
        public final float hardness;
        public final float resistance;
        public final double walkingSpeed;

        StoneVariant(@Nonnull String id) {
            this(id, id);
        }

        StoneVariant(@Nonnull String id, double walkingSpeed) {
            this(id, id, 1.5F, 10.0F, walkingSpeed);
        }

        StoneVariant(@Nonnull String id, @Nonnull String translationKey) {
            this(id, translationKey, 1.5F, 10.0F, 0);
        }

        StoneVariant(@Nonnull String id, float hardness, float resistance) {
            this(id, id, hardness, resistance, 0);
        }

        StoneVariant(@Nonnull String id, @Nonnull String translationKey, float hardness, float resistance,
                     double walkingSpeed) {
            this.id = id;
            this.translationKey = translationKey;
            this.hardness = hardness;
            this.resistance = resistance;
            this.walkingSpeed = walkingSpeed;
        }
    }

    public enum StoneType {
        GABBRO("gabbro", MapColor.STONE),
        GNEISS("gneiss", MapColor.TERRACOTTA_RED),
        LIMESTONE("limestone", MapColor.TERRACOTTA_LIGHT_GRAY),
        PHYLLITE("phyllite", MapColor.STONE),
        QUARTZITE("quartzite", MapColor.QUARTZ),
        SHALE("shale", MapColor.TERRACOTTA_RED),
        SLATE("slate", MapColor.TERRACOTTA_RED),
        SOAPSTONE("soapstone", MapColor.TERRACOTTA_LIGHT_GRAY),
        KIMBERLITE("kimberlite", MapColor.STONE),
        INDUSTRIAL_CONCRETE("industrial_concrete", MapColor.TERRACOTTA_YELLOW),
        MILITARY_CONCRETE("minitary_concrete", MapColor.COLOR_BLACK),
        ANORTHOSITE("anorthosite", MapColor.STONE);

        private final String name;
        public final MapColor mapColor;

        StoneType(@Nonnull String name, @Nonnull MapColor mapColor) {
            this.name = name;
            this.mapColor = mapColor;
        }

        @Nonnull
        public String getName() {
            return this.name;
        }
    }
}
