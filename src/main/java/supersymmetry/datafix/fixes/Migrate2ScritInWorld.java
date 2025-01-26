package supersymmetry.datafix.fixes;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import org.jetbrains.annotations.NotNull;
import supersymmetry.datafix.util.DataFixHelper;
import supersymmetry.datafix.util.DataFixHelper.RemappedBlock;

import static supersymmetry.datafix.SuSyDataVersion.V2_MIGRATE_ZIRCON;
import static supersymmetry.datafix.fixes.ScritMigration.METAS_TO_LOOK_UP;
import static supersymmetry.datafix.fixes.ScritMigration.SUSY_TO_SCRIT_ID_SHIFT;
import static supersymmetry.datafix.util.BlockIDRemapper.idsToLookUp;
import static supersymmetry.datafix.util.BlockIDRemapper.oldIDToRl;

public class Migrate2ScritInWorld implements IFixableData {

    @Override
    public int getFixVersion() {
        return V2_MIGRATE_ZIRCON.ordinal();
    }

    @NotNull
    @Override
    public NBTTagCompound fixTagCompound(@NotNull NBTTagCompound compound) {

        ForgeRegistry<Block> blockRegistry = (ForgeRegistry<Block>) ForgeRegistries.BLOCKS;

        DataFixHelper.rewriteBlocks(compound, (id, data) -> {
            if (idsToLookUp.contains(id)) {
                int oldMeta = Integer.parseInt(oldIDToRl.get(id).substring(31)) * 16 + data;
                if (METAS_TO_LOOK_UP.contains((short) oldMeta)) {
                    int newMeta = SUSY_TO_SCRIT_ID_SHIFT.get(oldMeta);
                    int newId = blockRegistry.getID(new ResourceLocation("supercritical:meta_block_compressed_" + newMeta / 16));
                    return new RemappedBlock(newId, (short) ((short) newMeta % 16));
                }
            }
            return null;
        });
        return compound;
    }
}
