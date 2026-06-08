package com.mrcrayfish.guns.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public interface IHelm
{
    default boolean isNvActive(ItemStack stack)
    {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getBoolean("nv");
    }

    default boolean isVisierDown(ItemStack stack)
    {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getBoolean("visier");
    }

    default boolean isThermalActive(ItemStack stack)
    {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getBoolean("thermal");
    }

    default float getHelmState(ItemStack stack)
    {
        boolean nv      = isNvActive(stack);
        boolean visier  = isVisierDown(stack);
        if(nv && visier) return 3.0f;
        if(visier)       return 2.0f;
        if(nv)           return 1.0f;
        return 0.0f;
    }

    default void setNvActive(ItemStack stack, boolean val)
    {
        stack.getOrCreateTag().putBoolean("nv", val);
    }

    default void setVisierDown(ItemStack stack, boolean val)
    {
        stack.getOrCreateTag().putBoolean("visier", val);
    }

    default void setThermalActive(ItemStack stack, boolean val)
    {
        stack.getOrCreateTag().putBoolean("thermal", val);
    }
}
