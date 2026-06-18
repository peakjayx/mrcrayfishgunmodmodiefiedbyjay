package com.mrcrayfish.guns.event;

import com.mrcrayfish.guns.api.CgmGunApi;
import com.mrcrayfish.guns.common.GunRegistry;
import com.mrcrayfish.guns.item.GunItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.ArrayList;

/** ID-Vergabe bei Inventar-Scan */
public class GunIdHandler
{
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END) return;
        PlayerEntity player = event.player;
        if(player.level.isClientSide()) return;
        if(player.tickCount % 20 != 0) return;

        ServerWorld world = (ServerWorld) player.level;
        GunRegistry reg   = GunRegistry.get(world);

        List<ItemStack> stacks = new ArrayList<>();
        stacks.addAll(player.inventory.items);
        stacks.addAll(player.inventory.offhand);

        for(ItemStack stack : stacks)
        {
            if(!(stack.getItem() instanceof GunItem)) continue;
            if(CgmGunApi.getGunId(stack) != null) continue;

            String id = reg.assignNewId();
            stack.getOrCreateTag().putString(CgmGunApi.GUN_ID_KEY, id);
            reg.bindOwner(id, player);
        }
    }
}
