package com.mrcrayfish.guns.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ThermalModeManager
{
    private static final Set<UUID> allowed = new HashSet<>();

    public static boolean isAllowed(UUID id)   { return allowed.contains(id); }
    public static void allow(UUID id)          { allowed.add(id); }
    public static void disallow(UUID id)       { allowed.remove(id); }
    public static Set<UUID> getAllowed()        { return Collections.unmodifiableSet(allowed); }
}
