package com.mrcrayfish.guns.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SwatHelmItem extends Item
{
    public SwatHelmItem(Properties props)
    {
        super(props);
    }

    @Nullable
    @Override
    public EquipmentSlotType getEquipmentSlot(ItemStack stack)
    {
        return EquipmentSlotType.HEAD;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if(player.getItemBySlot(EquipmentSlotType.HEAD).isEmpty())
        {
            if(!world.isClientSide)
            {
                player.setItemSlot(EquipmentSlotType.HEAD, stack.copy());
                stack.shrink(1);
            }
            return ActionResult.sidedSuccess(stack, world.isClientSide);
        }
        return ActionResult.pass(stack);
    }

    public static boolean isNvActive(ItemStack stack)
    {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getBoolean("nv");
    }

    public static boolean isVisierDown(ItemStack stack)
    {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getBoolean("visier");
    }

    public static void setNvActive(ItemStack stack, boolean val)
    {
        stack.getOrCreateTag().putBoolean("nv", val);
    }

    public static void setVisierDown(ItemStack stack, boolean val)
    {
        stack.getOrCreateTag().putBoolean("visier", val);
    }

    public static boolean isThermalActive(ItemStack stack)
    {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getBoolean("thermal");
    }

    public static void setThermalActive(ItemStack stack, boolean val)
    {
        stack.getOrCreateTag().putBoolean("thermal", val);
    }

    public static float getHelmState(ItemStack stack)
    {
        boolean nv = isNvActive(stack);
        boolean visier = isVisierDown(stack);
        if(nv && visier) return 3.0f;
        if(visier) return 2.0f;
        if(nv) return 1.0f;
        return 0.0f;
    }
}
