package io.github.symmetricdevs.supersymmetry.common.blocks;

import static io.github.symmetricdevs.supersymmetry.common.faction.FactionHateManager.addHate;
import static io.github.symmetricdevs.supersymmetry.common.faction.FactionHateManager.getHate;

import java.util.Random;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;
import io.github.symmetricdevs.supersymmetry.common.tileentities.TileEntityFlare;

public class BlocksRaidFlare extends VariantBlock<BlocksRaidFlare.BlockRaidFlareType> {

    public BlocksRaidFlare() {
        super(Material.IRON);
        this.setTranslationKey("raid_flare_block");
        this.setHardness(0.5f);
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("pickaxe", 1);
        this.setLightLevel(1.0f);
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, BlockState state, Player player) {
        return false;
    }

    public static enum BlockRaidFlareType implements IStringSerializable, IStateHarvestLevel {

        BANDITFLARE("bandit_flare", 2, 1.0f, 0.0f, 0.0f, "Bandits"),
        FEDFLARE("fed_flare", 2, 0.0f, 0.0f, 1.0f, "Feds");

        private final String name;
        private final int harvestLevel;
        private final float red;
        private final float green;
        private final float blue;
        private final String faction;

        private BlockRaidFlareType(String name, int harvestLevel, float red, float green, float blue, String faction) {
            this.name = name;
            this.harvestLevel = harvestLevel;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.faction = faction;
        }

        public float getRed() {
            return red;
        }

        public float getGreen() {
            return green;
        }

        public float getBlue() {
            return blue;
        }

        @Override
        public int getHarvestLevel(BlockState BlockState) {
            return this.harvestLevel;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public String getFaction() {
            return this.faction;
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockEntity createTileEntity(World world, BlockState state) {
        BlockRaidFlareType type = this.getState(state);

        TileEntityFlare flare = new TileEntityFlare();
        flare.setColor(type.getRed(), type.getGreen(), type.getBlue());

        flare.setFlareFaction(type.getFaction());
        flare.setFlareType(type.getName());

        return flare;
    }

    // HATE
    @Override
    public void breakBlock(World world, BlockPos pos, BlockState state) {
        if (!world.isRemote) {
            BlockEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileEntityFlare) {
                TileEntityFlare flare = (TileEntityFlare) tile;

                String faction = flare.getFlareFaction();
                java.util.UUID targetUUID = flare.getTarget();

                if (faction != null && !faction.isEmpty() && targetUUID != null) {
                    Player player = world.getPlayerEntityByUUID(targetUUID);

                    if (player != null) {
                        int currentHate = getHate(player, faction);
                        addHate(player, faction, -currentHate);
                    }
                }
            }
        }

        super.breakBlock(world, pos, state);
    }
}
