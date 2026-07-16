package io.github.symmetricdevs.supersymmetry.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import io.github.symmetricdevs.supersymmetry.SuSyValues;
import io.github.symmetricdevs.supersymmetry.api.machine.multiblock.IRedstoneControllable;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modern port of the 1.12.2 {@code MetaTileEntityComponentRedstoneController}:
 * a multiblock part that selects one operation from its controller's
 * {@link IRedstoneControllable} signal list and pulses it when the front face
 * receives a redstone signal.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ComponentRedstoneControllerMachine extends MultiblockPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ComponentRedstoneControllerMachine.class, MultiblockPartMachine.MANAGED_FIELD_HOLDER);

    public static final PartAbility ABILITY = new PartAbility("component_redstone_controller");

    @Persisted
    @DescSynced
    private int signalIndex;

    private boolean wasPowered;

    public ComponentRedstoneControllerMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            subscribeServerTick(null, this::checkRedstone);
        }
    }

    private void checkRedstone() {
        if (!isFormed()) {
            wasPowered = false;
            return;
        }
        Direction front = getFrontFacing();
        BlockPos frontPos = getPos().relative(front);
        int signal = getLevel().getSignal(frontPos, front.getOpposite());
        if (signal > 0 && !wasPowered) {
            pulseControllers();
        }
        wasPowered = signal > 0;
    }

    private void pulseControllers() {
        for (IMultiController controller : getControllers()) {
            if (controller instanceof IRedstoneControllable controllable && controllable.redstoneControlEnabled()) {
                controllable.pulse(signalIndex);
            }
        }
    }

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 176, 80);
        group.addWidget(new LabelWidget(8, 8,
                Component.translatable("susy.machine.component_redstone_controller.title")));

        var current = new LabelWidget(8, 28, getSignalComponent());
        group.addWidget(current);

        group.addWidget(new ButtonWidget(8, 48, 20, 20,
                new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("<")),
                click -> {
                    if (!click.isRemote) {
                        cycleSignal(-1);
                    }
                }));
        group.addWidget(new ButtonWidget(148, 48, 20, 20,
                new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture(">")),
                click -> {
                    if (!click.isRemote) {
                        cycleSignal(1);
                    }
                }));
        return group;
    }

    private void cycleSignal(int delta) {
        var names = getControllerSignalNames();
        if (names.isEmpty()) return;
        signalIndex = Math.floorMod(signalIndex + delta, names.size());
    }

    private Component getSignalComponent() {
        var names = getControllerSignalNames();
        if (names.isEmpty()) {
            return Component.translatable("susy.machine.component_redstone_controller.no_controller")
                    .withStyle(ChatFormatting.RED);
        }
        int index = Math.floorMod(signalIndex, names.size());
        MutableComponent name = Component.translatable("susy.signal." + names.get(index));
        return Component.translatable("susy.machine.component_redstone_controller.signal",
                Component.literal(String.valueOf(index)).withStyle(ChatFormatting.BLUE),
                name.withStyle(ChatFormatting.WHITE));
    }

    private java.util.List<String> getControllerSignalNames() {
        for (IMultiController controller : getControllers()) {
            if (controller instanceof IRedstoneControllable controllable && controllable.redstoneControlEnabled()) {
                return controllable.getSignals();
            }
        }
        return java.util.Collections.emptyList();
    }

    public static TraceabilityPredicate controllerPredicate() {
        return Predicates.abilities(ABILITY);
    }
}
