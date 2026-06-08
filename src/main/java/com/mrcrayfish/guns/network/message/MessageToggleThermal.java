package com.mrcrayfish.guns.network.message;

import com.mrcrayfish.guns.common.ThermalModeManager;
import com.mrcrayfish.guns.item.SwatHelmItem;
import com.mrcrayfish.guns.network.PacketHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class MessageToggleThermal implements IMessage
{
    public MessageToggleThermal() {}

    @Override public void encode(PacketBuffer buf) {}
    @Override public void decode(PacketBuffer buf) {}

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = ctx.get().getSender();
            if(player == null) return;
            if(!ThermalModeManager.isAllowed(player.getUUID())) return;
            ItemStack helm = player.getItemBySlot(EquipmentSlotType.HEAD);
            if(!(helm.getItem() instanceof SwatHelmItem)) return;
            boolean next = !SwatHelmItem.isThermalActive(helm);
            SwatHelmItem.setThermalActive(helm, next);
            PacketHandler.getPlayChannel().send(
                PacketDistributor.PLAYER.with(() -> player),
                new MessageSyncThermal(next));
        });
        ctx.get().setPacketHandled(true);
    }
}
