package com.mrcrayfish.guns.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.guns.capability.VestCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VestLayer
    extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>
{
    public VestLayer(PlayerRenderer renderer)
    {
        super(renderer);
    }

    @Override
    public void render(MatrixStack ms, IRenderTypeBuffer buffer, int light,
                       AbstractClientPlayerEntity player,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch)
    {
        player.getCapability(VestCapability.VEST_HANDLER).ifPresent(handler ->
        {
            ItemStack vest = handler.getVest();
            if(vest.isEmpty()) return;

            ms.pushPose();
            this.getParentModel().body.translateAndRotate(ms);
            ms.translate(0.0D, -0.2375D, -0.0625D);
            ms.scale(0.625F, -0.625F, -0.625F);
            Minecraft.getInstance().getItemRenderer().renderStatic(
                vest,
                ItemCameraTransforms.TransformType.HEAD,
                light,
                OverlayTexture.NO_OVERLAY,
                ms,
                buffer
            );
            ms.popPose();
        });
    }
}
