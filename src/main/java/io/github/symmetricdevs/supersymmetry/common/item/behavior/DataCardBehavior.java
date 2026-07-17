package io.github.symmetricdevs.supersymmetry.common.item.behavior;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;

import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class DataCardBehavior implements IAddInformation {

    private final Consumer<List<Component>> lines;
    private final List<String> keys;

    public DataCardBehavior(@NotNull Consumer<List<Component>> lines, List<String> keys) {
        this.lines = lines;
        this.keys = keys;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        lines.accept(tooltipComponents);
        var tag = stack.getTag();
        if (tag != null) {
            for (String key : keys) {
                if (tag.contains(key, Tag.TAG_STRING)) {
                    tooltipComponents.add(Component.translatable(stack.getDescriptionId() + ".tag." + tag.getString(key)));
                }
            }
        }
    }
}
