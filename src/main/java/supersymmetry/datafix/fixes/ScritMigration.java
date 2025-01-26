package supersymmetry.datafix.fixes;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import supersymmetry.datafix.util.BlockIDRemapper;

import java.util.HashSet;
import java.util.Set;

public class ScritMigration {

    public static final Set<Short> METAS_TO_LOOK_UP = new HashSet<>();
    public static final Int2IntOpenHashMap SUSY_TO_SCRIT_ID_SHIFT = new Int2IntOpenHashMap();

    public static void register(int susyId, int scritId) {
        METAS_TO_LOOK_UP.add((short) susyId);
        SUSY_TO_SCRIT_ID_SHIFT.put(susyId, scritId);
        BlockIDRemapper.rlsToLookUp.add("gregtech:meta_block_compressed_" + susyId /16);
    }

    public static void init() {
        register(7047, 506);
        register(8412, 507);
    }
}
