package io.github.symmetricdevs.supersymmetry.mixins.bdsandm;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import funwayguy.bdsandm.inventory.capability.BdsmCapabilies;
import funwayguy.bdsandm.inventory.capability.ICrate;
import funwayguy.bdsandm.items.ItemCrate;

@Mixin(value = ItemCrate.class, remap = false)
public class ItemCrateMixin extends ItemBlock {

    public ItemCrateMixin(Block block) {
        super(block);
    }

    /**
     * @author Bruberu
     * @reason It was literally using an assert in client-side logic, causing crashes in servers.
     */
    @Overwrite
    public CompoundTag getNBTShareTag(ItemStack stack) {
        ICrate crate = (ICrate) stack.getCapability(BdsmCapabilies.CRATE_CAP, (Direction) null);
        if (crate != null) {
            stack.setTagInfo("crateCap", crate.serializeNBT());
        }
        return super.getNBTShareTag(stack);
    }

    /**
     * @author Bruberu
     * @reason It was literally using an assert in client-side logic, causing crashes in servers.
     */
    @Overwrite
    public void readNBTShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        super.readNBTShareTag(stack, nbt);
        ICrate crate = (ICrate) stack.getCapability(BdsmCapabilies.CRATE_CAP, (Direction) null);

        if (crate != null) {
            crate.deserializeNBT(stack.getOrCreateSubCompound("crateCap"));
        }
    }
}
