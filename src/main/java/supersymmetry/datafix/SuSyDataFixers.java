package supersymmetry.datafix;

import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import supersymmetry.Supersymmetry;
import supersymmetry.datafix.fixes.Migrate2ScritInWorld;
import supersymmetry.datafix.fixes.Migrate2Scrit;
import supersymmetry.datafix.fixes.ScritMigration;
import supersymmetry.datafix.walker.WalkChunkSection;
import supersymmetry.datafix.walker.WalkItemStackLike;


public final class SuSyDataFixers {

    public static final Logger LOGGER = LogManager.getLogger("Supersymmetry DataFixers");
    private static final IDataWalker ITEM_STACK_WALKER = new WalkItemStackLike();
    private static final IDataWalker CHUNK_SECTION_WALKER = new WalkChunkSection();

    public static void init() {
        ScritMigration.init();
        final CompoundDataFixer forgeFixer = FMLCommonHandler.instance().getDataFixer();
        registerWalkers(forgeFixer);
        registerFixes(forgeFixer);
    }

    private static void registerWalkers(@NotNull CompoundDataFixer fixer) {
        fixer.registerVanillaWalker(FixTypes.BLOCK_ENTITY, ITEM_STACK_WALKER);
        fixer.registerVanillaWalker(FixTypes.ENTITY, ITEM_STACK_WALKER);
        fixer.registerVanillaWalker(FixTypes.PLAYER, ITEM_STACK_WALKER);
        fixer.registerVanillaWalker(FixTypes.CHUNK, CHUNK_SECTION_WALKER);
    }

    private static void registerFixes(@NotNull CompoundDataFixer forgeFixer) {
        LOGGER.info("Current SuSy data version is: {}", SuSyDataVersion.currentVersion());
        ModFixs fixer = forgeFixer.init(Supersymmetry.MODID, SuSyDataVersion.currentVersion().ordinal());

        for (SuSyDataVersion version : SuSyDataVersion.VALUES) {
            registerFixes(version, fixer);
        }
    }

    private static void registerFixes(@NotNull SuSyDataVersion version, @NotNull ModFixs fixer) {
        if (version != SuSyDataVersion.V0_BEFORE_EVERYTHING) {
            LOGGER.info("Registering fixer for data version {}", version);
        }
        switch (version) {
            case V1_MIGRATE_REGISTRY -> {
//                fixer.registerFix(SuSyFixType.ITEM_STACK_LIKE, new MigrateMTEItems(migrator));
//                fixer.registerFix(FixTypes.CHUNK, new MigrateMTEBlockTE(migrator));
            }
            case V2_MIGRATE_ZIRCON -> {
                fixer.registerFix(SuSyFixType.ITEM_STACK_LIKE, new Migrate2Scrit());
                fixer.registerFix(SuSyFixType.CHUNK_SECTION, new Migrate2ScritInWorld());
            }
            default -> {
            }
        }
    }
}
