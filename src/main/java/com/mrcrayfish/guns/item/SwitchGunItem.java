package com.mrcrayfish.guns.item;

import com.mrcrayfish.guns.common.Gun;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

public class SwitchGunItem extends GunItem
{
    public static final int SEMI = 0;
    public static final int BURST = 1;
    public static final int AUTO = 2;

    public SwitchGunItem(Item.Properties properties)
    {
        super(properties);
    }

    @Override
    public Gun getModifiedGun(ItemStack stack)
    {
        Gun base = super.getModifiedGun(stack);
        if(getFireMode(stack) == AUTO)
        {
            CompoundNBT gunTag = base.serializeNBT();
            gunTag.getCompound("General").putBoolean("Auto", true);
            Gun autoGun = new Gun();
            autoGun.deserializeNBT(gunTag);
            return autoGun;
        }
        return base;
    }

    public static int getFireMode(ItemStack stack)
    {
        CompoundNBT tag = stack.getTag();
        if(tag != null && tag.contains("FireMode", Constants.NBT.TAG_ANY_NUMERIC))
            return tag.getInt("FireMode");
        return SEMI;
    }

    public static void setFireMode(ItemStack stack, int mode)
    {
        stack.getOrCreateTag().putInt("FireMode", mode);
    }

    public static String getModeName(int mode)
    {
        switch(mode)
        {
            case BURST: return "BURST";
            case AUTO:  return "AUTO";
            default:    return "SEMI";
        }
    }
}
