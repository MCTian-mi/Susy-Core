package io.github.symmetricdevs.supersymmetry.common.metatileentities.single.electric;

import java.util.List;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.gui.ModularUI;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TieredMetaTileEntity;
import com.gregtechceu.gtceu.api.machine.IMachine;

public class MetaTileEntityRTG extends TieredMetaTileEntity {

    public MetaTileEntityRTG(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityRTG(this.metaTileEntityId, this.getTier());
    }

    @Override
    protected ModularUI createUI(Player Player) {
        return null;
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }

    @Override
    protected void reinitializeEnergyContainer() {
        super.reinitializeEnergyContainer();
    }

    @Override
    public void update() {
        super.update();
        this.energyContainer.changeEnergy(GTValues.VH[getTier() - 1]);
    }

    @Override
    protected boolean isEnergyEmitter() {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        tooltip.add(I18n.format("susy.machine.rtg.tooltip.info"));
        tooltip.add(I18n.format("susy.machine.rtg.tooltip.description"));
        tooltip.add(I18n.format("susy.machine.rtg.voltage_produced",
                new Object[] { GTValues.VH[getTier() - 1], GTValues.VNF[this.getTier() - 1] }));
        tooltip.add(I18n.format("gregtech.universal.tooltip.max_voltage_out",
                new Object[] { this.energyContainer.getOutputVoltage(), GTValues.VNF[this.getTier()] }));
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity",
                new Object[] { this.energyContainer.getEnergyCapacity() }));
    }

    @Override
    public boolean getIsWeatherOrTerrainResistant() {
        return true;
    }
}
