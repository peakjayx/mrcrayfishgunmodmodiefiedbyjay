package com.mrcrayfish.guns.client.handler;

import com.mrcrayfish.guns.client.KeyBinds;
import com.mrcrayfish.guns.item.SwatHelmItem;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.MessageToggleThermal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class ThermalModeHandler
{
    private static ThermalModeHandler instance;

    private ThermalModeHandler() {}

    public static ThermalModeHandler get()
    {
        if(instance == null) instance = new ThermalModeHandler();
        return instance;
    }

    private ItemStack getHelm()
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null) return ItemStack.EMPTY;
        return mc.player.getItemBySlot(EquipmentSlotType.HEAD);
    }

    private boolean isThermal()
    {
        ItemStack helm = getHelm();
        return helm.getItem() instanceof SwatHelmItem
            && SwatHelmItem.isThermalActive(helm);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null || mc.screen != null) return;
        if(!(getHelm().getItem() instanceof SwatHelmItem)) return;
        if(KeyBinds.KEY_THERMAL.consumeClick())
            PacketHandler.getPlayChannel().sendToServer(new MessageToggleThermal());
    }

    /** cold blue overlay */
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event)
    {
        if(event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if(!isThermal()) return;
        Minecraft mc = Minecraft.getInstance();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        AbstractGui.fill(event.getMatrixStack(), 0, 0, w, h, 0x22000066);
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
    }

    /** orange outlines through walls on all living entities */
    @SubscribeEvent
    public void onRenderEntity(RenderLivingEvent.Post<?, ?> event)
    {
        if(!isThermal()) return;
        LivingEntity entity = event.getEntity();
        Minecraft mc = Minecraft.getInstance();
        if(entity == mc.player) return;

        AxisAlignedBB bb = entity.getBoundingBox().move(
            -entity.getX(), -entity.getY(), -entity.getZ());

        com.mojang.blaze3d.systems.RenderSystem.depthFunc(GL11.GL_ALWAYS);
        WorldRenderer.renderLineBox(
            event.getMatrixStack(),
            event.getBuffers().getBuffer(RenderType.lines()),
            bb,
            1.0F, 0.4F, 0.0F, 1.0F);
        com.mojang.blaze3d.systems.RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }
}
