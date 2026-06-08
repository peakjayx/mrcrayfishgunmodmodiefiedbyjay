package com.mrcrayfish.guns.network.message;

import com.mrcrayfish.guns.capability.VestCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSyncVest implements IMessage
{
    private int playerId;
    private ItemStack vest;

    public MessageSyncVest() {}

    public MessageSyncVest(int playerId, ItemStack vest)
    {
        this.playerId = playerId;
        this.vest = vest.copy();
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeVarInt(playerId);
        buf.writeItem(vest);
    }

    @Override
    public void decode(PacketBuffer buf)
    {
        playerId = buf.readVarInt();
        vest = buf.readItem();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            if(Minecraft.getInstance().level == null) return;
            Entity entity = Minecraft.getInstance().level.getEntity(playerId);
            if(!(entity instanceof PlayerEntity)) return;
            ((PlayerEntity) entity).getCapability(VestCapability.VEST_HANDLER)
                .ifPresent(h -> h.setVest(vest.copy()));
        });
        ctx.get().setPacketHandled(true);
    }
}
