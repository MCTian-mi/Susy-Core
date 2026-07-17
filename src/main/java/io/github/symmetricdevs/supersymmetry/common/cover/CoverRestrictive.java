package io.github.symmetricdevs.supersymmetry.common.cover;

import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.utils.ItemStackHashStrategy;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Ported from the 1.12.2 {@code supersymmetry.common.covers.CoverRestrictive}.
 * <p>
 * Prevents more than one slot from automatically filling with the same item type.
 * Only allows an item into an empty slot if no other slot already holds that item.
 */
public class CoverRestrictive extends CoverBehavior {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(CoverRestrictive.class,
            CoverBehavior.MANAGED_FIELD_HOLDER);

    protected ItemHandlerRestrictive itemHandler;

    public CoverRestrictive(@NotNull CoverDefinition definition, @NotNull ICoverable coverHolder,
                            @NotNull Direction attachedSide) {
        super(definition, coverHolder, attachedSide);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public boolean canAttach() {
        return coverHolder.getItemHandlerCap(attachedSide, false) != null;
    }

    @Override
    public boolean canPipePassThrough() {
        return true;
    }

    @Nullable
    @Override
    public IItemHandlerModifiable getItemHandlerCap(@Nullable IItemHandlerModifiable defaultValue) {
        if (defaultValue == null) {
            return null;
        }
        IItemHandlerModifiable delegate = defaultValue;
        if (itemHandler == null || itemHandler.delegate != delegate) {
            this.itemHandler = new ItemHandlerRestrictive(delegate);
        }
        return itemHandler;
    }

    protected static class ItemHandlerRestrictive implements IItemHandlerModifiable {

        private final IItemHandlerModifiable delegate;
        private final Map<ItemStack, Set<Integer>> multimap = new Object2ObjectOpenCustomHashMap<>(
                ItemStackHashStrategy.comparingAllButCount());

        public ItemHandlerRestrictive(IItemHandlerModifiable delegate) {
            this.delegate = delegate;
        }

        private void addToMap(int slot, ItemStack stack) {
            multimap.computeIfAbsent(stack, s -> new ObjectArraySet<>()).add(slot);
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            delegate.setStackInSlot(slot, stack);
        }

        @Override
        public int getSlots() {
            return delegate.getSlots();
        }

        @NotNull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return delegate.getStackInSlot(slot);
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            // Check if the current slot already has the item (by checking if it stacks). If not:
            // Check if it happens to be somewhere else. This is cached in a multimap.
            // If it is, we reject the stack, but otherwise, we let it through.
            if (!getStackInSlot(slot).isEmpty() && ItemHandlerHelper.canItemStacksStack(stack, getStackInSlot(slot))) {
                return delegate.insertItem(slot, stack, simulate);
            }
            if (getStackInSlot(slot).isEmpty()) {
                if (multimap.containsKey(stack)) {
                    // We do have to make sure it's actually there! (We also have to stop CMEs.)
                    for (int i : new ArrayList<>(multimap.get(stack))) {
                        if (ItemHandlerHelper.canItemStacksStack(stack, getStackInSlot(i))) {
                            return stack;
                        } else {
                            multimap.get(stack).remove(i);
                        }
                    }
                }
                // If it's not already in the set of what goes where, we search if it happens to be anywhere already,
                // for some reason.
                for (int i = 0; i < getSlots(); i++) {
                    if (ItemHandlerHelper.canItemStacksStack(stack, getStackInSlot(i))) {
                        addToMap(i, stack);
                        return stack;
                    }
                }
                // OK, we let it through now that we know it's not anywhere else.
                return delegate.insertItem(slot, stack, simulate);
            }
            // It simply wouldn't even fit in that slot anyway.
            return stack;
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack result = delegate.extractItem(slot, amount, simulate);
            if (!simulate && !result.isEmpty()) {
                // Clean up multimap for the extracted item
                multimap.values().forEach(s -> s.remove(slot));
            }
            return result;
        }

        @Override
        public int getSlotLimit(int slot) {
            return delegate.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return delegate.isItemValid(slot, stack);
        }
    }
}
