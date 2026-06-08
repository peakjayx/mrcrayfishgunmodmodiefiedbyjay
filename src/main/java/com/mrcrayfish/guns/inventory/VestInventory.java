package com.mrcrayfish.guns.inventory;

import com.mrcrayfish.guns.capability.IVestHandler;
import com.mrcrayfish.guns.event.VestEventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class VestInventory implements IInventory
{
    private final IVestHandler handler;
    private final PlayerEntity player;

    public VestInventory(IVestHandler handler, PlayerEntity player)
    {
        this.handler = handler;
        this.player = player;
    }

    @Override
    public int getContainerSize()
    {
        return 1;
    }

    @Override
    public boolean isEmpty()
    {
        return handler.getVest().isEmpty();
    }

    @Override
    public ItemStack getItem(int index)
    {
        return index == 0 ? handler.getVest() : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        if(index != 0) return ItemStack.EMPTY;
        ItemStack vest = handler.getVest().copy();
        handler.setVest(ItemStack.EMPTY);
        return vest;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        return removeItem(index, 1);
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if(index == 0)
            handler.setVest(stack);
    }

    @Override
    public void setChanged()
    {
        if(!player.level.isClientSide() && player instanceof ServerPlayerEntity)
            VestEventHandler.syncVest((ServerPlayerEntity) player);
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        return true;
    }

    @Override
    public void clearContent()
    {
        handler.setVest(ItemStack.EMPTY);
    }
}
