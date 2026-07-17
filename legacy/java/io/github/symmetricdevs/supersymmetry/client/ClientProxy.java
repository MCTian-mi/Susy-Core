package io.github.symmetricdevs.supersymmetry.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.item.IGTTool;
import com.gregtechceu.gtceu.api.item.armor.ArmorComponentItem;
import com.gregtechceu.gtceu.utils.input.SyncedKeyMappings;

import io.github.symmetricdevs.supersymmetry.Supersymmetry;
import io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystGroup;
import io.github.symmetricdevs.supersymmetry.api.recipes.catalysts.CatalystInfo;
import io.github.symmetricdevs.supersymmetry.api.util.RenderMaskManager;
import io.github.symmetricdevs.supersymmetry.common.CommonProxy;
import io.github.symmetricdevs.supersymmetry.common.blocks.SheetedFrameItemBlock;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyMetaBlocks;
import io.github.symmetricdevs.supersymmetry.common.item.armor.AdvancedBreathingApparatus;
import io.github.symmetricdevs.supersymmetry.common.item.behavior.PipeNetWalkerBehavior;
import io.github.symmetricdevs.supersymmetry.loaders.SuSyFluidTooltipLoader;
import io.github.symmetricdevs.supersymmetry.loaders.SuSyIRLoader;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Supersymmetry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public static int titleRenderTimer = -1;
    private static final int TITLE_RENDER_LENGTH = 150;

    @Override
    public void preLoad() {
        super.preLoad();
        // GeckoLib removed in 1.20.1; entity renderers registered via SusyMetaEntities.registerRenderers on mod bus
        SuSyIRLoader.initEntityRenderers();
        // VariantCoverableBlockRenderer deferred — codechicken.lib not available in 1.20.1
    }

    @Override
    public void load() {
        super.load();
        SuSyMetaBlocks.registerColors();
        SuSyFluidTooltipLoader.registerTooltips();
    }

    @Override
    public void postLoad() {
        super.postLoad();
        // sussypatches and SuSyConnectedTextures not yet ported to 1.20.1
        // TODO: Re-evaluate CTM connected textures integration
    }

    @SubscribeEvent
    public static void addMaterialFormulaHandler(@Nonnull ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (!(itemStack.getItem() instanceof SheetedFrameItemBlock)) return;

        MaterialEntry materialEntry = ChemicalHelper.getMaterialEntry(itemStack.getItem());

        if (materialEntry != null && materialEntry.material() != null) {
            if (materialEntry.material().getChemicalFormula() != null &&
                    !materialEntry.material().getChemicalFormula().isEmpty()) {
                event.getToolTip().add(
                        Component.literal(materialEntry.material().getChemicalFormula())
                                .withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    @SubscribeEvent
    public static void addPipelinerTooltip(@Nonnull ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltips = event.getToolTip();

        if (stack.getItem() instanceof IGTTool tool &&
                tool.getDefinition().getBehaviors().contains(PipeNetWalkerBehavior.INSTANCE)) {
            tooltips.add(Component.translatable("item.susy.tool.tooltip.pipeliner",
                    SyncedKeyMappings.TOOL_AOE_CHANGE.getTranslatedKeyMessage()));
        }
    }

    @SubscribeEvent
    public static void addCatalystTooltipHandler(@Nonnull ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        Collection<Component> tooltips = new ArrayList<>();

        // MetaOreDictItem is not ported to 1.20.1; ore dict name retrieval changed.
        // TODO: Re-add ore dict formula tooltip when available.

        for (CatalystGroup group : CatalystGroup.getCatalystGroups()) {
            ItemStack is = itemStack.copy();
            is.setCount(1);
            CatalystInfo catalystInfo = group.getCatalystInfos().get(is);
            if (catalystInfo != null) {
                tooltips.add(Component.translatable("susy.catalyst_group." + group.getName() + ".name")
                        .withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BLUE));
                if (catalystInfo.getTier() == CatalystInfo.NO_TIER) {
                    tooltips.add(Component.literal(
                            ChatFormatting.RED + "Disclaimer: Catalyst bonuses for non-tiered catalysts have not yet been implemented."));
                    tooltips.add(Component.translatable("susy.universal.catalysts.tooltip.yield",
                            catalystInfo.getYieldEfficiency()));
                    tooltips.add(Component.translatable("susy.universal.catalysts.tooltip.energy",
                            catalystInfo.getEnergyEfficiency()));
                    tooltips.add(Component.translatable("susy.universal.catalysts.tooltip.speed",
                            catalystInfo.getSpeedEfficiency()));
                } else {
                    tooltips.add(Component.translatable("susy.universal.catalysts.tooltip.tier",
                            GTValues.V[catalystInfo.getTier()], GTValues.VNF[catalystInfo.getTier()]));
                    tooltips.add(Component.translatable("susy.universal.catalysts.tooltip.yield.tiered",
                            catalystInfo.getYieldEfficiency()));
                    tooltips.add(Component.translatable("susy.universal.catalysts.tooltip.energy.tiered",
                            catalystInfo.getEnergyEfficiency()));
                    tooltips.add(Component.translatable("susy.universal.catalysts.tooltip.speed.tiered",
                            catalystInfo.getSpeedEfficiency()));
                }
            }
        }

        event.getToolTip().addAll(tooltips);
    }

    @SuppressWarnings("DataFlowIssue")
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        if (titleRenderTimer >= 0) {
            titleRenderTimer++;
            if (titleRenderTimer % TITLE_RENDER_LENGTH == 0) {
                int i = titleRenderTimer / TITLE_RENDER_LENGTH;
                if (i == 3) {
                    titleRenderTimer = -1;
                    return;
                }
                Minecraft mc = Minecraft.getInstance();
                mc.gui.setTitle(Component.translatable("supersymmetry.title." + i));
                mc.gui.updateSubtitle(Component.translatable("supersymmetry.subtitle." + i));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void afterRenderSubtitles(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() == VanillaGuiOverlay.SUBTITLES.type() && titleRenderTimer >= 0) {
            int screenWidth = event.getWindow().getGuiScaledWidth();
            int screenHeight = event.getWindow().getGuiScaledHeight();

            int topAlpha = 255;
            if (titleRenderTimer > TITLE_RENDER_LENGTH * 5 / 2) {
                topAlpha -= (titleRenderTimer - TITLE_RENDER_LENGTH * 5 / 2) * 255 / TITLE_RENDER_LENGTH;
                if (topAlpha < 0) topAlpha = 0;
            }
            int bottomAlpha = 255;
            if (titleRenderTimer > TITLE_RENDER_LENGTH * 2) {
                bottomAlpha -= (titleRenderTimer - TITLE_RENDER_LENGTH * 2) * 255 / TITLE_RENDER_LENGTH;
                if (bottomAlpha < 0) bottomAlpha = 0;
            }

            int colorTop = (topAlpha << 24) | 0x000000;
            int colorBottom = (bottomAlpha << 24) | 0x000000;
            event.getGuiGraphics().fillGradient(0, 0, screenWidth, screenHeight, colorTop, colorBottom, 0.0F);
        }
    }

    @SubscribeEvent
    public static void onLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
        var livingBase = event.getEntity();
        if (!(livingBase instanceof Player)) return;

        ItemStack from = event.getFrom(), into = event.getTo();

        if (from.isItemEqual(into)) return;

        EquipmentSlot slot = event.getSlot();
        changeSkinVisibility(from, slot, false);
        changeSkinVisibility(into, slot, true);
    }

    public static void changeSkinVisibility(ItemStack armor, EquipmentSlot slot, boolean into) {
        if (armor.getItem() instanceof ArmorComponentItem metaArmor) {
            var armorLogic = metaArmor.getArmorLogic();
            if (armorLogic != null &&
                    armorLogic.getClass().equals(AdvancedBreathingApparatus.class)) {
                boolean visible = !into;
                Options settings = Minecraft.getInstance().options;
                switch (slot) {
                    case HEAD -> settings.setModelPart(PlayerModelPart.HAT, visible);
                    case CHEST -> {
                        settings.setModelPart(PlayerModelPart.CAPE, visible);
                        settings.setModelPart(PlayerModelPart.JACKET, visible);
                        settings.setModelPart(PlayerModelPart.LEFT_SLEEVE, visible);
                        settings.setModelPart(PlayerModelPart.RIGHT_SLEEVE, visible);
                    }
                    case LEGS -> {
                        settings.setModelPart(PlayerModelPart.LEFT_PANTS_LEG, visible);
                        settings.setModelPart(PlayerModelPart.RIGHT_PANTS_LEG, visible);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (Minecraft.getInstance().level == event.getLevel()) {
            RenderMaskManager.clearDisabled();
        }
    }
}
