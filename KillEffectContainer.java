package de.junior.untitled.core.gui;

import de.junior.untitled.core.events.KillEffectListener;
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

public class KillEffectContainer extends ChestContainer {

    public KillEffectContainer(int id, PlayerInventory playerInv, IInventory gui) {
        super(ContainerType.GENERIC_9x1, id, playerInv, gui, 1);
    }

    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
        if (slotId >= 0 && slotId < 9) {
            if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                if (slotId == 0) toggleAceEffect(serverPlayer);
                if (slotId == 1) toggleMarioEffect(serverPlayer);
                if (slotId == 2) toggleTrashEffect(serverPlayer);
                if (slotId == 3) toggleRocketEffect(serverPlayer);
                if (slotId == 4) toggleCreeperEffect(serverPlayer);
                if (slotId == 5) toggleExplosionEffect(serverPlayer);
                if (slotId == 6) toggleTotemEffect(serverPlayer);
                if (slotId == 7) toggleMexicoEffect(serverPlayer);
                if (slotId == 8) toggleWaterEffect(serverPlayer);
            }
            return ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    private void removeAll(UUID uuid) {
        KillEffectListener.rocketEffectPlayers.remove(uuid);
        KillEffectListener.creeperEffectPlayers.remove(uuid);
        KillEffectListener.explosionEffectPlayers.remove(uuid);
        KillEffectListener.trashEffectPlayers.remove(uuid);
        KillEffectListener.marioEffectPlayers.remove(uuid);
        KillEffectListener.aceEffectPlayers.remove(uuid);
        KillEffectListener.totemEffectPlayers.remove(uuid);
        KillEffectListener.mexicoEffectPlayers.remove(uuid);
        KillEffectListener.waterEffectPlayers.remove(uuid);
    }

    private void toggleWaterEffect(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (KillEffectListener.waterEffectPlayers.contains(uuid)) {
            KillEffectListener.waterEffectPlayers.remove(uuid);
            player.sendMessage(new StringTextComponent("§bWater Effect §3deaktiviert!"), uuid);
        } else {
            removeAll(uuid);
            KillEffectListener.waterEffectPlayers.add(uuid);
            player.sendMessage(new StringTextComponent("§bWater Effect §3aktiviert!"), uuid);
        }
    }

    private void toggleMexicoEffect(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (KillEffectListener.mexicoEffectPlayers.contains(uuid)) {
            KillEffectListener.mexicoEffectPlayers.remove(uuid);
            player.sendMessage(new StringTextComponent("§aMexico Effect §2deaktiviert!"), uuid);
        } else {
            removeAll(uuid);
            KillEffectListener.mexicoEffectPlayers.add(uuid);
            player.sendMessage(new StringTextComponent("§aMexico Effect §2aktiviert!"), uuid);
        }
    }

    private void toggleTotemEffect(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (KillEffectListener.totemEffectPlayers.contains(uuid)) {
            KillEffectListener.totemEffectPlayers.remove(uuid);
            player.sendMessage(new StringTextComponent("§6Totem Effect §edeaktiviert!"), uuid);
        } else {
            removeAll(uuid);
            KillEffectListener.totemEffectPlayers.add(uuid);
            player.sendMessage(new StringTextComponent("§6Totem Effect §eaktiviert!"), uuid);
        }
    }

    private void toggleAceEffect(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (KillEffectListener.aceEffectPlayers.contains(uuid)) {
            KillEffectListener.aceEffectPlayers.remove(uuid);
            player.sendMessage(new StringTextComponent("§fAce Effect §7deaktiviert!"), uuid);
        } else {
            removeAll(uuid);
            KillEffectListener.aceEffectPlayers.add(uuid);
            player.sendMessage(new StringTextComponent("§fAce Effect §7aktiviert!"), uuid);
        }
    }

    private void toggleMarioEffect(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (KillEffectListener.marioEffectPlayers.contains(uuid)) {
            KillEffectListener.marioEffectPlayers.remove(uuid);
            player.sendMessage(new StringTextComponent("§eMario Effect §6deaktiviert!"), uuid);
        } else {
            removeAll(uuid);
            KillEffectListener.marioEffectPlayers.add(uuid);
            player.sendMessage(new StringTextComponent("§eMario Effect §6aktiviert!"), uuid);
        }
    }

    private void toggleTrashEffect(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (KillEffectListener.trashEffectPlayers.contains(uuid)) {
            KillEffectListener.trashEffectPlayers.remove(uuid);
            player.sendMessage(new StringTextComponent("§8Trash Effect §4deaktiviert!"), uuid);
        } else {
            removeAll(uuid);
            KillEffectListener.trashEffectPlayers.add(uuid);
            player.sendMessage(new StringTextComponent("§8Trash Effect §7aktiviert!"), uuid);
        }
    }

    private void toggleRocketEffect(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (KillEffectListener.rocketEffectPlayers.contains(uuid)) {
            KillEffectListener.rocketEffectPlayers.remove(uuid);
            player.sendMessage(new StringTextComponent("§cRocket Effect §4deaktiviert!"), uuid);
        } else {
            removeAll(uuid);
            KillEffectListener.rocketEffectPlayers.add(uuid);
            player.sendMessage(new StringTextComponent("§aRocket Effect §2aktiviert!"), uuid);
        }
    }

    private void toggleCreeperEffect(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (KillEffectListener.creeperEffectPlayers.contains(uuid)) {
            KillEffectListener.creeperEffectPlayers.remove(uuid);
            player.sendMessage(new StringTextComponent("§cCreeper Effect §4deaktiviert!"), uuid);
        } else {
            removeAll(uuid);
            KillEffectListener.creeperEffectPlayers.add(uuid);
            player.sendMessage(new StringTextComponent("§aCreeper Effect §2aktiviert!"), uuid);
        }
    }

    private void toggleExplosionEffect(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (KillEffectListener.explosionEffectPlayers.contains(uuid)) {
            KillEffectListener.explosionEffectPlayers.remove(uuid);
            player.sendMessage(new StringTextComponent("§cExplosion Effect §4deaktiviert!"), uuid);
        } else {
            removeAll(uuid);
            KillEffectListener.explosionEffectPlayers.add(uuid);
            player.sendMessage(new StringTextComponent("§aExplosion Effect §2aktiviert!"), uuid);
        }
    }
}