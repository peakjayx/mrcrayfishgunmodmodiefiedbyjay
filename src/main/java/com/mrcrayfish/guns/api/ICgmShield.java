package com.mrcrayfish.guns.api;

import net.minecraft.entity.LivingEntity;

public interface ICgmShield
{
    boolean isFrontalHit(LivingEntity blocker, double atkX, double atkZ);
}
