package com.mrcrayfish.guns.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class VestHandler implements IVestHandler, INBTSerializable<CompoundNBT>
{
    private ItemStack vest = ItemStack.EMPTY;

    @Override
    public ItemStack getVest()
    {
        return vest;
    }

    @Override
    public void setVest(ItemStack stack)
    {
        this.vest = stack == null ? ItemStack.EMPTY : stack;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT tag = new CompoundNBT();
        if(!vest.isEmpty())
            tag.put("Vest", vest.save(new CompoundNBT()));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT tag)
    {
        if(tag.contains("Vest"))
            vest = ItemStack.of(tag.getCompound("Vest"));
        else
            vest = ItemStack.EMPTY;
    }
}
