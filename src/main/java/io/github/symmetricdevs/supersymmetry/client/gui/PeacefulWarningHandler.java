package io.github.symmetricdevs.supersymmetry.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;

@Mod.EventBusSubscriber(modid = Supersymmetry.MOD_ID, value = Dist.CLIENT)
public class PeacefulWarningHandler {

    @SubscribeEvent
    public static void onDraw(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof OptionsScreen)) return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null) return;

        if (mc.level.getDifficulty() == Difficulty.PEACEFUL) {

            int x = screen.width / 2 + 122;
            int y = screen.height / 6 - 6;

            event.getGuiGraphics().drawString(mc.font, "⚠", x, y, 0xFF5555);
        }
    }
}
