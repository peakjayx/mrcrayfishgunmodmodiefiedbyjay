package com.mrcrayfish.guns.item;

import com.mrcrayfish.guns.api.ICgmShield;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CgmShieldItem extends Item implements ICgmShield
{
    public CgmShieldItem(Properties properties)
    {
        super(properties.durability(120));
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack)
    {
        return UseAction.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        player.startUsingItem(hand);
        return ActionResult.consume(player.getItemInHand(hand));
    }

    @Override
    public boolean isFrontalHit(LivingEntity blocker, double atkX, double atkZ)
    {
        double yaw = Math.toRadians(blocker.yRot);
        double lookX = -Math.sin(yaw);
        double lookZ = Math.cos(yaw);
        double dx = blocker.getX() - atkX;
        double dz = blocker.getZ() - atkZ;
        double len = Math.sqrt(dx * dx + dz * dz);
        if(len < 0.001) return true;
        return (lookX * (dx / len) + lookZ * (dz / len)) < 0;
    }
}
