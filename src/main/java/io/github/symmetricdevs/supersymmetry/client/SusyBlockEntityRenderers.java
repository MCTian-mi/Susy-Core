package io.github.symmetricdevs.supersymmetry.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.client.renderer.handler.EccentricRollRenderer;
import io.github.symmetricdevs.supersymmetry.common.data.SusyBlockEntities;

/**
 * Client-side block-entity renderer registrations, on the mod event bus (mirrors the
 * {@code BreathingArmorModels} layer-registration handler).
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Supersymmetry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SusyBlockEntityRenderers {

    private SusyBlockEntityRenderers() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(SusyBlockEntities.ECCENTRIC_ROLL.get(), ctx -> new EccentricRollRenderer());
    }
}
