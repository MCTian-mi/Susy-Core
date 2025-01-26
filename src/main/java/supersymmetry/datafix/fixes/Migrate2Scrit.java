package supersymmetry.datafix.fixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;
import org.jetbrains.annotations.NotNull;

import static supersymmetry.datafix.fixes.ScritMigration.METAS_TO_LOOK_UP;
import static supersymmetry.datafix.fixes.ScritMigration.SUSY_TO_SCRIT_ID_SHIFT;
import static supersymmetry.datafix.util.DataFixConstants.*;
import static supersymmetry.datafix.SuSyDataVersion.*;

public class Migrate2Scrit implements IFixableData {

    @Override
    public int getFixVersion() {
        return V2_MIGRATE_ZIRCON.ordinal();
    }

    @NotNull
    @Override
    public NBTTagCompound fixTagCompound(@NotNull NBTTagCompound compound) {

        if (compound.hasKey(ITEM_ID)) {
            final String id = compound.getString(ITEM_ID);
            if (!id.isEmpty()) {
                // must check hasKey() since non-items can have ITEM_ID but not have ITEM_DAMAGE and ITEM_COUNT
                if (compound.hasKey(ITEM_DAMAGE) && compound.hasKey(ITEM_COUNT)) {
                    final short meta = compound.getShort(ITEM_DAMAGE);
                    if (id.startsWith("gregtech:meta_block_compressed_")) {
                        short blockMaterialMeta = (short) (Integer.parseInt(id.substring(31)) * 16 + meta);
                        if (METAS_TO_LOOK_UP.contains(blockMaterialMeta)) {
                            int scritId = SUSY_TO_SCRIT_ID_SHIFT.get(blockMaterialMeta);
                            compound.setString(ITEM_ID, "supercritical:meta_block_compressed_" + scritId / 16);
                            compound.setShort(ITEM_DAMAGE, (short) ((short) scritId % 16));
                        }
                    } else if (METAS_TO_LOOK_UP.contains(meta)) {
                        compound.setString(ITEM_ID, id.replace("gregtech:", "supercritical:"));
                        compound.setShort(ITEM_DAMAGE, (short) SUSY_TO_SCRIT_ID_SHIFT.get(meta));
                    }
                }
            }
        }

        return compound;
    }
}
