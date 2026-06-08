package com.mrcrayfish.guns.item;

import com.mrcrayfish.guns.item.attachment.IAttachment;
import com.mrcrayfish.guns.item.attachment.ISideMount;
import com.mrcrayfish.guns.item.attachment.impl.SideMount;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;

/**
 * Attachment item for left/right side slots.
 * Kann in SIDE_LEFT oder SIDE_RIGHT platziert werden.
 */
public class SideMountItem extends AttachmentItem implements ISideMount
{
    private final SideMount sideMount;

    public SideMountItem(SideMount sideMount, Properties properties)
    {
        super(properties);
        this.sideMount = sideMount;
    }

    @Override
    public IAttachment.Type getType()
    {
        // Default-Typ; echter Slot wird über AttachmentSlot/Container gesteuert
        return IAttachment.Type.SIDE_LEFT;
    }

    @Override
    public SideMount getProperties()
    {
        return this.sideMount;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment == Enchantments.BINDING_CURSE
            || super.canApplyAtEnchantingTable(stack, enchantment);
    }
}
