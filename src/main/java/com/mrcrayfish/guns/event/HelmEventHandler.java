package com.mrcrayfish.guns.event;

import com.mrcrayfish.guns.api.IHelm;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HelmEventHandler
{
    /** visier down blocks blindness */
    @SubscribeEvent
    public void onPotionApplicable(PotionEvent.PotionApplicableEvent event)
    {
        if(!(event.getEntityLiving() instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        ItemStack helm = player.getItemBySlot(EquipmentSlotType.HEAD);
        if(!(helm.getItem() instanceof IHelm)) return;
        if(!((IHelm) helm.getItem()).isVisierDown(helm)) return;

        net.minecraft.potion.Effect effect = event.getPotionEffect().getEffect();
        net.minecraft.util.ResourceLocation rl =
            net.minecraftforge.registries.ForgeRegistries.POTIONS.getKey(effect);

        boolean isBlind = effect == Effects.BLINDNESS
            || (rl != null && rl.getPath().contains("blind"));

        if(isBlind) event.setResult(Event.Result.DENY);
    }
}
