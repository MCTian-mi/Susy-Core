package io.github.symmetricdevs.supersymmetry.common.machine.storage;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.storage.CrateMachine;

import io.github.symmetricdevs.supersymmetry.mixins.gtceu.CrateMachineAccessor;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

/**
 * SuSy's "locked" loot crate (1.12.2 {@code MetaTileEntityLockedCrate}). A
 * {@link CrateMachine} that is sealed against automation and against opening
 * without a code breacher: the inventory filter rejects every item, so neither
 * hoppers nor the GUI slots accept insertions, and {@link #shouldOpenUI} is
 * gated below.
 * <p>
 * It is registered with a loot {@code "inventory"} NBT payload (see
 * {@code SusyMachines}); {@code CrateMachine.onMachinePlaced} then loads that
 * loot and the crate drops with its contents preserved ({@code saveBreak()}).
 */
public class LockedCrateMachine extends CrateMachine {

    public LockedCrateMachine(IMachineBlockEntity holder, Material material, int inventorySize, Object... args) {
        super(holder, material, inventorySize);
        // Inaccessible to automation and to insertion through the GUI slots.
        this.inventory.setFilter(stack -> false);
        // Locked crates are permanently taped so they drop their contents when broken.
        ((CrateMachineAccessor) this).setTaped(true);
    }

    @Override
    public void onMachinePlaced(LivingEntity player, ItemStack stack) {
        super.onMachinePlaced(player, stack);
        // Re-apply taped flag after placement from an item; GTCEu may reset it.
        ((CrateMachineAccessor) this).setTaped(true);
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        // TODO)) code-breacher gate: only open when the held item is
        // SuSyMetaItems.CODE_BREACHER (Phase 5 item) and play the locked-crate
        // sound otherwise (Phase 6). See 1.12.2 MetaTileEntityLockedCrate.
        return false;
    }
}
