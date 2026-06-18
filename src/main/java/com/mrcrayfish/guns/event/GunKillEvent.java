package com.mrcrayfish.guns.event;

import com.mrcrayfish.guns.api.EvidenceData;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/** Waffen-Kill mit Spur */
public class GunKillEvent extends Event
{
    private final ServerWorld  world;
    private final BlockPos     evidencePos;
    private final Entity       victim;
    @Nullable
    private final Entity       shooter;
    private final ItemStack    weapon;
    private final EvidenceData data;

    public GunKillEvent(
        ServerWorld world,
        BlockPos evidencePos,
        Entity victim,
        @Nullable Entity shooter,
        ItemStack weapon,
        EvidenceData data)
    {
        this.world       = world;
        this.evidencePos = evidencePos;
        this.victim      = victim;
        this.shooter     = shooter;
        this.weapon      = weapon;
        this.data        = data;
    }

    public ServerWorld  getWorld()       { return world; }
    public BlockPos     getEvidencePos() { return evidencePos; }
    public Entity       getVictim()      { return victim; }
    @Nullable
    public Entity       getShooter()     { return shooter; }
    public ItemStack    getWeapon()      { return weapon; }
    public EvidenceData getData()        { return data; }
}
