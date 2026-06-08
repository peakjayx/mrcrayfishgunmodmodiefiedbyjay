package com.mrcrayfish.guns.capability;

import net.minecraft.item.ItemStack;

public interface IVestHandler
{
    ItemStack getVest();
    void setVest(ItemStack stack);
}
