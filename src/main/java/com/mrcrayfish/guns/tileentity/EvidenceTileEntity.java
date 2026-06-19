package com.mrcrayfish.guns.tileentity;

import com.mrcrayfish.guns.api.EvidenceData;
import com.mrcrayfish.guns.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Spurendaten am Tatort */
public class EvidenceTileEntity extends SyncedTileEntity
    implements ITickableTileEntity
{
    private static final long EXPIRE_MS = 6L * 60 * 60 * 1000;

    private final List<EvidenceData> entries = new ArrayList<>();
    private int tickCounter = 0;

    public EvidenceTileEntity()
    {
        super(ModTileEntities.EVIDENCE.get());
    }

    public List<EvidenceData> getEntries()
    {
        return Collections.unmodifiableList(entries);
    }

    public void addEntry(EvidenceData data)
    {
        entries.add(data);
        setChanged();
        syncToClient();
    }

    /** erstes Entry entfernen; Block wird entfernt wenn Liste leer */
    @Nullable
    public EvidenceData pollEntry()
    {
        if(entries.isEmpty()) return null;
        EvidenceData d = entries.remove(0);
        setChanged();
        if(entries.isEmpty() && level != null)
        {
            level.removeBlock(worldPosition, false);
            return d;
        }
        syncToClient();
        return d;
    }

    @Override
    public void tick()
    {
        if(level == null || level.isClientSide()) return;
        if(++tickCounter < 1200) return;
        tickCounter = 0;

        long now = System.currentTimeMillis();
        boolean changed = entries.removeIf(e -> now - e.epoch >= EXPIRE_MS);
        if(entries.isEmpty())
            level.removeBlock(worldPosition, false);
        else if(changed)
        {
            setChanged();
            syncToClient();
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag)
    {
        ListNBT list = new ListNBT();
        for(EvidenceData e : entries)
            list.add(e.writeNbt(new CompoundNBT()));
        tag.put("entries", list);
        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag)
    {
        super.load(state, tag);
        entries.clear();
        ListNBT list = tag.getList("entries", Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < list.size(); i++)
            entries.add(EvidenceData.readNbt(list.getCompound(i)));
    }
}
