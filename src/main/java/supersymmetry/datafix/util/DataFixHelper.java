package supersymmetry.datafix.util;

import com.github.bsideup.jabel.Desugar;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public final class DataFixHelper {

    /**
     * Recursively rewrites NBTTagCompounds
     *
     * @param tag      the tag to rewrite
     * @param rewriter the tag rewriter
     */
    public static void rewriteCompoundTags(@NotNull NBTTagCompound tag,
                                           @NotNull UnaryOperator<NBTTagCompound> rewriter) {
        for (String key : tag.getKeySet()) {
            NBTBase child = tag.getTag(key);

            final byte id = child.getId();
            if (id == Constants.NBT.TAG_LIST) {
                rewriteCompoundTags((NBTTagList) child, rewriter);
            } else if (id == Constants.NBT.TAG_COMPOUND) {
                NBTTagCompound childCompound = (NBTTagCompound) child;
                rewriteCompoundTags(childCompound, rewriter);
                childCompound = rewriter.apply(childCompound);
                if (childCompound != null) {
                    tag.setTag(key, childCompound);
                }
            }
        }
    }

    /**
     * @param tagList  recursively rewrites NBTTagCompounds in an NBTTagList
     * @param rewriter the tag rewriter
     */
    public static void rewriteCompoundTags(@NotNull NBTTagList tagList,
                                           @NotNull UnaryOperator<NBTTagCompound> rewriter) {
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTBase child = tagList.get(i);

            final byte id = child.getId();
            if (id == Constants.NBT.TAG_LIST) {
                rewriteCompoundTags((NBTTagList) child, rewriter);
            } else if (id == Constants.NBT.TAG_COMPOUND) {
                NBTTagCompound childCompound = (NBTTagCompound) child;
                rewriteCompoundTags(childCompound, rewriter);
                childCompound = rewriter.apply(childCompound);
                if (childCompound != null) {
                    tagList.set(i, childCompound);
                }
            }
        }
    }

    /**
     * Rewrites blocks in a chunk section
     *
     * @param chunkSectionTag the chunk section tag
     * @param rewriter        the block rewriter
     */
    public static void rewriteBlocks(NBTTagCompound chunkSectionTag, BlockRewriter rewriter) {
        byte[] blockIds = chunkSectionTag.getByteArray("Blocks");
        NibbleArray blockData = new NibbleArray(chunkSectionTag.getByteArray("Data"));
        NibbleArray extendedIds = chunkSectionTag.hasKey("Add", Constants.NBT.TAG_BYTE_ARRAY)
                ? new NibbleArray(chunkSectionTag.getByteArray("Add")) : null;
        for (int i = 0; i < 4096; ++i) {
            int x = i & 0x0F, y = i >> 8 & 0x0F, z = i >> 4 & 0x0F;
            int id = extendedIds == null ? (blockIds[i] & 0xFF)
                    : ((blockIds[i] & 0xFF) | (extendedIds.get(x, y, z) << 8));
            RemappedBlock remapped = rewriter.rewrite(id, (short) blockData.get(x, y, z));
            if (remapped != null) {
                blockIds[i] = (byte) (remapped.id() & 0xFF);
                int idExt = (remapped.id() >> 8) & 0x0F;
                if (idExt != 0) {
                    if (extendedIds == null) {
                        extendedIds = new NibbleArray();
                    }
                    extendedIds.set(x, y, z, idExt);
                }
                blockData.set(x, y, z, remapped.data() & 0x0F);
            }
        }
        if (extendedIds != null) {
            chunkSectionTag.setByteArray("Add", extendedIds.getData());
        }
    }

    @FunctionalInterface
    public interface BlockRewriter {

        @Nullable
        RemappedBlock rewrite(int id, short data);

    }

    @Desugar
    public record RemappedBlock(int id, short data) {}
}
