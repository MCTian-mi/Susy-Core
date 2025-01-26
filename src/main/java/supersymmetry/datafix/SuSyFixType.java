package supersymmetry.datafix;

import net.minecraft.util.datafix.IFixType;
import supersymmetry.datafix.walker.WalkChunkSection;
import supersymmetry.datafix.walker.WalkItemStackLike;

public enum SuSyFixType implements IFixType {
    /**
     * Any NBTTagCompound that looks like an ItemStack.
     * <p>
     * It must have the fields: {@code String id}, {@code int count}, {@code short Damage}.
     *
     * @see WalkItemStackLike
     */
    ITEM_STACK_LIKE,

    /**
     * A vertical section of a chunk containing block state data.
     *
     * @see WalkChunkSection
     */
    CHUNK_SECTION
}
