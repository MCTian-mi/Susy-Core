package io.github.symmetricdevs.supersymmetry.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.block.VariantBlock;

public class BlockDeposit extends VariantBlock<BlockDeposit.DepositBlockType> {

    public BlockDeposit() {
        super(Material.ROCK);
        setTranslationKey("deposit_block");
        setResistance(1200.0f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(DepositBlockType.ORTHOMAGMATIC));
        setBlockUnbreakable();
    }

    @Override
    public boolean canSilkHarvest(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state,
                                  @NotNull Player player) {
        return false;
    }

    @NotNull
    @Override
    protected ItemStack getSilkTouchDrop(@NotNull BlockState state) {
        return new ItemStack(Blocks.AIR, 1);
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public EnumPushReaction getPushReaction(@NotNull BlockState state) {
        return EnumPushReaction.BLOCK;
    }

    @Override
    public void dropBlockAsItemWithChance(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull BlockState state,
                                          float chance, int fortune) {
        super.dropBlockAsItemWithChance(worldIn, pos, state, 0.0F, 0);
    }

    public enum DepositBlockType implements IStringSerializable {

        ORTHOMAGMATIC("orthomagmatic"),
        METAMORPHIC("metamorphic"),
        SEDIMENTARY("sedimentary"),
        HYDROTHERMAL("hydrothermal"),
        ALLUVIAL("alluvial"),
        MAGMATIC_HYDROTHERMAL("magmatic_hydrothermal"),
        ICE_CAP("ice_cap"),
        EVAPORITE("evaporite");

        private final String name;

        DepositBlockType(String name) {
            this.name = name;
        }

        @Nonnull
        public String getName() {
            return this.name;
        }
    }
}
