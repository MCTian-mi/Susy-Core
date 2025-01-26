package supersymmetry.datafix.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockIDRemapper {

    public static final Set<String> rlsToLookUp = new HashSet<>();
    public static final Map<String, Integer> rlToOldID = new HashMap<>();
    public static final Map<Integer, String> oldIDToRl = new HashMap<>();
    public static final Set<Integer> idsToLookUp = new HashSet<>();

    public static void onWorldLoad(SaveHandler saveHandler, NBTTagCompound levelTag) {
        // the meta block ID fix only matters on the server side
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
            return;
        }

        rlToOldID.clear();
        oldIDToRl.clear();
        idsToLookUp.clear();

        if (levelTag.hasKey("FML", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound fmlTag = levelTag.getCompoundTag("FML");
            if (fmlTag.hasKey("Registries", Constants.NBT.TAG_COMPOUND)) {
                NBTTagCompound registriesTag = fmlTag.getCompoundTag("Registries");
                if (registriesTag.hasKey("minecraft:blocks", Constants.NBT.TAG_COMPOUND)) {
                    NBTTagCompound blocksTag = registriesTag.getCompoundTag("minecraft:blocks");
                    if (blocksTag.hasKey("ids", Constants.NBT.TAG_LIST)) {
                        NBTTagList idsTag = blocksTag.getTagList("ids", Constants.NBT.TAG_COMPOUND);
                        idsTag.forEach(tag -> {
                            NBTTagCompound idTag = (NBTTagCompound) tag;
                            String key = idTag.getString("K");
                            if (rlsToLookUp.contains(key)) {
                                int id = idTag.getInteger("V");
                                rlToOldID.put(key, id);
                                oldIDToRl.put(id, key);
                                idsToLookUp.add(id);
                            }
                        });
                    }
                }
            }
        }
    }
}
