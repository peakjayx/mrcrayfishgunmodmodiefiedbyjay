package com.mrcrayfish.guns.mixin.client;

import com.mrcrayfish.guns.api.IHelm;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin
{
    @Inject(method = "setModelProperties", at = @At("RETURN"))
    private void onSetModelProperties(AbstractClientPlayerEntity player, CallbackInfo ci)
    {
        ItemStack head = player.getItemBySlot(EquipmentSlotType.HEAD);
        if(!(head.getItem() instanceof IHelm)) return;
        PlayerRenderer self = (PlayerRenderer)(Object)this;
        ((PlayerModel<?>) self.getModel()).hat.visible = false;
    }
}
