package com.mrcrayfish.guns.api;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.UUID;

/** forensic trace data */
public final class EvidenceData
{
    private static final String KEY_GUN_ID        = "gunId";
    private static final String KEY_GUN_TYPE      = "gunType";
    private static final String KEY_AMMO_TYPE     = "ammoType";
    private static final String KEY_SHOOTER_UUID  = "shooterUuid";
    private static final String KEY_SHOOTER_NAME  = "shooterName";
    private static final String KEY_VICTIM_UUID   = "victimUuid";
    private static final String KEY_VICTIM_NAME   = "victimName";
    private static final String KEY_EPOCH         = "epoch";

    @Nullable public final String       gunId;
    @Nullable public final ResourceLocation gunType;
    @Nullable public final ResourceLocation ammoType;
    @Nullable public final UUID         shooterUuid;
    public final String                 shooterName;
    public final UUID                   victimUuid;
    public final String                 victimName;
    public final long                   epoch;

    public EvidenceData(
        @Nullable String gunId,
        @Nullable ResourceLocation gunType,
        @Nullable ResourceLocation ammoType,
        @Nullable UUID shooterUuid,
        String shooterName,
        UUID victimUuid,
        String victimName,
        long epoch)
    {
        this.gunId       = gunId;
        this.gunType     = gunType;
        this.ammoType    = ammoType;
        this.shooterUuid = shooterUuid;
        this.shooterName = shooterName;
        this.victimUuid  = victimUuid;
        this.victimName  = victimName;
        this.epoch       = epoch;
    }

    public CompoundNBT writeNbt(CompoundNBT tag)
    {
        if(gunId != null)    tag.putString(KEY_GUN_ID, gunId);
        if(gunType != null)  tag.putString(KEY_GUN_TYPE, gunType.toString());
        if(ammoType != null) tag.putString(KEY_AMMO_TYPE, ammoType.toString());
        if(shooterUuid != null)
            tag.putUUID(KEY_SHOOTER_UUID, shooterUuid);
        tag.putString(KEY_SHOOTER_NAME, shooterName);
        tag.putUUID(KEY_VICTIM_UUID, victimUuid);
        tag.putString(KEY_VICTIM_NAME, victimName);
        tag.putLong(KEY_EPOCH, epoch);
        return tag;
    }

    public static EvidenceData readNbt(CompoundNBT tag)
    {
        String gunId = tag.contains(KEY_GUN_ID)
            ? tag.getString(KEY_GUN_ID) : null;
        ResourceLocation gunType = tag.contains(KEY_GUN_TYPE)
            ? new ResourceLocation(tag.getString(KEY_GUN_TYPE)) : null;
        ResourceLocation ammoType = tag.contains(KEY_AMMO_TYPE)
            ? new ResourceLocation(tag.getString(KEY_AMMO_TYPE)) : null;
        UUID shooterUuid = tag.hasUUID(KEY_SHOOTER_UUID)
            ? tag.getUUID(KEY_SHOOTER_UUID) : null;
        String shooterName = tag.getString(KEY_SHOOTER_NAME);
        UUID victimUuid   = tag.getUUID(KEY_VICTIM_UUID);
        String victimName = tag.getString(KEY_VICTIM_NAME);
        long epoch        = tag.getLong(KEY_EPOCH);
        return new EvidenceData(
            gunId, gunType, ammoType,
            shooterUuid, shooterName,
            victimUuid, victimName, epoch);
    }
}
