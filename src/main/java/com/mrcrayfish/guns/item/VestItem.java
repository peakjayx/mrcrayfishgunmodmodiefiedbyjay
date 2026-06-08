package com.mrcrayfish.guns.item;

import com.mrcrayfish.guns.api.IProtectiveVest;
import net.minecraft.item.Item;

public class VestItem extends Item implements IProtectiveVest
{
    private final float protection;

    public VestItem(Properties props, float protection)
    {
        super(props.stacksTo(1));
        this.protection = protection;
    }

    @Override
    public float getProtection()
    {
        return protection;
    }
}
