package com.mrcrayfish.guns.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VestCapabilityProvider implements ICapabilitySerializable<CompoundNBT>
{
    private final VestHandler handler = new VestHandler();
    private final LazyOptional<IVestHandler> optional = LazyOptional.of(() -> handler);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        return VestCapability.VEST_HANDLER.orEmpty(cap, optional);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        return handler.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        handler.deserializeNBT(nbt);
    }
}
