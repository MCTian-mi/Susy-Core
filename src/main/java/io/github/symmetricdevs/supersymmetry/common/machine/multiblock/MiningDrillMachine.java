package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.predicates.SimplePredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import io.github.symmetricdevs.supersymmetry.common.data.SusyBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityMiningDrill}: an LV+ perfect-OC
 * multiblock that mines the ore deposit block located directly above the structure.
 * The deposit block is consumed when the matching recipe runs; recipes whose first
 * item input is non-consumable leave the block in world. A ghost copy of the deposit
 * is presented to the recipe matcher via a fake single-slot import inventory.
 * <p>
 * Key re-derivation for GTCEu-Modern:
 * <ul>
 * <li>{@code doModifyRecipe} is final on multiblocks, so the deposit-input logic is
 * implemented by overriding {@link RecipeLogic#setupRecipe(GTRecipe)}. After
 * {@code super.setupRecipe(recipe)} has consumed the recipe's inputs, the real
 * deposit block is destroyed if the first item input is consumable.</li>
 * <li>The fake input inventory is added as a machine trait so
 * {@link WorkableMultiblockMachine#onStructureFormed()} includes it in the
 * multiblock's IN item capability list. It is marked {@code shouldSearchContent=true}.</li>
 * <li>{@code depositPredicate} records the scanned block position via the match
 * context; {@link #onStructureFormed()} reads it back and refreshes the ghost
 * inventory.</li>
 * </ul>
 */
public class MiningDrillMachine extends WorkableElectricMultiblockMachine {

    private static final String DEPOSIT_KEY = "MiningDrillDeposit";

    /** Single-slot ghost inventory that exposes the deposit block as recipe input. */
    private final NotifiableItemStackHandler depositInventory;

    public MiningDrillMachine(IMachineBlockEntity holder) {
        super(holder);
        this.depositInventory = new NotifiableItemStackHandler(this, 1, IO.IN, IO.NONE);
        // Register the ghost handler as a trait so the multiblock's recipe capability
        // proxy includes it alongside the input buses. NotifiableItemStackHandler
        // searches content by default, so the ghost slot is visible to recipe matching.
        this.getTraits().add(depositInventory);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        refreshDeposit();
    }

    /**
     * Re-read the scanned deposit position from the match context (set by the
     * deposit traceability predicate) and update the ghost input slot. Called from
     * {@link #onStructureFormed()} and from the recipe logic each time a recipe is
     * searched, so mining continues as the deposit block changes state.
     */
    public void refreshDeposit() {
        BlockPos depositPos = getDepositPos();
        if (depositPos == null) {
            depositInventory.setStackInSlot(0, ItemStack.EMPTY);
            return;
        }
        Level level = getLevel();
        if (level == null) {
            return;
        }
        BlockState state = level.getBlockState(depositPos);
        ItemStack deposit = state.getBlock().getCloneItemStack(level, depositPos, state);
        depositInventory.setStackInSlot(0, deposit);
    }

    /**
     * The deposit predicate stores its absolute position under {@value #DEPOSIT_KEY}
     * in the match context. The predicate itself only runs during structure checks,
     * so {@link #onStructureFormed()} is the normal path; this helper also falls back
     * to re-scanning if the context key is missing.
     */
    private BlockPos getDepositPos() {
        if (getMultiblockState() == null) return null;
        Object o = getMultiblockState().getMatchContext().get(DEPOSIT_KEY);
        if (o instanceof BlockPos pos) return pos;
        // Fallback: scan one block above the controller (legacy 1.12.2 layout).
        return getPos().above();
    }

    /** True if this machine has a deposit block and the recipe logic should consume it. */
    public boolean hasDeposit() {
        return !depositInventory.getStackInSlot(0).isEmpty();
    }

    @Override
    protected @NotNull RecipeLogic createRecipeLogic(Object... args) {
        return new MiningDrillRecipeLogic(this);
    }

    @Override
    public MiningDrillRecipeLogic getRecipeLogic() {
        return (MiningDrillRecipeLogic) super.getRecipeLogic();
    }

    /**
     * Destroy the deposit block in world. Called by the recipe logic once a recipe
     * has matched and is about to consume inputs (and the recipe is not
     * non-consumable on its first item input).
     */
    public void consumeDeposit() {
        BlockPos depositPos = getDepositPos();
        if (depositPos == null) return;
        Level level = getLevel();
        if (level == null || level.isClientSide) return;
        level.destroyBlock(depositPos, false);
        depositInventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    /**
     * Full 1.12.2 {@code MetaTileEntityMiningDrill} structure pattern, ported to the
     * Modern {@code FactoryBlockPattern} DSL. The deposit block is matched at the 'H'
     * position and its absolute coordinate is stored in the match context.
     */
    public static @NotNull BlockPattern buildPattern(@NotNull MultiblockMachineDefinition definition) {
        return FactoryBlockPattern.start()
                .aisle("               ", "     DDDDD     ", "     DDDDD     ", "               ", "               ",
                        "               ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ")
                .aisle("               ", "   DDDDDDDDD   ", "   DDDDDDDDD   ", "    BB   BB    ", "    BB   BB    ",
                        "    BB   BB    ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ")
                .aisle("               ", "  DDDDDDDDDDD  ", "  DDDDDDDDDDD  ", "    BB   BB    ", "    BB   BB    ",
                        "    BB   BB    ", "     BB BB     ", "     BB BB     ", "     BB BB     ", "               ",
                        "               ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ")
                .aisle("               ", " DDDDDDDDDDDDD ", " DDDDDDDDDDDDD ", "               ", "               ",
                        "               ", "     BB BB     ", "     BB BB     ", "     BB BB     ", "     BB BB     ",
                        "               ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ")
                .aisle("               ", " DDDDDDDDDDDDD ", " DDDDDDDDDDDDD ", " BB         BB ", " BB         BB ",
                        " BB         BB ", "               ", "       E       ", "     BBEBB     ", "     AAEAA     ",
                        "       E       ", "       E       ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ")
                .aisle("               ", "DDDDDD   DDDDDD", "DDDDDD   DDDDDD", " BB         BB ", " BB         BB ",
                        " BB         BB ", "  BB       BB  ", "  BB   E   BB  ", "  BBBAAAAABBB  ", "   BAAACAAAB   ",
                        "     GGCGG     ", "     BGEGB     ", "     BGAGB     ", "     B   B     ", "     B   B     ",
                        "     BB  B     ", "     B B B     ", "     B  BB     ", "     B   B     ")
                .aisle("               ", "DDDDD     DDDDD", "DDDDD     DDDDD", "      AAA      ", "      BAB      ",
                        "      BAB      ", "  BB  BAB  BB  ", "  BB  BAB  BB  ", "  BBBAAAAABBB  ", "   BAACCCAAB   ",
                        "     GCCCG     ", "     GCCCG     ", "     GGCGG     ", "               ", "               ",
                        "         B     ", "      ACA      ", "     BAAA      ", "               ")
                .aisle("       H       ", "DDDDD  F  DDDDD", "DDDDD  C  DDDDD", "      ACA      ", "      ACA      ",
                        "      ACA      ", "      ACA      ", "    EEACAEE    ", "    EAACAAE    ", "    ECCCCCE    ",
                        "    ECCCCCE    ", "    EECCCEE    ", "     AGGGA     ", "       C       ", "       C       ",
                        "       C       ", "     BCCCB     ", "      AAA      ", "               ")
                .aisle("               ", "DDDDD     DDDDD", "DDDDD     DDDDD", "      AAA      ", "      BSB      ",
                        "      BAB      ", "  BB  BAB  BB  ", "  BB  BAB  BB  ", "  BBBAAAAABBB  ", "   BAACCCAAB   ",
                        "     GCCCG     ", "     GCCCG     ", "     GGCGG     ", "               ", "               ",
                        "     B         ", "      ACA      ", "      AAAB     ", "               ")
                .aisle("               ", "DDDDDD   DDDDDD", "DDDDDD   DDDDDD", " BB         BB ", " BB         BB ",
                        " BB         BB ", "  BB       BB  ", "  BB   E   BB  ", "  BBBAAAAABBB  ", "   BAAACAAAB   ",
                        "     GGCGG     ", "     BGEGB     ", "     BGAGB     ", "     B   B     ", "     B   B     ",
                        "     B  BB     ", "     B B B     ", "     BB  B     ", "     B   B     ")
                .aisle("               ", " DDDDDDDDDDDDD ", " DDDDDDDDDDDDD ", " BB         BB ", " BB         BB ",
                        " BB         BB ", "               ", "       E       ", "     BBEBB     ", "     AAEAA     ",
                        "       E       ", "       E       ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ")
                .aisle("               ", " DDDDDDDDDDDDD ", " DDDDDDDDDDDDD ", " BB         BB ", " BB         BB ",
                        " BB         BB ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ")
                .aisle("               ", "  DDDDDDDDDDD  ", "  DDDDDDDDDDD  ", "    BB   BB    ", "    BB   BB    ",
                        "    BB   BB    ", "     BB BB     ", "     BB BB     ", "     BB BB     ", "               ",
                        "               ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ")
                .aisle("               ", "   DDDDDDDDD   ", "   DDDDDDDDD   ", "    BB   BB    ", "    BB   BB    ",
                        "    BB   BB    ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ")
                .aisle("               ", "     DDDDD     ", "     DDDDD     ", "               ", "               ",
                        "               ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "               ", "               ")
                .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                .where('A', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()))
                .where('B', Predicates.frames(GTMaterials.Steel).or(drillAbilities()))
                .where('C', Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                .where('D', Predicates.blocks(GTBlocks.LIGHT_CONCRETE.get()))
                .where('E', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                .where('F', Predicates.blocks(SusyBlocks.STEEL_DRILL_HEAD.get()))
                .where('G', Predicates.blocks(GTBlocks.CASING_GRATE.get()))
                .where('H', depositPredicate())
                .where(' ', Predicates.any())
                .build();
    }

    /**
     * 1.12.2 {@code autoAbilities(true, true, true, true, true, true, false)} by
     * meaning: maintenance + energy/item/fluid in/out, no muffler. Modern's 2-arg
     * {@code autoAbilities} only covers maintenance/muffler, so the rest are wired
     * explicitly with the same limits/preview counts as the legacy call.
     */
    private static TraceabilityPredicate drillAbilities() {
        return Predicates.autoAbilities(true, false, false)
                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1)
                        .setMaxGlobalLimited(2).setPreviewCount(1))
                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(2)
                        .setPreviewCount(1))
                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2)
                        .setPreviewCount(1))
                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2)
                        .setPreviewCount(1))
                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2)
                        .setPreviewCount(1));
    }

    /**
     * Predicate factory used in the registration pattern. It records the position of
     * the matched block as the drill deposit.
     */
    public static TraceabilityPredicate depositPredicate() {
        return new TraceabilityPredicate(new SimplePredicate("mining_drill_deposit",
                blockWorldState -> {
                    if (blockWorldState.getMatchContext() != null) {
                        blockWorldState.getMatchContext().set(DEPOSIT_KEY, blockWorldState.getPos());
                    }
                    return true;
                }, null));
    }

    /**
     * Custom recipe logic that refreshes the ghost deposit before recipe matching
     * and destroys the real deposit block when a consumable recipe starts.
     */
    public static class MiningDrillRecipeLogic extends RecipeLogic {

        public MiningDrillRecipeLogic(IRecipeLogicMachine machine) {
            super(machine);
        }

        @Override
        public MiningDrillMachine getMachine() {
            return (MiningDrillMachine) super.getMachine();
        }

        @Override
        public @NotNull Iterator<GTRecipe> searchRecipe() {
            getMachine().refreshDeposit();
            return super.searchRecipe();
        }

        @Override
        public void setupRecipe(GTRecipe recipe) {
            super.setupRecipe(recipe);
            if (lastRecipe == null || getStatus() != Status.WORKING) return;

            List<Content> itemInputs = recipe.inputs.get(ItemRecipeCapability.CAP);
            if (itemInputs == null || itemInputs.isEmpty()) return;

            // Non-consumable inputs have chance == 0; leave the deposit block intact.
            Content first = itemInputs.get(0);
            if (first.chance == 0) return;

            int amount = 1;
            if (first.content instanceof SizedIngredient sized) {
                amount = sized.getAmount();
            }
            if (amount <= 0) return;

            getMachine().consumeDeposit();
        }
    }
}
