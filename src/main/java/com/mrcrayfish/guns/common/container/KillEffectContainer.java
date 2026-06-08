package com.mrcrayfish.guns.common.container;

import com.mrcrayfish.guns.common.KillEffectListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

import java.util.UUID;

public class KillEffectContainer extends ChestContainer
{
    public KillEffectContainer(int id, PlayerInventory playerInv, IInventory gui)
    {
        super(ContainerType.GENERIC_9x1, id, playerInv, gui, 1);
    }

    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickType, PlayerEntity player)
    {
        if(slotId >= 0 && slotId < 9 && player instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            switch(slotId)
            {
                case 0: toggleEffect(serverPlayer, KillEffectListener.aceEffectPlayers,       "§fAce Effect",       "§7");       break;
                case 1: toggleEffect(serverPlayer, KillEffectListener.marioEffectPlayers,     "§eMario Effect",     "§6");       break;
                case 2: toggleEffect(serverPlayer, KillEffectListener.trashEffectPlayers,     "§8Trash Effect",     "§7");       break;
                case 3: toggleEffect(serverPlayer, KillEffectListener.rocketEffectPlayers,    "§cRocket Effect",    "§4");       break;
                case 4: toggleEffect(serverPlayer, KillEffectListener.creeperEffectPlayers,   "§2Creeper Effect",   "§4");       break;
                case 5: toggleEffect(serverPlayer, KillEffectListener.explosionEffectPlayers, "§6Explosion Effect", "§4");       break;
                case 6: toggleEffect(serverPlayer, KillEffectListener.totemEffectPlayers,     "§6Totem Effect",     "§e");       break;
                case 7: toggleEffect(serverPlayer, KillEffectListener.mexicoEffectPlayers,    "§aMexico Effect",    "§2");       break;
                case 8: toggleEffect(serverPlayer, KillEffectListener.waterEffectPlayers,     "§bWater Effect",     "§3");       break;
            }
        }
        return ItemStack.EMPTY;
    }

    private void toggleEffect(ServerPlayerEntity player, java.util.Set<UUID> effectSet, String name, String offColor)
    {
        UUID uuid = player.getUUID();
        if(effectSet.contains(uuid))
        {
            effectSet.remove(uuid);
            player.sendMessage(new StringTextComponent(name + " " + offColor + "deactivated!"), uuid);
        }
        else
        {
            removeAll(uuid);
            effectSet.add(uuid);
            player.sendMessage(new StringTextComponent(name + " §aactivated!"), uuid);
        }
    }

    private void removeAll(UUID uuid)
    {
        KillEffectListener.aceEffectPlayers.remove(uuid);
        KillEffectListener.marioEffectPlayers.remove(uuid);
        KillEffectListener.trashEffectPlayers.remove(uuid);
        KillEffectListener.rocketEffectPlayers.remove(uuid);
        KillEffectListener.creeperEffectPlayers.remove(uuid);
        KillEffectListener.explosionEffectPlayers.remove(uuid);
        KillEffectListener.totemEffectPlayers.remove(uuid);
        KillEffectListener.mexicoEffectPlayers.remove(uuid);
        KillEffectListener.waterEffectPlayers.remove(uuid);
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        return true;
    }
}
