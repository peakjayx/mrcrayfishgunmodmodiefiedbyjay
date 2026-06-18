package com.mrcrayfish.guns.tileentity;

import com.mrcrayfish.guns.api.EvidenceData;
import com.mrcrayfish.guns.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

/** Spurendaten am Tatort */
public class EvidenceTileEntity extends SyncedTileEntity
{
    @Nullable
    private EvidenceData data;

    public EvidenceTileEntity()
    {
        super(ModTileEntities.EVIDENCE.get());
    }

    @Nullable
    public EvidenceData getData() { return data; }

    public void setData(EvidenceData data)
    {
        this.data = data;
        syncToClient(); // protected — ok aus Subklasse
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
