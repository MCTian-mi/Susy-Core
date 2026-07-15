package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.TagPrefixItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.ActionResult;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import io.github.symmetricdevs.supersymmetry.api.item.MillBallItem;
import io.github.symmetricdevs.supersymmetry.api.unification.material.properties.SuSyPropertyKey;
import io.github.symmetricdevs.supersymmetry.api.unification.ore.SusyTagPrefixes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Modern Ball Mill logic: fixed 32-way batching plus eight non-recipe mill-ball supports. */
public class BallMillMachine extends SuSyOrientationFixupMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            BallMillMachine.class, SuSyOrientationFixupMachine.MANAGED_FIELD_HOLDER);

    public static final int PARALLEL_LIMIT = 32;
    public static final int MILL_BALL_REQUIREMENT = 8;
    private static final int EU_PER_DURABILITY = 256;

    private List<BallSlot> armedBalls = List.of();
    private boolean hasMillBalls = true;

    public BallMillMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new BallMillRecipeLogic(this);
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        clearArmedBalls();
        hasMillBalls = true;
        if (!super.beforeWorking(recipe)) {
            return false;
        }

        List<BallSlot> balls = findUsableMutableBalls();
        if (balls.size() < MILL_BALL_REQUIREMENT) {
            hasMillBalls = false;
            if (recipe != null) {
                RecipeLogic.putFailureReason(this, recipe,
                        Component.translatable("susy.multiblock.ball_mill.error.missing_mill_balls"));
            }
            return false;
        }

        armedBalls = List.copyOf(balls.subList(0, MILL_BALL_REQUIREMENT));
        return true;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        clearArmedBalls();
        hasMillBalls = true;
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed() && !hasMillBalls) {
            textList.add(Component.translatable("susy.multiblock.ball_mill.error.missing_mill_balls"));
        }
    }

    public static @NotNull ModifierFunction fixedParallel32(@NotNull MetaMachine machine,
                                                             @NotNull GTRecipe recipe) {
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, PARALLEL_LIMIT);
        if (parallels <= 0) {
            return ModifierFunction.NULL;
        }
        if (parallels == 1) {
            return ModifierFunction.IDENTITY;
        }
        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels))
                .eutMultiplier(parallels)
                .parallels(parallels)
                .build();
    }

    private List<BallSlot> findUsableMutableBalls() {
        List<BallSlot> found = new ArrayList<>(MILL_BALL_REQUIREMENT);
        for (IRecipeHandler<?> raw : getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP)) {
            if (!(raw instanceof IItemHandlerModifiable handler)) {
                continue;
            }
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (isUsableMillBall(stack)) {
                    found.add(new BallSlot(handler, slot));
                    if (found.size() == MILL_BALL_REQUIREMENT) {
                        return found;
                    }
                }
            }
        }
        return found;
    }

    private static boolean isUsableMillBall(ItemStack stack) {
        return stack.getItem() instanceof TagPrefixItem item &&
                item.tagPrefix == SusyTagPrefixes.millBall &&
                item.material.hasProperty(SuSyPropertyKey.MILL_BALL) &&
                MillBallItem.isUsable(stack);
    }

    private void damageOneArmedBall(GTRecipe recipe) {
        if (armedBalls.size() != MILL_BALL_REQUIREMENT) {
            return;
        }
        BallSlot chosen = armedBalls.get(ThreadLocalRandom.current().nextInt(MILL_BALL_REQUIREMENT));
        ItemStack current = chosen.handler().getStackInSlot(chosen.slot());
        if (!isUsableMillBall(current)) {
            return;
        }

        long eut = RecipeHelper.getRealEUt(recipe).getTotalEU();
        if (eut <= 0 || recipe.duration <= 0) {
            return;
        }

        long maxRelevantEnergy = (long) Integer.MAX_VALUE * EU_PER_DURABILITY;
        long totalEnergy = eut > maxRelevantEnergy / recipe.duration ?
                maxRelevantEnergy : eut * recipe.duration;
        int damage = (int) Math.min(Integer.MAX_VALUE, totalEnergy / EU_PER_DURABILITY);
        if (damage <= 0) {
            return;
        }

        ItemStack changed = current.copy();
        if (MillBallItem.applyDamage(changed, damage)) {
            changed = ItemStack.EMPTY;
        }
        chosen.handler().setStackInSlot(chosen.slot(), changed);
    }

    private void clearArmedBalls() {
        armedBalls = List.of();
    }

    private record BallSlot(IItemHandlerModifiable handler, int slot) {}

    private static final class BallMillRecipeLogic extends RecipeLogic {

        private final BallMillMachine ballMill;

        private BallMillRecipeLogic(BallMillMachine ballMill) {
            super(ballMill);
            this.ballMill = ballMill;
        }

        @Override
        protected ActionResult handleRecipeIO(GTRecipe recipe, IO io) {
            ActionResult result = super.handleRecipeIO(recipe, io);
            if (io == IO.IN) {
                try {
                    if (result.isSuccess()) {
                        ballMill.damageOneArmedBall(recipe);
                    }
                } finally {
                    ballMill.clearArmedBalls();
                }
            }
            return result;
        }
    }
}
