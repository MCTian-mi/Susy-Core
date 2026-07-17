package io.github.symmetricdevs.supersymmetry.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.client.utils.BloomEffectUtil;

import io.github.symmetricdevs.supersymmetry.api.blocks.VariantHorizontalRotatableBlock;

public class BlockHome extends VariantHorizontalRotatableBlock<BlockHome.HomeType> {

    public BlockHome() {
        super(Material.IRON);
        this.setTranslationKey("home_block");
        this.setHardness(0.5f);
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("pickaxe", 1);
        this.setDefaultState(getState(HomeType.HOME_PRIMITIVE));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull BlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean isBed(@NotNull BlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                         @Nullable Entity player) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, @NotNull BlockPos pos, @NotNull BlockState state,
                                    @NotNull Player playerIn,
                                    @NotNull InteractionHand hand, @NotNull Direction facing, float hitX, float hitY,
                                    float hitZ) {
        if (worldIn.isRemote) return true;
        if ((worldIn.provider.canRespawnHere() && worldIn.getBiome(pos) != net.minecraft.init.Biomes.HELL)) {
            net.minecraftforge.event.ForgeEventFactory.onPlayerSpawnSet(playerIn, pos, true);
            playerIn.bedLocation = pos;
            playerIn.setSpawnPoint(playerIn.bedLocation, false);
            playerIn.sendStatusMessage(Component.translatable("tile.home.allowed"), true);
            return true;
        } else {
            playerIn.sendStatusMessage(Component.translatable("tile.home.denied"), true);
            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    protected boolean isBloomEnabled() {
        return ConfigHolder.client.machinesEmissiveTextures;
    }

    @NotNull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean canRenderInLayer(@NotNull BlockState state, @NotNull BlockRenderLayer layer) {
        return layer == getRenderLayer() || (getState(state) == HomeType.HOME_SCIFI &&
                layer == BloomEffectUtil.getEffectiveBloomLayer(isBloomEnabled()));
    }

    public enum HomeType implements IStringSerializable {

        HOME_PRIMITIVE("home_primitive"),
        HOME_GT_BRUTALIST("home_gt_brutalist"),
        HOME_RENEWAL_BRUTALIST("home_renewal_brutalist"),
        HOME_SCIFI("home_scifi");

        public final String name;

        HomeType(String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.getName();
        }
    }
}
