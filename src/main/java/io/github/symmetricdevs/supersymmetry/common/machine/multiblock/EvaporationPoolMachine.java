package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.BlockInfo;

import io.github.symmetricdevs.supersymmetry.api.pattern.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.api.recipes.properties.SuSyRecipePropertyKeys;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Variable-size, solar/coil-heated Evaporation Pool. Heat is measured in Joules;
 * ten Joules are consumed per EU drawn by the optional heating coils.
 */
public class EvaporationPoolMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            EvaporationPoolMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int MIN_DIAMETER = 7;
    public static final int MAX_DIAMETER = 32;
    public static final int JOULES_PER_EU = 10;
    public static final int SOLAR_JOULES_PER_TICK = 50;

    @Persisted
    @DescSynced
    private int leftDistance;
    @Persisted
    @DescSynced
    private int rightDistance;
    @Persisted
    @DescSynced
    private int backDistance;

    @DescSynced
    private int exposedCollectionBlocks;
    @DescSynced
    private int coilCount;
    @DescSynced
    private int coilTemperature;
    @DescSynced
    private long coilHeatCapacity;

    private LongOpenHashSet collectionPositions = new LongOpenHashSet();
    @Nullable
    private TickableSubscription exposureSubscription;

    public EvaporationPoolMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new EvaporationPoolRecipeLogic(this);
    }

    /** Gate-only modifier. The pool deliberately has no ordinary EU/t overclock modifier. */
    public static @NotNull ModifierFunction evaporationEnergyGate(@NotNull MetaMachine machine,
                                                                  @NotNull GTRecipe recipe) {
        if (!(machine instanceof EvaporationPoolMachine)) {
            return RecipeModifier.nullWrongType(EvaporationPoolMachine.class, machine);
        }
        if (!recipe.data.contains(SuSyRecipePropertyKeys.EVAPORATION_ENERGY, Tag.TAG_INT) ||
                recipe.data.getInt(SuSyRecipePropertyKeys.EVAPORATION_ENERGY) <= 0) {
            return ModifierFunction.cancel(Component.translatable(
                    "susy.recipe_modifier.evaporation_energy_required"));
        }
        if (recipe.hasTick()) {
            return ModifierFunction.cancel(Component.translatable(
                    "susy.recipe_modifier.evaporation_tick_io_unsupported"));
        }
        return ModifierFunction.IDENTITY;
    }

    // TODO)) Optimization: cache the built BlockPattern + scanned PoolGeometry keyed by
    // (left, right, back) instead of rescanning the world and rebuilding the pattern on
    // every getPattern() call (structure re-checks, JEI previews). Invalidate on structure
    // invalidation. Behaviour is identical; this is a pure perf deferral.
    @Override
    public BlockPattern getPattern() {
        PoolGeometry geometry = scanGeometry();
        if (!geometry.valid()) {
            return createInvalidPattern();
        }
        return createPattern(geometry);
    }

    private BlockPattern createInvalidPattern() {
        TraceabilityPredicate never = new TraceabilityPredicate(state -> false, () -> new BlockInfo[0]);
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK)
                .aisle("S!")
                .where('S', Predicates.controller(Predicates.blocks(getDefinition().getBlock())))
                .where('!', never)
                .build();
    }

    private PoolGeometry scanGeometry() {
        Level level = getLevel();
        if (level == null) {
            return new PoolGeometry(2, 2, 3);
        }

        Direction back = getFrontFacing().getOpposite();
        // Legacy player-visible right was rotateYCCW; this intentionally differs
        // from RelativeDirection.RIGHT's controller-space naming.
        Direction right = getFrontFacing().getCounterClockWise();
        Direction left = right.getOpposite();
        BlockPos anchor = getPos().relative(back, 2);

        int scannedLeft = 0;
        int scannedRight = 0;
        int scannedBack = 0;
        for (int distance = 1; distance <= MAX_DIAMETER - 4; distance++) {
            if (scannedLeft == 0 && isPoolEdge(level, anchor.relative(left, distance))) {
                scannedLeft = distance;
            }
            if (scannedRight == 0 && isPoolEdge(level, anchor.relative(right, distance))) {
                scannedRight = distance;
            }
            if (scannedBack == 0 && isPoolEdge(level, anchor.relative(back, distance))) {
                scannedBack = distance;
            }
            if (scannedLeft != 0 && scannedRight != 0 && scannedBack != 0) {
                break;
            }
        }
        return new PoolGeometry(scannedLeft, scannedRight, scannedBack);
    }

    private static boolean isPoolEdge(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        if (level.getBlockState(pos).is(GTBlocks.LIGHT_CONCRETE.get())) {
            return true;
        }
        return MetaMachine.getMachine(level, pos) instanceof IMultiPart;
    }

    private BlockPattern createPattern(PoolGeometry geometry) {
        int left = geometry.left();
        int right = geometry.right();
        int back = geometry.back();

        TraceabilityPredicate optionalParts = Predicates.abilities(PartAbility.INPUT_ENERGY)
                .setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1)
                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS)
                        .setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1))
                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS)
                        .setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1))
                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS)
                        .setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1))
                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS)
                        .setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1));

        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK)
                .aisle(slice(Slice.BOTTOM_ALL, left, right), slice(Slice.TOP_NONE, left, right))
                .aisle(slice(Slice.BOTTOM_ALL, left, right), slice(Slice.TOP_SIDES, left, right))
                .aisle(slice(Slice.BOTTOM_END, left, right), slice(Slice.TOP_MIDDLE, left, right))
                .aisle(slice(Slice.BOTTOM_MIDDLE, left, right), slice(Slice.TOP_MIDDLE, left, right))
                .setRepeatable(Math.max(1, back - 2))
                .aisle(slice(Slice.BOTTOM_START, left, right), slice(Slice.TOP_MIDDLE, left, right))
                .aisle(slice(Slice.BOTTOM_ALL, left, right), slice(Slice.TOP_SIDES, left, right))
                .aisle(slice(Slice.BOTTOM_SELF, left, right), slice(Slice.TOP_NONE, left, right))
                .where('S', Predicates.controller(Predicates.blocks(getDefinition().getBlock())))
                .where('C', Predicates.blocks(GTBlocks.LIGHT_CONCRETE.get()).or(optionalParts))
                .where('B', SuSyPredicates.evaporationBed())
                .where('H', SuSyPredicates.evaporationCoilsOrBeds())
                .where('#', Predicates.air())
                .where(' ', Predicates.any())
                .build();
    }

    private static String slice(Slice slice, int left, int right) {
        int width = left + right + 3;
        StringBuilder row = new StringBuilder(Math.max(width, 0));
        for (int i = 0; i < width; i++) {
            row.append(slice.at(i, left, right));
        }
        return row.toString();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        PoolGeometry geometry = scanGeometry();
        if (geometry.valid()) {
            leftDistance = geometry.left();
            rightDistance = geometry.right();
            backDistance = geometry.back();
        }

        collectionPositions = SuSyPredicates.getEvaporationPositions(
                this, SuSyPredicates.EVAPORATION_COLLECTION_POSITIONS_KEY);
        LongOpenHashSet coilPositions = SuSyPredicates.getEvaporationPositions(
                this, SuSyPredicates.EVAPORATION_COIL_POSITIONS_KEY);
        ICoilType coilType = SuSyPredicates.getEvaporationCoilType(this);
        coilCount = coilType == null ? 0 : coilPositions.size();
        coilTemperature = coilType == null ? 0 : coilType.getCoilTemperature();
        coilHeatCapacity = saturatedMultiply(coilCount, coilTemperature);
        recountExposure();
        exposureSubscription = subscribeServerTick(exposureSubscription, this::exposureTick);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        clearRuntimeStructureState();
    }

    @Override
    public void onPartUnload() {
        super.onPartUnload();
        clearRuntimeStructureState();
    }

    @Override
    public void onUnload() {
        clearRuntimeStructureState();
        super.onUnload();
    }

    private void exposureTick() {
        if (!isFormed()) {
            return;
        }
        if (getOffsetTimer() % 20 == 0) {
            recountExposure();
        }
    }

    private void recountExposure() {
        // TODO)) Optimization: shard collectionPositions across ticks (one shard per tick)
        // instead of a full 50 J/t-position sweep every 20 ticks, to bound the per-tick
        // canSeeSunClearly cost for very large pools. Legacy offloaded this to a thread pool;
        // we keep it authoritative on the server thread per the task requirement.
        Level level = getLevel();
        if (level == null || level.isClientSide || !isFormed()) {
            return;
        }
        int exposed = 0;
        for (long packedPos : collectionPositions) {
            BlockPos pos = BlockPos.of(packedPos);
            if (level.isLoaded(pos) && GTUtil.canSeeSunClearly(level, pos)) {
                exposed++;
            }
        }
        exposedCollectionBlocks = exposed;
    }

    private void clearRuntimeStructureState() {
        unsubscribe(exposureSubscription);
        exposureSubscription = null;
        collectionPositions = new LongOpenHashSet();
        exposedCollectionBlocks = 0;
        coilCount = 0;
        coilTemperature = 0;
        coilHeatCapacity = 0;
    }

    private long drawCoilHeat() {
        if (energyContainer == null || coilHeatCapacity <= 0) {
            return 0;
        }
        long perTickInput = saturatedMultiply(energyContainer.getInputVoltage(), energyContainer.getInputAmperage());
        long requestedEU = Math.min(Math.min(energyContainer.getEnergyStored(), perTickInput),
                coilHeatCapacity / JOULES_PER_EU);
        if (requestedEU <= 0) {
            return 0;
        }
        long removedEU = Math.max(0, energyContainer.removeEnergy(requestedEU));
        return saturatedMultiply(removedEU, JOULES_PER_EU);
    }

    private long getSolarHeat() {
        return saturatedMultiply(exposedCollectionBlocks, SOLAR_JOULES_PER_TICK);
    }

    private static long saturatedMultiply(long left, long right) {
        if (left <= 0 || right <= 0) {
            return 0;
        }
        return left > Long.MAX_VALUE / right ? Long.MAX_VALUE : left * right;
    }

    private static long saturatedAdd(long left, long right) {
        if (left >= Long.MAX_VALUE - right) {
            return Long.MAX_VALUE;
        }
        return left + right;
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) {
            return;
        }
        textList.add(Component.translatable("susy.multiblock.evaporation_pool.dimensions",
                FormattingUtil.formatNumbers(leftDistance + rightDistance + 3),
                FormattingUtil.formatNumbers(backDistance + 4)));
        textList.add(Component.translatable("susy.multiblock.evaporation_pool.exposed_beds",
                FormattingUtil.formatNumbers(exposedCollectionBlocks)));
        textList.add(Component.translatable("susy.multiblock.evaporation_pool.solar_heat",
                FormattingUtil.formatNumbers(getSolarHeat())));
        textList.add(Component.translatable("susy.multiblock.evaporation_pool.coils",
                FormattingUtil.formatNumbers(coilCount), FormattingUtil.formatNumbers(coilTemperature)));
        textList.add(Component.translatable("susy.multiblock.evaporation_pool.coil_capacity",
                FormattingUtil.formatNumbers(coilHeatCapacity)));
        if (recipeLogic instanceof EvaporationPoolRecipeLogic logic) {
            textList.add(Component.translatable("susy.multiblock.evaporation_pool.thermal_remainder",
                    FormattingUtil.formatNumbers(logic.thermalRemainder)));
            if (logic.halted) {
                textList.add(Component.translatable("susy.multiblock.evaporation_pool.halted")
                        .withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    private record PoolGeometry(int left, int right, int back) {

        private boolean valid() {
            int width = left + right + 3;
            int length = back + 4;
            return left > 0 && right > 0 && width >= MIN_DIAMETER && width <= MAX_DIAMETER &&
                    length >= MIN_DIAMETER && length <= MAX_DIAMETER;
        }
    }

    private enum Slice {

        BOTTOM_ALL,
        BOTTOM_SELF,
        BOTTOM_START,
        BOTTOM_MIDDLE,
        BOTTOM_END,
        TOP_NONE,
        TOP_SIDES,
        TOP_MIDDLE;

        private char at(int index, int left, int right) {
            return switch (this) {
                case BOTTOM_ALL -> 'C';
                case BOTTOM_SELF -> index == left + 1 ? 'S' : 'C';
                case BOTTOM_START -> index < 2 || index > left + right ? 'C' :
                        index % 4 == 1 ? 'B' : 'H';
                case BOTTOM_MIDDLE -> index < 2 || index > left + right ? 'C' :
                        index % 2 == 1 ? 'B' : 'H';
                case BOTTOM_END -> index < 2 || index > left + right ? 'C' :
                        index % 4 == 3 ? 'B' : 'H';
                case TOP_NONE -> ' ';
                case TOP_SIDES -> index == 0 || index == left + right + 2 ? ' ' : 'C';
                case TOP_MIDDLE -> index == 0 || index == left + right + 2 ? ' ' :
                        index == 1 || index == left + right + 1 ? 'C' : '#';
            };
        }
    }

    private static final class EvaporationPoolRecipeLogic extends RecipeLogic {

        private static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
                EvaporationPoolRecipeLogic.class, RecipeLogic.MANAGED_FIELD_HOLDER);

        private final EvaporationPoolMachine pool;

        @Persisted
        @DescSynced
        private long thermalRemainder;
        @DescSynced
        private boolean heating;
        @DescSynced
        private boolean halted;

        private EvaporationPoolRecipeLogic(EvaporationPoolMachine pool) {
            super(pool);
            this.pool = pool;
        }

        @Override
        public @NotNull ManagedFieldHolder getFieldHolder() {
            return MANAGED_FIELD_HOLDER;
        }

        @Override
        public void setupRecipe(GTRecipe recipe) {
            super.setupRecipe(recipe);
            if (getLastRecipe() == recipe) {
                thermalRemainder = 0;
                heating = false;
                halted = false;
            }
        }

        @Override
        public void serverTick() {
            if (!isSuspend()) {
                if (!isIdle() && lastRecipe != null) {
                    if (runDelay > 0) {
                        runDelay--;
                    } else {
                        handleRecipeWorking();
                    }
                    // The legacy pool finishes only after progress passes duration.
                    if (progress > duration) {
                        onRecipeFinish();
                    }
                } else if (lastRecipe != null) {
                    findAndHandleRecipe();
                } else if (!machine.keepSubscribing() || getMachine().getOffsetTimer() % 5 == 0) {
                    findAndHandleRecipe();
                    if (lastFailedMatches != null) {
                        for (GTRecipe match : lastFailedMatches) {
                            if (checkMatchedRecipeAvailable(match)) {
                                break;
                            }
                        }
                    }
                }
            }

            boolean shouldUnsubscribe = isSuspend() ||
                    (lastRecipe == null && isIdle() && !machine.keepSubscribing() && !recipeDirty &&
                            lastFailedMatches == null);
            if (isIdle()) {
                failureReasons.clear();
                failureReasons.addAll(failureReasonMap.values());
            }
            if (shouldUnsubscribe && subscription != null) {
                subscription.unsubscribe();
                subscription = null;
            }
        }

        @Override
        public void handleRecipeWorking() {
            GTRecipe recipe = getLastRecipe();
            if (recipe == null) {
                return;
            }
            var conditionResult = RecipeHelper.checkConditions(recipe, this);
            if (!conditionResult.isSuccess()) {
                heating = false;
                halted = true;
                setWaiting(conditionResult.reason());
                return;
            }

            int requiredHeat = recipe.data.getInt(SuSyRecipePropertyKeys.EVAPORATION_ENERGY);
            if (requiredHeat <= 0) {
                heating = false;
                halted = true;
                setWaiting(Component.translatable("susy.recipe_modifier.evaporation_energy_required"));
                return;
            }

            long coilHeat = pool.drawCoilHeat();
            long totalHeat = saturatedAdd(saturatedAdd(pool.getSolarHeat(), coilHeat), thermalRemainder);
            long virtualProgress = totalHeat / requiredHeat;
            thermalRemainder = totalHeat % requiredHeat;
            heating = coilHeat > 0;
            halted = virtualProgress == 0;

            if (halted) {
                setWaiting(Component.translatable("susy.multiblock.evaporation_pool.halted"));
                return;
            }

            setStatus(Status.WORKING);
            if (!machine.onWorking()) {
                interruptRecipe();
                return;
            }
            progress = virtualProgress >= Integer.MAX_VALUE - progress ? Integer.MAX_VALUE :
                    progress + (int) virtualProgress;
            totalContinuousRunningTime++;
        }

        @Override
        public void onRecipeFinish() {
            thermalRemainder = 0;
            heating = false;
            halted = false;
            super.onRecipeFinish();
        }

        @Override
        public void resetRecipeLogic() {
            thermalRemainder = 0;
            heating = false;
            halted = false;
            super.resetRecipeLogic();
        }
    }
}
