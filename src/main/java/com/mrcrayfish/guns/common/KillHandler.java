package com.mrcrayfish.guns.common;

import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.api.CgmGunApi;
import com.mrcrayfish.guns.api.EvidenceData;
import com.mrcrayfish.guns.block.EvidenceBlock;
import com.mrcrayfish.guns.entity.DamageSourceProjectile;
import com.mrcrayfish.guns.event.GunKillEvent;
import com.mrcrayfish.guns.init.ModBlocks;
import com.mrcrayfish.guns.init.ModSounds;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.tileentity.EvidenceTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class KillHandler
{
    public static volatile boolean debugMode = false;

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        Entity victim = event.getEntityLiving();
        if(victim.level.isClientSide()) return;

        DamageSource source = event.getSource();
        Entity killer = source.getEntity();

        if(source instanceof DamageSourceProjectile)
            placeEvidence(victim, (DamageSourceProjectile) source);

        if(debugMode)
        {
            GunMod.LOGGER.info("[CGM-DEBUG] === LivingDeathEvent ===");
            GunMod.LOGGER.info("[CGM-DEBUG]   victim      : {} ({})",
                victim.getName().getString(), victim.getClass().getName());
            GunMod.LOGGER.info("[CGM-DEBUG]   source.msgId: {}", source.getMsgId());
            GunMod.LOGGER.info("[CGM-DEBUG]   source.class: {}", source.getClass().getName());
            GunMod.LOGGER.info("[CGM-DEBUG]   isProjectile: {}", source.isProjectile());
            if(killer != null)
            {
                GunMod.LOGGER.info("[CGM-DEBUG]   killer      : {} ({})",
                    killer.getName().getString(), killer.getClass().getName());
                GunMod.LOGGER.info("[CGM-DEBUG]   killer.isPlayer: {}",
                    killer instanceof PlayerEntity);
            }
            else
                GunMod.LOGGER.info("[CGM-DEBUG]   killer      : null");
            GunMod.LOGGER.info("[CGM-DEBUG]   victim.isPlayer: {}", victim instanceof PlayerEntity);
            GunMod.LOGGER.info("[CGM-DEBUG] =====================");
        }

        if(!(victim instanceof PlayerEntity) || !(killer instanceof PlayerEntity)) return;

        MinecraftServer server = victim.getServer();
        if(server == null) return;

        BlockPos pos = victim.blockPosition();
        String date = LocalDateTime.now().format(DATE_FORMAT);
        String msg = String.format(
            "[CGM] \"%s\" killed \"%s\" at \"%d, %d, %d\" at \"%s\"",
            killer.getName().getString(), victim.getName().getString(),
            pos.getX(), pos.getY(), pos.getZ(), date);

        if(debugMode) GunMod.LOGGER.info("[CGM-DEBUG] Kill recorded: {}", msg);

        StringTextComponent component =
            new StringTextComponent(TextFormatting.GOLD + msg);
        for(ServerPlayerEntity op : server.getPlayerList().getPlayers())
            if(op.hasPermissions(2))
                op.sendMessage(component, op.getUUID());

        triggerKillEffect((ServerPlayerEntity) killer, victim);
    }

    private void triggerKillEffect(ServerPlayerEntity killer, Entity victim)
    {
        ServerWorld world = (ServerWorld) victim.level;
        double x = victim.getX();
        double y = victim.getY() + 1.0;
        double z = victim.getZ();
        BlockPos pos = victim.blockPosition();
        UUID uuid = killer.getUUID();

        boolean hasEffect =
            KillEffectListener.aceEffectPlayers.contains(uuid)
            || KillEffectListener.marioEffectPlayers.contains(uuid)
            || KillEffectListener.trashEffectPlayers.contains(uuid)
            || KillEffectListener.mexicoEffectPlayers.contains(uuid)
            || KillEffectListener.rocketEffectPlayers.contains(uuid)
            || KillEffectListener.creeperEffectPlayers.contains(uuid)
            || KillEffectListener.explosionEffectPlayers.contains(uuid)
            || KillEffectListener.totemEffectPlayers.contains(uuid)
            || KillEffectListener.waterEffectPlayers.contains(uuid);

        if(!hasEffect) return;

        world.playSound(null, pos,
            ModSounds.KILL_EFFECT_KILL.get(), SoundCategory.PLAYERS, 1.25F, 1.0F);

        if(KillEffectListener.aceEffectPlayers.contains(uuid))
            world.playSound(null, pos, ModSounds.KILL_EFFECT_ACE.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
        else if(KillEffectListener.marioEffectPlayers.contains(uuid))
            world.playSound(null, pos, ModSounds.KILL_EFFECT_MARIO.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
        else if(KillEffectListener.trashEffectPlayers.contains(uuid))
            world.playSound(null, pos, ModSounds.KILL_EFFECT_TRASH.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
        else if(KillEffectListener.mexicoEffectPlayers.contains(uuid))
            world.playSound(null, pos, ModSounds.KILL_EFFECT_MEXICO.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
        else if(KillEffectListener.rocketEffectPlayers.contains(uuid))
            world.sendParticles(ParticleTypes.FIREWORK, x, y, z, 80, 0.4, 0.4, 0.4, 0.35);
        else if(KillEffectListener.creeperEffectPlayers.contains(uuid))
            world.sendParticles(ParticleTypes.POOF, x, y, z, 60, 0.5, 0.5, 0.5, 0.1);
        else if(KillEffectListener.explosionEffectPlayers.contains(uuid))
            world.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 3, 0.5, 0.5, 0.5, 0.0);
        else if(KillEffectListener.totemEffectPlayers.contains(uuid))
        {
            world.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, x, y, z, 100, 0.5, 1.0, 0.5, 0.5);
            world.playSound(null, pos, SoundEvents.TOTEM_USE, SoundCategory.PLAYERS, 1.25F, 1.0F);
        }
        else if(KillEffectListener.waterEffectPlayers.contains(uuid))
            world.sendParticles(ParticleTypes.SPLASH, x, y, z, 100, 0.5, 0.5, 0.5, 0.2);
    }

    /** nur bei Spieler-Tod platzieren, Hülsen stapeln */
    private void placeEvidence(Entity victim, DamageSourceProjectile dsp)
    {
        if(!(victim instanceof PlayerEntity)) return;

        ItemStack weapon = dsp.getWeapon();
        if(!(weapon.getItem() instanceof GunItem)) return;

        GunItem gunItem = (GunItem) weapon.getItem();
        String gunId = CgmGunApi.getGunId(weapon);
        ResourceLocation gunType = weapon.getItem().getRegistryName();
        ResourceLocation ammoType =
            gunItem.getModifiedGun(weapon).getProjectile().getItem();

        Entity shooter = dsp.getEntity();
        UUID shooterUuid = shooter != null ? shooter.getUUID() : null;
        String shooterName = shooter != null
            ? shooter.getName().getString() : "unknown";

        EvidenceData data = new EvidenceData(
            gunId, gunType, ammoType,
            shooterUuid, shooterName,
            victim.getUUID(), victim.getName().getString(),
            System.currentTimeMillis()
        );

        ServerWorld world = (ServerWorld) victim.level;
        BlockPos pos = victim.blockPosition();

        // stack auf existierendem Block, sonst neuen platzieren
        TileEntity te = world.getBlockEntity(pos);
        if(!(te instanceof EvidenceTileEntity))
        {
            if(!world.getBlockState(pos).getMaterial().isReplaceable())
                pos = pos.above();
            te = world.getBlockEntity(pos);
        }

        if(te instanceof EvidenceTileEntity)
        {
            ((EvidenceTileEntity) te).addEntry(data);
        }
        else
        {
            FluidState fluid = world.getFluidState(pos);
            BlockState bs = ModBlocks.EVIDENCE.get().defaultBlockState()
                .setValue(EvidenceBlock.WATERLOGGED, fluid.getType() == Fluids.WATER);
            world.setBlockAndUpdate(pos, bs);
            TileEntity newTe = world.getBlockEntity(pos);
            if(newTe instanceof EvidenceTileEntity)
                ((EvidenceTileEntity) newTe).addEntry(data);
        }

        MinecraftForge.EVENT_BUS.post(
            new GunKillEvent(world, pos, victim, shooter, weapon, data));
    }
}
