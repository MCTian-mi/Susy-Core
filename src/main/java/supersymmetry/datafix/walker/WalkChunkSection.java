package supersymmetry.datafix.walker;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import supersymmetry.datafix.SuSyFixType;

public class WalkChunkSection implements IDataWalker {

    @NotNull
    @Override
    public NBTTagCompound process(@NotNull IDataFixer fixer, NBTTagCompound compound, int versionIn) {
        if (compound.hasKey("Level", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound levelTag = compound.getCompoundTag("Level");
            if (levelTag.hasKey("Sections", Constants.NBT.TAG_LIST)) {
                NBTTagList sectionListTag = levelTag.getTagList("Sections", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < sectionListTag.tagCount(); i++) {
                    sectionListTag.set(i, fixer.process(
                            SuSyFixType.CHUNK_SECTION, sectionListTag.getCompoundTagAt(i), versionIn));
                }
            }
        }
        return compound;
    }

}
