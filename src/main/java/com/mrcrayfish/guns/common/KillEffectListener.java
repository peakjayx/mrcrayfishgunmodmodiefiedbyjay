package com.mrcrayfish.guns.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class KillEffectListener
{
    public static final Set<UUID> aceEffectPlayers      = Collections.synchronizedSet(new HashSet<>());
    public static final Set<UUID> marioEffectPlayers    = Collections.synchronizedSet(new HashSet<>());
    public static final Set<UUID> trashEffectPlayers    = Collections.synchronizedSet(new HashSet<>());
    public static final Set<UUID> rocketEffectPlayers   = Collections.synchronizedSet(new HashSet<>());
    public static final Set<UUID> creeperEffectPlayers  = Collections.synchronizedSet(new HashSet<>());
    public static final Set<UUID> explosionEffectPlayers= Collections.synchronizedSet(new HashSet<>());
    public static final Set<UUID> totemEffectPlayers    = Collections.synchronizedSet(new HashSet<>());
    public static final Set<UUID> mexicoEffectPlayers   = Collections.synchronizedSet(new HashSet<>());
    public static final Set<UUID> waterEffectPlayers    = Collections.synchronizedSet(new HashSet<>());
}
