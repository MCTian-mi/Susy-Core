package io.github.symmetricdevs.supersymmetry.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlockSuSyMultiblockCasing extends VariantBlock<BlockSuSyMultiblockCasing.CasingType> {

    public BlockSuSyMultiblockCasing() {
        super(Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(5.0f, 10.0f));
        setTranslationKey("susy_multiblock_casing");
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(CasingType.SILICON_CARBIDE_CASING));
    }

    @Override
    public boolean canCreatureSpawn(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos,
                                    @Nonnull Mob.SpawnPlacementType type) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    @SuppressWarnings("deprecation")
    public boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        if (state.getValue(VARIANT) == CasingType.COALESCENCE_PLATE) {
            return true;
        }
        return super.useShapeForLightOcclusion(state);
    }

    public enum CasingType implements StringRepresentable {

        SILICON_CARBIDE_CASING("silicon_carbide_casing"),
        SIEVE_TRAY("sieve_tray"),
        STRUCTURAL_PACKING("structural_packing"),
        ULV_STRUCTURAL_CASING("ulv_structural_casing"),
        DRONE_PAD("drone_pad"),
        MONEL_500_CASING("monel_casing"),
        MONEL_500_PIPE("monel_casing_pipe"),
        COPPER_PIPE("copper_casing_pipe"),
        HEAVY_DUTY_PAD("heavy_duty_pad"),
        TABULAR_ALUMINA_REFRACTORY("tabular_alumina_refractory"),
        COALESCENCE_PLATE("coalescence_plate"),
        SYNTHETIC_MULLITE_REFRACTORY("synthetic_mullite_refractory"),
        HYDROSTATIC_CASING("hydrostatic_casing"),
        ALUMINIUM_GEARBOX("aluminium_gearbox");

        private final String name;

        CasingType(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
