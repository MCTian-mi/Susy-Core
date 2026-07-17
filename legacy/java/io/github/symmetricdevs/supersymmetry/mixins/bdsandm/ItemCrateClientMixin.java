package io.github.symmetricdevs.supersymmetry.mixins.bdsandm;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import funwayguy.bdsandm.inventory.capability.BdsmCapabilies;
import funwayguy.bdsandm.inventory.capability.ICrate;
import funwayguy.bdsandm.items.ItemCrate;

@Mixin(ItemCrate.class)
public class ItemCrateClientMixin extends ItemBlock {

    public ItemCrateClientMixin(Block block) {
        super(block);
    }

    /**
     * @author Bruberu
     * @reason It was literally using an assert in client-side logic, causing crashes in servers.
     */
    @Overwrite(remap = true)
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip,
                               @Nonnull ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        ICrate crate = stack.getCapability(BdsmCapabilies.CRATE_CAP, null);

        if (crate == null) {
            return;
        }

        if (!crate.getRefItem().isEmpty()) {
            tooltip.add("Item: " + crate.getRefItem().getDisplayName());
            tooltip.add("Amount: " + formatValue(crate.getCount()));
        } else {
            tooltip.add("[EMPTY]");
        }
    }

    private static final DecimalFormat df = new DecimalFormat("0.##");
    private static final String[] suffixes = new String[] { "", "K", "M", "B", "T" };

    private static String formatValue(long value) {
        String s = "";
        double n = 1.0F;

        for (int i = suffixes.length - 1; i >= 0; --i) {
            n = Math.pow(1000.0F, i);
            if ((double) Math.abs(value) >= n) {
                s = suffixes[i];
                break;
            }
        }

        return df.format((double) value / n) + s;
    }
}
