package com.mrcrayfish.guns.client;

import com.mrcrayfish.guns.client.handler.FlashlightHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// soft-dep ryoamiclights — via reflection, kein compile-time import nötig
@OnlyIn(Dist.CLIENT)
public class FlashlightLightIntegration
{
    public static void register()
    {
        try
        {
            Class<?> dhClass  = Class.forName("me.lambdaurora.lambdynlights.api.DynamicLightHandler");
            Class<?> dhsClass = Class.forName("me.lambdaurora.lambdynlights.api.DynamicLightHandlers");

            Object handler = Proxy.newProxyInstance(
                FlashlightLightIntegration.class.getClassLoader(),
                new Class<?>[]{ dhClass },
                (proxy, method, args) ->
                {
                    if (args != null && args.length > 0 && args[0] instanceof PlayerEntity)
                    {
                        PlayerEntity player = (PlayerEntity) args[0];
                        ItemStack held = player.getMainHandItem();
                        return FlashlightHandler.hasFlashlight(held)
                            && held.getOrCreateTag().getBoolean("FlashlightOn") ? 15 : 0;
                    }
                    return 0;
                }
            );

            Method reg = dhsClass.getMethod("registerDynamicLightHandler", EntityType.class, dhClass);
            reg.invoke(null, EntityType.PLAYER, handler);
        }
        catch (Exception ignored) {}
    }
}
