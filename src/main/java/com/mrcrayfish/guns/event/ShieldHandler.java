package com.mrcrayfish.guns.event;

import com.mrcrayfish.guns.api.ICgmShield;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ShieldHandler
{
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        if(entity.level.isClientSide) return;
        if(!(entity instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) entity;
        if(!player.isUsingItem()) return;

        ItemStack useItem = player.getUseItem();
        if(!(useItem.getItem() instanceof ICgmShield)) return;
        ICgmShield shield = (ICgmShield) useItem.getItem();

        DamageSource src = event.getSource();
        if(src.isBypassArmor() || src.isBypassMagic() || src.isExplosion()) return;

        boolean frontal;
        if(src.getDirectEntity() != null)
        {
            frontal = shield.isFrontalHit(player,
                src.getDirectEntity().getX(),
                src.getDirectEntity().getZ());
        }
        else if(src.getSourcePosition() != null)
        {
            frontal = shield.isFrontalHit(player,
                src.getSourcePosition().x,
                src.getSourcePosition().z);
        }
        else
        {
            return;
        }

        if(!frontal) return;

        event.setCanceled(true);
        int durabilityDamage = MathHelper.clamp((int) event.getAmount(), 1, 120);
        useItem.hurtAndBreak(durabilityDamage, player,
            p -> p.broadcastBreakEvent(p.getUsedItemHand()));
        player.level.playSound(null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.SHIELD_BLOCK, SoundCategory.PLAYERS,
            1.0F, 0.8F + player.level.random.nextFloat() * 0.4F);
    }
}
