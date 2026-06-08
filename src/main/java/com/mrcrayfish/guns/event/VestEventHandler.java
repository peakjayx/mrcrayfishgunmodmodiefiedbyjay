package com.mrcrayfish.guns.event;

import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.api.IProtectiveVest;
import com.mrcrayfish.guns.capability.VestCapability;
import com.mrcrayfish.guns.capability.VestCapabilityProvider;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.MessageSyncVest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class VestEventHandler
{
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event)
    {
        if(event.getEntityLiving().level.isClientSide()) return;
        if(!(event.getEntityLiving() instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        player.getCapability(VestCapability.VEST_HANDLER).ifPresent(handler ->
        {
            ItemStack vest = handler.getVest();
            if(vest.isEmpty()) return;
            float reduction = getProtection(vest.getItem());
            if(reduction > 0f)
                event.setAmount(event.getAmount() * (1f - reduction));
        });
    }

    private static float getProtection(Item item)
    {
        if(item instanceof IProtectiveVest)
            return ((IProtectiveVest) item).getProtection();
        return 0f;
    }

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof PlayerEntity)
            event.addCapability(new ResourceLocation(Reference.MOD_ID, "vest"), new VestCapabilityProvider());
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        event.getOriginal().revive();
        event.getOriginal().getCapability(VestCapability.VEST_HANDLER).ifPresent(old ->
            event.getPlayer().getCapability(VestCapability.VEST_HANDLER).ifPresent(n ->
                n.setVest(old.getVest().copy())
            )
        );
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(event.getPlayer() instanceof ServerPlayerEntity)
            syncVest((ServerPlayerEntity) event.getPlayer());
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        if(event.getPlayer() instanceof ServerPlayerEntity)
            syncVest((ServerPlayerEntity) event.getPlayer());
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(!(event.getTarget() instanceof ServerPlayerEntity)) return;
        if(!(event.getPlayer() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity tracked = (ServerPlayerEntity) event.getTarget();
        ServerPlayerEntity tracker = (ServerPlayerEntity) event.getPlayer();
        tracked.getCapability(VestCapability.VEST_HANDLER).ifPresent(h ->
        {
            if(!h.getVest().isEmpty())
                PacketHandler.getPlayChannel().send(
                    PacketDistributor.PLAYER.with(() -> tracker),
                    new MessageSyncVest(tracked.getId(), h.getVest())
                );
        });
    }

    public static void syncVest(ServerPlayerEntity player)
    {
        player.getCapability(VestCapability.VEST_HANDLER).ifPresent(h ->
            PacketHandler.getPlayChannel().send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                new MessageSyncVest(player.getId(), h.getVest())
            )
        );
    }
}
