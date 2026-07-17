package io.github.symmetricdevs.supersymmetry.common.item;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.forge.IComponentCapability;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Modern port of the 1.12.2 {@code ToolBehaviorExtender}.
 * <p>
 * In 1.12.2 this class added an {@code IToolBehavior} ({@code PipeNetWalkerBehavior})
 * to the wrench and wire-cutter tool definitions at runtime. In GTCEu-Modern the
 * tool behaviour system uses {@link IItemComponent} attachments on
 * {@link com.gregtechceu.gtceu.api.item.ComponentItem} instances.
 * <p>
 * This class provides a minimal {@link IComponentCapability} implementation that
 * can be attached to tool items to expose forge capabilities. The more complex
 * pipe-net-walking logic from 1.12.2 is deferred to a later phase; for now this
 * is a simplified placeholder that adds a tooltip indicating pipe-net-walker
 * support.
 * <p>
 * Usage (in a registration callback):
 * <pre>{@code
 * REGISTRATE.item("wrench", ComponentItem::create)
 *     .onRegister(attach(new PipeNetWalkerTooltip()))
 *     ...
 * }</pre>
 *
 * @see IAddInformation
 * @see IComponentCapability
 */
public class ToolBehaviorExtender {

    private ToolBehaviorExtender() {}

    /**
     * An {@link IAddInformation} component that adds a "Pipe-net walker"
     * tooltip, indicating the tool has pipe-walking capabilities.
     */
    public static class PipeNetWalkerTooltip implements IAddInformation {

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                    @NotNull List<Component> tooltipComponents,
                                    @NotNull TooltipFlag isAdvanced) {
            tooltipComponents.add(Component.translatable("susy.item.pipe_net_walker.tooltip"));
        }
    }

    /**
     * A capability provider that exposes the pipe-net-walking capability
     * on tools that have this component attached.
     */
    public static class PipeNetWalkerCapability implements IComponentCapability {

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull ItemStack itemStack,
                                                           @NotNull Capability<T> capability) {
            // TODO: Phase 6 -- attach the actual pipe-net-walking capability
            // when SuSyCapabilities is ported.
            return LazyOptional.empty();
        }
    }
}
