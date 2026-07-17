package io.github.symmetricdevs.supersymmetry.common.metatileentities.single.railinterfaces;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import org.jetbrains.annotations.NotNull;

import cam72cam.immersiverailroading.entity.Freight;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityStockItemExchanger extends MetaTileEntityStockInteractor {

    private static final ItemStackHandler dummyHandler = new ItemStackHandler(0);

    public MetaTileEntityStockItemExchanger(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SusyTextures.STOCK_ITEM_EXCHANGER);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityStockItemExchanger(this.metaTileEntityId);
    }

    // TODO: cache this?
    @Override
    protected <T> T getStockCapability(Capability<T> capability, Direction side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            IItemHandler itemHandler = null;
            if (this.stock == null || this.stock.isDead()) {
                itemHandler = dummyHandler;
            } else if (this.stock instanceof Freight itemStock) {
                itemHandler = itemStock.cargoItems.internal;
            } // TODO: add more if-else arguments if there's more kinds of stocks. Or maybe a utility method
            if (itemHandler != null) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler);
            }
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        tooltip.add(I18n.format("susy.stock_interfaces.item_exchanger.description"));
    }
}
