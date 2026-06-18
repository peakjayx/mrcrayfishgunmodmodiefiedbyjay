package com.mrcrayfish.guns.event;

import com.mrcrayfish.guns.api.CgmGunApi;
import com.mrcrayfish.guns.item.GunItem;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** ID-Zeile im Tooltip */
public class GunTooltipHandler
{
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event)
    {
        if(!(event.getItemStack().getItem() instanceof GunItem)) return;
        String id = CgmGunApi.getGunId(event.getItemStack());
        if(id == null) return;
        event.getToolTip().add(
            new StringTextComponent(
                TextFormatting.DARK_GRAY + "ID: "
                + TextFormatting.GRAY + id)
        );
    }
}
