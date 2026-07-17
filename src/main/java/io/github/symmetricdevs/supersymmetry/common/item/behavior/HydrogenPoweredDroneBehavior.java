package io.github.symmetricdevs.supersymmetry.common.item.behavior;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IDurabilityBar;
import com.gregtechceu.gtceu.api.item.component.forge.IComponentCapability;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import com.gregtechceu.gtceu.api.item.capability.ElectricItem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Hydrogen-powered drone behavior.
 * Provides electric energy storage (rechargeable) using GTCEu-Modern's ElectricItem pattern,
 * and renders a custom durability bar for the energy level.
 */
public class HydrogenPoweredDroneBehavior implements IComponentCapability, IDurabilityBar, IAddInformation {

    public final long maxCharge;

    public HydrogenPoweredDroneBehavior(long maxCharge) {
        this.maxCharge = maxCharge;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(ItemStack itemStack, @NotNull Capability<T> cap) {
        return GTCapability.CAPABILITY_ELECTRIC_ITEM.orEmpty(cap,
                LazyOptional.of(() -> new ElectricItem(itemStack, maxCharge, GTValues.LV, true, false)));
    }

    // -- Durability bar --

    @Override
    public float getDurabilityForDisplay(ItemStack stack) {
        var electricItem = getElectricItem(stack);
        if (electricItem != null) {
            return 1.0F - (float) electricItem.getCharge() / (float) electricItem.getMaxCharge();
        }
        return 1.0F;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        var electricItem = getElectricItem(stack);
        return electricItem != null && electricItem.getCharge() < electricItem.getMaxCharge();
    }

    @Override
    @Nullable
    public IntIntPair getDurabilityColorsForDisplay(ItemStack itemStack) {
        // Blue gradient for hydrogen energy bar: 0x0097CE
        return IntIntPair.of(0x0097CE, 0x004E73);
    }

    @Override
    public boolean showFullBar(ItemStack itemStack) {
        return false;
    }

    // -- Tooltip --

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        var electricItem = getElectricItem(stack);
        if (electricItem != null) {
            tooltipComponents.add(Component.translatable("metaitem.generic.electric_item.tooltip",
                    electricItem.getCharge(), electricItem.getMaxCharge(), GTValues.VNF[electricItem.getTier()]));
        }
    }

    @Nullable
    private static com.gregtechceu.gtceu.api.capability.IElectricItem getElectricItem(ItemStack stack) {
        var cap = stack.getCapability(GTCapability.CAPABILITY_ELECTRIC_ITEM);
        return cap.resolve().orElse(null);
    }
}
