package com.mrcrayfish.guns.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

/**
 * Author: MrCrayfish
 */
public class KeyBinds
{
    public static final KeyBinding KEY_RELOAD = new KeyBinding("key.cgm.reload", GLFW.GLFW_KEY_R, "key.categories.cgm");
    public static final KeyBinding KEY_UNLOAD = new KeyBinding("key.cgm.unload", GLFW.GLFW_KEY_U, "key.categories.cgm");
    public static final KeyBinding KEY_ATTACHMENTS = new KeyBinding("key.cgm.attachments", GLFW.GLFW_KEY_Z, "key.categories.cgm");
    public static final KeyBinding KEY_FIRE_MODE = new KeyBinding("key.cgm.fire_mode",  GLFW.GLFW_KEY_V,         "key.categories.cgm");
    public static final KeyBinding KEY_NV          = new KeyBinding("key.cgm.nv",          GLFW.GLFW_KEY_N,         "key.categories.cgm");
    public static final KeyBinding KEY_VISIER      = new KeyBinding("key.cgm.visier",      GLFW.GLFW_KEY_SEMICOLON, "key.categories.cgm");
    public static final KeyBinding KEY_FLASHLIGHT  = new KeyBinding("key.cgm.flashlight",  GLFW.GLFW_KEY_L,         "key.categories.cgm");
    public static final KeyBinding KEY_LASER       = new KeyBinding("key.cgm.laser",       GLFW.GLFW_KEY_K,         "key.categories.cgm");
    public static final KeyBinding KEY_THERMAL     = new KeyBinding("key.cgm.thermal",     GLFW.GLFW_KEY_T,         "key.categories.cgm");

    public static void register()
    {
        ClientRegistry.registerKeyBinding(KEY_RELOAD);
        ClientRegistry.registerKeyBinding(KEY_UNLOAD);
        ClientRegistry.registerKeyBinding(KEY_ATTACHMENTS);
        ClientRegistry.registerKeyBinding(KEY_FIRE_MODE);
        ClientRegistry.registerKeyBinding(KEY_NV);
        ClientRegistry.registerKeyBinding(KEY_VISIER);
        ClientRegistry.registerKeyBinding(KEY_FLASHLIGHT);
        ClientRegistry.registerKeyBinding(KEY_LASER);
        ClientRegistry.registerKeyBinding(KEY_THERMAL);
    }
}
