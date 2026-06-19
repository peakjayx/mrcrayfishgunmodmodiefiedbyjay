package com.mrcrayfish.guns.api;

import com.mrcrayfish.guns.common.GunRegistry;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.tileentity.EvidenceTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

/** öffentliche API für Metropolia & Co. */
public final class CgmGunApi
{
    /** NBT-Schlüssel der Waffen-ID */
    public static final String GUN_ID_KEY = "cgm:GunId";

    private CgmGunApi() {}

    public static boolean isGun(ItemStack stack)
    {
        return stack.getItem() instanceof GunItem;
    }

    @Nullable
    public static String getGunId(ItemStack stack)
    {
        if(!isGun(stack)) return null;
        if(!stack.hasTag()) return null;
        String id = stack.getTag().getString(GUN_ID_KEY);
        return id.isEmpty() ? null : id;
    }

    @Nullable
    public static ResourceLocation getGunType(ItemStack stack)
    {
        return isGun(stack) ? stack.getItem().getRegistryName() : null;
    }

    @Nullable
    public static ResourceLocation getAmmoType(ItemStack stack)
    {
        if(!isGun(stack)) return null;
        Gun gun = ((GunItem) stack.getItem()).getModifiedGun(stack);
        return gun.getProjectile().getItem();
    }

    @Nullable
    public static OwnerInfo getOwner(ServerWorld world, String gunId)
    {
        return GunRegistry.get(world).getOwner(gunId);
    }

    @Nullable
    public static List<EvidenceData> getEvidence(IBlockReader world, BlockPos pos)
    {
        TileEntity te = world.getBlockEntity(pos);
        if(te instanceof EvidenceTileEntity)
            return ((EvidenceTileEntity) te).getEntries();
        return null;
    }
}
