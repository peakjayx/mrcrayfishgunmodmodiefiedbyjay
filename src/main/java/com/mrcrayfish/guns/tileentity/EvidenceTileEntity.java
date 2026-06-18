package com.mrcrayfish.guns.tileentity;

import com.mrcrayfish.guns.api.EvidenceData;
import com.mrcrayfish.guns.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;

import javax.annotation.Nullable;

/** Spurendaten am Tatort */
public class EvidenceTileEntity extends SyncedTileEntity
    implements ITickableTileEntity
{
    private static final long EXPIRE_MS = 6L * 60 * 60 * 1000; // 6h

    @Nullable
    private EvidenceData data;
    private int tickCounter = 0;

    public EvidenceTileEntity()
    {
        super(ModTileEntities.EVIDENCE.get());
    }

    @Nullable
    public EvidenceData getData() { return data; }

    public void setData(EvidenceData data)
    {
        this.data = data;
        syncToClient();
    }

    @Override
    public void tick()
    {
        if(level == null || level.isClientSide()) return;
        if(++tickCounter < 1200) return; // alle 60s prüfen
        tickCounter = 0;

        if(data == null) return;
        if(System.currentTimeMillis() - data.epoch >= EXPIRE_MS)
            level.removeBlock(worldPosition, false);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag)
    {
        if(data != null)
        {
            CompoundNBT sub = new CompoundNBT();
            data.writeNbt(sub);
            tag.put("evidence", sub);
        }
        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag)
    {
        super.load(state, tag);
        if(tag.contains("evidence"))
            data = EvidenceData.readNbt(tag.getCompound("evidence"));
        else
            data = null;
    }
}
