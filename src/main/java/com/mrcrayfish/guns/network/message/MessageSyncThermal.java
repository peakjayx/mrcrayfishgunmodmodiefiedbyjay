package com.mrcrayfish.guns.network.message;

import com.mrcrayfish.guns.item.SwatHelmItem;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSyncThermal implements IMessage
{
    private boolean active;

    public MessageSyncThermal() {}
    public MessageSyncThermal(boolean active) { this.active = active; }

    @Override public void encode(PacketBuffer buf) { buf.writeBoolean(active); }
    @Override public void decode(PacketBuffer buf) { active = buf.readBoolean(); }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            Minecraft mc = Minecraft.getInstance();
            if(mc.player == null) return;
            ItemStack helm = mc.player.getItemBySlot(EquipmentSlotType.HEAD);
            if(helm.getItem() instanceof SwatHelmItem)
                SwatHelmItem.setThermalActive(helm, active);
        });
        ctx.get().setPacketHandled(true);
    }
}
