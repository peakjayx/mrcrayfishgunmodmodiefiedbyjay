package com.mrcrayfish.guns.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin
{
    @Inject(method = "renderBg", at = @At("TAIL"))
    private void renderVestSlotBg(MatrixStack ms, float partial, int mx, int my, CallbackInfo ci)
    {
        ContainerScreenAccessor self = (ContainerScreenAccessor) (Object) this;
        int x = self.getLeftPos() + 77;
        int y = self.getTopPos() + 44;
        AbstractGui.fill(ms, x - 1, y - 1, x + 17, y + 17, 0xFF373737);
        AbstractGui.fill(ms, x, y, x + 16, y + 16, 0xFF8B8B8B);
    }
}
