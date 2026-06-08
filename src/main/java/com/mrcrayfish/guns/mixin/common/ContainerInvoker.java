package com.mrcrayfish.guns.mixin.common;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Container.class)
public interface ContainerInvoker
{
    @Invoker("addSlot")
    Slot callAddSlot(Slot slot);
}
