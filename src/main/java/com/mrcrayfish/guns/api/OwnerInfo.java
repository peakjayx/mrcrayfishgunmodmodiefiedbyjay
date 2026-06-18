package com.mrcrayfish.guns.api;

import java.util.UUID;

/** immutable owner record */
public final class OwnerInfo
{
    public final UUID   uuid;
    public final String name;
    public final long   firstSeenEpoch;

    public OwnerInfo(UUID uuid, String name, long firstSeenEpoch)
    {
        this.uuid           = uuid;
        this.name           = name;
        this.firstSeenEpoch = firstSeenEpoch;
    }
}
