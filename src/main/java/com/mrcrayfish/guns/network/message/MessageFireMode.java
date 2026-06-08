package com.mrcrayfish.guns.network.message;

import com.mrcrayfish.guns.common.network.ServerPlayHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageFireMode implements IMessage
{
    private int mode;

    public MessageFireMode() {}

    public MessageFireMode(int mode)
    {
        this.mode = mode;
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeVarInt(mode);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        mode = buffer.readVarInt();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                ServerPlayHandler.handleFireMode(this, player);
            }
        });
        supplier.get().setPacketHandled(true);
    }

    public int getMode()
    {
        return mode;
    }
}
