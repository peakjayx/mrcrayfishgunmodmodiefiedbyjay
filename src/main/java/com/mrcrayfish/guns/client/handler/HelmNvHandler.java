package com.mrcrayfish.guns.client.handler;

import com.mrcrayfish.guns.client.KeyBinds;
import com.mrcrayfish.guns.item.SwatHelmItem;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.MessageToggleHelm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class HelmNvHandler
{
    private static HelmNvHandler instance;

    private HelmNvHandler() {}

    public static HelmNvHandler get()
    {
        if(instance == null) instance = new HelmNvHandler();
        return instance;
    }

    private ItemStack getHelm()
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null) return ItemStack.EMPTY;
        return mc.player.getItemBySlot(EquipmentSlotType.HEAD);
    }

    private boolean hasHelm()
    {
        return getHelm().getItem() instanceof SwatHelmItem;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END) return;
        if(!hasHelm()) return;

        // keep client NBT in sync with server after respawn etc.
        ItemStack helm = getHelm();
        // nothing to do; just ensures hasHelm() is checked each tick
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null || mc.screen != null) return;
        if(!hasHelm()) return;

        if(KeyBinds.KEY_NV.consumeClick())
        {
            ItemStack helm = getHelm();
            SwatHelmItem.setNvActive(helm, !SwatHelmItem.isNvActive(helm));
            PacketHandler.getPlayChannel().sendToServer(new MessageToggleHelm(MessageToggleHelm.NV));
        }

        if(KeyBinds.KEY_VISIER.consumeClick())
        {
            ItemStack helm = getHelm();
            SwatHelmItem.setVisierDown(helm, !SwatHelmItem.isVisierDown(helm));
            PacketHandler.getPlayChannel().sendToServer(new MessageToggleHelm(MessageToggleHelm.VISIER));
        }
    }

    /** green NV screen overlay */
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event)
    {
        if(event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if(!hasHelm()) return;
        if(!SwatHelmItem.isNvActive(getHelm())) return;

        Minecraft mc = Minecraft.getInstance();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        AbstractGui.fill(event.getMatrixStack(), 0, 0, w, h, 0x33008800);
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
    }

    /** visier HUD: alt, speed, hp, dir */
    @SubscribeEvent
    public void onRenderVisierHud(RenderGameOverlayEvent.Post event)
    {
        if(event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if(!hasHelm()) return;
        if(!SwatHelmItem.isVisierDown(getHelm())) return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null) return;

        int alt = (int) mc.player.getY();

        Entity speedSource = mc.player.getVehicle() != null
            ? mc.player.getVehicle() : mc.player;
        Vector3d motion = speedSource.getDeltaMovement();
        double speed = Math.sqrt(motion.x * motion.x + motion.z * motion.z) * 20.0;

        float hearts = mc.player.getHealth() / 2.0f;
        String dir = getDirection(mc.player.yRot);

        int w = mc.getWindow().getGuiScaledWidth();
        int green = 0x00FF00;
        String distLine = getDistanceLine(mc);

        String[] lines = {
            "ALT: " + alt + "m",
            String.format("SPD: %.1f b/s", speed),
            String.format("HP:  %.1f", hearts),
            "DIR: " + dir
        };
        for(int i = 0; i < lines.length; i++)
        {
            mc.font.drawShadow(
                event.getMatrixStack(),
                lines[i],
                w - 4 - mc.font.width(lines[i]),
                5 + i * 10,
                green);
        }
        if(distLine != null)
        {
            mc.font.drawShadow(
                event.getMatrixStack(),
                distLine,
                w - 4 - mc.font.width(distLine),
                5 + 4 * 10,
                green);
        }
    }

    private String getDistanceLine(Minecraft mc)
    {
        if(mc.level == null || mc.player == null) return null;

        Vector3d eye  = mc.player.getEyePosition(1.0f);
        Vector3d look = mc.player.getLookAngle();
        double maxDist = 100.0;
        Vector3d far  = eye.add(look.scale(maxDist));

        // block raycast
        BlockRayTraceResult blockHit = mc.level.clip(new RayTraceContext(
            eye, far,
            RayTraceContext.BlockMode.OUTLINE,
            RayTraceContext.FluidMode.NONE,
            mc.player));
        double blockDist = blockHit.getType() != RayTraceResult.Type.MISS
            ? eye.distanceTo(blockHit.getLocation()) : maxDist;

        // entity raycast
        Entity closest = null;
        double closestDist = blockDist;
        AxisAlignedBB scanBox = new AxisAlignedBB(eye, far).inflate(0.5);
        for(Entity e : mc.level.getEntities(mc.player, scanBox))
        {
            java.util.Optional<Vector3d> hit = e.getBoundingBox().clip(eye, far);
            if(hit.isPresent())
            {
                double d = eye.distanceTo(hit.get());
                if(d < closestDist) { closestDist = d; closest = e; }
            }
        }

        if(closest != null)
            return String.format("TARGET: %s (%.1fm)", closest.getName().getString(), closestDist);
        if(blockHit.getType() != RayTraceResult.Type.MISS)
            return String.format("DIST: %.1fm", blockDist);
        return null;
    }

    private String getDirection(float yaw)
    {
        float y = ((yaw % 360) + 360) % 360;
        if(y < 22.5 || y >= 337.5) return "S";
        if(y < 67.5)  return "SW";
        if(y < 112.5) return "W";
        if(y < 157.5) return "NW";
        if(y < 202.5) return "N";
        if(y < 247.5) return "NO";
        if(y < 292.5) return "O";
        return "SO";
    }

    /** green bounding-box outline around visible players when NV active */
    @SubscribeEvent
    public void onRenderEntity(RenderLivingEvent.Post<?, ?> event)
    {
        if(!hasHelm()) return;
        if(!SwatHelmItem.isNvActive(getHelm())) return;

        LivingEntity entity = event.getEntity();
        if(!(entity instanceof net.minecraft.client.entity.player.AbstractClientPlayerEntity)) return;
        Minecraft mc = Minecraft.getInstance();
        if(entity == mc.player) return;

        AxisAlignedBB bb = entity.getBoundingBox().move(
            -entity.getX(), -entity.getY(), -entity.getZ()
        );
        WorldRenderer.renderLineBox(
            event.getMatrixStack(),
            event.getBuffers().getBuffer(RenderType.lines()),
            bb,
            0.0F, 1.0F, 0.2F, 0.8F
        );
    }
}
