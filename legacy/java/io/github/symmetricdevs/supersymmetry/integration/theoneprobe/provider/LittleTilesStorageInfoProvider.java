package io.github.symmetricdevs.supersymmetry.integration.theoneprobe.provider;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import com.creativemd.creativecore.common.utils.type.Pair;
import com.creativemd.littletiles.common.structure.LittleStructure;
import com.creativemd.littletiles.common.structure.connection.StructureChildConnection;
import com.creativemd.littletiles.common.structure.exception.CorruptedConnectionException;
import com.creativemd.littletiles.common.structure.exception.NotYetConnectedException;
import com.creativemd.littletiles.common.structure.type.LittleStorage;
import com.creativemd.littletiles.common.tile.LittleTile;
import com.creativemd.littletiles.common.tile.parent.IParentTileList;
import com.creativemd.littletiles.common.BlockEntity.TileEntityLittleTiles;

import mcjty.theoneprobe.api.*;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;

public class LittleTilesStorageInfoProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return Supersymmetry.MODID + ":little_tiles_storage_info_provider";
    }

    static boolean searchChildren(LittleStructure structure) throws CorruptedConnectionException,
                                                             NotYetConnectedException {
        if (structure == null) return false;
        for (StructureChildConnection child : structure.getChildren()) {
            if (child.getStructure() instanceof LittleStorage storage) {
                for (int i = 0; i < storage.inventory.getSizeInventory(); i++) {
                    if (!storage.inventory.getStackInSlot(i).isEmpty()) {
                        return true;
                    }
                }
            }
            if (searchChildren(child.getStructure())) {
                return true;
            }
        }
        return false;
    }

    private static final String lt_loot = "" + TextStyleClass.INFOIMP + ChatFormatting.RED + "{*susy.top.lt_loot*}";

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, World world, BlockState state,
                             IProbeHitData data) {
        if (state.getBlock().hasTileEntity(state)) {
            BlockEntity te = world.getTileEntity(data.getPos());
            if (te instanceof TileEntityLittleTiles lt) {
                // if the tile has items;
                IItemHandler item = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (item != null) {
                    for (int i = 0; i < item.getSlots(); i++) {
                        if (!item.getStackInSlot(i).isEmpty()) {
                            info.text(lt_loot);
                            return;
                        }
                    }
                }

                // if the lt structure contains a tile with items
                for (Pair<IParentTileList, LittleTile> pair : lt.allTiles()) {
                    if (pair.key instanceof IParentTileList parent) {
                        if (parent.isStructure()) {
                            try {
                                LittleStructure structure = parent.getStructure();
                                while (structure.getParent() != null) {
                                    structure = structure.getParent().getStructure();
                                }
                                if (searchChildren(structure)) {
                                    info.text(lt_loot);
                                    return;
                                }
                            } catch (CorruptedConnectionException | NotYetConnectedException e) {}
                        }
                    }
                }
            }
        }
    }
}
