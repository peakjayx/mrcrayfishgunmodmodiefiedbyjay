package com.mrcrayfish.guns.inventory;

import com.mrcrayfish.guns.api.IProtectiveVest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class VestSlot extends Slot
{
    public VestSlot(IInventory inv, int index, int x, int y)
    {
        super(inv, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return stack.getItem() instanceof IProtectiveVest;
    }

    @Override
    public int getMaxStackSize()
    {
        return 1;
    }
}
