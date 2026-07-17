package io.github.symmetricdevs.supersymmetry.mixins.gregtech;

import java.util.List;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerBase;


@Mixin(MultiblockControllerBase.class)
public abstract class MultiblockControllerBaseMixin extends MetaTileEntity {

    public MultiblockControllerBaseMixin(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        if (ConfigHolder.machines.doTerrainExplosion && getIsWeatherOrTerrainResistant()) {
            tooltip.add(I18n.format("gregtech.universal.tooltip.terrain_resist"));
        }
    }
}
