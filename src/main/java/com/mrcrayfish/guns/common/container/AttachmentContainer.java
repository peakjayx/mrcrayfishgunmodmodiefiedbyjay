package com.mrcrayfish.guns.common.container;

import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.common.container.slot.AttachmentSlot;
import com.mrcrayfish.guns.init.ModContainers;
import com.mrcrayfish.guns.item.attachment.IAttachment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/**
 * Author: MrCrayfish
 */
public class AttachmentContainer extends Container
{
    // x-Position der Side-Slots relativ zum Container-Origin (links neben Haupt-Panel)
    public static final int SIDE_X = -22;

    private ItemStack weapon;
    private IInventory playerInventory;
    private IInventory weaponInventory = new Inventory(IAttachment.Type.values().length)
    {
        @Override
        public void setChanged()
        {
            super.setChanged();
            AttachmentContainer.this.slotsChanged(this);
        }
    };
    private boolean loaded = false;

    public AttachmentContainer(int windowId, PlayerInventory playerInventory, ItemStack stack)
    {
        this(windowId, playerInventory);
        ItemStack[] attachments = new ItemStack[IAttachment.Type.values().length];
        for(int i = 0; i < attachments.length; i++)
        {
            attachments[i] = Gun.getAttachment(IAttachment.Type.values()[i], stack);
        }
        for(int i = 0; i < attachments.length; i++)
        {
            this.weaponInventory.setItem(i, attachments[i]);
        }
        this.loaded = true;
    }

    public AttachmentContainer(int windowId, PlayerInventory playerInventory)
    {
        super(ModContainers.ATTACHMENTS.get(), windowId);
        this.weapon = playerInventory.getSelected();
        this.playerInventory = playerInventory;

        // 4 Haupt-Attachment-Slots (linke Spalte, Originalposition)
        this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.SCOPE,        playerInventory.player, 0, 8, 17));
        this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.BARREL,       playerInventory.player, 1, 8, 35));
        this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.STOCK,        playerInventory.player, 2, 8, 53));
        this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.UNDER_BARREL, playerInventory.player, 3, 8, 71));

        // Side-Slots — links neben dem Haupt-Panel
        this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.SIDE_LEFT,  playerInventory.player, 4, SIDE_X, 17));
        this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.SIDE_RIGHT, playerInventory.player, 5, SIDE_X, 35));

        // Spieler-Inventar (Originalpositionen — unverändert)
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            if(i == playerInventory.selected)
            {
                this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 160)
                {
                    @Override
                    public boolean mayPickup(PlayerEntity playerIn)
                    {
                        return false;
                    }
                });
            }
            else
            {
                this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 160));
            }
        }
    }

    public boolean isLoaded()
    {
        return this.loaded;
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn)
    {
        return true;
    }

    @Override
    public void slotsChanged(IInventory inventoryIn)
    {
        CompoundNBT attachments = new CompoundNBT();

        // Slot-Index bestimmt Tag-Key (nicht der Item-Typ) → ISideMount passt in beide Slots
        for(int i = 0; i < IAttachment.Type.values().length; i++)
        {
            ItemStack attachment = this.getSlot(i).getItem();
            if(!attachment.isEmpty() && attachment.getItem() instanceof IAttachment)
            {
                attachments.put(IAttachment.Type.values()[i].getTagKey(), attachment.save(new CompoundNBT()));
            }
        }

        CompoundNBT tag = this.weapon.getOrCreateTag();
        tag.put("Attachments", attachments);
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index)
    {
        ItemStack copyStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if(slot != null && slot.hasItem())
        {
            ItemStack slotStack = slot.getItem();
            copyStack = slotStack.copy();
            if(index < this.weaponInventory.getContainerSize())
            {
                if(!this.moveItemStackTo(slotStack, this.weaponInventory.getContainerSize(), this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.moveItemStackTo(slotStack, 0, this.weaponInventory.getContainerSize(), false))
            {
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }
        }

        return copyStack;
    }

    public IInventory getPlayerInventory()
    {
        return this.playerInventory;
    }

    public IInventory getWeaponInventory()
    {
        return this.weaponInventory;
    }
}
