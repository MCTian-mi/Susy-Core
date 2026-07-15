package io.github.symmetricdevs.supersymmetry.common.machine.storage;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.storage.CrateMachine;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
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
 * <p>
 * TODO)) Phase 7: add a {@code CrateMachineAccessor} mixin so this crate can be
 * forced taped at construction (1.12.2 semantics: {@code setTaped(true)}) and so
 * {@code getItemStackLimit} can be overridden to 1. {@code CrateMachine.isTaped}
 * is private with no setter and there is no {@code getItemStackLimit} hook in
 * GTCEu-Modern, so a tiny accessor is the clean route — the same pattern 1.12.2
 * used ({@code MetaTileEntityCrateAccessor}).
 */
public class LockedCrateMachine extends CrateMachine {

    public LockedCrateMachine(IMachineBlockEntity holder, Material material, int inventorySize, Object... args) {
        super(holder, material, inventorySize);
        // Inaccessible to automation and to insertion through the GUI slots.
        this.inventory.setFilter(stack -> false);
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        // TODO)) code-breacher gate: only open when the held item is
        // SuSyMetaItems.CODE_BREACHER (Phase 5 item) and play the locked-crate
        // sound otherwise (Phase 6). See 1.12.2 MetaTileEntityLockedCrate.
        return false;
    }
}
