package io.github.symmetricdevs.supersymmetry.common.blocks;

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

public class BlockGrinderCasing extends VariantBlock<BlockGrinderCasing.Type> {

    public BlockGrinderCasing() {
        super(Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(5.0f, 10.0f));
        setTranslationKey("grinder_casing");
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(Type.ABRASION_RESISTANT_CASING));
    }

    @Override
    public boolean canCreatureSpawn(
                                    @NotNull BlockState state,
                                    @NotNull BlockGetter world,
                                    @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    @SuppressWarnings("deprecation")
    public boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        if (state.getValue(VARIANT) == Type.INTERMEDIATE_DIAPHRAGM) {
            return true;
        }
        return super.useShapeForLightOcclusion(state);
    }

    public enum Type implements StringRepresentable {

        ABRASION_RESISTANT_CASING("abrasion_resistant_casing"),
        HYDRAULIC_MECHANICAL_GEARBOX("hydraulic_mechanical_gearbox"),
        WEAR_RESISTANT_LINED_MILL_SHELL("wear_resistant_lined_mill_shell"),
        WEAR_RESISTANT_LINED_SHELL_HEAD("wear_resistant_lined_shell_head"),
        INTERMEDIATE_DIAPHRAGM("intermediate_diaphragm"),
        ;

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
