package supersymmetry.datafix.walker;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import supersymmetry.datafix.SuSyFixType;
import supersymmetry.datafix.util.DataFixHelper;

import static supersymmetry.datafix.util.DataFixConstants.*;


public final class WalkItemStackLike implements IDataWalker {

    @NotNull
    @Override
    public NBTTagCompound process(@NotNull IDataFixer fixer, @NotNull NBTTagCompound compound, int versionIn) {
        DataFixHelper.rewriteCompoundTags(compound, tag -> {
            if (tag.hasKey(ITEM_ID, Constants.NBT.TAG_STRING) && tag.hasKey(ITEM_COUNT/*, Constants.NBT.TAG_INT*/) &&
                    tag.hasKey(ITEM_DAMAGE, Constants.NBT.TAG_SHORT)) {
                return fixer.process(SuSyFixType.ITEM_STACK_LIKE, tag, versionIn);
            }
            return null;
        });
        return compound;
    }
}
