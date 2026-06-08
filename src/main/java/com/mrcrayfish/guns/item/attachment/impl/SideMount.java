package com.mrcrayfish.guns.item.attachment.impl;

import com.mrcrayfish.guns.interfaces.IGunModifier;

/**
 * Side-mount attachment — passt in SIDE_LEFT oder SIDE_RIGHT Slot
 */
public class SideMount extends Attachment
{
    private SideMount(IGunModifier... modifiers)
    {
        super(modifiers);
    }

    public static SideMount create(IGunModifier... modifiers)
    {
        return new SideMount(modifiers);
    }
}
