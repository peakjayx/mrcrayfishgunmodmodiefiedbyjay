package com.mrcrayfish.guns.common;

import com.mrcrayfish.guns.api.OwnerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/** server-persistentes Waffen-Register */
public class GunRegistry extends WorldSavedData
{
    public static final String ID = "cgm_gun_registry";

    private final Map<String, OwnerInfo> owners = new HashMap<>();
    private final Random rand = new Random();

    public GunRegistry() { super(ID); }

    /** holt/erstellt Register aus Overworld-Storage */
    public static GunRegistry get(ServerWorld world)
    {
        return world.getServer()
            .overworld()
            .getDataStorage()
            .computeIfAbsent(GunRegistry::new, ID);
    }

    /** neue zufällige 12-stellige ID */
    public synchronized String assignNewId()
    {
        String id;
        do {
            long num = 100000000000L
                + (long)(rand.nextDouble() * 900000000000L);
            id = Long.toString(num);
        } while(owners.containsKey(id));
        return id;
    }

    /** bindet ersten Besitzer; nachträgliche Aufrufe ignoriert */
    public synchronized void bindOwner(String id, PlayerEntity player)
    {
        if(owners.containsKey(id)) return;
        owners.put(id, new OwnerInfo(
            player.getUUID(),
            player.getName().getString(),
            System.currentTimeMillis()
        ));
        setDirty();
    }

    @Nullable
    public synchronized OwnerInfo getOwner(String id)
    {
        return owners.get(id);
    }

    @Override
    public void load(CompoundNBT tag)
    {
        owners.clear();
        ListNBT list = tag.getList("entries", Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < list.size(); i++)
        {
            CompoundNBT e = list.getCompound(i);
            String   id    = e.getString("id");
            OwnerInfo info = new OwnerInfo(
                e.getUUID("uuid"),
                e.getString("name"),
                e.getLong("epoch")
            );
            owners.put(id, info);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag)
    {
        ListNBT list = new ListNBT();
        for(Map.Entry<String, OwnerInfo> entry : owners.entrySet())
        {
            CompoundNBT e = new CompoundNBT();
            e.putString("id",   entry.getKey());
            e.putUUID("uuid",   entry.getValue().uuid);
            e.putString("name", entry.getValue().name);
            e.putLong("epoch",  entry.getValue().firstSeenEpoch);
            list.add(e);
        }
        tag.put("entries", list);
        return tag;
    }
}
