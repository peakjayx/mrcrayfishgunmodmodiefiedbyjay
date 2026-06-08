package com.mrcrayfish.guns.client.handler;

import com.mrcrayfish.guns.client.KeyBinds;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.item.SwitchGunItem;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.MessageFireMode;
import com.mrcrayfish.guns.util.GunEnchantmentHelper;
import com.mrcrayfish.guns.util.GunModifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FireModeHandler
{
    private static FireModeHandler instance;

    public static FireModeHandler get()
    {
        if(instance == null) instance = new FireModeHandler();
        return instance;
    }

    private int burstRemaining = 0;
    private int burstTickTimer = 0;

    private FireModeHandler() {}

    private boolean isInGame()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.overlay == null && mc.screen == null
            && mc.mouseHandler.isMouseGrabbed() && mc.isWindowActive();
    }

    /** cycle fire mode on V key */
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if(!isInGame()) return;
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null) return;
        if(!KeyBinds.KEY_FIRE_MODE.consumeClick()) return;

        ItemStack held = mc.player.getMainHandItem();
        if(!(held.getItem() instanceof SwitchGunItem)) return;

        int next = (SwitchGunItem.getFireMode(held) + 1) % 3;
        SwitchGunItem.setFireMode(held, next);
        PacketHandler.getPlayChannel().sendToServer(new MessageFireMode(next));
    }

    /** schedule 2 extra shots when burst mode triggers */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onMouseClick(InputEvent.ClickInputEvent event)
    {
        if(event.isCanceled() || !event.isAttack()) return;
        if(!isInGame()) return;

        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if(player == null) return;

        ItemStack held = player.getMainHandItem();
        if(!(held.getItem() instanceof SwitchGunItem)) return;
        if(SwitchGunItem.getFireMode(held) != SwitchGunItem.BURST) return;
        if(player.getCooldowns().isOnCooldown(held.getItem())) return;
        if(!Gun.hasAmmo(held) && !player.isCreative()) return;

        // ShootingHandler (LOWEST priority) fires shot #1; we queue shots #2 & #3
        SwitchGunItem item = (SwitchGunItem) held.getItem();
        Gun gun = item.getModifiedGun(held);
        int rate = GunEnchantmentHelper.getRate(held, gun);
        rate = GunModifierHelper.getModifiedRate(held, rate);
        burstRemaining = 2;
        burstTickTimer = rate;
    }

    /** fires queued burst shots */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END) return;
        if(burstRemaining <= 0) return;

        burstTickTimer--;
        if(burstTickTimer > 0) return;

        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if(player == null)
        {
            burstRemaining = 0;
            return;
        }

        ItemStack held = player.getMainHandItem();
        if(!(held.getItem() instanceof SwitchGunItem))
        {
            burstRemaining = 0;
            return;
        }

        ShootingHandler.get().fire(player, held);
        burstRemaining--;

        if(burstRemaining > 0)
        {
            SwitchGunItem item = (SwitchGunItem) held.getItem();
            Gun gun = item.getModifiedGun(held);
            int rate = GunEnchantmentHelper.getRate(held, gun);
            rate = GunModifierHelper.getModifiedRate(held, rate);
            burstTickTimer = rate;
        }
    }

    /** bottom-right HUD: current fire mode */
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event)
    {
        if(event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null) return;

        ItemStack held = mc.player.getMainHandItem();
        if(!(held.getItem() instanceof SwitchGunItem)) return;

        int mode = SwitchGunItem.getFireMode(held);
        String label = SwitchGunItem.getModeName(mode);

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        int tw = mc.font.width(label);

        mc.font.drawShadow(event.getMatrixStack(), label, sw - tw - 8, sh - 16, 0xFFFFFF);
    }
}
