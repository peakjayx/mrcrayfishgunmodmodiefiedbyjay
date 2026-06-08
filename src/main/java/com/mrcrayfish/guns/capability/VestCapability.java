package com.mrcrayfish.guns.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class VestCapability
{
    @CapabilityInject(IVestHandler.class)
    public static Capability<IVestHandler> VEST_HANDLER = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IVestHandler.class, new Capability.IStorage<IVestHandler>()
        {
            @Override
            public @Nullable INBT writeNBT(Capability<IVestHandler> cap, IVestHandler inst, Direction side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<IVestHandler> cap, IVestHandler inst, Direction side, INBT nbt) {}
        }, VestHandler::new);
    }
}
