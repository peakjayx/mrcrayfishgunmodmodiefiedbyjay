package com.mrcrayfish.guns.network.message;

import com.mrcrayfish.guns.common.network.ServerPlayHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageToggleHelm implements IMessage
{
    public static final int NV     = 0;
    public static final int VISIER = 1;

    private int type;

    public MessageToggleHelm() {}

    public MessageToggleHelm(int type)
    {
        this.type = type;
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeVarInt(type);
    }

    @Override
    public void decode(PacketBuffer buf)
    {
        type = buf.readVarInt();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = ctx.get().getSender();
            if(player != null) ServerPlayHandler.handleToggleHelm(this, player);
        });
        ctx.get().setPacketHandled(true);
    }

    public int getType() { return type; }
}
