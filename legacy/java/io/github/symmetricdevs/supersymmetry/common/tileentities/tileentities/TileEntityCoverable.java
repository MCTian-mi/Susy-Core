package io.github.symmetricdevs.supersymmetry.common.tileentities;

import java.util.ArrayList;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import lombok.Getter;
import lombok.Setter;

/**
 * Tile entity for VariantDirectionalCoverableBlock.
 * Ported from 1.12.2: removed TickableTileEntityBase/CCL dependency,
 * uses standard BlockEntity with manual sync.
 */
public class TileEntityCoverable extends BlockEntity {

    private byte coverSpots; // 0 - 63, bits going in the order of Direction
    private ItemStack coverItem;

    public static boolean RENDER_SWITCH = true; // false -> regular render; true -> tile rendering
    private BakedModel sourceModel;

    public TileEntityCoverable(BlockPos pos, BlockState state) {
        super(pos, state);
        coverSpots = 0;
        coverItem = ItemStack.EMPTY;
    }

    public boolean isCovered(Direction direction) {
        return (coverSpots >> direction.ordinal()) % 2 == 1;
    }

    private void setCovered(Direction direction, boolean cov) {
        coverSpots = cov ? (byte) (coverSpots | (1 << direction.ordinal())) :
                (byte) (coverSpots & ~(1 << direction.ordinal()));
        if (coverSpots == 0) {
            coverItem = ItemStack.EMPTY;
        }
        this.setChanged();
    }

    public Direction[] getSides() {
        ArrayList<Direction> ret = new ArrayList<>(6);
        for (Direction val : Direction.values()) {
            if (isCovered(val)) {
                ret.add(val);
            }
        }
        return ret.toArray(new Direction[0]);
    }

    public ItemStack getCoverItem() {
        return coverItem;
    }

    public int getCoverCount() {
        int ret = 0;
        for (Direction side : Direction.values()) {
            if (isCovered(side)) {
                ret++;
            }
        }
        return ret;
    }

    public ItemStack placeCover(Direction direction, ItemStack inp, Player player) {
        Level world = getLevel();
        ItemStack ret = inp.copy();
        if (isCovered(direction)) {
            if (inp.isEmpty()) {
                ret = coverItem.copy();
                ret.setCount(1);
                setCovered(direction, false);
            } else if (inp.isItemEqual(coverItem)) {
                ret = coverItem.copy();
                if (inp.getCount() == 64) {
                    ItemStack dropped = coverItem.copy();
                    dropped.setCount(1);
                    player.drop(dropped, false, true);
                } else {
                    ret.setCount(inp.getCount() + 1);
                }
                setCovered(direction, false);
            } else {
                ItemStack dropped = coverItem.copy();
                dropped.setCount(getSides().length);
                player.drop(dropped, false, true);
                coverSpots = 0;
                coverItem = inp.copy();
                coverItem.setCount(1);
                setCovered(direction, true);
                ret.setCount(inp.getCount() - 1);
            }
        } else {
            if (inp.isEmpty()) {
                ret = ItemStack.EMPTY;
            } else if (inp.isItemEqual(coverItem) || coverItem.isEmpty()) {
                ret.setCount(inp.getCount() - 1);
                setCovered(direction, true);
                if (coverItem.isEmpty()) {
                    coverItem = inp.copy();
                    coverItem.setCount(1);
                }
            } else {
                if (!coverItem.isEmpty()) {
                    ItemStack dropped = coverItem.copy();
                    dropped.setCount(getSides().length);
                    player.drop(dropped, false, true);
                    coverSpots = 0;
                }
                coverItem = inp.copy();
                coverItem.setCount(1);
                setCovered(direction, true);
                ret.setCount(inp.getCount() - 1);
            }
        }
        if (world != null && !world.isClientSide) {
            setChanged();
        }
        if (player.getAbilities().instabuild) return inp;
        return ret;
    }

    public void addCollisionBoundingBox(ArrayList<net.minecraft.world.phys.AABB> boundingBox) {
        boundingBox.add(new net.minecraft.world.phys.AABB(0, 0, 0, 1, 1, 1));
    }

    public Pair<TextureAtlasSprite, Integer> getParticleTexture() {
        setSourceModel();
        if (sourceModel == null) {
            return null;
        }
        return Pair.of(sourceModel.getParticleIcon(), 0xFFFFFF);
    }

    @OnlyIn(Dist.CLIENT)
    public void setSourceModel() {
        if (getLevel() == null || getBlockPos() == null) return;
        this.sourceModel = Minecraft.getInstance().getBlockRenderer()
                .getBlockModel(getLevel().getBlockState(getBlockPos()));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("cover_type", coverItem.save(new CompoundTag()));
        compound.putByte("spots", coverSpots);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        coverItem = ItemStack.of(compound.getCompound("cover_type"));
        coverSpots = compound.getByte("spots");
    }
}
