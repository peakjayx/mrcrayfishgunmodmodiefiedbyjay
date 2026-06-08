package com.mrcrayfish.guns.client.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.guns.client.KeyBinds;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.init.ModItems;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.item.attachment.IAttachment;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.MessageToggleLight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class LaserRenderer
{
    private static LaserRenderer instance;

    // hit distance per player UUID — read by GunRenderingHandler for Z-scaling
    private static final Map<UUID, Float> hitDistances = new HashMap<>();

    private LaserRenderer() {}

    public static LaserRenderer get()
    {
        if (instance == null) instance = new LaserRenderer();
        return instance;
    }

    /** Returns hit distance in blocks for the given player, or 300 if no data. */
    public static float getHitDist(UUID uuid)
    {
        return hitDistances.getOrDefault(uuid, 300.0F);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        if (!KeyBinds.KEY_LASER.consumeClick()) return;
        ItemStack held = mc.player.getMainHandItem();
        if (!hasLaser(held)) return;

        CompoundNBT tag = held.getOrCreateTag();
        tag.putBoolean("LaserOn", !tag.getBoolean("LaserOn"));
        PacketHandler.getPlayChannel().sendToServer(new MessageToggleLight(MessageToggleLight.LASER));
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        float pt = event.getPartialTicks();
        MatrixStack ms = event.getMatrixStack();
        Vector3d cam = mc.player.getEyePosition(pt);

        hitDistances.clear();

        for (PlayerEntity p : mc.level.players())
        {
            ItemStack held = p.getMainHandItem();
            if (!hasLaser(held) || !held.getOrCreateTag().getBoolean("LaserOn")) continue;

            HitResult result = computeHit(mc, p, pt);
            hitDistances.put(p.getUUID(), result.dist);
        }
    }

    private void renderBeam(MatrixStack ms, Vector3d cam, Vector3d start, Vector3d end)
    {
        Vector3d dir = end.subtract(start);
        if (dir.length() < 0.01) return;

        Vector3d midpoint = start.add(end).scale(0.5);
        Vector3d toCam = cam.subtract(midpoint);
        Vector3d side = dir.cross(toCam);
        double sLen = side.length();
        if (sLen < 1e-6) return;
        side = side.scale(0.015 / sLen);

        ms.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.enableDepthTest();

        float ox = (float)-cam.x, oy = (float)-cam.y, oz = (float)-cam.z;
        Matrix4f mat = ms.last().pose();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();

        float sx = (float)(start.x + ox), sy = (float)(start.y + oy), sz = (float)(start.z + oz);
        float ex = (float)(end.x + ox),   ey = (float)(end.y + oy),   ez = (float)(end.z + oz);
        float dx = (float)side.x,         dy = (float)side.y,         dz = (float)side.z;

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buf.vertex(mat, sx - dx, sy - dy, sz - dz).color(255, 0, 0, 38).endVertex();
        buf.vertex(mat, ex - dx, ey - dy, ez - dz).color(255, 0, 0, 38).endVertex();
        buf.vertex(mat, ex + dx, ey + dy, ez + dz).color(255, 0, 0, 38).endVertex();
        buf.vertex(mat, sx + dx, sy + dy, sz + dz).color(255, 0, 0, 38).endVertex();
        tess.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        ms.popPose();
    }

    private HitResult computeHit(Minecraft mc, PlayerEntity holder, float pt)
    {
        Vector3d eye  = holder.getEyePosition(pt);
        Vector3d look = holder.getLookAngle();
        Vector3d far  = eye.add(look.scale(300));

        // iterative block raycast — transparent blocks (glass etc.) werden übersprungen
        Vector3d start = eye;
        BlockRayTraceResult opaqueHit = null;
        for (int i = 0; i < 64; i++)
        {
            RayTraceContext ctx = new RayTraceContext(
                start, far, RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE, holder);
            BlockRayTraceResult hit = mc.level.clip(ctx);
            if (hit.getType() == RayTraceResult.Type.MISS) break;
            net.minecraft.block.BlockState state =
                mc.level.getBlockState(hit.getBlockPos());
            boolean transparent =
                net.minecraft.client.renderer.RenderTypeLookup.canRenderInLayer(
                    state, net.minecraft.client.renderer.RenderType.translucent());
            if (!transparent) { opaqueHit = hit; break; }
            start = hit.getLocation().add(look.scale(0.01));
            if (start.distanceTo(eye) >= 299) break;
        }

        boolean hitBlock = opaqueHit != null;
        double  blockDist = hitBlock ? opaqueHit.getLocation().distanceTo(eye) : 300;

        // entity check
        Vector3d entityHitPos = null;
        double   entityDist   = Double.MAX_VALUE;
        AxisAlignedBB box = new AxisAlignedBB(
            Math.min(eye.x, far.x) - 1, Math.min(eye.y, far.y) - 1, Math.min(eye.z, far.z) - 1,
            Math.max(eye.x, far.x) + 1, Math.max(eye.y, far.y) + 1, Math.max(eye.z, far.z) + 1);
        List<Entity> candidates = mc.level.getEntities(holder, box,
            e -> e instanceof LivingEntity && e != holder);
        for (Entity e : candidates)
        {
            Optional<Vector3d> hit = e.getBoundingBox().inflate(0.1).clip(eye, far);
            if (hit.isPresent())
            {
                double d = eye.distanceTo(hit.get());
                if (d < entityDist) { entityDist = d; entityHitPos = hit.get(); }
            }
        }

        if (entityHitPos != null && entityDist < blockDist)
            return new HitResult(true, (float) entityDist, entityHitPos);
        if (hitBlock)
            return new HitResult(true, (float) blockDist, opaqueHit.getLocation());
        return new HitResult(false, 300.0F, far);
    }

    private void renderDot(MatrixStack ms, Vector3d cam, Vector3d hitPos)
    {
        ms.pushPose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.enableDepthTest();

        double ox = -cam.x, oy = -cam.y, oz = -cam.z;
        Matrix4f mat = ms.last().pose();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();

        Vector3d toCam   = cam.subtract(hitPos).normalize();
        Vector3d worldUp = Math.abs(toCam.y) > 0.99 ? new Vector3d(1, 0, 0) : new Vector3d(0, 1, 0);
        Vector3d rv = toCam.cross(worldUp).normalize();
        Vector3d uv = rv.cross(toCam).normalize();

        float cx = (float)(hitPos.x + ox);
        float cy = (float)(hitPos.y + oy);
        float cz = (float)(hitPos.z + oz);

        // solid inner dot
        renderBillboard(buf, mat, tess, cx, cy, cz, rv, uv, 0.05F, 255, 0, 0, 255);
        // soft glow ring
        renderBillboard(buf, mat, tess, cx, cy, cz, rv, uv, 0.10F, 255, 0, 0, 80);

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        ms.popPose();
    }

    private void renderBillboard(BufferBuilder buf, Matrix4f mat, Tessellator tess,
        float cx, float cy, float cz,
        Vector3d rv, Vector3d uv,
        float size, int r, int g, int b, int a)
    {
        float rx = (float)(rv.x * size), ry = (float)(rv.y * size), rz = (float)(rv.z * size);
        float ux = (float)(uv.x * size), uy = (float)(uv.y * size), uz = (float)(uv.z * size);
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buf.vertex(mat, cx - rx - ux, cy - ry - uy, cz - rz - uz).color(r, g, b, a).endVertex();
        buf.vertex(mat, cx + rx - ux, cy + ry - uy, cz + rz - uz).color(r, g, b, a).endVertex();
        buf.vertex(mat, cx + rx + ux, cy + ry + uy, cz + rz + uz).color(r, g, b, a).endVertex();
        buf.vertex(mat, cx - rx + ux, cy - ry + uy, cz - rz + uz).color(r, g, b, a).endVertex();
        tess.end();
    }

    public static boolean hasLaser(ItemStack stack)
    {
        if (!(stack.getItem() instanceof GunItem)) return false;
        ItemStack l = Gun.getAttachment(IAttachment.Type.SIDE_LEFT,   stack);
        ItemStack r = Gun.getAttachment(IAttachment.Type.SIDE_RIGHT,  stack);
        ItemStack u = Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack);
        return (!l.isEmpty() && l.getItem() == ModItems.RED_DOT.get())
            || (!r.isEmpty() && r.getItem() == ModItems.RED_DOT.get())
            || (!u.isEmpty() && u.getItem() == ModItems.RED_DOT.get());
    }

    private static class HitResult
    {
        final boolean hasHit;
        final float   dist;
        final Vector3d pos;
        HitResult(boolean hasHit, float dist, Vector3d pos) {
            this.hasHit = hasHit; this.dist = dist; this.pos = pos;
        }
    }
}
