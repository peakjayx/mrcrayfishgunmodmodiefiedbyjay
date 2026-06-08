package com.mrcrayfish.guns.mixin.common;

import com.mrcrayfish.guns.capability.VestCapability;
import com.mrcrayfish.guns.inventory.VestInventory;
import com.mrcrayfish.guns.inventory.VestSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerContainer.class)
public abstract class PlayerContainerMixin
{
    @Inject(
        method = "<init>(Lnet/minecraft/entity/player/PlayerInventory;ZLnet/minecraft/entity/player/PlayerEntity;)V",
        at = @At("TAIL")
    )
    private void addVestSlot(PlayerInventory playerInv, boolean isLocalWorld, PlayerEntity player, CallbackInfo ci)
    {
        player.getCapability(VestCapability.VEST_HANDLER).ifPresent(handler ->
            ((ContainerInvoker) (Object) this).callAddSlot(
                new VestSlot(new VestInventory(handler, player), 0, 77, 44)
            )
        );
    }
}
