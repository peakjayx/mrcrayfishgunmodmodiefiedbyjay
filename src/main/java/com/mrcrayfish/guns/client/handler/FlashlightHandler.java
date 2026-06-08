package com.mrcrayfish.guns.client.handler;

import com.mrcrayfish.guns.client.KeyBinds;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.init.ModItems;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.item.attachment.IAttachment;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.MessageToggleLight;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// Visual dynamic light pending RyoamicLights integration.
// Currently only handles toggle + NBT sync.
@OnlyIn(Dist.CLIENT)
public class FlashlightHandler
{
    private static FlashlightHandler instance;

    private FlashlightHandler() {}

    public static FlashlightHandler get()
    {
        if (instance == null) instance = new FlashlightHandler();
        return instance;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        if (!KeyBinds.KEY_FLASHLIGHT.consumeClick()) return;
        ItemStack held = mc.player.getMainHandItem();
        if (!hasFlashlight(held)) return;

        CompoundNBT tag = held.getOrCreateTag();
        tag.putBoolean("FlashlightOn", !tag.getBoolean("FlashlightOn"));
        PacketHandler.getPlayChannel().sendToServer(new MessageToggleLight(MessageToggleLight.FLASHLIGHT));
    }

    public static boolean hasFlashlight(ItemStack stack)
    {
        if (!(stack.getItem() instanceof GunItem)) return false;
        ItemStack l = Gun.getAttachment(IAttachment.Type.SIDE_LEFT,   stack);
        ItemStack r = Gun.getAttachment(IAttachment.Type.SIDE_RIGHT,  stack);
        ItemStack u = Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack);
        return (!l.isEmpty() && l.getItem() == ModItems.FLASHLIGHT.get())
            || (!r.isEmpty() && r.getItem() == ModItems.FLASHLIGHT.get())
            || (!u.isEmpty() && u.getItem() == ModItems.FLASHLIGHT.get());
    }
}
