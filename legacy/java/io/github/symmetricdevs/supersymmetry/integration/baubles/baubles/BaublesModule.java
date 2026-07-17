package io.github.symmetricdevs.supersymmetry.integration.baubles;

import java.util.Collections;
import java.util.List;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.jetbrains.annotations.NotNull;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.gregtechceu.gtceu.api.modules.GregTechModule;
import com.gregtechceu.gtceu.common.items.MetaItems;
import com.gregtechceu.gtceu.integration.IntegrationSubmodule;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.SusyLog;
import io.github.symmetricdevs.supersymmetry.common.item.SuSyMetaItems;
import io.github.symmetricdevs.supersymmetry.common.item.behavior.ArmorBaubleBehavior;
import io.github.symmetricdevs.supersymmetry.modules.SuSyModules;

@GregTechModule(
                moduleID = SuSyModules.MODULE_BAUBLES,
                containerID = Supersymmetry.MODID,
                modDependencies = "baubles",
                name = "SuSy Baubles Integration",
                description = "SuSy Baubles Integration Module")
public class BaublesModule extends IntegrationSubmodule {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerItems(RegistryEvent.Register<Item> event) {
        MetaItems.SEMIFLUID_JETPACK.addComponents(new ArmorBaubleBehavior(BaubleType.BODY));
        MetaItems.ELECTRIC_JETPACK.addComponents(new ArmorBaubleBehavior(BaubleType.BODY));
        MetaItems.ELECTRIC_JETPACK_ADVANCED.addComponents(new ArmorBaubleBehavior(BaubleType.BODY));
        SuSyMetaItems.JET_WINGPACK.addComponents(new ArmorBaubleBehavior(BaubleType.BODY));
        MetaItems.NIGHTVISION_GOGGLES.addComponents(new ArmorBaubleBehavior(BaubleType.HEAD));
    }

    @NotNull
    @Override
    public List<Class<?>> getEventBusSubscribers() {
        return Collections.singletonList(BaublesModule.class);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        SusyLog.logger.info("Baubles found. Enabling integration...");
    }

    public static ItemStack getElytraBauble(@NotNull LivingEntity entity) {
        if (entity instanceof Player player) {
            // The body slot is 5
            return BaublesApi.getBaublesHandler(player).getStackInSlot(5);
        }
        return ItemStack.EMPTY;
    }
}
